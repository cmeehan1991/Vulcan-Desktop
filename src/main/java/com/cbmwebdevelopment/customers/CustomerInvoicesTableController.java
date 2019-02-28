/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.customers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class CustomerInvoicesTableController {
    
    private ObservableList<CustomerInvoices> data = FXCollections.observableArrayList();
    
    public void tableController(TableView tableView){
        TableColumn<CustomerInvoices, Integer> invoiceNumberCell = new TableColumn<>("Invoice No.");
        TableColumn<CustomerInvoices, String> invoiceTotalCell = new TableColumn<>("Total $");
        TableColumn<CustomerInvoices, String> amountDueCell = new TableColumn<>("Amount Due");
        TableColumn<CustomerInvoices, String> statusCell = new TableColumn<>("Status");
        TableColumn<CustomerInvoices, String> dateCell = new TableColumn<>("Date");
        
        invoiceNumberCell.setCellValueFactory(new PropertyValueFactory("invoiceNumber"));
        invoiceTotalCell.setCellValueFactory(new PropertyValueFactory("invoiceTotal"));
        amountDueCell.setCellValueFactory(new PropertyValueFactory("amountDue"));
        statusCell.setCellValueFactory(new PropertyValueFactory("status"));
        dateCell.setCellValueFactory(new PropertyValueFactory("date"));
        
        invoiceNumberCell.setPrefWidth(tableView.getWidth()/5);
        invoiceTotalCell.setPrefWidth(tableView.getWidth()/5);
        amountDueCell.setPrefWidth(tableView.getWidth()/5);
        statusCell.setPrefWidth(tableView.getWidth()/5);
        dateCell.setPrefWidth(tableView.getWidth()/5);
        
        tableView.getColumns().setAll(invoiceNumberCell, invoiceTotalCell, amountDueCell, statusCell, dateCell);
        tableView.setItems(data);
    }
    
    public static class CustomerInvoices{
        private final SimpleIntegerProperty INVOICE_NUMBER;
        private final SimpleStringProperty INVOICE_TOTAL, AMOUNT_DUE, STATUS, DATE;
        
        public CustomerInvoices(Integer invoiceNumber, String invoiceTotal, String amountDue, String status, String date){
            this.INVOICE_NUMBER = new SimpleIntegerProperty(invoiceNumber);
            this.INVOICE_TOTAL = new SimpleStringProperty(invoiceTotal);
            this.AMOUNT_DUE = new SimpleStringProperty(amountDue);
            this.STATUS = new SimpleStringProperty(status);
            this.DATE = new SimpleStringProperty(date);
        }
        
        public void setInvoiceNumber(Integer val){
            this.INVOICE_NUMBER.set(val);
        }
        
        public Integer getInvoiceNumber(){
            return this.INVOICE_NUMBER.getValue();
        }
        
        public void setInvoiceTotal(String val){
            this.INVOICE_TOTAL.set(val);
        }
        
        public String getInvoiceTotal(){
            return this.INVOICE_TOTAL.getValue();
        }
        
        public void setAmountDue(String val){
            this.AMOUNT_DUE.set(val);
        }
        
        public String getAmountDue(){
            return this.AMOUNT_DUE.get();
        }
        
        public void setStatus(String val){
            this.STATUS.set(val);
        }
        
        public String getStatus(){
            return this.STATUS.get();
        }
        
        public void setDate(String val){
            this.DATE.set(val);
        }
        
        public String getDate(){
            return this.DATE.get();
        }
    }
}
