/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.project;

import com.cbmwebdevelopment.customers.Customer;
import static com.cbmwebdevelopment.main.Strings.STATUS;
import com.cbmwebdevelopment.notifications.Notifications;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ProjectFXMLController implements Initializable {

    @FXML
    JFXTextField projectIdTextField, projectTitleTextField, streetAddressTextField, secondaryAddressTextField, cityTextField, postalCodeTextField;

    @FXML
    JFXComboBox<String> statusComboBox, stateComboBox;
    
    @FXML
    JFXComboBox<Customer> clientComboBox;

    @FXML
    JFXDatePicker startDatePicker, deadlineDatePicker;

    @FXML
    RadioButton commissionRadioButton, residentialRadioButton, commercialRadioButton;

    @FXML
    TableView projectIncomeExpenseTableView, projectMaterialsTableView;

    protected boolean isNew = true;
    
    @FXML
    protected void exportProjectMaterialsTableAction(ActionEvent event) {

    }

    @FXML
    protected void exportIncomeExpensesTableAction(ActionEvent evt) {

    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusComboBox.setItems(STATUS);
        statusComboBox.getSelectionModel().select("Planning");
        statusComboBox.selectionModelProperty().addListener((obs, ov, nv)->{
            
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(()->{
                boolean setStatus = new Projects().setStatus(nv.toString());
                Platform.runLater(()->{
                    executor.shutdown();
                    if(setStatus){
                        Notifications.snackbarNotification("Status Updated");
                    }else{
                        Notifications.snackbarNotification("Failed To Update Status");
                        statusComboBox.getSelectionModel().select(ov.toString());
                    }
                });
            });
        });
        
    }

}
