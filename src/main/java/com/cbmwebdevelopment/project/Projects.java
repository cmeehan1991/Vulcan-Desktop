/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.project;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.project.ProjectMaterialsTableController.ProjectMaterials;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Projects {
    
    public ObservableList<Project> getProjects() {
        ObservableList<Project> projects = FXCollections.observableArrayList();

        return projects;
    }

    /**
     *
     * @param status
     * @return true if status is updated
     */
    public boolean setStatus(String status) {
        boolean setStatus = false;
        String sql = "UPDATE PROJECTS SET STATUS = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            int result = ps.executeUpdate();

            setStatus = result >= 1;

            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return setStatus;
    }

    /**
     *
     * @param project
     * @return
     */
    public String saveProject(Project project) {
        String projectId = project.getProjectId();

        String sql = "INSERT INTO projects(PROJECT_ID, PROJECT_NAME, CLIENT_ID, PROJECT_TYPE, START_DATE, DEADLINE, PROJECT_DESCRIPTION, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE PROJECT_NAME = ?, CLIENT_ID = ?, PROJECT_TYPE = ?, START_DATE = ?, DEADLINE = ?, PROJECT_DESCRIPTION = ?, PRIMARY_ADDRESS = ?, SECONDARY_ADDRESS = ?, CITY = ?, STATE = ?, POSTAL_CODE = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, project.getProjectId());
            ps.setString(2, project.getTitle());
            ps.setString(3, project.getCustomer());
            ps.setString(4, project.getType());
            ps.setString(5, project.getStartDate());
            ps.setString(6, project.getDeadline());
            ps.setString(7, project.getDescription());
            ps.setString(8, project.getPrimaryAddress());
            ps.setString(9, project.getSecondaryAddress());
            ps.setString(10, project.getCity());
            ps.setString(11, project.getState());
            ps.setString(12, project.getPostalCode());
            ps.setString(13, project.getTitle());
            ps.setString(14, project.getCustomer());
            ps.setString(15, project.getType());
            ps.setString(16, project.getStartDate());
            ps.setString(17, project.getDeadline());
            ps.setString(18, project.getDescription());
            ps.setString(19, project.getPrimaryAddress());
            ps.setString(20, project.getSecondaryAddress());
            ps.setString(21, project.getCity());
            ps.setString(22, project.getState());
            ps.setString(23, project.getPostalCode());

            int rs = ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (rs >= 1 && project.getMaterials().isEmpty()) {
                if (project.getProjectId().isEmpty()) {
                    projectId = String.valueOf(generatedKeys.getInt(1));
                }
            } else if (rs >= 1 && !project.getMaterials().isEmpty()) {
                if (saveProjectMaterials(project.getMaterials())) {
                    projectId = project.getProjectId();
                }else{
                    Notifications.snackbarNotification("Failed to save.");
                }
            } else {
                Notifications.snackbarNotification("Failed to save.");
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return projectId;
    }

    /**
     * 
     * @param materials
     * @return 
     */
    public boolean saveProjectMaterials(ObservableList<ProjectMaterials> materials) {
        boolean saved = false;
        String sql = "INSERT INTO project_materials(material_id, project_id, item, quantity, description, item_price) VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE item = ?, quantity = ?, description = ?, item_price = ?";
        try {
            Connection conn = new DBConnection().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Notifications.snackbarNotification("Failed to Save.");
            System.err.println(ex.getMessage());
        }
        return saved;
    }
}
