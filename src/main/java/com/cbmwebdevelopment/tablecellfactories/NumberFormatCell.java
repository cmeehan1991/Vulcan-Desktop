/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecellfactories;

import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
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
 */
public class NumberFormatCell extends TableCell<InvoiceItems, Double> {

    private TextField textField;

    public NumberFormatCell() {
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(String.valueOf(getItem()));
        setGraphic(null);
    }

    @Override
    public void commitEdit(Double newValue) {
        super.commitEdit(newValue);
        setGraphic(null);
        textField.deselect();
    }

    @Override
    public void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(String.valueOf(item));
        }
    }

    private void createTextField() {
        textField = new TextField();
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> arg, Boolean arg1, Boolean arg2) -> {
            if (!arg2) {
                commitEdit(Double.parseDouble(textField.getText()));
            }
        });
        textField.setOnKeyPressed((KeyEvent event) -> {
            if (!textField.getText().isEmpty()) {
                switch (event.getCode()) {
                    case ENTER:
                        commitEdit(Double.parseDouble(textField.getText()));
                        break;
                    case TAB:
                        commitEdit(Double.parseDouble(textField.getText()));
                        TableColumn nextColumn = getNextColumn(!event.isShiftDown());
                        event.consume();
                        if (nextColumn != null) {
                            getTableView().edit(getTableRow().getIndex(), nextColumn);
                            getTableView().getFocusModel().focus(getTableRow().getIndex() + 1);
                            getTableView().requestFocus();
                            textField.requestFocus();
                        }
                        break;
                    default:
                        break;
                }
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

}
