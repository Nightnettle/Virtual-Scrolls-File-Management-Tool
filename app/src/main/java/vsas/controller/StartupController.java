package vsas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import vsas.utils.UserSession;

public class StartupController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public Button loginButton;

    public void registerOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register-ui.fxml"));
            root = loader.load();

            // Reference to login controller
            RegisterController registerController = loader.getController();
            registerController.setParentStage((Stage) ((Node) actionEvent.getSource()).getScene().getWindow());

            stage = new Stage();
            stage.setTitle("Register");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Failed to load register-ui.fxml");
        }
    }

    public void loginAsGuestOnAction(ActionEvent actionEvent) {
        try {
            // Set the session to Guest user
            UserSession.getInstance().setIsGuest(true);
            UserSession.getInstance().setUserId("Guest");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-standard-ui.fxml"));
            root = loader.load();
            stage = new Stage();
            stage.setTitle("Login as Guest");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

	        Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            System.out.println("Failed to load login-guest-ui.fxml");
        }
    }

    public void loginOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-ui.fxml"));
            root = loader.load();

            // Reference to login controller
            LoginController loginController = loader.getController();
            loginController.setParentStage((Stage) ((Node) actionEvent.getSource()).getScene().getWindow());
            stage = new Stage();
            stage.setTitle("Login");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Failed to load login-ui.fxml");
            e.printStackTrace();
        }
    }
}
