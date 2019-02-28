/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.quote;

import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class QuoteData {
    private final SimpleStringProperty QUOTE_ID, CLIENT_ID, BILLING_ADDRESS, QUOTE_DATE;
    private final ObservableList<InvoiceItems> QUOTE_ITEMS;
   
    public QuoteData(){
        this.QUOTE_ID = new SimpleStringProperty();
        this.CLIENT_ID = new SimpleStringProperty();
        this.BILLING_ADDRESS = new SimpleStringProperty();
        this.QUOTE_DATE = new SimpleStringProperty();
        this.QUOTE_ITEMS = FXCollections.observableArrayList();
    }
    
    public QuoteData(String quoteId, String clientId, String billingAddress, String quoteDate, ObservableList<InvoiceItems> quoteItems){
        this.QUOTE_ID = new SimpleStringProperty(quoteId);
        this.CLIENT_ID = new SimpleStringProperty(clientId);
        this.BILLING_ADDRESS = new SimpleStringProperty(billingAddress);
        this.QUOTE_DATE = new SimpleStringProperty(quoteDate);
        this.QUOTE_ITEMS = quoteItems;
    }
        
    public String getQuoteId(){
        return this.QUOTE_ID.get();
    }
    
    public void setQuoteId(String val){
        this.QUOTE_ID.set(val);
    }
    
    public String getClientId(){
        return this.CLIENT_ID.get();
    }
    
    public void setClientId(String val){
        this.CLIENT_ID.set(val);
    }
    
    public String getBillingAddress(){
        return this.BILLING_ADDRESS.get();
    }
    
    public void setBillingAddress(String val){
        this.BILLING_ADDRESS.set(val);
    }
    
    public String getQuoteDate(){
        return this.QUOTE_DATE.get();
    }
    
    public void setQuoteDate(String val){
        this.QUOTE_DATE.set(val);
    }
    
    public ObservableList<InvoiceItems> getQuoteItems(){
        return this.QUOTE_ITEMS;
    }
    
    public void setQuoteItems(ObservableList<InvoiceItems> val){
        this.QUOTE_ITEMS.setAll(val);
    }
}
