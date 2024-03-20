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
    private VBox leftPanelVBox;

    @FXML
    private VBox rightPanelVBox;

    @FXML
    private HBox containerHBox;

    @FXML
    private Label smallTitle;

    @FXML
    private StackPane leftPanelPane;

    @FXML
    private StackPane rightPanelPane;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label smallWelcomeLabel;

    @FXML
    private Button loginButton;

    @FXML
    private void switchToSecondary() throws IOException {

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        double halfWidth = AppConfig.LOGIN_STAGE_WIDTH / 2.0;
        leftPanelVBox.setPrefWidth(halfWidth);
        rightPanelVBox.setPrefWidth(halfWidth);

        leftPanelPane.setPrefWidth(halfWidth);
        leftPanelPane.setPrefHeight(AppConfig.LOGIN_STAGE_HEIGHT);
        leftPanelPane.setLayoutX(0);
        leftPanelPane.setLayoutY(0);

        rightPanelPane.setPrefWidth(halfWidth);
        rightPanelPane.setPrefHeight(AppConfig.LOGIN_STAGE_HEIGHT);
        rightPanelPane.setLayoutX(halfWidth);
        rightPanelPane.setLayoutY(0);

        smallTitle.setTranslateX(0);
        smallTitle.setTranslateY(-(AppConfig.LOGIN_STAGE_HEIGHT / 2 - 145));

        phoneNumberLabel.setTranslateX(-100);
        phoneNumberLabel.setTranslateY(-80);

        passwordLabel.setTranslateX(-115);
        passwordLabel.setTranslateY(-10);

        phoneNumberField.setPrefWidth(280);
        phoneNumberField.setMaxWidth(280);
        phoneNumberField.setPrefHeight(35);
        phoneNumberField.setTranslateX(0);
        phoneNumberField.setTranslateY(-50);

        passwordField.setPrefWidth(280);
        passwordField.setMaxWidth(280);
        passwordField.setPrefHeight(35);
        passwordField.setTranslateX(0);
        passwordField.setTranslateY(20);

        loginButton.setTranslateX(0);
        loginButton.setTranslateY(80);

        welcomeLabel.setTranslateX(0);
        welcomeLabel.setTranslateY(-30);

        smallWelcomeLabel.setTranslateX(-100);
        smallWelcomeLabel.setTranslateY(10);
    }

}
