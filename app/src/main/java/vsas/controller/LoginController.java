package vsas.controller;

import javafx.scene.control.Alert;
import vsas.db.DBHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import vsas.utils.UserSession;

public class LoginController {
    public TextField useridField;
    public PasswordField passwordField;

    private final DBHelper dbHelper = DBHelper.getInstance(true); // true for production

    private Stage parentStage;
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
        System.out.println("Parent stage set to: " + this.parentStage);
    }

    public void loginOnAction(ActionEvent actionEvent) throws Exception {
        String userid = useridField.getText();
        String password = passwordField.getText();

        if (userid.isEmpty() || password.isEmpty()) {
            dbHelper.displayMsgBox(Alert.AlertType.ERROR,"Form Error!", "Please enter both User ID and Password.");
            System.out.println("Enter both user id and password");
            return;
        }

        String storedHashedPassword = dbHelper.getPasswordFromDB(userid);

        // Check if the user ID exists in the database
        boolean userExists = dbHelper.checkUserIdExists(userid);

        if (storedHashedPassword == null || !userExists) {
            dbHelper.displayMsgBox(Alert.AlertType.ERROR, "Login Failed", "Invalid User ID or Password.");
            System.out.println("Invalid user ID or password");
            return;
        }

        // Hash the entered password
        String hashedPassword = DBHelper.getHashString(password);


        if (hashedPassword.equals(storedHashedPassword)) {
            redirectToMainUI();
        } else {
            // Login failed (bad password)
            dbHelper.displayMsgBox(Alert.AlertType.ERROR, "Login Failed", "Invalid User ID or Password.");
        }
    }

    private void redirectToMainUI() throws Exception {

        // Create the new user session based on their userid
        UserSession.getInstance().setUserId(useridField.getText());

        // Load the main UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-standard-ui.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Main Standard UI");
        stage.setScene(new Scene(root));
        stage.show();

        // Close current login window
        Stage currentStage = (Stage) useridField.getScene().getWindow();
        currentStage.close();

        // Close start up window upon successful login
        if (parentStage != null) {
            parentStage.close();
        }
    }
}
