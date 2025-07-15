package vsas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import vsas.db.DBHelper;
import vsas.utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

public class MainStandardUIController implements Initializable {

    public Label usernameLabel;
    public Label accountTypeLabel;
    public Label uidLabel;
    public Button manageUsersButton;
    public Button statisticsButton;
    public Label manageUsersLabel;
    public Label statisticsLabel;
    public Button addScrollsButton;
    public Button editScrollsButton;
    public Button removeScrollsButton;
    public Button viewScrollsButton;
    public Label addScrollsLabel;
    public Label editScrollsLabel;
    public Label removeScrollsLabel;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public Button logout;

    private final DBHelper dbHelper = DBHelper.getInstance(true);

    private HashMap<String,String> userMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the saved user login session
        String loggedInUser = UserSession.getInstance().getUserId();

        // Retrieve the user's details
        if (!loggedInUser.equals("Guest")) {
            userMap = dbHelper.getUserFromDB(loggedInUser);
            userMap.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

            // Set the username label
            if (userMap.get("fullname") == null) {
                usernameLabel.setText(userMap.get("userid"));
            } else {
                usernameLabel.setText(userMap.get("fullname"));
            }

            // Set the UID label
            uidLabel.setText("UID: " + userMap.get("userid"));

            // Set the account type label
            String accountType = userMap.get("utype");
            if (accountType.equals("admin")) {
                accountTypeLabel.setText("Admin");
                accountTypeLabel.setTextFill(Paint.valueOf("#cc0202"));
            } else if (accountType.equals("normal")) {
                accountTypeLabel.setText("Standard User");
            }
        } else {
            userMap = null;
            usernameLabel.setText("Guest");
            accountTypeLabel.setText("Guest");
            uidLabel.setText("UID: guest");
        }

        // Modify how the UI is displayed based on the user type
        setComponentsInteractable(userMap);
    }

    private void setComponentsInteractable(HashMap<String,String> userMap) {
        if (userMap != null) {
            // Regular/admin user
            String userType = userMap.get("utype");
            if (Objects.equals(userType, "normal")) {
                // make the admin UI Components invisible to normal users
                disableAdminComponents();
            }
        } else {
            // Guest user
            // Make some registered user features 'grayed' out to guest users
            // Guests can only view scrolls not upload/download
            disableAdminComponents();
            disableRegularUserComponents();
        }
    }

    private void disableAdminComponents() {
        manageUsersButton.setVisible(false);
        manageUsersButton.setManaged(false); // Prevents the button from occupying layout space
        statisticsButton.setVisible(false);
        statisticsButton.setManaged(false);
        manageUsersLabel.setVisible(false);
        manageUsersLabel.setManaged(false);
        statisticsLabel.setVisible(false);
        statisticsLabel.setManaged(false);
    }

    private void disableRegularUserComponents() {
        addScrollsButton.setDisable(true);
        removeScrollsButton.setDisable(true);
        editScrollsButton.setDisable(true);
        addScrollsLabel.setDisable(true);
        removeScrollsLabel.setDisable(true);
        editScrollsLabel.setDisable(true);
    }

    public void settingsOnAction(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user-settings.fxml"));
            root = loader.load();
            stage = new Stage();
            stage.setTitle("User Settings");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Failed to load user settings UI");
        }
    }

    public void addScrollOnAction(ActionEvent actionEvent) {
    }

    public void editScrollOnAction(ActionEvent actionEvent) {
    }

    public void removeScrollOnAction(ActionEvent actionEvent) {
    }

    public void viewScrollOnAction(ActionEvent actionEvent) {
    }

    public void logoutOnAction(ActionEvent actionEvent) {
	try {
        // reset the user session
        UserSession.getInstance().setUserId(null);
        UserSession.getInstance().setIsGuest(false);

        // Load the startup UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/startup-ui.fxml"));
        root = loader.load();
        stage = new Stage();
        stage.setTitle("Logged out");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        Stage currentStage = (Stage) logout.getScene().getWindow();
        currentStage.close();
        } catch (Exception e) {
            System.out.println("Failed to load login-guest-ui.fxml");
        }
    }

    public void manageUsersOnAction(ActionEvent actionEvent) {
        try {
            // Load the startup UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage-users.fxml"));
            root = loader.load();
            stage = new Stage();
            stage.setTitle("Manage Users");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Failed to load manage-users.fxml");
        }
    }

    public void scrollStatisticsOnAction(ActionEvent actionEvent) {

    }
}
