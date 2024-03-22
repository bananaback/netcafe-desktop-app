package dev.hideftbanana.netcafejavafxapp.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

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
