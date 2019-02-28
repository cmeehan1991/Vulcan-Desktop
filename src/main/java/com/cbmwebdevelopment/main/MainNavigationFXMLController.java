/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.main;

import com.cbmwebdevelopment.notifications.Notifications;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class MainNavigationFXMLController implements Initializable {

    @FXML
    VBox navigationBox;

    @FXML
    Button quoteButton, clientButton;
    PopOver popOver;

    @FXML
    protected void showQuotePopOver(MouseEvent event) {
        JFXButton newQuoteButton = new JFXButton("New");
        JFXButton updateQuoteButton = new JFXButton("View/Edit");

        newQuoteButton.widthProperty().add(150);
        updateQuoteButton.widthProperty().add(150);

        VBox vBox = new VBox(newQuoteButton, updateQuoteButton);

        popOver = new PopOver(vBox);
        popOver.setWidth(vBox.getWidth());

        if (!popOver.isShowing()) {
            popOver.show(quoteButton);
        }

        newQuoteButton.setOnAction(evt -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Quote.fxml"));
                AnchorPane quotePane = (AnchorPane) loader.load();
                quotePane.setMinSize(MainApp.mainPane.getWidth(), MainApp.mainPane.getHeight());
                MainApp.mainPane.getChildren().add(quotePane);
                popOver.hide();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });

        updateQuoteButton.setOnAction(evt -> {
            MainApp.mainPane.getChildren().add(null);
        });
    }

    @FXML
    protected void showClientPopOver(MouseEvent event) {
        JFXButton newClientButton = new JFXButton("New");
        JFXButton updateClientButton = new JFXButton("View/Edit");

        newClientButton.widthProperty().add(150);
        updateClientButton.widthProperty().add(150);

        VBox vBox = new VBox(newClientButton, updateClientButton);

        popOver = new PopOver(vBox);
        popOver.setWidth(vBox.getWidth());

        if (!popOver.isShowing()) {
            popOver.show(clientButton);
        }

        newClientButton.setOnAction(evt -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerFXML.fxml"));
                AnchorPane customerPane = (AnchorPane) loader.load();
                customerPane.setMinSize(MainApp.mainPane.getWidth(), MainApp.mainPane.getHeight());
                MainApp.mainPane.getChildren().add(customerPane);
                popOver.hide();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });

        updateClientButton.setOnAction(evt -> {
            new Notifications().customerId();
            //popOver.hide();
        });
    }

    @FXML
    protected void hidePopOver(MouseEvent event) {
        //popOver.hide();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
