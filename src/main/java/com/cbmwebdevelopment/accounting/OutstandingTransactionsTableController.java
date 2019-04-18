/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.tablecellfactories.CheckBoxCellFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

/**
 *
 * @author cmeehan
 */
public class OutstandingTransactionsTableController {

    protected ObservableList<OutstandingTransactions> data = FXCollections.observableArrayList();
    private Double remainingBalance;
    private final ObservableList<OutstandingTransactions> testData = FXCollections.observableArrayList(new OutstandingTransactions(1, false, "1 - Test Invoice", "2018-03-01", "$1,500", "$1,500", null), new OutstandingTransactions(2, false, "2 - Test Invoice", "2018-03-31", "$850", "$850", null));
    final Callback<TableColumn<OutstandingTransactions, Boolean>, TableCell<OutstandingTransactions, Boolean>> checkboxCellFactory = (TableColumn<OutstandingTransactions, Boolean> param) -> new CheckBoxCellFactory();

    public void tableController(TableView tableView) {
        TableColumn<OutstandingTransactions, Integer> idColumn = new TableColumn<>("");
        TableColumn<OutstandingTransactions, Boolean> checkedColumn = new TableColumn<>("");
        TableColumn<OutstandingTransactions, String> descriptionColumn = new TableColumn<>("DESCRIPTION");
        TableColumn<OutstandingTransactions, String> dueDateColumn = new TableColumn<>("DUE DATE");
        TableColumn<OutstandingTransactions, String> originalAmountColumn = new TableColumn<>("ORIGINAL AMOUNT");
        TableColumn<OutstandingTransactions, String> openBalanceColumn = new TableColumn<>("OPEN BALANCE");
        TableColumn<OutstandingTransactions, String> paymentColumn = new TableColumn<>("PAYMENT");

        // ID column needs to be "invisible"
        idColumn.setMaxWidth(0);
        idColumn.setMinWidth(0);
        idColumn.setPrefWidth(0);
        idColumn.setVisible(false);
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));

        checkedColumn.setCellValueFactory(new PropertyValueFactory<>("isSelected"));
        checkedColumn.setEditable(true);
        checkedColumn.setPrefWidth(34);
        checkedColumn.setCellFactory(cbtc -> new CheckBoxTableCell<>());
        

        descriptionColumn.setCellValueFactory(new PropertyValueFactory("description"));
        descriptionColumn.setPrefWidth(268);

        dueDateColumn.setCellValueFactory(new PropertyValueFactory("dueDate"));
        dueDateColumn.setPrefWidth(100);
        originalAmountColumn.setCellValueFactory(new PropertyValueFactory("originalAmount"));
        originalAmountColumn.setPrefWidth(200);
        openBalanceColumn.setCellValueFactory(new PropertyValueFactory("openBalance"));
        openBalanceColumn.setPrefWidth(200);

        paymentColumn.setCellValueFactory(new PropertyValueFactory("payment"));
        paymentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        paymentColumn.setEditable(false);
        paymentColumn.setPrefWidth(200);

        // Set the columns to the tableview
        tableView.getColumns().setAll(idColumn, descriptionColumn, dueDateColumn, originalAmountColumn, openBalanceColumn);
        tableView.getItems().setAll(testData);
    }

    public static class OutstandingTransactions {

        private final SimpleIntegerProperty ID;
        private final SimpleStringProperty DESCRIPTION, DUE_DATE, ORIGINAL_AMOUNT, OPEN_BALANCE, PAYMENT;
        private final SimpleBooleanProperty SELECTED;

        public OutstandingTransactions(Integer id, boolean selected, String description, String dueDate, String originalAmount, String openBalance, String payment) {
            this.ID = new SimpleIntegerProperty(id);
            this.SELECTED = new SimpleBooleanProperty(selected);
            this.DESCRIPTION = new SimpleStringProperty(description);
            this.DUE_DATE = new SimpleStringProperty(dueDate);
            this.ORIGINAL_AMOUNT = new SimpleStringProperty(originalAmount);
            this.OPEN_BALANCE = new SimpleStringProperty(openBalance);
            this.PAYMENT = new SimpleStringProperty(payment);
        }

        public Integer getId() {
            return this.ID.get();
        }

        public void setId(Integer val) {
            this.ID.set(val);
        }

        public Boolean getIsSelected() {
            return this.SELECTED.get();
        }

        public void setIsSelected(Boolean val) {
            this.SELECTED.set(val);
        }
        
        public BooleanProperty transactionIsSelectedProperty(){
            return SELECTED;
        }

        public String getDescription() {
            return this.DESCRIPTION.get();
        }

        public void setDescription(String val) {
            this.DESCRIPTION.set(val);
        }

        public String getDueDate() {
            return this.DUE_DATE.get();
        }

        public void setDueDate(String val) {
            this.DUE_DATE.set(val);
        }

        public String getOriginalAmount() {
            return this.ORIGINAL_AMOUNT.get();
        }

        public void setOriginalAmount(String val) {
            this.ORIGINAL_AMOUNT.set(val);
        }

        public String getOpenBalance() {
            return this.OPEN_BALANCE.get();
        }

        public void setOpenBalance(String val) {
            this.OPEN_BALANCE.set(val);
        }

        public String getPayment() {
            return this.PAYMENT.get();
        }

        public void setPayment(String val) {
            this.PAYMENT.set(val);
        }
    }
}
