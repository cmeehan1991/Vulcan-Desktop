/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.customers;

import com.cbmwebdevelopment.customers.CustomerInvoicesTableController.CustomerInvoices;
import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.values.Values;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.sibvisions.rad.ui.javafx.ext.FXSelectableLabel;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class CustomerFXMLController implements Initializable {

    String id = null;

    @FXML
    AnchorPane anchorPane;

    @FXML
    FXSelectableLabel customerIdLabel;

    @FXML
    JFXComboBox<String> prefixComboBox, suffixComboBox, stateComboBox, countryComboBox;

    @FXML
    JFXRadioButton individualTypeRadioButton, businessTypeRadioButton;

    @FXML
    ToggleGroup clientType;

    @FXML
    JFXTextField firstNameTextField, lastNameTextField, companyNameTextField, primaryAddressTextField, secondaryAddressTextField, cityTextField, stateTextField, postalCodeTextField, emailAddressTextField, phoneNumberTextField;

    @FXML
    TableView<CustomerInvoices> invoiceTableTableView;

    public boolean isNew = true;
    private List<Node> missingFields;

    private boolean requiredFields() {
        missingFields = new ArrayList<>();

        if (firstNameTextField.getText().isEmpty()) {
            missingFields.add(firstNameTextField);
        }

        if (lastNameTextField.getText().isEmpty()) {
            missingFields.add(lastNameTextField);
        }

        return missingFields.isEmpty();
    }

    @FXML
    protected void saveInfoAction(ActionEvent action) {
        System.out.println("Save info");
        if (requiredFields()) {
            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(() -> {
                Customers customers = new Customers();

                String customerId = customers.saveCustomer(new Customer(prefixComboBox.getSelectionModel().getSelectedItem(), id, companyNameTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), suffixComboBox.getSelectionModel().getSelectedItem(), primaryAddressTextField.getText(), secondaryAddressTextField.getText(), cityTextField.getText(), stateComboBox.getSelectionModel().getSelectedItem(), postalCodeTextField.getText(), countryComboBox.getSelectionModel().getSelectedItem(), emailAddressTextField.getText(), phoneNumberTextField.getText(), businessTypeRadioButton.isSelected(), individualTypeRadioButton.isSelected()));

                Platform.runLater(() -> {
                    if (isNew && customerId != null) {
                        customerIdLabel.setText(customerId);
                        id = customerId;
                        isNew = false;
                    } else {
                        System.out.println("Existing");
                    }
                    Notifications.snackbarNotification("Saved");
                });
                executor.shutdown();
            });
        } else {
            System.out.println("Missing: " + missingFields);
            missingFields.forEach(field -> {
                field.getStyleClass().add("error");
            });
            new Notifications().missingFieldsNotification();
        }
    }

    public void setCustomerInformation(String customerId) {
        id = customerId;
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Customers customers = new Customers();

            Customer customer = customers.getCustomer(id);

            Platform.runLater(() -> {
                initInvoiceTable(id);
                customerIdLabel.setText(id);
                prefixComboBox.getSelectionModel().select(customer.getPrefix());
                companyNameTextField.setText(customer.getCompanyName());
                firstNameTextField.setText(customer.getFirstName());
                lastNameTextField.setText(customer.getLastName());
                suffixComboBox.getSelectionModel().select(customer.getSuffix());
                primaryAddressTextField.setText(customer.getPrimaryAddress());
                secondaryAddressTextField.setText(customer.getSecondaryAddress());
                cityTextField.setText(customer.getCity());
                stateComboBox.getSelectionModel().select(customer.getState());
                postalCodeTextField.setText(customer.getPostalCode());
                countryComboBox.getSelectionModel().select(customer.getCountry());
                emailAddressTextField.setText(customer.getEmailAddress());
                phoneNumberTextField.setText(customer.getPhoneNumber());
                individualTypeRadioButton.setSelected(customer.getIsIndividualType());
                businessTypeRadioButton.setSelected(customer.getIsBusinessType());
            });
            executor.shutdown();

        });
    }

    /**
     * Open the selected invoice.
     *
     * @param id
     */
    private void openInvoice(Integer id) {
        
    }

    /**
     * Initialize the invoice table. We are also going to be adding invoice
     * information in this table here.
     */
    private void initInvoiceTable(String id) {
        invoiceTableTableView.setRowFactory(tv -> {
            TableRow<CustomerInvoices> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !(row.isEmpty())) {
                    CustomerInvoices data = row.getItem();
                    openInvoice(data.getInvoiceNumber());
                }
            });
            return row;
        });

        CustomerInvoicesTableController tableController = new CustomerInvoicesTableController();
        tableController.tableController(invoiceTableTableView);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {

            Customers customers = new Customers();

            ObservableList<CustomerInvoices> data = customers.getCustomerInvoices(id);
            if (!data.isEmpty()) {
                Platform.runLater(() -> {

                    invoiceTableTableView.getItems().setAll(data);
                });
            }
            executor.shutdown();
            System.out.println(executor.isShutdown());
        });
    }

    /**
     * Initialize the inputs. This is where we are populating the combo boxes
     * and handling any filtering.
     */
    private void initInputs() {
        // Set the states and countries
        stateComboBox.setItems(Values.STATES);
        countryComboBox.setItems(Values.COUNTRIES);
        prefixComboBox.setItems(Values.PREFIXES);
        suffixComboBox.setItems(Values.SUFFIXES);

        // set the default values
        stateComboBox.getSelectionModel().select("North Carolina");
        countryComboBox.getSelectionModel().select("United States");

        FilteredList<String> filteredStates = new FilteredList<>(Values.STATES, p -> true);
        FilteredList<String> filteredCountries = new FilteredList<>(Values.COUNTRIES, p -> true);

        // Set the combobox listeners        
        stateComboBox.getEditor().textProperty().addListener((obs, ov, nv) -> {
            final TextField editor = stateComboBox.getEditor();
            final String selected = stateComboBox.getSelectionModel().getSelectedItem();

            Platform.runLater(() -> {
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredStates.setPredicate(item -> {
                        return item.toLowerCase().contains(nv.toLowerCase());
                    });
                }
            });

            stateComboBox.setItems(filteredStates);
            if (stateComboBox.isFocused()) {
                stateComboBox.show();
            } else {
                stateComboBox.hide();
            }
        });
        // Set the textfield listeners        
        countryComboBox.getEditor().textProperty().addListener((obs, ov, nv) -> {
            final TextField editor = countryComboBox.getEditor();
            final String selected = countryComboBox.getSelectionModel().getSelectedItem();

            Platform.runLater(() -> {
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredCountries.setPredicate(item -> {
                        return item.toLowerCase().contains(nv.toLowerCase());
                    });
                }
            });

            countryComboBox.setItems(filteredCountries);
            if (countryComboBox.isFocused() && !filteredCountries.isEmpty()) {
                countryComboBox.show();
            } else {
                countryComboBox.hide();
            }
        });

        clientType.selectedToggleProperty().addListener((obs, ov, nv) -> {
            JFXRadioButton selectedButton = (JFXRadioButton) nv.getToggleGroup().getSelectedToggle();

            String selectedButtonId = selectedButton.getId();
            companyNameTextField.setVisible(selectedButtonId.equals("businessTypeRadioButton"));
        });
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initInvoiceTable(null);
        initInputs();
    }

}
