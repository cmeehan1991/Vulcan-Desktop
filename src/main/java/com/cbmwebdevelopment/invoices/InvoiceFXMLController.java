/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import com.cbmwebdevelopment.accounting.PaymentsController;
import com.cbmwebdevelopment.customers.Customer;
import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.MainFXMLController;
import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.output.InvoicePDF;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
public class InvoiceFXMLController implements Initializable {

    String invoiceId;

    @FXML
    JFXComboBox<Customer> customerComboBox;

    @FXML
    JFXDatePicker invoiceDatePicker;

    @FXML
    JFXTextArea billingAddressTextArea, shippingAddressTextArea, memoTextArea;

    @FXML
    TableView<InvoiceItems> invoiceItemsTableView;

    @FXML
    JFXComboBox<String> taxComboBox;

    @FXML
    JFXTextField invoiceIdTextField, subtotalTextField, totalTextField, paymentsAppliedTextField, balanceDueTextField;

    protected boolean isNew = true;
    private HashMap<String, Node> missingItems;
    private InvoiceTableController invoiceTableController;
    private String id, customerId, invoiceDate, status, billingAddress, shippingAddress, memo, taxRate;
    double subTotal = 0.00, total;
    private ObservableList<InvoiceItems> invoiceItems;
    private InvoiceData invoiceData;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ObservableList<String> TAX_RATES = FXCollections.observableArrayList("No Tax", "6.75%", "6.00%");

    @FXML
    protected void newInvoiceAction(ActionEvent event) {
        clearFields();
        isNew = true;
    }

    @FXML
    protected void saveInvoiceAction(ActionEvent event) {
        if (validateFields()) {
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(() -> {
                id = invoiceIdTextField.getText();
                customerId = customerComboBox.getSelectionModel().getSelectedItem().getId();
                invoiceDate = invoiceDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                billingAddress = billingAddressTextArea.getText();
                shippingAddress = shippingAddressTextArea.getText();
                invoiceItems = invoiceItemsTableView.getItems();
                taxRate = taxComboBox.getSelectionModel().getSelectedItem();
                memo = memoTextArea.getText();

                invoiceData = new InvoiceData(id, customerId, invoiceDate, status, billingAddress, shippingAddress, memo, taxRate, invoiceItems);
                invoiceId = new Invoice().saveInvoice(invoiceData);

                Platform.runLater(() -> {
                    if (invoiceId.matches("-?\\d+(\\.\\d_)?")) {
                        invoiceIdTextField.setText(invoiceId);
                        isNew = false;
                        notifyUpdate("Invoice Saved");
                    } else {
                        System.err.println(invoiceId);
                    }
                });
            });

        } else {
            new Notifications().missingFieldsNotification();
        }
    }

    @FXML
    protected void deleteInvoiceAction(ActionEvent event) {

    }

    @FXML
    protected void exportInvoiceAction(ActionEvent event) {
        invoiceData = setVariables();
        InvoicePDF invoicePDF = new InvoicePDF(invoiceData);

        invoicePDF.savePdf();
    }

    @FXML
    protected void receivePaymentAction(ActionEvent event) {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentsFXML.fxml"));
            AnchorPane paymentsPane = (AnchorPane) loader.load();

            PaymentsController paymentsController = (PaymentsController) loader.getController();
            MainFXMLController.setPane(paymentsPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void issueRefundAction(ActionEvent event) {

    }

    @FXML
    protected void calculateTotalAction(ActionEvent event) {
        taxRate = taxComboBox.getSelectionModel().getSelectedItem() == null ? "No Tax" : taxComboBox.getSelectionModel().getSelectedItem();
        totalTextField.setText(calculateTotal(NumberFormat.getCurrencyInstance().format(subTotal), taxRate));
    }

    private InvoiceData setVariables() {
        id = invoiceIdTextField.getText();
        customerId = customerComboBox.getSelectionModel().getSelectedItem().getId();
        invoiceDate = invoiceDatePicker.getValue().format(FORMATTER);
        billingAddress = billingAddressTextArea.getText();
        shippingAddress = shippingAddressTextArea.getText();
        invoiceItems = invoiceItemsTableView.getItems();
        memo = memoTextArea.getText();
        taxRate = taxComboBox.getSelectionModel().getSelectedItem();
        invoiceItems = invoiceItemsTableView.getItems();
        invoiceData = new InvoiceData(id, customerId, invoiceDate, null, billingAddress, shippingAddress, memo, taxRate, invoiceItems);
        return invoiceData;
    }

    private void notifyUpdate(String text) {
        JFXSnackbar snackbar = new JFXSnackbar(MainApp.parentPane);
        snackbar.enqueue(new SnackbarEvent(text));
    }

    /**
     * Clear all of the fields in preparation for future events
     */
    private void clearFields() {
        customerComboBox.getSelectionModel().clearSelection();
        invoiceDatePicker.setValue(LocalDate.now());
        invoiceIdTextField.clear();
        billingAddressTextArea.clear();
        shippingAddressTextArea.clear();
        invoiceItemsTableView.getItems().clear();
        memoTextArea.clear();
        subtotalTextField.clear();
        taxComboBox.getSelectionModel().clearSelection();
        totalTextField.clear();
        paymentsAppliedTextField.clear();
        balanceDueTextField.clear();
    }

    private boolean validateFields() {
        if (customerComboBox.getSelectionModel().getSelectedItem().getId() == null) {
            missingItems.put("Customer", customerComboBox);
        }

        return missingItems.isEmpty();
    }

    public void getInvoice(String invoiceId) {
        MainApp.loadingDialog.show();

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Invoice invoice = new Invoice();
            InvoiceData data = invoice.getInvoice(invoiceId);
            Platform.runLater(() -> {
                id = data.getId();
                customerId = data.getCustomerId();
                DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dt = LocalDate.parse(data.getInvoiceDate(), FORMATTER);
                invoiceDate = FORMATTER.parse(data.getInvoiceDate()).toString();
                billingAddress = data.getBillingAddress();
                shippingAddress = data.getShippingAddress();
                memo = data.getMemo();
                taxRate = data.getTaxRate();
                invoiceItems = data.getInvoiceItems();

                invoiceIdTextField.setText(id);
                customerComboBox.getItems().stream().filter((c) -> (c.getId().equals(customerId))).forEachOrdered((c) -> {
                    customerComboBox.getSelectionModel().select(c);
                });
                invoiceDatePicker.setValue(dt);
                billingAddressTextArea.setText(billingAddress);
                shippingAddressTextArea.setText(shippingAddress);
                memoTextArea.setText(memo);
                taxComboBox.getSelectionModel().select(taxRate);
                invoiceItemsTableView.getItems().setAll(invoiceItems);
                invoiceItems.forEach(item -> {
                    try {
                        subTotal += NumberFormat.getCurrencyInstance().parse(item.getTotal()).doubleValue();
                    } catch (ParseException ex) {
                        System.err.println(ex.getMessage());
                    }
                });
                subtotalTextField.setText(NumberFormat.getCurrencyInstance().format(subTotal));
                taxRate = taxRate == null ? "No Tax" : taxRate;
                totalTextField.setText(calculateTotal(NumberFormat.getCurrencyInstance().format(subTotal), taxRate));
                setInputsForUpdate(true);
                MainApp.loadingDialog.close();
            });
            executor.shutdown();
        });
    }

    /**
     * Calculate the total invoice
     *
     * @param subTotal
     * @param tax
     * @return
     */
    private String calculateTotal(String subTotal, String tax) {
        try {
            double st = NumberFormat.getCurrencyInstance().parse(subTotal).doubleValue();
            total = st;
            if (!tax.equals("No Tax")) {
                double tx = Double.parseDouble(tax.replace("%", "")) / 100;

                total = (tx * st) + st;
            }
            return NumberFormat.getCurrencyInstance().format(total);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            return subTotal;
        }

    }

    private void getCustomers() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Customers customers = new Customers();
            ObservableList<Customer> data = FXCollections.observableArrayList(customers.getCustomers());
            Platform.runLater(() -> {
                customerComboBox.setItems(data);
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

    /**
     * Enable/disable the inputs based on the invoice status of new/update.
     *
     * @param disable
     */
    public void setInputsForUpdate(boolean disable) {
        if (disable) {
            invoiceIdTextField.requestFocus();
        }

        customerComboBox.setDisable(disable);
        invoiceDatePicker.setDisable(disable);
        billingAddressTextArea.setDisable(disable);
        shippingAddressTextArea.setDisable(disable);
        invoiceItemsTableView.setDisable(disable);
        memoTextArea.setDisable(disable);
        subtotalTextField.setDisable(disable);
        taxComboBox.setDisable(disable);
        totalTextField.setDisable(disable);
//        paymentsAppliedTextField.setDisable(disable);
        balanceDueTextField.setDisable(disable);
    }

    private void initInputs() {
        MainApp.loadingDialog.show();
        getCustomers();
        taxComboBox.setItems(TAX_RATES);
        invoiceDatePicker.setValue(LocalDate.now());
        invoiceTableController = new InvoiceTableController();
        invoiceTableController.tableController(invoiceItemsTableView);
        invoiceItemsTableView.setEditable(true);

        invoiceItems = invoiceItemsTableView.getItems();
        invoiceItems.addListener((ListChangeListener.Change<? extends InvoiceItems> c) -> {
            total = 0;
            subTotal = 0;
            c.getList().forEach(item -> {
                try {
                    subTotal += NumberFormat.getCurrencyInstance().parse(item.getTotal()).doubleValue();
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            });
            taxRate = taxComboBox.getSelectionModel().getSelectedItem();

            if (taxRate != null && !taxRate.equals("No Tax") && !taxRate.isEmpty()) {
                double tx = Double.parseDouble(taxRate.replace("%", "")) / 100;
                total = (tx * subTotal) + subTotal;
            }
            subtotalTextField.setText(NumberFormat.getCurrencyInstance(Locale.US).format(subTotal));
            totalTextField.setText(NumberFormat.getCurrencyInstance(Locale.US).format(total));
        });

        invoiceIdTextField.setOnAction(evt -> {
            getInvoice(invoiceIdTextField.getText());
        });

        taxComboBox.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (!nv.equals("No Tax") && subTotal != 0) {
                double tax = Double.parseDouble(taxRate.replace("$=%", "")) / 100;
                total = (subTotal * tax) + total;
            } else {
                total = subTotal;
            }

            totalTextField.setText(NumberFormat.getCurrencyInstance(Locale.US).format(total));

        });

        MainApp.loadingDialog.close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initInputs();
    }

}
