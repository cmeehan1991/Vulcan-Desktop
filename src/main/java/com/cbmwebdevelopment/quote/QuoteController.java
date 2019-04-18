/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.quote;

import com.cbmwebdevelopment.customers.Customer;
import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.invoices.Invoice;
import com.cbmwebdevelopment.invoices.InvoiceData;
import com.cbmwebdevelopment.invoices.InvoiceFXMLController;
import com.cbmwebdevelopment.invoices.InvoiceTableController;
import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.output.QuotePDF;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class QuoteController implements Initializable {

    private InvoiceTableController quoteTableController = new InvoiceTableController();
    public boolean isNew = true;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-mm-dd");

    @FXML
    JFXComboBox<Customer> customerComboBox;

    @FXML
    JFXDatePicker quoteDateDatePicker;

    @FXML
    public JFXTextField quoteIdTextField;

    @FXML
    JFXButton printQuoteButton, downloadQuoteButton, sendToInvoiceButton;

    @FXML
    JFXTextArea billingAddressTextArea;

    @FXML
    TableView<InvoiceItems> quoteItemTableView;

    List<Node> missingItems;

    String quoteId, customerId, customer, quoteDate, billingAddress;
    ObservableList<InvoiceItems> quoteItems;

    @FXML
    protected void sendToInvoiceAction(ActionEvent event) {
        MainApp.loadingDialog.show();

        customerId = customerComboBox.getSelectionModel().getSelectedItem().getId();
        quoteDate = quoteDateDatePicker.getValue().toString();
        String shippingAddress = billingAddress = billingAddressTextArea.getText();
        quoteItems = quoteItemTableView.getItems();

        InvoiceData invoiceData = new InvoiceData(null, customerId, quoteDate, null, billingAddress, shippingAddress, null, null, quoteItems);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Invoice invoice = new Invoice();
            String invoiceId = String.valueOf(invoice.saveInvoice(invoiceData));
            
            Platform.runLater(() -> {
                if (invoiceId != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InvoiceFXML.fxml"));
                        AnchorPane anchorPane = (AnchorPane) loader.load();
                        InvoiceFXMLController invoiceController = (InvoiceFXMLController) loader.getController();
                        invoiceController.getInvoice(invoiceId);

                        MainApp.mainPane.getChildren().setAll(anchorPane);

                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                } else { // Handle null value
                    Notifications.snackbarNotification("Failed to create invoice");
                }
                MainApp.loadingDialog.close();
            });

            executor.shutdown();
        });
    }

    @FXML
    protected void saveQuoteAction(ActionEvent event) {
        MainApp.loadingDialog.show();

        if (isValid()) {
            System.out.println("Valid: " + isValid());
            ExecutorService executor = Executors.newFixedThreadPool(15);
            executor.submit(() -> {
                customerId = customerComboBox.getSelectionModel().getSelectedItem().getId();
                quoteDate = quoteDateDatePicker.getValue().toString();
                quoteId = quoteIdTextField.getText().isEmpty() ? null : quoteIdTextField.getText();
                billingAddress = billingAddressTextArea.getText().isEmpty() ? "" : billingAddressTextArea.getText();
                ObservableList<InvoiceItems> items = quoteItemTableView.getItems().isEmpty() ? FXCollections.observableArrayList() : quoteItemTableView.getItems();
                quoteId = new Quote().saveQuote(customerId, quoteDate, quoteId, billingAddress, items);
                Platform.runLater(() -> {
                    if (quoteId != null) {
                        isNew = false;
                        setButtonsDisabled(isNew);
                        quoteIdTextField.setText(quoteId);
                        Notifications.snackbarNotification("Quote " + quoteId + " saved.");
                    }
                    MainApp.loadingDialog.close();
                });
                executor.shutdown();
            });
        } else {
            missingItems.forEach(item -> {
                item.getStyleClass().add("missing");
            });
        }
    }

    @FXML
    protected void printQuoteAction(ActionEvent event) {

    }

    @FXML
    protected void downloadQuoteAction(ActionEvent event) {
        QuoteData quoteData = new QuoteData(quoteId, customer, billingAddress, quoteDate, quoteItems);
        QuotePDF quotePdf = new QuotePDF(quoteData);
        quotePdf.savePdf();
    }

    private boolean isValid() {
        missingItems = new ArrayList<>();

        if (customerComboBox.getSelectionModel().getSelectedIndex() < 0) {
            missingItems.add(customerComboBox);
        }

        return missingItems.isEmpty();
    }

    protected void populateCustomers() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Customers customers = new Customers();
            ObservableList<Customer> cust = FXCollections.observableArrayList(customers.getCustomers());
            Platform.runLater(() -> {
                customerComboBox.setItems(cust);
                customerComboBox.setConverter(new StringConverter<Customer>() {

                    @Override
                    public String toString(Customer object) {
                        return object.getName();
                    }

                    @Override
                    public Customer fromString(String string) {
                        return customerComboBox.getItems().stream().filter(ap
                                -> ap.getName().equals(string)).findFirst().orElse(null);
                    }
                });
            });
            executor.shutdown();
        });
    }

    private void getQuote(String quoteId) {
        MainApp.loadingDialog.show();

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Quote quote = new Quote();
            QuoteData quoteData = quote.getQuoteData(quoteId);

            // Set quote objects
            this.quoteId = quoteData.getQuoteId();
            this.customer = quoteData.getClientId();
            this.quoteDate = quoteData.getQuoteDate();
            this.billingAddress = quoteData.getBillingAddress();
            this.quoteItems = quoteData.getQuoteItems();

            Platform.runLater(() -> {

                if (quoteData.getQuoteId() != null && !quoteData.getQuoteId().isEmpty()) {

                    // Set UI
                    quoteIdTextField.setText(quoteData.getQuoteId());
                    for (Customer c : customerComboBox.getItems()) {
                        if (c.getId().equals(customer)) {
                            customerComboBox.getSelectionModel().select(c);
                            break;
                        }
                    }
                    quoteDateDatePicker.setValue(LocalDate.parse(quoteDate));
                    billingAddressTextArea.setText(billingAddress);
                    System.out.println(billingAddress);
                    if (!quoteItems.isEmpty()) {
                        System.out.println("Not empty");
                        quoteItemTableView.getItems().setAll(quoteItems);
                    } else {
                        System.out.println("Empty");
                        quoteItemTableView.getItems().setAll(FXCollections.observableArrayList(new InvoiceItems(1, "", "", 0.0, "$0.00", "$0.00")));
                    }

                    isNew = false;
                    setButtonsDisabled(isNew);

                } else {
                    Notifications.snackbarNotification("Quote does not exist.");
                }
                MainApp.loadingDialog.close();
            });
            executor.shutdown();
        });
    }

    private void setButtonsDisabled(boolean activate) {
        printQuoteButton.setDisable(activate);
        downloadQuoteButton.setDisable(activate);
        sendToInvoiceButton.setDisable(activate);
    }

    private void setCustomerInfo() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            System.out.println("Setting customer address");
            String customerAddress = new Customers().getCustomerAddress(customerComboBox.getSelectionModel().getSelectedItem().getId());
            System.out.println("Customer Address: " + customerAddress);
            Platform.runLater(() -> {
                billingAddressTextArea.setText(customerAddress);
            });
            executor.shutdown();
        });
    }

    private void initInputs() {
        MainApp.loadingDialog.show();
        quoteTableController.tableController(quoteItemTableView);
        quoteItemTableView.setEditable(true);
        quoteDateDatePicker.setValue(LocalDate.now());
        populateCustomers();
        setButtonsDisabled(true);

        customerComboBox.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            System.out.println(nv.getId());
            setCustomerInfo();
        });

        quoteIdTextField.setOnAction(evt -> {
            getQuote(quoteIdTextField.getText());
        });

        MainApp.loadingDialog.close();
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initInputs();
    }

}
