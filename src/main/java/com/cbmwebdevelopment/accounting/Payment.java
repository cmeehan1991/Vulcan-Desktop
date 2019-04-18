/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Payment {

    private final SimpleStringProperty CUSTOMER_ID, TRANSACTION_NUMBER, PAYMENT_DATE, PAYMENT_METHOD, REFERENCE_NUMBER, DEPOSIT_TO, AMOUNT_DUE, AMOUNT_APPLIED;
    private final SimpleDoubleProperty AMOUNT_RECEIVED;
    private final ObservableList<SelectedTransactions> SELECTED_TRANSACTIONS;

    public Payment(String customerId, String transactionNumber, Double amountReceived, String paymentDate, String paymentMethod, String referenceNumber, String depositTo, ObservableList<SelectedTransactions> selectedTransactions, String amountDue, String amountApplied) {
        this.CUSTOMER_ID = new SimpleStringProperty(customerId);
        this.TRANSACTION_NUMBER = new SimpleStringProperty(transactionNumber);
        this.PAYMENT_DATE = new SimpleStringProperty(paymentDate);
        this.PAYMENT_METHOD = new SimpleStringProperty(paymentMethod);
        this.REFERENCE_NUMBER = new SimpleStringProperty(referenceNumber);
        this.DEPOSIT_TO = new SimpleStringProperty(depositTo);
        this.AMOUNT_DUE = new SimpleStringProperty(amountDue);
        this.AMOUNT_APPLIED = new SimpleStringProperty(amountApplied);
        this.AMOUNT_RECEIVED = new SimpleDoubleProperty(amountReceived);
        this.SELECTED_TRANSACTIONS = selectedTransactions;
    }

    public void setCustomerId(String val) {
        this.CUSTOMER_ID.set(val);
    }

    public String getCustomerId() {
        return this.CUSTOMER_ID.get();
    }

    public void setTransactionNumber(String val) {
        this.TRANSACTION_NUMBER.set(val);
    }

    public String getTransactionNumber() {
        return this.TRANSACTION_NUMBER.get();
    }

    public void setPaymentDate(String val) {
        this.PAYMENT_DATE.set(val);
    }

    public String getPaymentDate() {
        return this.PAYMENT_DATE.get();
    }

    public void setPaymentMethod(String val) {
        this.PAYMENT_METHOD.set(val);
    }

    public String getPaymentMethod() {
        return this.PAYMENT_METHOD.get();
    }

    public void setReferenceNumber(String val) {
        this.REFERENCE_NUMBER.set(val);
    }

    public String getReferenceNumber() {
        return this.REFERENCE_NUMBER.get();
    }

    public void setDepositTo(String val) {
        this.DEPOSIT_TO.set(val);
    }

    public String getDepositTo() {
        return this.DEPOSIT_TO.get();
    }

    public void setAmountdue(String val) {
        this.AMOUNT_DUE.set(val);
    }

    public String getAmountDue() {
        return this.AMOUNT_DUE.get();
    }

    public void setAmountApplied(String val) {
        this.AMOUNT_APPLIED.set(val);
    }

    public String getAmountApplied() {
        return this.AMOUNT_APPLIED.get();
    }

    public void setAmountReceived(Double val) {
        this.AMOUNT_RECEIVED.set(val);
    }

    public Double getAmountReceived() {
        return this.AMOUNT_RECEIVED.get();
    }

    public void setSelectedTransactions(ObservableList<SelectedTransactions> val) {
        this.SELECTED_TRANSACTIONS.setAll(val);
    }

    public ObservableList<SelectedTransactions> getSelectedTransaction() {
        return this.SELECTED_TRANSACTIONS;
    }

    public static class SelectedTransactions {

        private final SimpleIntegerProperty TRANSACTION_ID;
        private final SimpleStringProperty DESCRIPTION;
        private final SimpleDoubleProperty ORIGINAL_AMOUNT, OPEN_BALANCE, PAYMENT_AMOUNT;

        public SelectedTransactions(Integer transactionId, Double paymentAmount) {
            this.TRANSACTION_ID = new SimpleIntegerProperty(transactionId);
            this.DESCRIPTION = new SimpleStringProperty();
            this.ORIGINAL_AMOUNT = new SimpleDoubleProperty();
            this.OPEN_BALANCE = new SimpleDoubleProperty();
            this.PAYMENT_AMOUNT = new SimpleDoubleProperty(paymentAmount);
        }
        
        public SelectedTransactions(Integer transactionId, String description, Double originalAmount, Double openBalance, Double paymentAmount){
            this.TRANSACTION_ID = new SimpleIntegerProperty(transactionId);
            this.DESCRIPTION = new SimpleStringProperty(description);
            this.ORIGINAL_AMOUNT = new SimpleDoubleProperty(originalAmount);
            this.OPEN_BALANCE = new SimpleDoubleProperty(openBalance);   
            this.PAYMENT_AMOUNT = new SimpleDoubleProperty(paymentAmount);         
        }
        
        public void setTransactionId(Integer val){
            this.TRANSACTION_ID.set(val);
        }
        
        public Integer getTransactionId(){
            return this.TRANSACTION_ID.get();
        }
        
        public void setDescription(String val){
            this.DESCRIPTION.set(val);
        }
        
        public String getDescription(){
            return this.DESCRIPTION.get();
        }
        
        public void setOriginalAmount(Double val){
            this.ORIGINAL_AMOUNT.set(val);
        }
        
        public Double getOriginalAmount(){
            return this.ORIGINAL_AMOUNT.get();
        }
        
        public void setOpenBalance(Double val){
            this.OPEN_BALANCE.set(val);
        }
        
        public Double getOpenBalance(){
            return this.OPEN_BALANCE.get();
        }
        
        public void setPaymentAmount(Double val){
            this.PAYMENT_AMOUNT.set(val);
        }
        
        public Double getPaymentAmount(){
            return this.PAYMENT_AMOUNT.get();
        }
    }
}
