package dev.hideftbanana.netcafejavafxapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginController extends BaseController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private void loginUser() throws IOException {
        sceneManager.closeStage();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
