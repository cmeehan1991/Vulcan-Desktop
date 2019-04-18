/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.project;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class ProjectsTableController {

    public void tableController(TableView tableView) {
        TableColumn<Integer, Project> idColumn = new TableColumn<>();
        TableColumn<String, Project> projectColumn = new TableColumn<>("Project");
        TableColumn<String, Project> statusColumn = new TableColumn<>("Status");
        TableColumn<String, Project> deadlineColumn = new TableColumn<>("Deadline");

        // set the column value factories
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("project"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadeline"));

        // Hide the ID column
        idColumn.setVisible(false);

        // Set the column sizes
        projectColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.70));
        statusColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        deadlineColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        statusColumn.setMinWidth(115);
        deadlineColumn.setMinWidth(115);

        tableView.getColumns().setAll(idColumn, projectColumn, statusColumn, deadlineColumn);
    }

    public static class AllProjects {
        private final SimpleIntegerProperty ID;
        private final SimpleStringProperty PROJECT, STATUS, DEADLINE;
        
        public AllProjects() {
            this.ID = new SimpleIntegerProperty();
            this.PROJECT = new SimpleStringProperty();
            this.STATUS = new SimpleStringProperty();
            this.DEADLINE = new SimpleStringProperty();
        }
        
        public AllProjects(Integer id, String project, String status, String deadline) {
            this.ID = new SimpleIntegerProperty(id);
            this.PROJECT = new SimpleStringProperty(project);
            this.STATUS = new SimpleStringProperty(status);
            this.DEADLINE = new SimpleStringProperty(deadline);
        }
        
        public Integer getId(){
            return this.ID.get();
        }
        
        public void setId(Integer id){
            this.ID.set(id);
        }
        
        public String getProject(){
            return this.PROJECT.get();
        }
        
        public void setProject(String val){
            this.PROJECT.set(val);
        }
        
        public String getStatus(){
            return this.STATUS.get();
        }
        
        public void setStatus(String val){
            this.STATUS.set(val);
        }
        
        public String getDeadline(){
            return this.DEADLINE.get();
        }
        
        public void setDeadline(String val){
            this.DEADLINE.set(val);
        }
        
    }
}
