/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.accounting.OutstandingTransactionsTableController.OutstandingTransactions;
import com.cbmwebdevelopment.accounting.Payment.SelectedTransactions;
import com.cbmwebdevelopment.customers.Customer;
import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.output.PaymentReceipt;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static java.util.Locale.US;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
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
    JFXButton newPaymentButotn, deletePaymentButton, savePaymentButton, emailPaymentButton, printPaymentButton, exportPaymentButton, findByInvoiceNumberButton;

    @FXML
    JFXTextField invoiceNumberTextField, transactionNumberTextField, referenceNumberTextField, amountReceivedTextField, findInvoiceTextField;

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
    Label selectedAmountDueLabel, selectedAmountAppliedLabel;

    ObservableList<OutstandingTransactions> ot;
    private OutstandingTransactionsTableController outstandingTransactionsTableController;
    public static String invoiceId;
    protected String transactionNumber;
    private String customerId;
    List<String> missingItems;
    private Payment payment;
    public final SimpleDoubleProperty AMOUNT_RECEIVED = new SimpleDoubleProperty();
    private final SimpleDoubleProperty AMOUNT_APPLIED = new SimpleDoubleProperty();
    private final SimpleDoubleProperty BALANCE_DUE = new SimpleDoubleProperty();
    private final SimpleDoubleProperty AMOUNT_REMAINING = new SimpleDoubleProperty();

    @FXML
    protected void newPaymentAction(ActionEvent event) {
        clearInputs();
    }

    @FXML
    protected void deletePaymentAction(ActionEvent event) {
        JFXDialog dialog = Notifications.deleteConfirmationDialog();
        dialog.setOnDialogClosed((evt) -> {
            System.out.println(evt.getEventType());
            System.out.println(evt.getSource());
            System.out.println(evt.getTarget());
        });
    }

    @FXML
    protected void savePaymentAction(ActionEvent event) throws ParseException {
        if (validateFields()) {
            MainApp.loadingDialog.show();
            ObservableList<SelectedTransactions> selectedInvoices = FXCollections.observableArrayList();
            outstandingTransactionsTableView.getSelectionModel().getSelectedItems().forEach(item -> {                
                selectedInvoices.add(new SelectedTransactions(item.getId(), Double.parseDouble(item.getPayment())));
            });
            transactionNumber = transactionNumberTextField.getText().isEmpty() ? "0" : transactionNumberTextField.getText();
            Payment payment = new Payment(customerId, transactionNumber, NumberFormat.getCurrencyInstance(US).parse(amountReceivedTextField.getText()).doubleValue(), paymentDateDatePicker.getValue().toString(), paymentMethodComboBox.getSelectionModel().getSelectedItem(), referenceNumberTextField.getText(), depositToComboBox.getSelectionModel().getSelectedItem(), selectedInvoices, NumberFormat.getCurrencyInstance().parse(selectedAmountDueLabel.getText()).toString(), NumberFormat.getCurrencyInstance().parse(selectedAmountAppliedLabel.getText()).toString());

            Accounting accounting = new Accounting();

            ExecutorService executor = Executors.newCachedThreadPool();
            executor.execute(() -> {
                transactionNumber = accounting.savePayment(payment);
                executor.shutdown();
                Platform.runLater(() -> {
                    if (transactionNumber != null) {
                        Notifications.snackbarNotification("Transaction saved");
                        if (transactionNumberTextField.getText().isEmpty()) {
                            transactionNumberTextField.setText(transactionNumber);
                            disableControls(false);
                        } else {
                            System.out.println("Blah");
                        }
                    }
                    MainApp.loadingDialog.close();
                });
            });
        } else {
            // Notify of missing fields. 
            Notifications.snackbarNotification("Some required fields are missing.");
        }
    }

    @FXML
    protected void emailPaymentAction(ActionEvent event) {
        payment = getPayment();
        PaymentReceipt paymentReceipt = new PaymentReceipt(payment);
        paymentReceipt.emailPdf("cmeehan1991@gmail.com");
    }

    @FXML
    protected void printPaymentAction(ActionEvent event) {

    }

    @FXML
    protected void exportPaymentAction(ActionEvent event) {
        payment = getPayment();
        PaymentReceipt paymentReceipt = new PaymentReceipt(payment);
        paymentReceipt.savePdf();
    }

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

    private Payment getPayment() {
        ObservableList<SelectedTransactions> selectedTransactions = FXCollections.observableArrayList();
        final SimpleDoubleProperty amountDue = new SimpleDoubleProperty();
        final SimpleDoubleProperty amountApplied = new SimpleDoubleProperty();

        outstandingTransactionsTableView.getItems().forEach(item -> {
            if (item.getIsSelected()) {
                selectedTransactions.add(new SelectedTransactions(item.getId(), item.getDescription(), Double.parseDouble(item.getOriginalAmount()), Double.parseDouble(item.getOpenBalance()), Double.parseDouble(item.getPayment())));
                amountDue.set(amountDue.add(Double.parseDouble(item.getOpenBalance())).doubleValue());
                amountApplied.set(amountApplied.add(Double.parseDouble(item.getPayment())).doubleValue());
            }
        });

        paymentDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-HH-mm"));

        return new Payment(customerId, transactionNumberTextField.getText(), Double.parseDouble(amountReceivedTextField.getText()), paymentDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-HH-mm")), paymentMethodComboBox.getSelectionModel().getSelectedItem(), referenceNumberTextField.getText(), depositToComboBox.getSelectionModel().getSelectedItem(), selectedTransactions, String.valueOf(amountDue.get()), String.valueOf(amountApplied.get()));
    }

    private boolean validateFields() {
        missingItems = new ArrayList<>();
        if (paymentMethodComboBox.getSelectionModel().isEmpty()) {
            missingItems.add("Payment Method");
        }

        if (depositToComboBox.getSelectionModel().isEmpty()) {
            missingItems.add("Deposit To");
        }

        if (outstandingTransactionsTableView.getSelectionModel().getSelectedItems() == null) {
            missingItems.add("You must select at least one invoice to apply the payment to.");
        }

        return missingItems.isEmpty();
    }

    private void filterOutstandingTransactions(String filterBy) {
        FilteredList<OutstandingTransactions> filteredList = new FilteredList<>(ot, o -> true);
        filteredList.setPredicate(t -> {
            String transactionId = String.valueOf(t.getId());

            return transactionId.contains(filterBy) || transactionId.equals(filterBy);
        });
    }

    /**
     * Get the customer by the invoice number
     *
     * @param invoiceNumber
     */
    public void getByInvoiceNumber(String invoiceNumber) {
        MainApp.loadingDialog.show();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Customers customers = new Customers();
            Accounting accounting = new Accounting();
            String custId = customers.getCustomerByInvoiceId(invoiceNumber);
            ObservableList<OutstandingTransactions> outstandingTransactions = accounting.getOutstandingTransactions(custId);
            Platform.runLater(() -> {
                invoiceId = invoiceNumber;
                outstandingTransactionsTableView.getItems().setAll(outstandingTransactions);
                findInvoiceTextField.setText(invoiceNumber);
                customerId = custId;
                MainApp.loadingDialog.close();
            });
            executor.shutdown();
        });
    }

    /**
     * Get all outstanding transactions
     *
     * @param customerId
     */
    private void getOutstandingTransactions(String customerId) {
        MainApp.loadingDialog.show();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Accounting accounting = new Accounting();
            ot = accounting.getOutstandingTransactions(customerId);

            ot.forEach((item) -> {
                item.transactionIsSelectedProperty().addListener((obs, ov, nv) -> {
                    System.out.println("Changed: " + item.getOpenBalance());
                });
            });

            executor.shutdown();
            Platform.runLater(() -> {
                outstandingTransactionsTableView.getItems().setAll(ot);
                MainApp.loadingDialog.close();
            });

        });
    }

    /**
     * Initialize all the controls
     */
    private void initControls() {
        outstandingTransactionsTableController = new OutstandingTransactionsTableController();
        outstandingTransactionsTableController.tableController(outstandingTransactionsTableView);
        outstandingTransactionsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outstandingTransactionsTableView.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            BALANCE_DUE.set(0.00);
            AMOUNT_REMAINING.set(0.00);
            outstandingTransactionsTableView.getSelectionModel().getSelectedItems().forEach(item -> {
                try {
                    BALANCE_DUE.set(BALANCE_DUE.add(NumberFormat.getCurrencyInstance().parse(item.getOpenBalance()).doubleValue()).doubleValue());
                    AMOUNT_REMAINING.set(AMOUNT_APPLIED.subtract(BALANCE_DUE).doubleValue());
                    if (BALANCE_DUE.doubleValue() >= AMOUNT_RECEIVED.doubleValue()) {
                        selectedAmountAppliedLabel.setText(NumberFormat.getCurrencyInstance(US).format(AMOUNT_RECEIVED.get()));
                        item.setPayment(String.valueOf(AMOUNT_RECEIVED.get()));
                    } else {
                        selectedAmountAppliedLabel.setText(NumberFormat.getCurrencyInstance(US).format(BALANCE_DUE.get()));
                        item.setPayment(String.valueOf(BALANCE_DUE.get()));
                    }

                    selectedAmountDueLabel.setText(NumberFormat.getCurrencyInstance(US).format(BALANCE_DUE.get()));
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            });
        });

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

        clientNameComboBox.valueProperty().addListener((obs, ov, nv) -> {
            getOutstandingTransactions(nv.getId());
        });

        findInvoiceTextField.textProperty().addListener((obs, ov, nv) -> {
            filterOutstandingTransactions(nv);
        });

        amountReceivedTextField.textProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                // Currency textfield converter
                if (nv.matches("\\d*\\.\\d\\d")) {
                    String newValue = NumberFormat.getCurrencyInstance(US).format(Double.parseDouble(nv));
                    amountReceivedTextField.setText(newValue);
                }
                try {
                    AMOUNT_RECEIVED.set(NumberFormat.getCurrencyInstance(US).parse(nv).doubleValue());
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            } else {
                AMOUNT_RECEIVED.set(0);
            }
        });

        invoiceNumberTextField.setOnAction(evt -> {
            filterOutstandingTransactions(invoiceNumberTextField.getText());
        });
    }

    /**
     * Clear the inputs for a new payment
     */
    private void clearInputs() {
        disableControls(true);
        amountReceivedTextField.clear();
        paymentDateDatePicker.setValue(LocalDate.now());
        paymentMethodComboBox.getSelectionModel().clearSelection();
        referenceNumberTextField.clear();
        depositToComboBox.getSelectionModel().clearSelection();
        transactionNumberTextField.clear();
        findInvoiceTextField.clear();
        outstandingTransactionsTableView.setItems(FXCollections.observableArrayList());

        transactionNumber = null;
        invoiceId = null;
        AMOUNT_RECEIVED.set(0.0);
    }

    /**
     * Disable buttons that are only to be used on existed payments.
     *
     * @param disable
     */
    private void disableControls(boolean disable) {
        deletePaymentButton.setDisable(disable);
        emailPaymentButton.setDisable(disable);
        printPaymentButton.setDisable(disable);
        exportPaymentButton.setDisable(disable);
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
