/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class InvoiceData {
    
    private final SimpleIntegerProperty ID;
    private final SimpleStringProperty CUSTOMER_ID, INVOICE_DATE, STATUS, BILLING_ADDRESS, SHIPPING_ADDRESS, MEMO, TAX_RATE;
    private final ObservableList<InvoiceItems> INVOICE_ITEMS;

    public InvoiceData() {
        this.ID = new SimpleIntegerProperty();
        this.CUSTOMER_ID = new SimpleStringProperty();
        this.INVOICE_DATE = new SimpleStringProperty();
        this.STATUS = new SimpleStringProperty();
        this.BILLING_ADDRESS = new SimpleStringProperty();
        this.SHIPPING_ADDRESS = new SimpleStringProperty();
        this.MEMO = new SimpleStringProperty();
        this.TAX_RATE = new SimpleStringProperty();
        this.INVOICE_ITEMS = FXCollections.observableArrayList();
    }

    public InvoiceData(Integer id, String customerId, String invoiceDate, String status, String billingAddress, String shippingAddress, String memo, String taxRate, ObservableList<InvoiceItems> invoiceItems) {
        this.ID = new SimpleIntegerProperty(id);
        this.CUSTOMER_ID = new SimpleStringProperty(customerId);
        this.INVOICE_DATE = new SimpleStringProperty(invoiceDate);
        this.STATUS = new SimpleStringProperty(status);
        this.BILLING_ADDRESS = new SimpleStringProperty(billingAddress);
        this.SHIPPING_ADDRESS = new SimpleStringProperty(shippingAddress);
        this.MEMO = new SimpleStringProperty(memo);
        this.TAX_RATE = new SimpleStringProperty(taxRate);
        this.INVOICE_ITEMS = invoiceItems;
    }

    public Integer getId() {
        return this.ID.get();
    }

    public void setId(Integer val) {
        this.ID.set(val);
    }

    public String getCustomerId() {
        return this.CUSTOMER_ID.get();
    }

    public void setCustomerId(String val) {
        this.CUSTOMER_ID.set(val);
    }

    public String getInvoiceDate() {
        return this.INVOICE_DATE.get();
    }

    public void setInvoiceDate(String val) {
        this.INVOICE_DATE.set(val);
    }

    public String getStatus() {
        return this.STATUS.get();
    }

    public void setStatus(String val) {
        this.STATUS.set(val);
    }

    public String getBillingAddress() {
        return this.BILLING_ADDRESS.get();
    }

    public void setBillingAddress(String val) {
        this.BILLING_ADDRESS.set(val);
    }

    public String getShippingAddress() {
        return this.SHIPPING_ADDRESS.get();
    }

    public void setShippingAddress(String val) {
        this.SHIPPING_ADDRESS.set(val);
    }

    public String getMemo() {
        return this.MEMO.get();
    }

    public void setMemo(String val) {
        this.MEMO.set(val);
    }

    public String getTaxRate() {
        return this.TAX_RATE.get();
    }

    public void setTaxRate(String val) {
        this.TAX_RATE.set(val);
    }

    public ObservableList<InvoiceItems> getInvoiceItems() {
        return this.INVOICE_ITEMS;
    }

    public void setInvoiceItems(ObservableList<InvoiceItems> val) {
        this.INVOICE_ITEMS.setAll(val);
    }

}
