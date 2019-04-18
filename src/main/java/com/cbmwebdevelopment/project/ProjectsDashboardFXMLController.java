/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.project;

import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.MainFXMLController;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ProjectsDashboardFXMLController implements Initializable {

    @FXML
    static JFXButton updateStatusButton;

    @FXML
    TableView<Project> projectsTableView;

    private ProjectsTableController projectsTableController;
    private ObservableList<String> projectStatus = FXCollections.observableArrayList("", "In Progress", "Complete", "Cancelled");

    @FXML
    protected void newProjectAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProjectFXML.fxml"));
            AnchorPane anchorPane = (AnchorPane) loader.load();
            MainFXMLController.setPane(anchorPane);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    protected void updateProjectStatusAction(ActionEvent event) {

    }

    protected void updateProjectsTable() {
        if (!MainApp.loadingDialog.isVisible()) {
            MainApp.loadingDialog.show();
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            Projects projects = new Projects();
            ObservableList<Project> allProjects = projects.getProjects();
            Platform.runLater(() -> {
                projectsTableView.setItems(allProjects);
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
        MainApp.loadingDialog.show();
        projectsTableController = new ProjectsTableController();
        projectsTableController.tableController(projectsTableView);
        
        
        updateProjectsTable();
        MainApp.loadingDialog.close();
    }

}
