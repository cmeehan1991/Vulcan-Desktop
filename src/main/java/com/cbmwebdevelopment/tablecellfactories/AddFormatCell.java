/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecellfactories;

import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

/**
 *
 * @author cmeehan
 */
public class AddFormatCell extends TableCell<InvoiceItems, Void>{
    private final Hyperlink addLink;
    
    public AddFormatCell(){
        addLink = new Hyperlink("Add");
        addLink.setTextFill(Color.BLACK);
        addLink.setOnAction(evt -> {
            getTableView().getItems().add(new InvoiceItems(getTableView().getItems().size() + 1, "", "", 0.0,"", ""));
        });
    }
    
    @Override 
    protected void updateItem(Void item, boolean empty){
        super.updateItem(item, empty);
        
        setGraphic(empty? null : addLink);
    }
}
