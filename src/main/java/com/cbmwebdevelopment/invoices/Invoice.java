/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.customers.CustomerInvoicesTableController.CustomerInvoices;
import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import com.cbmwebdevelopment.notifications.Notifications;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import static java.util.Locale.US;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Invoice {

    /**
     * Return an observable list of the invoices associated with the selected customer
     * @param id
     * @return 
     */
    public ObservableList<CustomerInvoices> getCustomerInvoices(String id){
        ObservableList<CustomerInvoices> customerInvoices = FXCollections.observableArrayList();
        
        String sql = "SELECT invoices.invoice_id, if(invoices.tax != 'No Tax', sum(invoice_items.unit_price * invoice_items.quantity) * SUM(1 + (invoices.tax/100)), sum(invoice_items.unit_price * invoice_items.quantity)) as 'TOTAL', IF(SUM(transaction_data.transaction_amount) IS NULL, IF(invoices.tax != 'No Tax', sum(invoice_items.unit_price * invoice_items.quantity) * SUM(1 + (invoices.tax/100)), sum(invoice_items.unit_price * invoice_items.quantity)), IF(invoices.tax != 'No Tax', sum(invoice_items.unit_price * invoice_items.quantity) * SUM(1 + (invoices.tax/100)), sum(invoice_items.unit_price * invoice_items.quantity)) - SUM(transaction_data.transaction_amount)) as 'BALANCE', invoices.status, invoices.invoice_date FROM invoices INNER JOIN invoice_items ON invoice_items.invoice_id = invoices.invoice_id LEFT JOIN transaction_data ON transaction_data.invoice_id = invoices.invoice_id WHERE invoices.customer_id = ? GROUP BY invoices.invoice_id;";
        try{
            Connection conn = new DBConnection().connect();
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    int invoiceId = rs.getInt("invoice_id");
                    String invoiceAmount = NumberFormat.getCurrencyInstance(US).format(rs.getDouble("total"));
                    String amountDue = NumberFormat.getCurrencyInstance(US).format(rs.getDouble("balance"));
                    String status = rs.getBoolean("status") ? "Paid" : "Unpaid";
                    String date = rs.getString("invoice_date");
                    
                    customerInvoices.add(new CustomerInvoices(invoiceId, invoiceAmount, amountDue, status, date));
                }while(rs.next());
            }
        }catch(SQLException ex){
            Notifications.snackbarNotification("Error retrieving invoices.");
            System.err.println(ex.getMessage());
        }
        
        return customerInvoices;
    }
    
    /**
     * Save the invoice information. Invoice data will be saved in a different method.
     * @param invoiceData
     * @return 
     */
    public Integer saveInvoice(InvoiceData invoiceData) {
        Integer invoiceId = invoiceData.getId();
        String sql = "INSERT INTO INVOICES(INVOICE_ID, CUSTOMER_ID, INVOICE_DATE, STATUS, BILLING_ADDRESS, SHIPPING_ADDRESS, MEMO, TAX) VALUES(?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE CUSTOMER_ID =?, INVOICE_DATE =?, STATUS =?, BILLING_ADDRESS =?, SHIPPING_ADDRESS =?, MEMO =?, TAX =?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, invoiceData.getId());
            ps.setString(2, invoiceData.getCustomerId());
            ps.setString(3, invoiceData.getInvoiceDate());
            ps.setInt(4, 0);
            ps.setString(5, invoiceData.getBillingAddress());
            ps.setString(6, invoiceData.getShippingAddress());
            ps.setString(7, invoiceData.getMemo());
            ps.setString(8, invoiceData.getTaxRate());
            ps.setString(9, invoiceData.getCustomerId());
            ps.setString(10, invoiceData.getInvoiceDate());
            ps.setInt(11, 0);
            ps.setString(12, invoiceData.getBillingAddress());
            ps.setString(13, invoiceData.getShippingAddress());
            ps.setString(14, invoiceData.getMemo());
            ps.setString(15, invoiceData.getTaxRate());

            int success = ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (success >= 1 && rs.next()) {
                invoiceId = invoiceId == 0 ? rs.getInt(1) : invoiceId;
                updateInvoiceItems(invoiceId, invoiceData.getInvoiceItems());
            }
            conn.close();
        } catch (SQLException ex) {
            Notifications.snackbarNotification("Failed to save invoice");
            System.err.println(ex.getMessage());
        }
        return invoiceId;
    }

    /**
     * Remove an invoice item from the database.
     * @param invoiceId
     * @param itemId 
     */
    public void removeInvoiceItem(String invoiceId, String itemId){
        String sql = "DELETE FROM invoice_items WHERE invoice_id = ? and id = ?";
        try{
            Connection conn = new DBConnection().connect();
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            ps.setString(2, itemId);
            ps.executeUpdate();
            
            conn.close();
        }catch(SQLException ex){
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error deleting record. Please try again.");
        }
    }
    
    /**
     * Updates or inserts invoice items.
     * @param invoiceId
     * @param items 
     */
    protected void updateInvoiceItems(Integer invoiceId, ObservableList<InvoiceItems> items) {
        String sql = "INSERT INTO INVOICE_ITEMS(ID, INVOICE_ID, ITEM, DESCRIPTION, QUANTITY, UNIT_PRICE) VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE ITEM = ?, DESCRIPTION = ?, QUANTITY = ?, UNIT_PRICE = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            items.forEach(item -> {
                try {
                    ps.setInt(1, item.getId());
                    ps.setInt(2, invoiceId);
                    ps.setString(3, item.getItem());
                    ps.setString(4, item.getDescription());
                    ps.setDouble(5, item.getQuantity());
                    ps.setString(6, item.getPrice());
                    ps.setString(7, item.getItem());
                    ps.setString(8, item.getDescription());
                    ps.setDouble(9, item.getQuantity());
                    ps.setString(10, item.getPrice());
                    ps.addBatch();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }

            });
            ps.executeBatch();
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Notifications.snackbarNotification("Error updating invoice items.");
            System.err.println(ex.getMessage());
        }

    }

    /**
     * Get the invoice data. Save to getter/setter class instance
     *
     * @param invoiceId
     * @return
     */
    protected InvoiceData getInvoice(String invoiceId) {

        InvoiceData invoiceData = new InvoiceData();
        String sql = "SELECT INVOICE_ID, CUSTOMER_ID, DATE_FORMAT(INVOICE_DATE, '%Y-%m-%d') as 'INVOICE_DATE', STATUS, BILLING_ADDRESS, SHIPPING_ADDRESS, MEMO, TAX FROM INVOICES WHERE INVOICE_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                invoiceData.setId(rs.getInt("INVOICE_ID"));
                invoiceData.setCustomerId(rs.getString("CUSTOMER_ID"));
                invoiceData.setInvoiceDate(rs.getString("INVOICE_DATE"));
                invoiceData.setStatus(rs.getString("STATUS"));
                invoiceData.setBillingAddress(rs.getString("BILLING_ADDRESS"));
                invoiceData.setShippingAddress(rs.getString("SHIPPING_ADDRESS"));
                invoiceData.setMemo(rs.getString("MEMO"));
                invoiceData.setTaxRate(rs.getString("TAX"));
                invoiceData.setInvoiceItems(getInvoiceItems(invoiceId));
            }

            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error retrieving invoice");
        }
        return invoiceData;
    }

    /**
     * Gets the invoice items
     * @param invoiceId
     * @return 
     */
    private ObservableList<InvoiceItems> getInvoiceItems(String invoiceId) {
        ObservableList<InvoiceItems> items = FXCollections.observableArrayList();
        String sql = "SELECT ID, INVOICE_ID, ITEM, DESCRIPTION, QUANTITY, UNIT_PRICE, QUANTITY * UNIT_PRICE AS 'TOTAL' FROM INVOICE_ITEMS WHERE INVOICE_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    items.add(new InvoiceItems(rs.getInt("ID"), rs.getString("ITEM"), rs.getString("DESCRIPTION"), rs.getDouble("QUANTITY"), rs.getString("UNIT_PRICE"), rs.getString("TOTAL")));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error retrieving invoice items.");
        }
        return items;
    }
}
