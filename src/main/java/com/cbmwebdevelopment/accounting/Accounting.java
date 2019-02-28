/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.accounting.OutstandingTransactionsTableController.OutstandingTransactions;
import com.cbmwebdevelopment.accounting.TransactionsTableController.Transactions;
import com.cbmwebdevelopment.invoices.InvoiceStatus;
import com.cbmwebdevelopment.notifications.Notifications;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Iterator;
import static java.util.Locale.US;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class Accounting {

    private final String LINK = "http://www.meehanwoodworking.com/vulcan/classes/Accounting.php";

    public ObservableList<OutstandingTransactions> getOutstandingTransactions(String customerId){
        ObservableList<OutstandingTransactions> outstandingTransactions = FXCollections.observableArrayList();
        try{
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_customer_outstanding_transactions", "UTF-8");
            data += "&" + URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(customerId, "UTF-8");
            
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));                       
            JSONArray jsonArr = new JSONArray(reader.readLine());
            if(!reader.readLine().equals("null")){
                for(int i = 0; i < jsonArr.length(); i++){
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    outstandingTransactions.add(new OutstandingTransactions(jsonObj.getInt("INVOICE_ID"), false, jsonObj.getString("DESCRIPTION"), jsonObj.getString("DUE_DATE"), jsonObj.getString("INVOICE_AMOUNT"), jsonObj.getString("OPEN_BALANCE"), ""));
                    
                }
            }
            
        }catch(IOException ex){
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Error retrieving outstanding tranactions");
        }
        
        return outstandingTransactions;
    }
    
    public ObservableList<Transactions> getTransactions(String startDate, String endDate) {
        ObservableList<Transactions> transactions = FXCollections.observableArrayList();

        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_transactions", "UTF-8");
            data += "&" + URLEncoder.encode("start_date", "UTF-8") + "=" + URLEncoder.encode(startDate, "UTF-8");
            data += "&" + URLEncoder.encode("end_date", "UTF-8") + "=" + URLEncoder.encode(endDate, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(reader.readLine());
                break;
            }           
            if (!sb.toString().equals("null") ) {
                JSONArray jsonArr = new JSONArray(sb.toString());
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    transactions.add(new Transactions(jsonObj.getInt("ID"), jsonObj.getString("DESCRIPTION"), jsonObj.getString("TYPE"), jsonObj.getString("TRANSACTION_DATE"), NumberFormat.getCurrencyInstance(US).format(jsonObj.getDouble("AMOUNT"))));
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("Transactions: " + transactions);
        return transactions;
    }

    public Double[] profitLoss(String period) {
        Double[] pl = new Double[2];
        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_profit_loss", "UTF-8");
            data += "&" + URLEncoder.encode("period", "UTF-8") + "=" + URLEncoder.encode(period, "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }

            JSONObject jsonObj = new JSONObject(sb.toString());
            if (jsonObj.getBoolean("success")) {
                pl[0] = jsonObj.getDouble("INCOME");
                pl[1] = jsonObj.getDouble("EXPENSES");
            }

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return pl;
    }

    public ObservableList<PieChart.Data> totalExpenses(String period) {
        ObservableList<PieChart.Data> expenses = FXCollections.observableArrayList();
        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_expenses", "UTF-8");
            data += "&" + URLEncoder.encode("period", "UTF-8") + "=" + URLEncoder.encode(period, "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                Iterator<String> keys = jsonObj.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (jsonObj.get(key) instanceof JSONObject) {
                        expenses.add(new PieChart.Data(key, jsonObj.getDouble(key)));
                    }
                }
            }

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return expenses;
    }

    public ObservableList<InvoiceStatus> invoiceStatus(String period) {
        ObservableList<InvoiceStatus> invoiceStatus = FXCollections.observableArrayList();
        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_invoice_status", "UTF-8");
            data += "&" + URLEncoder.encode("period", "UTF-8") + "=" + URLEncoder.encode(period, "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            System.out.println(sb.toString());
            JSONArray jsonArray = new JSONArray(sb.toString());
            if (!jsonArray.isNull(0)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    invoiceStatus.add(new InvoiceStatus(jsonObject.getInt("INVOICE_ID"), jsonObject.getDouble("TOTAL"), jsonObject.getBoolean("STATUS")));
                }
            }

        } catch (IOException ex) {
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
