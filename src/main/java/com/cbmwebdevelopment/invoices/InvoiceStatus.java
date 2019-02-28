/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author cmeehan
 */
public class InvoiceStatus {

    private final SimpleIntegerProperty INVOICE_ID;
    private final SimpleDoubleProperty INVOICE_AMOUNT;
    private final SimpleBooleanProperty PAID;

    public InvoiceStatus(int invoiceId, double invoiceAmount, boolean paid) {
        this.INVOICE_ID = new SimpleIntegerProperty(invoiceId);
        this.INVOICE_AMOUNT = new SimpleDoubleProperty(invoiceAmount);
        this.PAID = new SimpleBooleanProperty(paid);
    }

    public void setInvoiceId(int val) {
        this.INVOICE_ID.set(val);
    }

    public Integer getInvoiceId() {
        return this.INVOICE_ID.get();
    }

    public void setInvoiceAmount(double invoiceAmount) {
        this.INVOICE_AMOUNT.set(invoiceAmount);
    }

    public Double getInvoiceAmount() {
        return INVOICE_AMOUNT.get();
    }
    
    public void setPaid(boolean paid){
        this.PAID.set(paid);
    }
    
    public boolean getPaid(){
        return this.PAID.get();
    }
}
