package dev.hideftbanana.netcafejavafxapp.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.AppConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class LoginController extends BaseController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private void loginUser() throws IOException {
        sceneManager.setStageSize(AppConfig.MAIN_STAGE_WIDTH, AppConfig.MAIN_STAGE_HEIGHT);
        sceneManager.setMinSize(AppConfig.MAIN_STAGE_MIN_WIDTH, AppConfig.MAIN_STAGE_MIN_HEIGHT);
        sceneManager.switchingScene("/dev/hideftbanana/netcafejavafxapp/fxml/main_app.fxml");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
