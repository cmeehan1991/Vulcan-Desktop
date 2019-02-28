package com.cbmwebdevelopment.main;

import com.cbmwebdevelopment.notifications.Notifications;
import com.cbmwebdevelopment.reporting.ErrorReport;
import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static AnchorPane mainPane;
    public static StackPane parentPane;
    public static ProgressIndicator loadingIndicator;
    public static JFXDialog loadingDialog;
    
    @Override
    public void start(Stage stage) throws Exception {
        boolean isLoggedIn = Boolean.parseBoolean(System.getProperty("LOGGED_IN", "false"));
        try {
            if (isLoggedIn) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainFXML.fxml"));
                BorderPane borderPane = (BorderPane) loader.load();
                MainFXMLController controller = (MainFXMLController) loader.getController();

                mainPane = controller.mainPane;
                parentPane = controller.parentPane;
                loadingIndicator = controller.loadingIndicator;
                loadingDialog = Notifications.loadingIndicator();
                controller.dashboardNavigationAction(new ActionEvent());

                Scene scene = new Scene(borderPane);

                stage.setTitle("JavaFX and Maven");
                stage.setScene(scene);
                stage.show();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserLoginFXML.fxml"));
                StackPane stackPane = (StackPane) loader.load();

                Scene scene = new Scene(stackPane);

                stage.setTitle("Sign In");
                stage.setScene(scene);
                stage.show();
            }

            stage.setOnCloseRequest(evt -> {
                /*Thread.getAllStackTraces().forEach((thread, stackTrace) -> {
                    System.out.println(thread.getContextClassLoader() + " : " + thread.getId());
                    thread.interrupt();
                });*/
                System.out.println("Stop");
                Platform.exit();
            });
        } catch (IOException ex) {
            ErrorReport error = new ErrorReport(ex.getMessage(), ex.getStackTrace());
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
