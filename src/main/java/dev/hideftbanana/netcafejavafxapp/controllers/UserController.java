package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.models.request.RegisterRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.UserInfoResponse;
import dev.hideftbanana.netcafejavafxapp.services.userservices.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UserController extends BaseController implements Initializable {

    @FXML
    private ListView<UserInfoResponse> userListView;

    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label identityNumberLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private TextField topUpTextField;
    private UserInfoResponse selectedUser;
    @FXML
    private VBox userTopUpVBox;
    @FXML
    private VBox userRegistrationVBox;

    // These text fields for user registration
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField identityNumberTextField;
    @FXML
    private TextField balanceTextField;

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateRegistrationFields() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String email = emailTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String identityNumber = identityNumberTextField.getText();
        String balance = balanceTextField.getText();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()
                || phoneNumber.isEmpty() || identityNumber.isEmpty() || balance.isEmpty()) {
            showErrorMessage("All fields are required.");
            return false;
        }
        // Validate balance
        try {
            double balanceAmount = Double.parseDouble(balance);
            if (balanceAmount <= 0) {
                showErrorMessage("Balance must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Balance must be a valid number.");
            return false;
        }

        // You can add more specific validations for each field as needed

        return true;
    }

    @FXML
    private void registerUser() {
        if (validateRegistrationFields()) {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(usernameTextField.getText());
            request.setPassword(passwordTextField.getText());
            request.setConfirmPassword(passwordTextField.getText());
            request.setEmail(emailTextField.getText());
            request.setPhoneNumber(phoneNumberTextField.getText());
            request.setIdentityNumber(identityNumberTextField.getText());
            request.setBalance(Double.parseDouble(balanceTextField.getText()));

            // Construct the API URL
            String apiUrl = "http://localhost:8080/api/auth/register";

            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // Create a POST request
                HttpRequest registerRequest = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
                        .build();

                HttpClient httpClient = HttpClient.newHttpClient();

                // Send the POST request asynchronously
                httpClient.sendAsync(registerRequest, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            Platform.runLater(() -> {
                                if (response.statusCode() == 200) {
                                    // Registration successful
                                    showSuccessMessage("User registered successfully.");
                                    // Clear input fields
                                    clearRegistrationFields();
                                    loadUsers();
                                } else {
                                    // Registration failed
                                    showErrorMessage("Failed to register user. Please try again later.");
                                    loadUsers();
                                }
                            });
                        })
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                // Exception occurred
                                showErrorMessage("An error occurred: " + ex.getMessage());
                                loadUsers();
                            });
                            return null;
                        });
            } catch (JsonProcessingException e) {
                showErrorMessage("Error processing JSON data: " + e.getMessage());
                loadUsers();
            }
        }
    }

    private void clearRegistrationFields() {
        usernameTextField.clear();
        passwordTextField.clear();
        emailTextField.clear();
        phoneNumberTextField.clear();
        identityNumberTextField.clear();
        balanceTextField.clear();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void sendEmail() {
        if (selectedUser != null) {
            String userEmail = selectedUser.getEmail();
            String apiUrl = "http://localhost:8080/api/email/send-promotion-email?userEmail=" + userEmail;

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            Platform.runLater(() -> {
                                showSuccessMessage("Promotional email sent successfully to: " + userEmail);

                            });
                        } else {
                            Platform.runLater(() -> {
                                showErrorMessage("Failed to send promotional email to: " + userEmail);

                            });
                        }
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            showErrorMessage("An error occurred: " + ex.getMessage());

                        });
                        return null;
                    });
        } else {
            Platform.runLater(() -> {
                showErrorMessage("Please select a user to send the promotional email.");

            });
        }
    }

    @FXML
    private void switchToAdd() {
        userTopUpVBox.setVisible(false);
        userRegistrationVBox.setVisible(true);
    }

    @FXML
    private void switchToTopup() {
        userTopUpVBox.setVisible(true);
        userRegistrationVBox.setVisible(false);
    }

    @FXML
    private void topupUser() {
        if (selectedUser != null) {
            String topUpAmountText = topUpTextField.getText();
            if (isValidTopUpAmount(topUpAmountText)) {
                try {
                    float topUpAmount = Float.parseFloat(topUpAmountText);
                    selectedUser.setBalance(0.0 + topUpAmount);
                    userService.updateUserInfo(selectedUser.getId(), selectedUser).thenAccept(updatedUserInfo -> {
                        loadUsers();
                        topUpTextField.clear();
                        Platform.runLater(() -> {
                            usernameLabel.setText("");
                            emailLabel.setText("");
                            phoneNumberLabel.setText("");
                            identityNumberLabel.setText("");
                            balanceLabel.setText(String.valueOf(""));
                        });
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        // Handle exception
                        return null;
                    });
                } catch (NumberFormatException ex) {
                    // Handle invalid top-up amount format
                }
            } else {
                // Handle invalid top-up amount
            }
        } else {
            // Handle no user selected
        }
    }

    private boolean isValidTopUpAmount(String amountText) {
        try {
            float amount = Float.parseFloat(amountText);
            return amount > 0;
        } catch (NumberFormatException ex) {
            return false; // Invalid format
        }
    }

    private UserService userService;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.userService = new UserService();
        loadUsers();
        setupListViewListener();
    }

    private void loadUsers() {
        // Clear the list view first
        userListView.getItems().clear();

        userService.getAllUserInfo().thenAccept(users -> {
            ObservableList<UserInfoResponse> userList = FXCollections.observableArrayList(users);
            userListView.setItems(userList);
            userListView.setCellFactory(userCell -> new UserListCell());
        }).exceptionally(ex -> {
            ex.printStackTrace();
            // Handle exception
            return null;
        });
    }

    private void setupListViewListener() {
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switchToTopup();
                renderUserInfo(newValue);
                selectedUser = newValue;

            }
        });
    }

    private void renderUserInfo(UserInfoResponse userInfo) {
        Platform.runLater(() -> {
            usernameLabel.setText(userInfo.getUsername());
            emailLabel.setText(userInfo.getEmail());
            phoneNumberLabel.setText(userInfo.getPhoneNumber());
            identityNumberLabel.setText(userInfo.getIdentityNumber());
            balanceLabel.setText(String.valueOf(userInfo.getBalance()));
        });
    }

}
