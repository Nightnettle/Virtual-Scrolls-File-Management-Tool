package vsas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserSettingsController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void updateDetailsOnAction(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personal-details-ui.fxml"));
            root = loader.load();
            stage = new Stage();
            stage.setTitle("Update Personal Details");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Failed to load personal-details-ui.fxml");
        }
    }

    public void changePasswordOnAction(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change-password-ui.fxml"));
            root = loader.load();
            stage = new Stage();
            stage.setTitle("Change Password");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Failed to load change-password-ui.fxml");
        }
    }
}
