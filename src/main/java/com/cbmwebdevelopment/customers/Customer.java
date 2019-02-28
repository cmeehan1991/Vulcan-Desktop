package com.cbmwebdevelopment.customers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cmeehan
 */
public class Customer {

    private SimpleStringProperty PREFIX, ID, NAME, COMPANY, FIRST_NAME, LAST_NAME, SUFFIX, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY, EMAIL_ADDRESS, PHONE_NUMBER;
    
    private SimpleBooleanProperty BUSINESS_TYPE, INDIVIDUAL_TYPE;

    /**
     * 
     */
    public Customer(){
    }    
    /**
     * 
     * @param id
     * @param name 
     */
    public Customer(String id, String name) {
        this.ID = new SimpleStringProperty(id);
        this.NAME = new SimpleStringProperty(name);
    }
    /**
     * 
     * @param prefix
     * @param id
     * @param company
     * @param firstName
     * @param lastName
     * @param suffix
     * @param primaryAddress
     * @param secondaryAddress
     * @param city
     * @param state
     * @param postalCode
     * @param country
     * @param emailAddress
     * @param phoneNumber 
     * @param businessType 
     * @param individualType 
     */
    public Customer(String prefix, String id, String company, String firstName, String lastName, String suffix, String primaryAddress, String secondaryAddress, String city, String state, String postalCode, String country, String emailAddress, String phoneNumber, boolean businessType, boolean individualType){
        this.PREFIX = new SimpleStringProperty(prefix);
        this.ID = new SimpleStringProperty(id);
        this.COMPANY = new SimpleStringProperty(company);
        this.FIRST_NAME = new SimpleStringProperty(firstName);
        this.LAST_NAME = new SimpleStringProperty(lastName);
        this.SUFFIX = new SimpleStringProperty(suffix);
        this.PRIMARY_ADDRESS = new SimpleStringProperty(primaryAddress);
        this.SECONDARY_ADDRESS = new SimpleStringProperty(secondaryAddress);
        this.CITY = new SimpleStringProperty(city);
        this.STATE = new SimpleStringProperty(state);
        this.POSTAL_CODE = new SimpleStringProperty(postalCode);
        this.COUNTRY = new SimpleStringProperty(country);
        this.EMAIL_ADDRESS = new SimpleStringProperty(emailAddress);
        this.PHONE_NUMBER = new SimpleStringProperty(phoneNumber);
        this.BUSINESS_TYPE = new SimpleBooleanProperty(businessType);
        this.INDIVIDUAL_TYPE = new SimpleBooleanProperty(individualType);
    }




    public String getId() {
        return this.ID.get();
    }

    public void setId(String val) {
        this.ID.set(val);
    }
    
    public String getCompanyName(){
        return this.COMPANY.get();
    }
    
    public void setCompanyName(String val){
        this.COMPANY.set(val);
    }
    
    public String getPrefix(){
        return this.PREFIX.get();
    }
    
    public void setPrefix(String val){
        this.PREFIX.set(val);
    }
    
    public String getName() {
        return this.NAME.get();
    }

    public void setName(String val) {
        this.NAME.set(val);
    }
    
    public String getFirstName(){
        return this.FIRST_NAME.get();
    }
    
    public void setFirstName(String val){
        this.FIRST_NAME.set(val);
    }
    
    public String getLastName(){
        return this.LAST_NAME.get();
    }
    
    public void setLastName(String val){
        this.LAST_NAME.set(val);
    }
    
    public String getSuffix(){
        return this.SUFFIX.get();
    }
    
    public void setSuffix(String val){
        this.SUFFIX.set(val);
    }
    
    public String getPrimaryAddress(){
        return this.PRIMARY_ADDRESS.get();
    }
    
    public void setPrimaryAddress(String val){
        this.PRIMARY_ADDRESS.set(val);
    }
    
    public String getSecondaryAddress(){
        return this.SECONDARY_ADDRESS.get();
    }
    
    public void setSecondaryAddress(String val){
        this.SECONDARY_ADDRESS.set(val);
    }
    
    public String getCity(){
        return this.CITY.get();
    }
    
    public void setCity(String val){
        this.CITY.set(val);
    }
    
    public String getState(){
        return this.STATE.get();
    }
    
    public void setState(String val){
        this.STATE.set(val);
    }
    
    public String getPostalCode(){
        return this.POSTAL_CODE.get();
    }
    
    public void setPostalCode(String val){
        this.POSTAL_CODE.set(val);
    }
    
    public String getCountry(){
        return this.COUNTRY.get();
    }
    
    public void setCountry(String val){
        this.COUNTRY.set(val);
    }
    
    public String getEmailAddress(){
        return this.EMAIL_ADDRESS.get();
    }
    
    public void setEmailAddress(String val){
        this.EMAIL_ADDRESS.set(val);
    } 
    
    public String getPhoneNumber(){
        return this.PHONE_NUMBER.get();
    }
    
    public void setPhoneNumber(String val){
        this.PHONE_NUMBER.set(val);
    }
    
    public boolean getIsBusinessType(){
        return this.BUSINESS_TYPE.get();
    }
    
    public void setIsBusinessType(boolean val){
        this.BUSINESS_TYPE.set(val);
    }
    
    public boolean getIsIndividualType(){
        return this.INDIVIDUAL_TYPE.get();
    }
    
    public void setIsIndividualType(boolean val){
        this.INDIVIDUAL_TYPE.set(val);
    }
}
