/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Strings {
    public static final String ACCOUNTING_LINK = "http://localhost/MeehanWoodWorking/vulcan/classes/Accounting.php";
    public static final String CUSTOMERS_LINK = "http://localhost/MeehanWoodWorking/vulcan/classes/Customers.php";
    public static final String INVOICES_LINK = "http://localhost/MeehanWoodWorking/vulcan/classes/Invoice.php";
    public static final String QUOTES_LINK = "http://localhost/MeehanWoodWorking/vulcan/classes/Quote.php";
    public static final String USERS_LINK = "http://localhost/MeehanWoodWorking/vulcan/classes/Users.php";
    public static final String ENC = "UTF-8";
    public static final ObservableList<String> STATUS = FXCollections.observableArrayList("Planning", "In Progress", "Cancelled", "Complete", "On Hold");
}
