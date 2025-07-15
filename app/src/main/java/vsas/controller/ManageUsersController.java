package vsas.controller;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.ArrayList;
import vsas.db.DBHelper;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class ManageUsersController implements Initializable {
	@FXML
	private ListView userList;

	private ObservableList<String> items;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		DBHelper dbHelper = DBHelper.getInstance(true);
		ArrayList<String> users = dbHelper.getAllUsers();
		items = FXCollections.observableArrayList(users);
		userList.setItems(items);

	}
}
