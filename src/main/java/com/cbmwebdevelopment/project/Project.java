/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.project;

import com.cbmwebdevelopment.customers.Customer;
import com.cbmwebdevelopment.project.ProjectIncomeExpensesTableController.ProjectIncomeExpenses;
import com.cbmwebdevelopment.project.ProjectMaterialsTableController.ProjectMaterials;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Project {
    private final SimpleStringProperty PROJECT_ID, STATUS, TITLE, CUSTOMER, START_DATE, DEADLINE, DESCRIPTION, TYPE, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE;
    private final ObservableList<ProjectIncomeExpenses> INCOME_EXPENSES;
    private final ObservableList<ProjectMaterials> MATERIALS;
    
    public Project(){
        this.PROJECT_ID = new SimpleStringProperty();
        this.STATUS = new SimpleStringProperty();
        this.TITLE = new SimpleStringProperty();
        this.CUSTOMER = new SimpleStringProperty();
        this.START_DATE = new SimpleStringProperty();
        this.DEADLINE = new SimpleStringProperty();
        this.DESCRIPTION = new SimpleStringProperty();
        this.TYPE = new SimpleStringProperty();
        this.PRIMARY_ADDRESS = new SimpleStringProperty();
        this.SECONDARY_ADDRESS = new SimpleStringProperty();
        this.CITY = new SimpleStringProperty();
        this.STATE = new SimpleStringProperty();
        this.POSTAL_CODE = new SimpleStringProperty();
        this.INCOME_EXPENSES = FXCollections.observableArrayList();
        this.MATERIALS = FXCollections.observableArrayList();
    }
    
    public Project(String projectId, String status, String title, String customerId, String startDate, String deadline, String description, String type, String primaryAddress, String secondaryAddress, String city, String state, String postalCode, ObservableList<ProjectIncomeExpenses> incomeExpenses, ObservableList<ProjectMaterials> materials){
        this.PROJECT_ID = new SimpleStringProperty(projectId);
        this.STATUS = new SimpleStringProperty(status);
        this.TITLE = new SimpleStringProperty(title);
        this.CUSTOMER = new SimpleStringProperty(customerId);
        this.START_DATE = new SimpleStringProperty(startDate);
        this.DEADLINE = new SimpleStringProperty(deadline);
        this.DESCRIPTION = new SimpleStringProperty(description);
        this.TYPE = new SimpleStringProperty(type);
        this.PRIMARY_ADDRESS = new SimpleStringProperty(primaryAddress);
        this.SECONDARY_ADDRESS = new SimpleStringProperty(secondaryAddress);
        this.CITY = new SimpleStringProperty(city);
        this.STATE = new SimpleStringProperty(state);
        this.POSTAL_CODE = new SimpleStringProperty(postalCode);
        this.INCOME_EXPENSES = incomeExpenses;
        this.MATERIALS = materials;
    }
    
    public void setProjectId(String val){
        this.PROJECT_ID.set(val);
    }
    
    public String getProjectId(){
        return this.PROJECT_ID.get();
    }
    
    public void setStatus(String val){
        this.STATUS.set(val);
    }
    
    public String gettatus(){
        return this.STATUS.get();
    }
    
    public void setTitle(String val){
        this.TITLE.set(val);
    }
    
    public String getTitle(){
        return this.TITLE.get();
    }
    
    public void setCustomer(String val){
        this.CUSTOMER.set(val);
    }
    
    public String getCustomer(){
        return this.CUSTOMER.get();
    }
    
    public void setStartDate(String val){
        this.START_DATE.set(val);
    }
    
    public String getStartDate(){
        return this.START_DATE.get();
    }
    
    public void setDeadline(String val){
        this.DEADLINE.set(val);
    }
    
    public String getDeadline(){
        return this.DEADLINE.get();
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
    
    public void setPrimaryAddress(String val){
        this.PRIMARY_ADDRESS.set(val);
    }
    
    public String getPrimaryAddress(){
        return this.PRIMARY_ADDRESS.get();
    }
    
    public void setSecondaryAddress(String val){
        this.SECONDARY_ADDRESS.set(val);
    }
    
    public String getSecondaryAddress(){
        return this.SECONDARY_ADDRESS.get();
    }
    
    public void setCity(String val){
        this.CITY.set(val);
    }
    
    public String getCity(){
        return this.CITY.get();
    }
    
    public void setState(String val){
        this.STATE.set(val);
    }
    
    public String getState(){
        return this.STATE.get();
    }
    
    public void setPostalCode(String val){
        this.POSTAL_CODE.set(val);
    }
    
    public String getPostalCode(){
        return this.POSTAL_CODE.get();
    }
    
    public void setIncomeExpenses(ObservableList<ProjectIncomeExpenses> val){
        this.INCOME_EXPENSES.setAll(val);
    }
    
    public ObservableList<ProjectIncomeExpenses> getIncomeExpenses(){
        return this.INCOME_EXPENSES;
    }
    
    public void setMaterials(ObservableList<ProjectMaterials> val){
        this.MATERIALS.setAll(val);
    }
    
    public ObservableList<ProjectMaterials> getMaterials(){
        return this.MATERIALS;
    }
}
