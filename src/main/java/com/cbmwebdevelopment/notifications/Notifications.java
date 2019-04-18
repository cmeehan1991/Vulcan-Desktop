package com.cbmwebdevelopment.notifications;

import com.cbmwebdevelopment.customers.Customer;
import com.cbmwebdevelopment.customers.CustomerFXMLController;
import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.MainFXMLController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cmeehan
 */
public class Notifications {

    private String id;

    public void missingFieldsNotification() {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Missing Information"));
        content.setBody(new Text("Some of the required fields have not been filled out. Please go back and fill in all of the required fields."));

        JFXDialog dialog = new JFXDialog(MainApp.parentPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton actionButton = new JFXButton("Okay");
        actionButton.setOnAction(evt -> {
            dialog.close();
        });

        content.setActions(actionButton);
        dialog.show();
    }

    public String customerId() {
        JFXComboBox<Customer> comboBox = new Customers().customersComboBox();

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Customers"));
        content.setBody(comboBox);

        JFXDialog dialog = new JFXDialog(MainApp.parentPane, content, JFXDialog.DialogTransition.CENTER);

        JFXButton okButton = new JFXButton("Okay");
        okButton.setOnAction(evt -> {
            if (comboBox.getSelectionModel().getSelectedItem().getId() != null) {
                try {
                    id = comboBox.getSelectionModel().getSelectedItem().getId();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerFXML.fxml"));
                    AnchorPane customerPane = (AnchorPane) loader.load();

                    CustomerFXMLController controller = (CustomerFXMLController) loader.getController();
                    controller.setCustomerInformation(id);

                    customerPane.setMinSize(MainApp.mainPane.getWidth(), MainApp.mainPane.getHeight());

                    MainFXMLController.setPane(customerPane);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            dialog.close();
        });

        JFXButton closeButton = new JFXButton("Close");
        closeButton.setOnAction(evt -> {
            id = null;
            dialog.close();
        });

        content.setActions(okButton, closeButton);

        dialog.show();

        return id;
    }

    public static void snackbarNotification(String message) {
        JFXSnackbar bar = new JFXSnackbar(MainApp.mainPane);
        bar.enqueue(new SnackbarEvent(message));
    }

    public static JFXDialog loadingIndicator() {
        // Dialog layout
        JFXDialogLayout content = new JFXDialogLayout();
        content.getStyleClass().add("dialog--loading-dialog");

        // Actual content
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.getStyleClass().add("progress-indicator--light");
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Label label = new Label("Loading");
        label.getStyleClass().add("dialog--loading-dialog__label");

        VBox loadingContent = new VBox();
        loadingContent.alignmentProperty().set(Pos.TOP_CENTER);
        loadingContent.getChildren().addAll(progressIndicator, label);
        loadingContent.setPrefSize(150, 150);

        content.setBody(loadingContent);
        content.setPrefSize(150, 150);

        // Dialog
        JFXDialog dialog = new JFXDialog(MainApp.parentPane, content, JFXDialog.DialogTransition.CENTER);
        dialog.autosize();

        return dialog;
    }
    
    public static JFXDialog deleteConfirmationDialog(){
        JFXDialogLayout content = new JFXDialogLayout(); 
        
        JFXDialog dialog = new JFXDialog(MainApp.parentPane, content, JFXDialog.DialogTransition.CENTER);
        
        return dialog;
    }

}
