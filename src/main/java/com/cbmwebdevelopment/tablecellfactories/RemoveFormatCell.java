/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecellfactories;

import com.cbmwebdevelopment.invoices.Invoice;
import com.cbmwebdevelopment.invoices.InvoiceFXMLController;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.PopOver;
import com.cbmwebdevelopment.invoices.InvoiceTableController.InvoiceItems;

/**
 *
 * @author cmeehan
 * @param <InvoiceItems>
 */
public class RemoveFormatCell extends TableCell<InvoiceItems, Void> {

    private final Hyperlink removeLink;

    public RemoveFormatCell() {
        removeLink = new Hyperlink("Remove");
        removeLink.setTextFill(Color.BLACK);
        removeLink.setOnAction(evt -> {
            confirmationPopOver().show(removeLink);
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(empty ? null : removeLink);
    }

    /*
    * Popover to provide confirmation when removing row. 
     */
    private PopOver confirmationPopOver() {

        PopOver popOver = new PopOver();

        Label label = new Label("Remove?");
        Hyperlink yesLink = new Hyperlink("Yes");
        Hyperlink noLink = new Hyperlink("No");

        yesLink.setTextFill(Color.RED);
        yesLink.setOnAction(evt -> {
            if (InvoiceFXMLController.invoiceId > 0) {
                InvoiceItems invoiceItems = getTableView().getItems().get(getTableRow().getIndex());
                new Invoice().removeInvoiceItem(String.valueOf(InvoiceFXMLController.invoiceId), invoiceItems.getId().toString());
            }
            getTableView().getItems().remove(getTableRow().getIndex());
            popOver.hide();
            if (getTableView().getItems().size() <= 0) {
                getTableView().getItems().addAll(new InvoiceItems(1, "", "", 0.00, "$0.00", "$0.00"));
            }
        });

        noLink.setOnAction(evt -> {
            popOver.hide();
        });

        VBox vBox = new VBox(label, yesLink, noLink);

        popOver.setContentNode(vBox);

        return popOver;
    }
}
