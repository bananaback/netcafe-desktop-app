package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.models.responses.UserInfoResponse;
import dev.hideftbanana.netcafejavafxapp.services.userservices.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
                    selectedUser.setBalance(selectedUser.getBalance() + topUpAmount);
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
