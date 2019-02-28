/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecellfactories;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

/**
 *
 * @author cmeehan
 * @param <InvoiceItems>
 */
public class TextFieldFormatCell<InvoiceItems> extends TableCell<InvoiceItems, String> {
    private TextField textField;
    
    public TextFieldFormatCell(){}
    
    @Override 
    public void startEdit(){
        super.startEdit();
        if(!isEmpty()){
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
    }
    
    @Override
    public void cancelEdit(){
        super.cancelEdit();
        setText((String) getItem());
        setGraphic(null);
    }
    
    @Override
    public void commitEdit(String newValue){
        super.commitEdit(newValue);
        setGraphic(null);
        textField.deselect();
    }
    
    @Override
    public void updateItem(String item, boolean empty){
        super.updateItem(item, empty);
        if(empty){
            setText(null);
        }else{
            setText(item);
        }
    }
    /**
     * 
     */
    private void createTextField(){
        textField = new TextField();
        textField.focusedProperty().addListener((obs, ov, nv)->{
            if(!nv){
                commitEdit(textField.getText());                        
            }
        });
        
        textField.setOnKeyPressed(evt -> {
            switch(evt.getCode()){
                case ENTER: 
                    commitEdit(textField.getText());
                    break;
                case TAB: 
                    commitEdit(textField.getText());
                    TableColumn nextColumn = getNextColumn(!evt.isShiftDown());
                    evt.consume();
                    if(nextColumn != null){
                        getTableView().edit(getTableRow().getIndex(), nextColumn);
                        getTableView().getFocusModel().focus(getTableRow().getIndex() + 1);
                        getTableView().requestFocus();
                        textField.requestFocus();
                    }
                default: break;
            }
        });
    }
    
    /**
     * Credit: Thanks to https://gist.github.com/abhinayagarwal/9383881
     * 
     */
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
    /**
     * 
     * @param root
     * @return 
     */
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
    /**
     * 
     * @return 
     */
    private String getString(){
        return getItem() == null ? "" : getItem();
    }
}
