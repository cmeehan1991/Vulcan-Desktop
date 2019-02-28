/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.login;

import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.MainFXMLController;
import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.user.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class UserLoginFXMLController implements Initializable {

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    AnchorPane signInPane;

    @FXML
    JFXTextField usernameTextField;

    @FXML
    JFXPasswordField passwordTextField;

    @FXML
    Label errorLabel;

    @FXML
    protected void signInAction(ActionEvent event) {
        startProgress();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();

            User user = new User();
            boolean signedIn = user.signIn(username, password);
            Platform.runLater(() -> {
                endProgress();
                if (signedIn) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainFXML.fxml"));
                        BorderPane borderPane = (BorderPane) loader.load();

                        MainFXMLController controller = (MainFXMLController) loader.getController();
                        MainApp.mainPane = controller.mainPane;
                        MainApp.parentPane = controller.parentPane;
                        MainApp.loadingIndicator = controller.loadingIndicator;
                        MainApp.loadingDialog = Notifications.loadingIndicator();
                        
                        controller.dashboardNavigationAction(new ActionEvent());

                        Stage currentStage = (Stage) errorLabel.getScene().getWindow();
                        Scene scene = new Scene(borderPane);
                        currentStage.setScene(scene);
                        currentStage.setTitle("Meehan Wood Working");
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                } else {
                    errorLabel.setText("Invalid username & password combination. Please try again.");
                }
            });
            executor.shutdown();

        });
    }

    private void startProgress() {
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    private void endProgress() {
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
