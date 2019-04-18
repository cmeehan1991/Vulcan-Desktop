/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import com.cbmwebdevelopment.tablecellfactories.AddFormatCell;
import com.cbmwebdevelopment.tablecellfactories.MoneyFormatCell;
import com.cbmwebdevelopment.tablecellfactories.NumberFormatCell;
import com.cbmwebdevelopment.tablecellfactories.RemoveFormatCell;
import com.cbmwebdevelopment.tablecellfactories.TextFieldFormatCell;
import java.text.NumberFormat;
import java.text.ParseException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 *
 * @author cmeehan
 */
public class InvoiceTableController {

    final Callback<TableColumn<InvoiceItems, String>, TableCell<InvoiceItems, String>> textFieldCell = (TableColumn<InvoiceItems, String> param) -> new TextFieldFormatCell();
    final Callback<TableColumn<InvoiceItems, String>, TableCell<InvoiceItems, String>> moneyCell = (TableColumn<InvoiceItems, String> param) -> new MoneyFormatCell();
    final Callback<TableColumn<InvoiceItems, Double>, TableCell<InvoiceItems, Double>> numberCell = (TableColumn<InvoiceItems, Double> param) -> new NumberFormatCell();
    final Callback<TableColumn<InvoiceItems, Void>, TableCell<InvoiceItems, Void>> removeCell = (TableColumn<InvoiceItems, Void> param) -> new RemoveFormatCell();
    final Callback<TableColumn<InvoiceItems, Void>, TableCell<InvoiceItems, Void>> addCell = (TableColumn<InvoiceItems, Void> param) -> new AddFormatCell();
    public static InvoiceFXMLController invoiceFxmlController;
    
    public void tableController(TableView tableView) {
        TableColumn<InvoiceItems, Integer> idColumn = new TableColumn<>("");
        TableColumn<InvoiceItems, String> itemColumn = new TableColumn<>("Item");
        TableColumn<InvoiceItems, String> descriptionColumn = new TableColumn<>("Description");
        TableColumn<InvoiceItems, Double> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<InvoiceItems, String> priceColumn = new TableColumn<>("Unit Price");
        TableColumn<InvoiceItems, String> totalColumn = new TableColumn<>("Total");
        TableColumn<InvoiceItems, Void> removeColumn = new TableColumn<>("");
        TableColumn<InvoiceItems, Void> addColumn = new TableColumn<>("");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setVisible(false);
        idColumn.setPrefWidth(0);

        ObservableList<String> items = FXCollections.observableArrayList("Materials", "Lumber", "Hardware", "Labor", "Shipping", "Finish", "Refinishing", "Product", "Other", "Discount");
        itemColumn.setCellFactory(ComboBoxTableCell.forTableColumn(items));
        itemColumn.setCellValueFactory(new PropertyValueFactory("item"));
        itemColumn.setEditable(true);
        itemColumn.setPrefWidth(150);
        itemColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int row = t.getTablePosition().getRow();
            t.getTableView().getItems().get(row).setItem(t.getNewValue());
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(textFieldCell);
        descriptionColumn.setEditable(true);
        descriptionColumn.setPrefWidth(300);
        descriptionColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int row = t.getTablePosition().getRow();
            t.getTableView().getItems().get(row).setDescription(t.getNewValue());
        });

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(numberCell);
        quantityColumn.setEditable(true);
        quantityColumn.widthProperty().add(40);
        quantityColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, Double> t) -> {

            // Get the current row
            int row = t.getTablePosition().getRow();

            // Set the new value
            t.getTableView().getItems().get(row).setQuantity(t.getNewValue());
            if (t.getNewValue() != null && t.getTableView().getItems().get(row).getPrice() != null) {
                try {
                    // Calculate the total
                    double quantity = t.getNewValue();
                    double price = NumberFormat.getInstance().parse(t.getTableView().getItems().get(row).getPrice()).doubleValue();
                    Number total = quantity * price;
                    
                    // Set the total
                    t.getTableView().getItems().get(row).setTotal(NumberFormat.getCurrencyInstance().format(total));
                    invoiceFxmlController.updateSubtotal();
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        quantityColumn.setOnEditCancel((TableColumn.CellEditEvent<InvoiceItems, Double> t) -> {

            // Get the current row
            int row = t.getTablePosition().getRow();

            // Set the new value
            t.getTableView().getItems().get(row).setQuantity(t.getNewValue());
            if (t.getNewValue() != null && t.getTableView().getItems().get(row).getPrice() != null) {
                try {
                    // Calculate the total
                    double quantity = t.getNewValue();
                    double price = NumberFormat.getInstance().parse(t.getTableView().getItems().get(row).getPrice()).doubleValue();
                    Number total = quantity * price;
                    // Set the total
                    t.getTableView().getItems().get(row).setTotal(NumberFormat.getCurrencyInstance().format(total));
                    t.getTableView().refresh();
                    invoiceFxmlController.updateSubtotal();
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(moneyCell);
        priceColumn.setEditable(true);
        priceColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            // Get the current row
            int row = t.getTablePosition().getRow();

            // Set the new value
            t.getTableView().getItems().get(row).setPrice(t.getNewValue());
            if (t.getNewValue() != null && t.getTableView().getItems().get(row).getPrice() != null) {
                try {
                    // Calculate the total
                    double quantity = t.getTableView().getItems().get(row).getQuantity();
                    double price = NumberFormat.getInstance().parse(t.getNewValue()).doubleValue();

                    Number total = quantity * price;

                    // Set the total
                    t.getTableView().getItems().get(row).setTotal(NumberFormat.getCurrencyInstance().format(total));
                    t.getTableView().refresh();
                    invoiceFxmlController.updateSubtotal();
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setCellFactory(moneyCell);
        totalColumn.setEditable(false);

        removeColumn.setCellFactory(removeCell);
        addColumn.setCellFactory(addCell);

        tableView.setItems(FXCollections.observableArrayList(new InvoiceItems(1, "", "", 0.0, "0.00", "0.00")));
        tableView.getColumns().setAll(idColumn, itemColumn, descriptionColumn, quantityColumn, priceColumn, totalColumn, removeColumn, addColumn);
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    }

    public static class InvoiceItems {

        private final SimpleIntegerProperty ID;
        private final SimpleStringProperty ITEM, DESCRIPTION, PRICE, TOTAL;
        private final SimpleDoubleProperty QUANTITY;

        public InvoiceItems(Integer id, String item, String description, Double quantity, String price, String total) {
            this.ID = new SimpleIntegerProperty(id);
            this.ITEM = new SimpleStringProperty(item);
            this.DESCRIPTION = new SimpleStringProperty(description);
            this.QUANTITY = new SimpleDoubleProperty(quantity);
            this.PRICE = new SimpleStringProperty(price);
            this.TOTAL = new SimpleStringProperty(total);
        }

        public void setId(Integer val) {
            this.ID.set(val);
        }

        public Integer getId() {
            return this.ID.get();
        }

        public void setItem(String val) {
            this.ITEM.set(val);
        }

        public String getItem() {
            return this.ITEM.get();
        }

        public void setDescription(String val) {
            this.DESCRIPTION.set(val);
        }

        public String getDescription() {
            return this.DESCRIPTION.get();
        }

        public void setQuantity(Double val) {
            this.QUANTITY.set(val);
        }

        public Double getQuantity() {
            return this.QUANTITY.get();
        }

        public void setPrice(String val) {
            this.PRICE.set(val);
        }

        public String getPrice() {
            return this.PRICE.get();
        }

        public void setTotal(String val) {
            this.TOTAL.set(val);
        }

        public String getTotal() {
            return this.TOTAL.get();
        }

    }
}
