/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecellfactories;

import com.cbmwebdevelopment.accounting.OutstandingTransactionsTableController.OutstandingTransactions;
import com.cbmwebdevelopment.accounting.PaymentsController;
import com.cbmwebdevelopment.notifications.Notifications;
import java.text.NumberFormat;
import java.text.ParseException;
import static java.util.Locale.US;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;

/**
 *
 * @author cmeehan
 */
public class CheckBoxCellFactory extends TableCell<OutstandingTransactions, Boolean> {

    private final CheckBox cb;
    private double remainingBalance = 0.00;

    public CheckBoxCellFactory() {
        cb = new CheckBox();
        cb.selectedProperty().addListener((obs, ov, nv) -> {
            int row = getTableRow().getIndex();
           /* if (PaymentsController.AMOUNT_RECEIVED.doubleValue() > 0) {
                try {
                    Double amountReceived = PaymentsController.AMOUNT_RECEIVED.doubleValue();

                    getTableView().getItems().forEach(item -> {
                        remainingBalance = item.getPayment().isEmpty() ? amountReceived : amountReceived - Double.parseDouble(item.getPayment());
                    });

                    Double openBalance = NumberFormat.getCurrencyInstance(US).parse(getTableView().getItems().get(row).getOpenBalance()).doubleValue();

                    getTableView().getItems().get(row).setPayment(NumberFormat.getCurrencyInstance(US).format(remainingBalance));
                    getTableView().getItems().get(row).setIsSelected(nv);
                    if (nv) {
                        String payment = amountReceived <= openBalance ? String.valueOf(amountReceived) : String.valueOf(openBalance);
                        getTableView().getItems().get(row).setPayment(payment);
                        getTableView().getItems().get(row).setIsSelected(true);
                        remainingBalance = amountReceived - openBalance;
                    } else {
                        getTableView().getItems().get(row).setPayment(null);
                        remainingBalance = amountReceived + openBalance;
                    }
                    getTableView().refresh();
                    cb.setSelected(nv);
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            } else {
                Notifications.snackbarNotification("Please add a payment amount");
            }*/
        });
    }

    @Override 
    public void startEdit(){
        super.startEdit();
        
        setGraphic(cb);
    }
    
    @Override
    public void commitEdit(Boolean newValue){
        super.commitEdit(newValue);
        System.out.println("Commit: " + newValue);
    }
    
    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(empty ? null : cb);
    }
}
