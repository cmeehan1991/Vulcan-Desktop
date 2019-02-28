/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.accounting.OutstandingTransactionsTableController.OutstandingTransactions;
import com.cbmwebdevelopment.accounting.TransactionsTableController.Transactions;
import com.cbmwebdevelopment.customers.Customer;
import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.main.MainApp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class PaymentsController implements Initializable {

    @FXML
    JFXTextField invoiceNumberTextField, referenceNumberTextField, amountReceivedTextField, findInvoiceTextField;

    @FXML
    JFXDatePicker paymentDateDatePicker;

    @FXML
    JFXComboBox<Customer> clientNameComboBox;

    @FXML
    JFXComboBox<String> paymentMethodComboBox, depositToComboBox;

    @FXML
    TableView<OutstandingTransactions> outstandingTransactionsTableView;

    @FXML
    StackPane invoiceFilterPane;

    @FXML
    JFXButton findByInvoiceNumberButton;

    ObservableList<OutstandingTransactions> ot;
    private OutstandingTransactionsTableController outstandingTransactionsTableController;
    protected static final SimpleDoubleProperty AMOUNT_RECEIVED = new SimpleDoubleProperty();
    public static String invoiceId;

    @FXML
    protected void findByInvoiceNumberAction(ActionEvent event) {
        ObservableList<Node> children = invoiceFilterPane.getChildren();

        if (findByInvoiceNumberButton.getText().equals("Find by Client Name")) {
            clientNameComboBox.setVisible(true);
            clientNameComboBox.toFront();
            invoiceNumberTextField.toBack();
            invoiceNumberTextField.setVisible(false);
            findByInvoiceNumberButton.setText("Find by Invoice No.");
        } else {
            clientNameComboBox.setVisible(false);
            clientNameComboBox.toBack();
            invoiceNumberTextField.toFront();
            invoiceNumberTextField.setVisible(true);
            findByInvoiceNumberButton.setText("Find by Client Name");
        }
    }

    private void filterOutstandingTransactions(String filterBy) {
        FilteredList<OutstandingTransactions> filteredList = new FilteredList<>(ot, o -> true);
        filteredList.setPredicate(t -> {
            String transactionId = String.valueOf(t.getId());

            if (transactionId.contains(filterBy)) {

            }

            return false;
        });
    }

    public void getByInvoiceNumber(String invoiceNumber) {
        MainApp.loadingDialog.show();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Platform.runLater(()->{
                Customers customers = new Customers();
                Accounting accounting = new Accounting();
                
                
            });
            executor.shutdown();
        });
    }

    public void receivePayment(String invoiceId) {

    }

    private void getOutstandingTransactions(String customerId){
        MainApp.loadingDialog.show();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
            Accounting accounting = new Accounting();
            ObservableList<OutstandingTransactions> outstandingTransactions = accounting.getOutstandingTransactions(customerId);
            Platform.runLater(()->{
            
            });
            executor.shutdown();
        });
    }
    
    private void initControls() {
        outstandingTransactionsTableController = new OutstandingTransactionsTableController();
        outstandingTransactionsTableController.tableController(outstandingTransactionsTableView);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            // Set the client name combo box
            Customers customers = new Customers();

            Accounting accounting = new Accounting();
            // Get the available accounts and payment methods
            ObservableList<String> accounts = FXCollections.observableArrayList("PayPal", "Fidelity Checking");
            ObservableList<String> paymentMethods = FXCollections.observableArrayList("PayPal", "Cash", "Check", "Online Payment");
            ObservableList<Customer> custPairs = customers.getCustomers();
            // Get the outstanding payments
            ot = FXCollections.observableArrayList();

            Platform.runLater(() -> {
                clientNameComboBox.getItems().setAll(custPairs);
                clientNameComboBox.setConverter(new StringConverter<Customer>() {
                    @Override
                    public String toString(Customer object) {
                        return object.getName();
                    }

                    @Override
                    public Customer fromString(String string) {
                        return clientNameComboBox.getItems().stream().filter(ap -> ap.getName().equals(string)).findFirst().orElse(null);
                    }
                });
                depositToComboBox.setItems(accounts);
                paymentMethodComboBox.setItems(paymentMethods);
                outstandingTransactionsTableView.setItems(ot);
            });
            executor.shutdown();
        });

        clientNameComboBox.valueProperty().addListener((obs, ov, nv)->{
            getOutstandingTransactions(nv.getId());
        });
        
        findInvoiceTextField.textProperty().addListener((obs, ov, nv) -> {
            filterOutstandingTransactions(nv);
        });

        amountReceivedTextField.textProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                AMOUNT_RECEIVED.set(Double.parseDouble(nv));
            } else {
                AMOUNT_RECEIVED.set(0);
            }
        });

        invoiceNumberTextField.setOnAction(evt -> {
            System.out.println("Get Invoice");
        });
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initControls();
    }

}
