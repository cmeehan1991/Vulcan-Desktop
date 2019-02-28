/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.dashboard;

import com.cbmwebdevelopment.accounting.Accounting;
import com.cbmwebdevelopment.customcharts.DoughnutChart;
import com.cbmwebdevelopment.invoices.Invoice;
import com.cbmwebdevelopment.invoices.InvoiceStatus;
import com.cbmwebdevelopment.main.MainApp;
import com.jfoenix.controls.JFXProgressBar;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import static java.util.Locale.US;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class DashboardFXMLController implements Initializable {

    @FXML
    Label netProfitLossLabel, incomeAmountLabel, lossAmountLabel, totalExpensesLabel, salesAmountLabel, totalInvoicesAmountLabel, invoicesAmountUnpaidLabel, invoicesAmountPaidBarLabel, invoicesAmountUnpaidBarLabel;

    @FXML
    JFXProgressBar incomeProgressBar, expensesProgressBar, paidInvoicesProgressBar, unpaidInvoicesProgressBar;

    @FXML
    PieChart expensesPieChart;

    @FXML
    ListView<String> bankAccountsListView;

    @FXML
    Hyperlink addExternalAccountHyperlink;

    @FXML
    ComboBox<String> invoicesFilterComboBox;

    @FXML
    LineChart salesLineChart;

    private final ObservableList<String> FILTER_BY = FXCollections.observableArrayList("Today", "This Week", "This Month", "This Year", "Last Week", "Last Month");

    @FXML
    protected void addExternalAccountAction(ActionEvent event){
        
    }
    
    private void initInputs() {
        MainApp.loadingDialog.show();
        
        // Combobox items
        invoicesFilterComboBox.setItems(FILTER_BY);

        // combobox defaults
        invoicesFilterComboBox.getSelectionModel().select(2);

        // setup pie chart
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Invoice invoice = new Invoice();
            Accounting accounting = new Accounting();

            Double[] pl = accounting.profitLoss("This Year");
            String netProfitLoss = NumberFormat.getCurrencyInstance(Locale.US).format(pl[0] + pl[1]);
            String income = NumberFormat.getCurrencyInstance(Locale.US).format(pl[0]);
            String expenses = NumberFormat.getCurrencyInstance(Locale.US).format(Math.abs(pl[1]));
            Double totalPl = Math.abs(pl[0]) + Math.abs(pl[1]);

            ObservableList<PieChart.Data> pieChartData = accounting.totalExpenses("This Year");
            expensesPieChart = new DoughnutChart(pieChartData);
            final SimpleDoubleProperty totalExpenses = new SimpleDoubleProperty();
            pieChartData.forEach(item -> {
                totalExpenses.add(Math.abs(item.getPieValue()));
            });
            
            System.out.println(totalExpenses);
            
            ObservableList<InvoiceStatus> invoiceStatus = accounting.invoiceStatus(invoicesFilterComboBox.getSelectionModel().getSelectedItem());
            final SimpleDoubleProperty totalInvoices = new SimpleDoubleProperty();
            final SimpleDoubleProperty totalPaidInvoices = new SimpleDoubleProperty();
            final SimpleDoubleProperty totalUnpaidInvoices = new SimpleDoubleProperty();
            invoiceStatus.forEach(status->{
                totalInvoices.add(Math.abs(status.getInvoiceAmount()));
                
                if(status.getPaid()){
                    totalPaidInvoices.add(Math.abs(status.getInvoiceAmount()));
                }else{
                    totalUnpaidInvoices.add(Math.abs(status.getInvoiceAmount()));
                }
                
            });
            String totalInvoiceAmount = NumberFormat.getCurrencyInstance(US).format(totalInvoices.get());
            Platform.runLater(() -> {
                
                System.out.println("Net profit loss: " + netProfitLoss);
                netProfitLossLabel.setText(netProfitLoss);
                incomeAmountLabel.setText(income);
                lossAmountLabel.setText(expenses);
                incomeProgressBar.setProgress(pl[0] / totalPl);
                expensesProgressBar.setProgress(pl[1] / totalPl);
                totalExpensesLabel.setText(NumberFormat.getCurrencyInstance(Locale.US).format(totalExpenses.get()));
                expensesPieChart.setData(pieChartData);
                totalInvoicesAmountLabel.setText(totalInvoiceAmount);
                paidInvoicesProgressBar.setProgress(totalPaidInvoices.doubleValue());
                invoicesAmountPaidBarLabel.setText(NumberFormat.getCurrencyInstance(US).format(totalPaidInvoices.doubleValue()));
                unpaidInvoicesProgressBar.setProgress(totalUnpaidInvoices.doubleValue());
                invoicesAmountUnpaidBarLabel.setText(NumberFormat.getCurrencyInstance(US).format(totalUnpaidInvoices.doubleValue()));
                
                MainApp.loadingDialog.close();
            });
            
            executor.shutdown();
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
        initInputs();
    }

}
