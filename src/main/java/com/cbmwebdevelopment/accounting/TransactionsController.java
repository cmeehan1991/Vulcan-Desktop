/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.accounting.TransactionsTableController.Transactions;
import com.cbmwebdevelopment.main.MainApp;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class TransactionsController implements Initializable {

    @FXML
    JFXTextField searchTransactionsTextField;

    @FXML
    JFXComboBox filterByComboBox;

    @FXML
    JFXDatePicker fromDatePicker, toDatePicker;

    @FXML
    TableView<Transactions> transactionsTableView;

    private TransactionsTableController transactionsTableController;
    private ObservableList<Transactions> transactions;

    @FXML
    protected void receivePaymentAction(ActionEvent event) {

    }

    @FXML
    protected void payBillAction(ActionEvent event) {

    }

    private void filterTable(String terms) {

        FilteredList<Transactions> filteredList = new FilteredList<>(transactions, p -> true);
        filteredList.setPredicate(transaction -> {
            if (terms == null || terms.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = terms.toLowerCase();

            if (transaction.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (String.valueOf(transaction.getId()).contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });

        SortedList<Transactions> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(transactionsTableView.comparatorProperty());

        transactionsTableView.setItems(sortedData);
    }

    private void populateTransactionsTableView() {
        if (!MainApp.loadingDialog.isVisible()) {
            MainApp.loadingDialog.show();
        }
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Accounting accounting = new Accounting();
            String startDate = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String toDate = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            transactions = accounting.getTransactions(startDate, toDate);
            Platform.runLater(() -> {
                if (transactions != null) {
                    transactionsTableView.getItems().setAll(transactions);
                }
                MainApp.loadingDialog.close();
            });
            executor.shutdown();
        });

    }

    private void initInputs() {
        MainApp.loadingDialog.show();
        // Set the date picker initial values
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());

        // Initialize and popoulate the transactions table
        transactionsTableController = new TransactionsTableController();
        transactionsTableController.tableController(transactionsTableView);
        populateTransactionsTableView();

        // Set the property listeners
        searchTransactionsTextField.textProperty().addListener((obs, ov, nv) -> {
            filterTable(nv);
        });

        fromDatePicker.valueProperty().addListener((obs, ov, nv) -> {
            populateTransactionsTableView();
        });

        toDatePicker.valueProperty().addListener((obs, ov, nv) -> {
            populateTransactionsTableView();
        });

        // Set the tableview click listener
        transactionsTableView.setRowFactory(tv -> {
            TableRow<Transactions> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && (!row.isEmpty())) {
                    Transactions t = row.getItem();

                    if (t.getType().equals("Credit")) {
                        // Open recieve payment screen
                    } else {
                        // Open pay bill screen
                    }
                }
            });
            return row;
        });
        
        if(MainApp.loadingDialog.isVisible()){
            MainApp.loadingDialog.close();
        }

    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initInputs();
    }

}
