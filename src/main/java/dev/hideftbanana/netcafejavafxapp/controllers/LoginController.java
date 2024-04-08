package dev.hideftbanana.netcafejavafxapp.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.AppConfig;
import dev.hideftbanana.netcafejavafxapp.TokenManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class LoginController extends BaseController implements Initializable {

    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label loginValidationLabel;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private void loginUser() throws IOException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        // Show spinning animation
        progressIndicator.setVisible(true);
        loginValidationLabel.setVisible(false);

        // Introduce a delay of 1 second
        PauseTransition delay = new PauseTransition(Duration.millis(1000));
        delay.setOnFinished(event -> {
            // Create JSON payload
            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            // Create HttpClient
            HttpClient httpClient = HttpClient.newHttpClient();

            // Create HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(getUri())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .build();

            // Send request and handle response
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        // Hide spinning animation
                        progressIndicator.setVisible(false);

                        if (response.statusCode() == 200) {
                            // Parse JSON response manually
                            String responseBody = response.body();
                            String accessToken = parseToken(responseBody, "accessToken");
                            String refreshToken = parseToken(responseBody, "refreshToken");

                            TokenManager.setAccessToken(accessToken);
                            TokenManager.setRefreshToken(refreshToken);

                            Platform.runLater(() -> {
                                loginValidationLabel.setText("Login successful.");
                                loginValidationLabel.setVisible(true);
                                // delay here 1 sec before
                                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                                pause.setOnFinished(e -> {
                                    sceneManager.setStageSize(AppConfig.MAIN_STAGE_WIDTH, AppConfig.MAIN_STAGE_HEIGHT);
                                    sceneManager.setMinSize(AppConfig.MAIN_STAGE_MIN_WIDTH,
                                            AppConfig.MAIN_STAGE_MIN_HEIGHT);
                                    sceneManager
                                            .switchingScene("/dev/hideftbanana/netcafejavafxapp/fxml/main_app.fxml");
                                });
                                pause.play();
                            });
                        } else {
                            Platform.runLater(() -> {
                                loginValidationLabel.setText("Login failed.");
                                loginValidationLabel.setVisible(true);
                            });
                        }
                    })
                    .exceptionally(e -> {
                        // Hide spinning animation
                        progressIndicator.setVisible(false);

                        Platform.runLater(() -> {
                            loginValidationLabel.setText("Error connecting to server.");
                            loginValidationLabel.setVisible(true);
                        });
                        return null;
                    });
        });
        delay.play();
    }

    private String parseToken(String responseBody, String tokenName) {
        int startIndex = responseBody.indexOf(tokenName) + tokenName.length() + 3;
        int endIndex = responseBody.indexOf('"', startIndex);
        return responseBody.substring(startIndex, endIndex);
    }

    // Get URI object from the URL string
    private URI getUri() {
        try {
            return new URI("http://localhost:8080/api/auth/login");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
