/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecellfactories;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author cmeehan
 * @param <InvoiceItems>
 */
public class MoneyFormatCell<InvoiceItems> extends TableCell<InvoiceItems, String> {

    private TextField textField;

    public MoneyFormatCell() {
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            createTextField();
        }
        setText(textField.getText());
        setGraphic(textField);
        textField.selectAll();
        textField.requestFocus();

    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
        setGraphic(null);
        textField.deselect();
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        // Format the number as if it were a monetary value using the 
        // formatting relevant to the current locale. 
        try {
            if (item != null && item.contains("(")) {
                item = item.replaceAll("(\\()|(\\))", "");
                item = "-" + item;
            }
            String parsedItem = item != null && item.contains("$") ? item.replace("$", "") : item == null ? "0.00" : item;
            Number number = NumberFormat.getNumberInstance().parse(parsedItem);
            setText(item == null ? "" : NumberFormat.getCurrencyInstance().format(number));

        } catch (ParseException ex) {
            System.err.println("Money Format Cell - Update Item Error: " + ex.getMessage());
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setEditable(true);
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> arg, Boolean arg1, Boolean arg2) -> {
            if (!arg2) {
                commitEdit(textField.getText());
            }
        });
        textField.setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                case ENTER:
                    commitEdit(textField.getText());
                    event.consume();
                    break;
                case TAB:
                    commitEdit(textField.getText());
                    TableColumn nextColumn = getNextColumn(!event.isShiftDown());
                    event.consume();
                    if (nextColumn != null) {
                        getTableView().edit(getTableRow().getIndex(), nextColumn);
                        getTableView().getFocusModel().focus(getTableRow().getIndex() + 1);
                        getTableView().requestFocus();
                        textField.selectAll();
                        textField.requestFocus();
                    }
                    break;
                default:
                    break;
            }
        });
    }

    // Thanks to https://gist.github.com/abhinayagarwal/9383881
    private TableColumn<InvoiceItems, ?> getNextColumn(boolean forward) {
        List<TableColumn<InvoiceItems, ?>> columns = new ArrayList<>();
        getTableView().getColumns().forEach((column) -> {
            columns.addAll(getLeaves(column));
        });
        // There is no other column that supports editing.
        if (columns.size() < 2) {
            return null;
        }
        int currentIndex = columns.indexOf(getTableColumn());
        int nextIndex = currentIndex;
        if (forward) {
            nextIndex++;
            if (nextIndex > columns.size() - 1) {
                nextIndex = 0;
            }
        } else {
            nextIndex--;
            if (nextIndex < 0) {
                nextIndex = columns.size() - 1;
            }
        }
        return columns.get(nextIndex);
    }

    private List<TableColumn<InvoiceItems, ?>> getLeaves(
            TableColumn<InvoiceItems, ?> root) {
        List<TableColumn<InvoiceItems, ?>> columns = new ArrayList<>();
        if (root.getColumns().isEmpty()) {
            // We only want the leaves that are editable.
            if (root.isEditable()) {
                columns.add(root);
            }
            return columns;
        } else {
            root.getColumns().forEach((column) -> {
                columns.addAll(getLeaves(column));
            });
            return columns;
        }
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
