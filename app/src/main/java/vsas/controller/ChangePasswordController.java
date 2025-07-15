package vsas.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangePasswordController {
    public TextField currentPasswordField;
    public TextField newPasswordField;

    public void changePasswordOnAction(ActionEvent actionEvent) {
        // TODO:
    }

    public void cancelOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
