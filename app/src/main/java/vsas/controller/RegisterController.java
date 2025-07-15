package vsas.controller;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import vsas.db.DBHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    public TextField useridField;
    @FXML
    public TextField passwordField;
    @FXML
    public TextField phoneNumberField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField fullNameField;

    private final DBHelper dbHelper = DBHelper.getInstance(true); // true for production

    private Stage parentStage;
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
        System.out.println("Parent stage set to: " + this.parentStage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void registerOnAction(ActionEvent actionEvent) throws Exception {
        // TODO: User input validation
        String userid = useridField.getText();
        String password = passwordField.getText();
        String phonenum = phoneNumberField.getText();
        String email = emailField.getText();
        String fullname = fullNameField.getText();
        String utype = "normal"; // everyone who registers is now a normal user

	    // Check if userid is empty, everything else is allowed to be empty
        if (userid.isEmpty() || password.isEmpty()) {
            dbHelper.displayMsgBox(Alert.AlertType.ERROR, "Form Error!", "Please fill in userid");
            return;
        }

        if (dbHelper.checkUserIdExists(userid)) {
            dbHelper.displayMsgBox(Alert.AlertType.ERROR, "Registration Error!", "User ID already exists.");
            return;
        }

        if (!dbHelper.goodPhonenum(phonenum)) {
            dbHelper.displayMsgBox(Alert.AlertType.ERROR, "Form Error!", "Phone number should not exceed " + DBHelper.MAX_PHONENUM_LEN + " characters.");
            return;
        }

        // Insert user data into the database
        dbHelper.addUser(userid, password, utype, phonenum, email, fullname);

        // Redirect to login page after successful registration
        redirectToLogin();
    }

    private void redirectToLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-ui.fxml"));
        Parent root = loader.load();

        // Reference to login controller
        LoginController loginController = loader.getController();
        // Set the login controller's parent to this stage
        loginController.setParentStage(this.parentStage);

        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
        Stage currentStage = (Stage) useridField.getScene().getWindow();
        currentStage.close();
    }
}
