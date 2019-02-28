/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.quote;

import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class Quote {

    private final String LINK = "http://www.meehanwoodworking.com/vulcan/classes/Quote.php";

    /**
     *
     * @param id
     * @return
     */
    protected QuoteData getQuoteData(String id) {
        QuoteData quoteData = new QuoteData();

        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_quote_data", "UTF-8");
            data += "&" + URLEncoder.encode("quote_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

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
            quoteData = new QuoteData(jsonObj.getString("QUOTE_ID"), jsonObj.getString("CUSTOMER_ID"), jsonObj.getString("BILLING_ADDRESS"), jsonObj.getString("QUOTE_DATE"), getQuoteItems(id));

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return quoteData;
    }

    /**
     *
     * @param id
     * @return
     */
    private ObservableList<InvoiceItems> getQuoteItems(String id) {
        ObservableList<InvoiceItems> quoteItems = FXCollections.observableArrayList();
        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_quote_items", "UTF-8");
            data += "&" + URLEncoder.encode("quote_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

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

            JSONArray jsonArr = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                System.out.println("Items object: " + jsonObj);
                String total = String.valueOf(jsonObj.getDouble("QUANTITY") * jsonObj.getDouble("UNIT_PRICE"));
                
                quoteItems.add(new InvoiceItems(jsonObj.getInt("ID"), jsonObj.getString("ITEM"), jsonObj.getString("DESCRIPTION"), jsonObj.getDouble("QUANTITY"), jsonObj.getString("UNIT_PRICE"), total));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        
        return quoteItems;
    }

    /**
     *
     * @param customerId
     * @param date
     * @param quoteNumber
     * @param billingAddress
     * @param quoteData
     * @return
     */
    public String saveQuote(String customerId, String date, String quoteNumber, String billingAddress, ObservableList<InvoiceItems> quoteData) {
        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("save_quote", "UTF-8");
            data += "&" + URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(customerId), "UTF-8");
            data += "&" + URLEncoder.encode("quote_date", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(date), "UTF-8");
            data += "&" + URLEncoder.encode("billing_address", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(billingAddress), "UTF-8");
            data += "&" + URLEncoder.encode("quote_number", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(quoteNumber), "UTF-8");

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
                quoteNumber = jsonObj.getString("key");
                System.out.println(quoteNumber);
            }

            if (!quoteData.isEmpty() && quoteNumber != null) {
                System.out.println("Saving Items");
                saveQuoteItems(quoteNumber, quoteData);
            }
            return quoteNumber;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return quoteNumber;
        }
    }

    /**
     *
     * @param quoteId
     * @param quoteData
     * @return
     */
    private boolean saveQuoteItems(String quoteId, ObservableList<InvoiceItems> quoteData) {
        try {
            URL url = new URL(LINK);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            JSONArray items = new JSONArray();
            quoteData.forEach(item -> {
                JSONObject obj = new JSONObject();
                obj.put("item", item.getItem());
                obj.put("quantity", item.getQuantity());
                obj.put("description", item.getDescription());
                obj.put("price", item.getPrice());
                obj.put("item_id", item.getId());
                obj.put("quote_id", quoteId);

                items.put(obj);
            });
            
            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("save_quote_items", "UTF-8");
            data += "&" + URLEncoder.encode("items", "UTF-8") + "=" + URLEncoder.encode(items.toString(), "UTF-8");
            
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
            
            JSONArray jsonArr = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                if (!jsonObject.getBoolean("success")) {
                    return false;
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        return true;
    }
}
