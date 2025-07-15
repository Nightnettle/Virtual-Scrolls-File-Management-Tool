package vsas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import vsas.db.DBHelper;
import vsas.utils.UserSession;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class UpdatePersonalDetailsController implements Initializable {
    public TextField useridField;
    public TextField fullNameField;
    public TextField emailField;
    public TextField phoneNumberField;

    private final DBHelper dbHelper = DBHelper.getInstance(true);

    private HashMap<String,String> userMap;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String loggedInUser = UserSession.getInstance().getUserId();

        userMap = dbHelper.getUserFromDB(loggedInUser);

        // Set the prompt labels for the existing user details if not null
        if(userMap.get("userid") != null)
            useridField.setPromptText(userMap.get("userid"));
        if(userMap.get("fullname") != null)
            fullNameField.setPromptText(userMap.get("fullname"));
        if(userMap.get("email") != null)
            emailField.setPromptText(userMap.get("email"));
        if(userMap.get("phonenum") != null)
            phoneNumberField.setPromptText(userMap.get("phonenum"));
    }

    public void updateOnAction(ActionEvent actionEvent) {
        // TODO:
    }

    public void cancelOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
