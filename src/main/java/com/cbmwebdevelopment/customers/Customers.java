/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.customers;

import com.cbmwebdevelopment.customers.CustomerInvoicesTableController.CustomerInvoices;
import com.jfoenix.controls.JFXComboBox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cmeehan
 */
public class Customers {

    private final String URL_STRING = "http://www.meehanwoodworking.com/vulcan/classes/Customers.php";
    private final String ENC = "UTF-8";

    /**
     *
     * @param id
     * @return
     */
    public String getCustomerById(String id) {
        try {
            // Establish Connection
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Set data
            String data = URLEncoder.encode("action", ENC) + "=" + URLEncoder.encode("get_customer_by_id", ENC);
            data += "&" + URLEncoder.encode("ID", ENC) + "=" + URLEncoder.encode(id, ENC);

            // Send the data
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            // Get the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObject = new JSONObject(reader.lines().collect(Collectors.joining()));

            if (!jsonObject.getBoolean("success")) {
                return null;
            }

            return jsonObject.getString("NAME");

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Get the customer address and format
     *
     * @param id
     * @return
     */
    public String getCustomerAddress(String id) {
        try {
            // Establish Connection
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            // Set data
            String data = URLEncoder.encode("action", ENC) + "=" + URLEncoder.encode("get_customer_address", ENC);
            data += "&" + URLEncoder.encode("ID", ENC) + "=" + URLEncoder.encode(id, ENC);

            // Send the data
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            // Get the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObject = new JSONObject(reader.lines().collect(Collectors.joining()));
            System.out.println(jsonObject);
            if (!jsonObject.getBoolean("success")) {
                return null;
            }
            String address = "";
            if (!jsonObject.getString("COMPANY_NAME").isEmpty()) {
                address += jsonObject.getString("COMPANY_NAME") + "\n";
            }

            address += jsonObject.getString("PRIMARY_ADDRESS") + "\n";

            if (!jsonObject.getString("SECONDARY_ADDRESS").isEmpty()) {
                address += jsonObject.getString("SECONDARY_ADDRESS") + "\n";
            }

            address += jsonObject.getString("CITY") + "," + jsonObject.getString("STATE") + " " + jsonObject.getString("POSTAL_CODE") + "\n";
            address += jsonObject.getString("COUNTRY");

            return address;

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public JFXComboBox customersComboBox() {
        JFXComboBox<Customer> cb = new JFXComboBox();
        cb.setPromptText("Select One");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            ObservableList<Customer> customers = FXCollections.observableArrayList(getCustomers());
            Platform.runLater(() -> {
                cb.setItems(customers);
                cb.getItems().add(customers.size(), new Customer(null, "<Add New Customer>"));
                cb.setConverter(new StringConverter<Customer>() {
                    @Override
                    public String toString(Customer object) {
                        return object.getName();
                    }

                    @Override
                    public Customer fromString(String string) {
                        return cb.getItems().stream().filter(ap -> ap.getName().equals(string)).findFirst().orElse(null);
                    }

                });
            });
            executor.shutdown();
            System.out.println(executor.isShutdown());
        });

        return cb;
    }

    /**
     * Return a HashMap of an individual customer's information.
     *
     * @param id
     * @return
     */
    public Customer getCustomer(String id) {
        Customer customer;
        try {
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENC) + "=" + URLEncoder.encode("get_customer", ENC);
            data += "&" + URLEncoder.encode("ID", ENC) + "=" + URLEncoder.encode(id, ENC);
            System.out.println(data);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject jsonObj = new JSONObject(reader.lines().collect(Collectors.joining()));
            if (!jsonObj.getBoolean("success")) {
                return null;
            }

            System.out.println(jsonObj);
            String prefix = String.valueOf(jsonObj.getString("PREFIX"));
            id = String.valueOf(jsonObj.getString("ID"));
            String company = String.valueOf(jsonObj.get("COMPANY_NAME"));
            String firstName = String.valueOf(jsonObj.get("FIRST_NAME"));
            String lastName = String.valueOf(jsonObj.get("LAST_NAME"));
            String suffix = String.valueOf(jsonObj.get("SUFFIX"));
            String primaryAddress = String.valueOf(jsonObj.get("PRIMARY_ADDRESS"));
            String secondaryAddress = String.valueOf(jsonObj.get("SECONDARY_ADDRESS"));
            String city = String.valueOf(jsonObj.get("CITY"));
            String state = String.valueOf(jsonObj.get("STATE"));
            String postalCode = String.valueOf(jsonObj.get("POSTAL_CODE"));
            String country = String.valueOf(jsonObj.get("COUNTRY"));
            String email = String.valueOf(jsonObj.get("EMAIL_ADDRESS"));
            String phone = String.valueOf(jsonObj.get("PHONE_NUMBER"));
            boolean isBusinessType = Boolean.parseBoolean(jsonObj.getString("BUSINESS_TYPE"));
            boolean isIndividualType = Boolean.parseBoolean(jsonObj.getString("INDIVIDUAL_TYPE"));
            
            customer = new Customer(prefix, id, company, firstName, lastName, suffix, primaryAddress, secondaryAddress, city, state, postalCode, country, email, phone, isBusinessType, isIndividualType);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            customer = new Customer();
        }
        return customer;
    }

    public String saveCustomer(Customer customer) {
        try {
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", customer.getId());
            jsonObject.put("PREFIX", customer.getPrefix());
            jsonObject.put("FIRST_NAME", customer.getFirstName());
            jsonObject.put("LAST_NAME", customer.getLastName());
            jsonObject.put("SUFFIX", customer.getSuffix());
            jsonObject.put("COMPANY_NAME", customer.getCompanyName());
            jsonObject.put("PRIMARY_ADDRESS", customer.getPrimaryAddress());
            jsonObject.put("SECONDARY_ADDRESS", customer.getSecondaryAddress());
            jsonObject.put("CITY", customer.getCity());
            jsonObject.put("STATE", customer.getState());
            jsonObject.put("POSTAL_CODE", customer.getPostalCode());
            jsonObject.put("COUNTRY", customer.getCountry());
            jsonObject.put("EMAIL_ADDRESS", customer.getEmailAddress());
            jsonObject.put("PHONE_NUMBER", customer.getPhoneNumber());
            jsonObject.put("BUSINESS_TYPE", customer.getIsBusinessType());
            jsonObject.put("INDIVIDUAL_TYPE", customer.getIsIndividualType());

            String data = URLEncoder.encode("action", ENC) + "=" + URLEncoder.encode("save_customer", ENC);
            data += "&" + URLEncoder.encode("data", ENC) + "=" + URLEncoder.encode(jsonObject.toString(), ENC);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            
            JSONObject jsonObj = new JSONObject(reader.lines().collect(Collectors.joining()));
            System.out.println(jsonObj);
            if (!jsonObj.getBoolean("success")) {
                System.err.println(jsonObj.getBoolean("success"));
                return null;
            }

            return jsonObj.getString("ID");

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return null;
        }

    }

    /**
     * Get a list of all of the customers.
     *
     * @return
     */
    public ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customer = FXCollections.observableArrayList();

        try {
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("get_customers", "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray jsonArr = new JSONArray(reader.lines().collect(Collectors.joining()));

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                customer.add(new Customer(jsonObj.getString("ID"), jsonObj.getString("NAME")));

            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return customer;
    }

    /**
     * Get the invoice data to display in the user information scene.
     *
     * @param id
     * @return
     */
    public ObservableList<CustomerInvoices> getCustomerInvoices(String id) {
        ObservableList<CustomerInvoices> invoices = FXCollections.observableArrayList();
        try {
            URL url = new URL(URL_STRING);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            String data = URLEncoder.encode("action", ENC) + "=" + URLEncoder.encode("get_customer_invoices", ENC);
            data += "&" + URLEncoder.encode("ID", ENC) + "=" + URLEncoder.encode(id, ENC);
            System.out.println(data);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONArray jsonArr = new JSONArray(reader.lines().collect(Collectors.joining()));
            System.out.println(jsonArr.isNull(0));
            if (!jsonArr.isNull(0)) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObject = jsonArr.getJSONObject(i);
                    invoices.add(new CustomerInvoices(jsonObject.getInt("ID"), jsonObject.getString("INVOICE_TOTAL"), jsonObject.getString("INVOICE_TOTAL"), jsonObject.getString("STATUS"), jsonObject.getString("INVOICE_DATE")));
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return invoices;
    }
}
