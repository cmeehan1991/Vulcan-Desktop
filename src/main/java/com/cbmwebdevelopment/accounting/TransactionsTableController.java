/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class TransactionsTableController {

    public void tableController(TableView tableView){
        TableColumn<Integer, Transactions> idColumn = new TableColumn<>("ID");
        TableColumn<Integer, Transactions> descriptionColumn = new TableColumn<>("DESCRIPTION");
        TableColumn<Integer, Transactions> typeColumn = new TableColumn<>("TYPE");
        TableColumn<Integer, Transactions> dateColumn = new TableColumn<>("DATE");
        TableColumn<Integer, Transactions> amountColumn = new TableColumn<>("AMOUNT");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        idColumn.prefWidthProperty().bind(tableView.prefWidthProperty().multiply(.15));
        descriptionColumn.prefWidthProperty().bind(tableView.prefWidthProperty().multiply(.4));
        typeColumn.prefWidthProperty().bind(tableView.prefWidthProperty().multiply(.15));
        dateColumn.prefWidthProperty().bind(tableView.prefWidthProperty().multiply(.15));
        amountColumn.prefWidthProperty().bind(tableView.prefWidthProperty().multiply(.15));
        
        
        tableView.getColumns().setAll(idColumn, descriptionColumn, typeColumn, dateColumn, amountColumn);
    }
    
    public static class Transactions {
        private final SimpleIntegerProperty ID;
        private final SimpleStringProperty DESCRIPTION, TYPE, DATE, AMOUNT;
        
        public Transactions(Integer id, String description, String type, String date, String amount){
            this.ID = new SimpleIntegerProperty(id);
            this.DESCRIPTION = new SimpleStringProperty(description);
            this.TYPE = new SimpleStringProperty(type);
            this.DATE = new SimpleStringProperty(date);
            this.AMOUNT = new SimpleStringProperty(amount);
        }
        
        public void setId(Integer id){
            this.ID.set(id);
        }
        
        public Integer getId(){
            return this.ID.get();
        }
        
        public void setDescription(String val){
            this.DESCRIPTION.set(val);
        }
        
        public String getDescription(){
            return this.DESCRIPTION.get();
        }
        
        public void setType(String val){
            this.TYPE.set(val);
        }
        
        public String getType(){
            return this.TYPE.get();
        }
        
        public void setDate(String val){
            this.DATE.set(val);
        }
        
        public String getDate(){
            return this.DATE.get();
        }
        
        public void setAmount(String val){
            this.AMOUNT.set(val);
        }
        
        public String getAmount(){
            return this.AMOUNT.get();
        }
    }
}
