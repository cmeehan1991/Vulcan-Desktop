/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.accounting.OutstandingTransactionsTableController.OutstandingTransactions;
import com.cbmwebdevelopment.accounting.Payment.SelectedTransactions;
import com.cbmwebdevelopment.accounting.TransactionsTableController.Transactions;
import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.invoices.InvoiceStatus;
import com.cbmwebdevelopment.notifications.Notifications;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import static java.util.Locale.US;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

/**
 *
 * @author cmeehan
 */
public class Accounting {

    public String savePayment(Payment payment) {
        String key = null;
        String sql = "INSERT INTO TRANSACTIONS(ID, AMOUNT, TRANSACTION_DATE, REFERENCE_NUMBER, CUSTOMER_ID) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE AMOUNT = ?, TRANSACTION_DATE = ?, REFERENCE_NUMBER = ?, CUSTOMER_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            payment.getSelectedTransaction().forEach(item -> {

            });

            ps.setString(1, payment.getTransactionNumber());
            ps.setString(2, payment.getAmountApplied());
            ps.setString(3, payment.getPaymentDate());
            ps.setString(4, payment.getReferenceNumber());
            ps.setString(5, payment.getCustomerId());
            ps.setDouble(6, payment.getAmountReceived());
            ps.setString(7, payment.getPaymentDate());
            ps.setString(8, payment.getReferenceNumber());
            ps.setString(9, payment.getCustomerId());
            int update = ps.executeUpdate();
            if (update > 0 && ps.getGeneratedKeys().next()) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println(rs.getInt(1));
                    key = !payment.getTransactionNumber().isEmpty() && !payment.getTransactionNumber().equals("0") ? payment.getTransactionNumber() : String.valueOf(rs.getInt(1));
                    saveTransactionItems(key, payment.getSelectedTransaction());

                }
            }
            ps.close();
            conn.close();
            return key;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error saving payment");
            return key;
        }
    }

    private void saveTransactionItems(String transactionId, ObservableList<SelectedTransactions> transactions) {
        String sql = "INSERT INTO transaction_data(transaction_id, invoice_id, transaction_amount) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE transaction_amount = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            transactions.forEach(t -> {
                try {
                    ps.setString(1, transactionId);
                    ps.setInt(2, t.getTransactionId());
                    ps.setDouble(3, t.getPaymentAmount());
                    ps.setDouble(4, t.getPaymentAmount());
                    ps.addBatch();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            });
            ps.executeBatch();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error saving transaction data");
        }
    }

    public ObservableList<OutstandingTransactions> getOutstandingTransactions(String customerId) {
        ObservableList<OutstandingTransactions> outstandingTransactions = FXCollections.observableArrayList();
        String sql = "SELECT invoices.INVOICE_ID AS 'INVOICE_ID', IF(invoices.PROJECT_ID IS NOT NULL, CONCAT('Project ', projects.PROJECT_TYPE), CONCAT('Invoice ', invoices.INVOICE_ID)) as 'DESCRIPTION', DATE_FORMAT(DATE_ADD(invoices.INVOICE_DATE, INTERVAL 30 DAY), '%Y-%m-%d') AS 'DUE_DATE', IF(invoices.TAX = 'No Tax', (SUM(invoice_items.QUANTITY * invoice_items.UNIT_PRICE)),(SUM(invoice_items.QUANTITY * invoice_items.UNIT_PRICE) * invoices.TAX)) AS 'ORIGINAL_AMOUNT', IF(SUM(transactions.AMOUNT) IS NULL, IF(invoices.TAX = 'No Tax', (SUM(invoice_items.QUANTITY * invoice_items.UNIT_PRICE)),(SUM(invoice_items.QUANTITY * invoice_items.UNIT_PRICE) * invoices.TAX)), IF(invoices.TAX = 'No Tax', (SUM(invoice_items.QUANTITY * invoice_items.UNIT_PRICE)),(SUM(invoice_items.QUANTITY * invoice_items.UNIT_PRICE) * invoices.TAX)) - SUM(transactions.AMOUNT)) AS 'OPEN_BALANCE' FROM invoices LEFT JOIN invoice_items on invoice_items.INVOICE_ID = invoices.INVOICE_ID LEFT JOIN projects ON projects.project_id = invoices.PROJECT_ID LEFT JOIN transactions on transactions.INVOICE_ID = invoices.INVOICE_ID WHERE invoices.CUSTOMER_ID = ? GROUP BY invoices.INVOICE_ID ORDER BY INVOICE_ID ASC;";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    String originalAmount = !String.valueOf(rs.getString("ORIGINAL_AMOUNT")).equals("null") ? NumberFormat.getCurrencyInstance(US).format(rs.getDouble("ORIGINAL_AMOUNT")) : "$0.00";
                    String openBalance = !String.valueOf(rs.getString("OPEN_BALANCE")).equals("null") ? NumberFormat.getCurrencyInstance(US).format(rs.getDouble("OPEN_BALANCE")) : "$0.00";
                    if (!openBalance.equals("$0.00")) {
                        outstandingTransactions.add(new OutstandingTransactions(rs.getInt("INVOICE_ID"), false, rs.getString("DESCRIPTION"), rs.getString("DUE_DATE"), originalAmount, openBalance, ""));
                    }
                } while (rs.next());
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error retrieving outstanding tranactions");
        }

        return outstandingTransactions;
    }

    public ObservableList<Transactions> getTransactions(String startDate, String endDate) {
        ObservableList<Transactions> transactions = FXCollections.observableArrayList();
        String sql = "SELECT ID, DESCRIPTION, IF(AMOUNT < 0, 'Debit', 'Credit') as 'TYPE', TRANSACTION_DATE, AMOUNT FROM TRANSACTIONS WHERE TRANSACTION_DATE BETWEEN ? AND ? ORDER BY TRANSACTION_DATE DESC;";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    transactions.add(new Transactions(rs.getInt("ID"), rs.getString("DESCRIPTION"), rs.getString("TYPE"), rs.getString("TRANSACTION_DATE"), NumberFormat.getCurrencyInstance(US).format(rs.getDouble("AMOUNT"))));
                } while (rs.next());
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error retrieving transactions.");
        }
        return transactions;
    }

    public Double[] profitLoss(String period) {
        Double[] pl = new Double[2];
        String sql = "SELECT transactions.ID, transactions.transaction_type, transactions.amount FROM transactions WHERE YEAR(transactions.transaction_date) = YEAR(CURDATE())";
        try {
            Connection conn = new DBConnection().connect();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double expenses = 0.00;
                double income = 0.00;

                do {
                    if (rs.getString("transaction_type").equals("debit")) {
                        expenses += rs.getDouble("AMOUNT");
                    } else {
                        income += rs.getDouble("AMOUNT");
                    }
                } while (rs.next());

                pl[0] = income;
                pl[1] = expenses;
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return pl;
    }

    public ObservableList<PieChart.Data> totalExpenses(String period) {
        ObservableList<PieChart.Data> expenses = FXCollections.observableArrayList();
        String sql = "SELECT category, amount from transactions where transaction_type = 'debit' AND YEAR(transaction_date) = YEAR(CURDATE());";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                do {
                    expenses.add(new PieChart.Data(rs.getString("category"), rs.getDouble("amount")));
                } while (rs.next());
            }

            conn.close();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return expenses;
    }

    private String parsePeriod(String period, String field){
        switch(period){
            case "Today": 
                period = "WHERE " + field + " = DATE_FORMAT(NOW(), '%Y-%m-%d')";
                break;
            case "This Week":
                period = "WHERE " + field + " BETWEEN DATE_ADD(NOW(), INTERVAL(-WEEKDAY(NOW())) DAY) AND DATE_ADD(NOW(), INTERVAL(WEEKDAY(NOW())) DAY)";
                break;
            case "This Month":
                period = "WHERE " + field + " BETWEEN date_add(date_add(LAST_DAY(NOW()),interval 1 DAY),interval -1 MONTH) AND LAST_DAY(NOW())";
                break;
            case "This Year":
                period = "WHERE DATE_FORMAT(" + field + ",'%Y') = DATE_FORMAT(NOW(), '%Y')";
                break;
            case "Last Week":
                period = "WHERE " + field + " BETWEEN DATE_ADD(CURDATE()-INTERVAL 1 WEEK, INTERVAL(-WEEKDAY(CURDATE()-INTERVAL 1 WEEK)) DAY) AND DATE_ADD(CURDATE()-INTERVAL 1 WEEK, INTERVAL(WEEKDAY(CURDATE()-INTERVAL 1 WEEK)) DAY)";
                break;
            case "Last Month":
                period = "WHERE " + field + " BETWEEN DATE_ADD(DATE_ADD(LAST_DAY(NOW()),interval 1 DAY),interval -2 MONTH) AND LAST_DAY(DATE_ADD(DATE_ADD(LAST_DAY(NOW()),interval 1 DAY),interval -2 MONTH))";
                break;
            default: break;
        }
        
        return period;
    }
    
    public ObservableList<InvoiceStatus> invoiceStatus(String period) {
        ObservableList<InvoiceStatus> invoiceStatus = FXCollections.observableArrayList();
        String sql = "SELECT invoices.invoice_id, if(invoices.tax != 'No Tax', sum(invoice_items.unit_price * invoice_items.quantity) * SUM(1 + (invoices.tax/100)), sum(invoice_items.unit_price * invoice_items.quantity)) as 'TOTAL', IF(SUM(transaction_data.transaction_amount) IS NULL, IF(invoices.tax != 'No Tax', sum(invoice_items.unit_price * invoice_items.quantity) * SUM(1 + (invoices.tax/100)), sum(invoice_items.unit_price * invoice_items.quantity)), IF(invoices.tax != 'No Tax', sum(invoice_items.unit_price * invoice_items.quantity) * SUM(1 + (invoices.tax/100)), sum(invoice_items.unit_price * invoice_items.quantity)) - SUM(transaction_data.transaction_amount)) as 'BALANCE' FROM invoices INNER JOIN invoice_items ON invoice_items.invoice_id = invoices.invoice_id LEFT JOIN transaction_data ON transaction_data.invoice_id = invoices.invoice_id " + parsePeriod(period, "invoices.invoice_date" ) + " GROUP BY invoices.invoice_id;";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    boolean paid = rs.getDouble("BALANCE") == 0;
                    invoiceStatus.add(new InvoiceStatus(rs.getInt("INVOICE_ID"), rs.getDouble("TOTAL"), paid));
                } while (rs.next());
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return invoiceStatus;
    }

    public static class Sales {

        private final SimpleStringProperty DATE;
        private final SimpleDoubleProperty AMOUNT;

        public Sales(String date, Double amount) {
            this.DATE = new SimpleStringProperty(date);
            this.AMOUNT = new SimpleDoubleProperty(amount);
        }

        public void setDate(String val) {
            this.DATE.set(val);
        }

        public String getDate() {
            return this.DATE.get();
        }

        public void setAmount(Double amount) {
            this.AMOUNT.set(amount);
        }

        public Double getAmount() {
            return this.AMOUNT.doubleValue();
        }
    }

}
