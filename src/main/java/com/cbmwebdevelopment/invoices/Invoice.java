/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class Invoice {
    private final String URL_STRING = "http://www.meehanwoodworking.com/vulcan/classes/Invoice.php";
    
    
    public String saveInvoice(InvoiceData invoiceData) {
        try{
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("save_invoice", "UTF-8");
            data += "&" + URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(invoiceData.getId(), "UTF-8");
            data += "&" + URLEncoder.encode("CUSTOMER_ID", "UTF-8") + "=" + URLEncoder.encode(invoiceData.getCustomerId(), "UTF-8");
            data += "&" + URLEncoder.encode("INVOICE_DATE", "UTF-8") + "=" + URLEncoder.encode(invoiceData.getInvoiceDate(), "UTF-8");
            data += "&" + URLEncoder.encode("STATUS", "UTF-8") + "=" + URLEncoder.encode("N/A", "UTF-8");
            data += "&" + URLEncoder.encode("BILLING_ADDRESS", "UTF-8") + "=" + URLEncoder.encode(invoiceData.getBillingAddress(), "UTF-8");
            data += "&" + URLEncoder.encode("MEMO", "UTF-8") + "=" + URLEncoder.encode(invoiceData.getShippingAddress(), "UTF-8");
            data += "&" + URLEncoder.encode("TAX", "UTF-8") + "=" + URLEncoder.encode(invoiceData.getMemo(), "UTF-8");
            
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObject = new JSONObject(reader.lines().collect(Collectors.joining()));
            
            return jsonObject.getString("key");
            
        }catch(IOException ex){
            System.err.println(ex.getMessage());
            return null;
        }
        
    }

    protected void updateInvoiceItems(String invoiceId, ObservableList<InvoiceItems> items) {
        try{
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            
            JSONArray jsonArray = new JSONArray();
            items.forEach(item -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("invoice_id", invoiceId);
                jsonObject.put("item_id", item.getId());
                jsonObject.put("item", item.getItem());
                jsonObject.put("description", item.getDescription());
                jsonObject.put("quantity", item.getQuantity());
                jsonObject.put("price", item.getPrice());
                
                jsonArray.put(jsonObject);
            });
            
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("update_invoice_items", "UTF-8");
            data += "&" + URLEncoder.encode("items", "UTF-8") + "=" + URLEncoder.encode(jsonArray.toString(), "UTF-8");
            
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            JSONArray jsonArr = new JSONArray(reader.lines().collect(Collectors.joining()));
            
        }catch(IOException ex){
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
        try{
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_invoice", "UTF-8");
            data += "&" + URLEncoder.encode("invoice_id", "UTF-8") + "=" + URLEncoder.encode(invoiceId, "UTF-8");
            
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObj = new JSONObject(reader.lines().collect(Collectors.joining()));
            invoiceData.setId(jsonObj.getString("INVOICE_ID"));
            invoiceData.setCustomerId(jsonObj.getString("CUSTOMER_ID"));
            invoiceData.setInvoiceDate(jsonObj.getString("INVOICE_DATE"));
            invoiceData.setStatus(jsonObj.getString("STATUS"));
            invoiceData.setBillingAddress(jsonObj.getString("BILLING_ADDRESS"));
            invoiceData.setShippingAddress(jsonObj.getString("SHIPPING_ADDRESS"));
            invoiceData.setMemo(jsonObj.getString("MEMO"));
            invoiceData.setTaxRate(jsonObj.getString("TAX"));
            invoiceData.setInvoiceItems(getInvoiceItems(invoiceId));
            
            wr.close();
            reader.close();
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        return invoiceData;
    }

    private ObservableList<InvoiceItems> getInvoiceItems(String invoiceId) {
        ObservableList<InvoiceItems> items = FXCollections.observableArrayList();
        try{
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_invoice_items", "UTF-8");
            data += "&" + URLEncoder.encode("invoice_id", "UTF-8") + "=" + URLEncoder.encode(invoiceId, "UTF-8");
            
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray jsonArr = new JSONArray(reader.lines().collect(Collectors.joining()));
            for(int i = 0; i < jsonArr.length(); i++){
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                
                items.add(new InvoiceItems(jsonObj.getInt("ID"), jsonObj.getString("ITEM"), jsonObj.getString("DESCRIPTION"), jsonObj.getDouble("QUANTITY"), jsonObj.getString("UNIT_PRICE"), jsonObj.getString("TOTAL")));
               
            }
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        return items;
    }
}
