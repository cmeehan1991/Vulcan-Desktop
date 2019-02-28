package com.cbmwebdevelopment.main;

import com.cbmwebdevelopment.accounting.PaymentsController;
import com.cbmwebdevelopment.accounting.TransactionsController;
import com.cbmwebdevelopment.dashboard.DashboardFXMLController;
import com.cbmwebdevelopment.invoices.InvoiceFXMLController;
import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.quote.QuoteController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainFXMLController implements Initializable {

    @FXML
    VBox navigationBox, clientsSubNavigation, invoicesSubNavigation, quotesSubNavigation, accountingSubNavigation;

    @FXML
    public StackPane parentPane;

    @FXML
    public AnchorPane mainPane;

    @FXML
    public ProgressIndicator loadingIndicator;

    @FXML
    public void dashboardNavigationAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardFXML.fxml"));
            AnchorPane anchorPane = (AnchorPane) loader.load();
            DashboardFXMLController controller = (DashboardFXMLController) loader.getController();
            MainFXMLController.setPane(anchorPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void clientsNavigationAction(ActionEvent event) {

        if (!clientsSubNavigation.isVisible()) {
            clientsSubNavigation.setVisible(true);
            clientsSubNavigation.setMinHeight(USE_COMPUTED_SIZE);
            clientsSubNavigation.setMaxHeight(USE_COMPUTED_SIZE);
            closeOthers(clientsSubNavigation);
        } else {
            clientsSubNavigation.setVisible(false);
            clientsSubNavigation.setMinHeight(0);
            clientsSubNavigation.setMaxHeight(0);
            clientsSubNavigation.setPrefHeight(0);
        }

    }

    @FXML
    protected void invoicesNavigationAction(ActionEvent event) {

        if (!invoicesSubNavigation.isVisible()) {
            invoicesSubNavigation.setVisible(true);
            invoicesSubNavigation.setMinHeight(USE_COMPUTED_SIZE);
            invoicesSubNavigation.setMaxHeight(USE_COMPUTED_SIZE);
            closeOthers(invoicesSubNavigation);
        } else {
            invoicesSubNavigation.setVisible(false);
            invoicesSubNavigation.setMinHeight(0);
            invoicesSubNavigation.setMaxHeight(0);
            invoicesSubNavigation.setPrefHeight(0);
        }

    }

    @FXML
    protected void quotesNavigationAction(ActionEvent event) {

        if (!quotesSubNavigation.isVisible()) {
            quotesSubNavigation.setVisible(true);
            quotesSubNavigation.setMinHeight(USE_COMPUTED_SIZE);
            quotesSubNavigation.setMaxHeight(USE_COMPUTED_SIZE);
            closeOthers(quotesSubNavigation);
        } else {
            quotesSubNavigation.setVisible(false);
            quotesSubNavigation.setMinHeight(0);
            quotesSubNavigation.setMaxHeight(0);
            quotesSubNavigation.setPrefHeight(0);
        }

    }

    @FXML
    protected void accountingNavigationAction(ActionEvent event) {
        System.out.println("Accounting Navigation");
        if (!accountingSubNavigation.isVisible()) {
            System.out.println("Not visible");
            accountingSubNavigation.setVisible(true);
            accountingSubNavigation.setMinHeight(USE_COMPUTED_SIZE);
            accountingSubNavigation.setMaxHeight(USE_COMPUTED_SIZE);
            closeOthers(accountingSubNavigation);
        } else {

            System.out.println("Visible");
            accountingSubNavigation.setVisible(false);
            accountingSubNavigation.setMinHeight(0);
            accountingSubNavigation.setMaxHeight(0);
            accountingSubNavigation.setPrefHeight(0);
        }
    }

    @FXML
    protected void newClientAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerFXML.fxml"));
            AnchorPane customerPane = (AnchorPane) loader.load();
            customerPane.setMinSize(MainApp.mainPane.getWidth(), MainApp.mainPane.getHeight());
            MainFXMLController.setPane(customerPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void viewClientAction(ActionEvent event) {
        new Notifications().customerId();
    }

    @FXML
    protected void newQuoteAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Quote.fxml"));
            AnchorPane quotePane = (AnchorPane) loader.load();
            quotePane.setMinSize(MainApp.mainPane.getWidth(), MainApp.mainPane.getHeight());
            MainFXMLController.setPane(quotePane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void viewQuoteAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Quote.fxml"));
            AnchorPane quotePane = (AnchorPane) loader.load();
            QuoteController controller = (QuoteController) loader.getController();
            controller.isNew = false;
            controller.quoteIdTextField.requestFocus();
            
            MainFXMLController.setPane(quotePane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void newInvoiceAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InvoiceFXML.fxml"));
            AnchorPane invoicePane = (AnchorPane) loader.load();

            InvoiceFXMLController invoiceController = (InvoiceFXMLController) loader.getController();
            invoiceController.setInputsForUpdate(false);
            MainFXMLController.setPane(invoicePane);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.err.println(ex.getLocalizedMessage());
            System.err.println(ex.getCause());
        }
    }

    @FXML
    protected void viewInvoiceAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InvoiceFXML.fxml"));
            AnchorPane invoicePane = (AnchorPane) loader.load();

            InvoiceFXMLController invoiceController = (InvoiceFXMLController) loader.getController();
            invoiceController.setInputsForUpdate(true);
            MainFXMLController.setPane(invoicePane);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void receivePaymentAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentsFXML.fxml"));
            AnchorPane paymentsPane = (AnchorPane) loader.load();

            PaymentsController paymentsController = (PaymentsController) loader.getController();

            MainFXMLController.setPane(paymentsPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    @FXML
    protected void transactionsDashboardAction(ActionEvent event){
        try{
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TransactionsFXML.fxml"));
            AnchorPane transactionsPane = (AnchorPane) loader.load();

            TransactionsController transactionsController = (TransactionsController) loader.getController();

            MainFXMLController.setPane(transactionsPane);
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    }

    private void closeOthers(VBox active) {
        ObservableList<Node> children = navigationBox.getChildren();
        children.forEach(item -> {
            if (item instanceof VBox && item != active) {
                VBox vbox = (VBox) item;
                vbox.setVisible(false);
                vbox.setMinHeight(0);
                vbox.setMaxHeight(0);
            }
        });

    }
    
    public static void setPane(AnchorPane anchorPane){
        MainApp.mainPane.getChildren().setAll(anchorPane);
        
        AnchorPane.setTopAnchor(anchorPane, 0.0);
        AnchorPane.setBottomAnchor(anchorPane, 0.0);
        AnchorPane.setLeftAnchor(anchorPane, 0.0);
        AnchorPane.setRightAnchor(anchorPane, 0.0);
        
        //anchorPane.prefWidthProperty().bind(MainApp.mainPane.widthProperty());
        //anchorPane.prefHeightProperty().bind(MainApp.mainPane.heightProperty());
        
        //anchorPane.minWidth(USE_COMPUTED_SIZE);
        //anchorPane.minHeight(USE_COMPUTED_SIZE);
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
