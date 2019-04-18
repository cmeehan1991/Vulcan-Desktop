/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.customers;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.customers.CustomerInvoicesTableController.CustomerInvoices;
import com.jfoenix.controls.JFXComboBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

/**
 *
 * @author cmeehan
 */
public class Customers {

    /**
     * Get the customer ID by the invoice ID Returns a string value of the
     * customer ID or null on error.
     *
     * @param id
     * @return
     */
    public String getCustomerByInvoiceId(String id) {
        String customer = null;
        String sql = "SELECT CUSTOMER_ID FROM INVOICES WHERE INVOICE_ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                customer = rs.getString("CUSTOMER_ID");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return customer;
    }

    /**
     *
     * @param id
     * @return
     */
    public String getCustomerById(String id) {
        String customer = null;
        String sql = "SELECT IF(BUSINESS_TYPE = TRUE, COMPANY_NAME, CONCAT(FIRST_NAME, ' ', LAST_NAME)) AS 'NAME' FROM CUSTOMERS WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                customer = rs.getString("NAME");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return customer;
    }

    /**
     * Get the customer address and format
     *
     * @param id
     * @return
     */
    public String getCustomerAddress(String id) {
        String address = "";
        String sql = "SELECT COMPANY_NAME, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY FROM CUSTOMERS WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (!rs.getString("COMPANY_NAME").isEmpty()) {
                    address += rs.getString("COMPANY_NAME") + "\n";
                }

                address += rs.getString("PRIMARY_ADDRESS") + "\n";

                if (!rs.getString("SECONDARY_ADDRESS").isEmpty()) {
                    address += rs.getString("SECONDARY_ADDRESS") + "\n";
                }

                address += rs.getString("CITY") + "," + rs.getString("STATE") + " " + rs.getString("POSTAL_CODE") + "\n";
                address += rs.getString("COUNTRY");
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return address;
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
        Customer customer = new Customer();
        String sql = "SELECT ID, PREFIX, COMPANY_NAME, FIRST_NAME, LAST_NAME, SUFFIX, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY, EMAIL_ADDRESS, PHONE_NUMBER, BUSINESS_TYPE, INDIVIDUAL_TYPE FROM CUSTOMERS WHERE ID = ?";
        try {
            Connection conn = new DBConnection().connect();

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String prefix = rs.getString("PREFIX");
                id = rs.getString("ID");
                String company = rs.getString("COMPANY_NAME");
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                String suffix = rs.getString("SUFFIX");
                String primaryAddress = rs.getString("PRIMARY_ADDRESS");
                String secondaryAddress = rs.getString("SECONDARY_ADDRESS");
                String city = rs.getString("CITY");
                String state = rs.getString("STATE");
                String postalCode = rs.getString("POSTAL_CODE");
                String country = rs.getString("COUNTRY");
                String email = rs.getString("EMAIL_ADDRESS");
                String phone = rs.getString("PHONE_NUMBER");
                boolean isBusinessType = rs.getBoolean("BUSINESS_TYPE");
                boolean isIndividualType = rs.getBoolean("INDIVIDUAL_TYPE");

                customer = new Customer(prefix, id, company, firstName, lastName, suffix, primaryAddress, secondaryAddress, city, state, postalCode, country, email, phone, isBusinessType, isIndividualType);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return customer;
    }

    public String saveCustomer(Customer customer) {
        String customerId = null;
        String sql = "INSERT INTO CUSTOMERS(ID, PREFIX, FIRST_NAME, LAST_NAME, SUFFIX, COMPANY_NAME, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY, EMAIL_ADDRESS, PHONE_NUMBER, BUSINESS_TYPE, INDIVIDUAL_TYPE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE PREFIX = ?, FIRST_NAME = ?, LAST_NAME = ?, SUFFIX = ?, COMPANY_NAME = ?, PRIMARY_ADDRESS = ?, SECONDARY_ADDRESS = ?, CITY = ?, STATE = ?, POSTAL_CODE = ?, COUNTRY = ?, EMAIL_ADDRESS = ?, PHONE_NUMBER = ?, BUSINESS_TYPE = ?, INDIVIDUAL_TYPE = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, RETURN_GENERATED_KEYS);

            ps.setString(1, customer.getId());
            ps.setString(2, customer.getPrefix());
            ps.setString(3, customer.getFirstName());
            ps.setString(4, customer.getLastName());
            ps.setString(5, customer.getSuffix());
            ps.setString(6, customer.getCompanyName());
            ps.setString(7, customer.getPrimaryAddress());
            ps.setString(8, customer.getSecondaryAddress());
            ps.setString(9, customer.getCity());
            ps.setString(10, customer.getState());
            ps.setString(11, customer.getPostalCode());
            ps.setString(12, customer.getCountry());
            ps.setString(13, customer.getEmailAddress());
            ps.setString(14, customer.getPhoneNumber());
            ps.setBoolean(15, customer.getIsBusinessType());
            ps.setBoolean(16, customer.getIsIndividualType());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (customer.getId().isEmpty() && rs.next()) {
                customerId = String.valueOf(rs.getInt(1));
            } else {
                customerId = customer.getId();
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return customerId;
    }

    /**
     * Get a list of all of the customers.
     *
     * @return
     */
    public ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customer = FXCollections.observableArrayList();
        
        
        String sql = "SELECT ID, IF(BUSINESS_TYPE = TRUE, COMPANY_NAME, CONCAT(FIRST_NAME, ' ', LAST_NAME)) AS 'NAME' FROM CUSTOMERS";
        
        try {
            Connection conn = new DBConnection().connect();

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                
                do {
            
                    customer.add(new Customer(rs.getString("ID"), rs.getString("NAME")));
                
                } while (rs.next());
            }
            
        } catch (SQLException ex) {
            
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
        
        String sql = "SELECT INVOICES.INVOICE_ID AS 'ID', CONCAT('$', ROUND(SUM(INVOICE_ITEMS.QUANTITY * INVOICE_ITEMS.UNIT_PRICE),2)) AS 'INVOICE_TOTAL', INVOICES.STATUS as 'STATUS', DATE_FORMAT(INVOICES.INVOICE_DATE, '%Y-%m-%d') AS 'INVOICE_DATE' FROM INVOICES INNER JOIN INVOICE_ITEMS ON INVOICE_ITEMS.INVOICE_ID = INVOICES.INVOICE_ID WHERE INVOICES.INVOICE_ID = ? GROUP BY INVOICES.INVOICE_ID ORDER BY INVOICES.INVOICE_DATE";
        
        try {
           Connection conn = new DBConnection().connect();
           
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setString(1, id);
           
           ResultSet rs = ps.executeQuery();
           
            if (rs.next()) {
                do{
                    invoices.add(new CustomerInvoices(rs.getInt("ID"), rs.getString("INVOICE_TOTAL"), rs.getString("INVOICE_TOTAL"), rs.getString("STATUS"), rs.getString("INVOICE_DATE")));
                }while(rs.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return invoices;
    }
}
