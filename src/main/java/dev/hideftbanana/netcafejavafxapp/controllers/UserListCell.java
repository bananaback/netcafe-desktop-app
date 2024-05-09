package dev.hideftbanana.netcafejavafxapp.controllers;

import dev.hideftbanana.netcafejavafxapp.models.responses.UserInfoResponse;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class UserListCell extends ListCell<UserInfoResponse> {

    @Override
    protected void updateItem(UserInfoResponse userInfo, boolean empty) {
        super.updateItem(userInfo, empty);

        if (empty || userInfo == null) {
            Platform.runLater(() -> {
                setText(null);
                setGraphic(null);
            });

        } else {
            Platform.runLater(() -> {
                Label nameLabel = new Label(userInfo.getUsername());
                HBox hbox = new HBox(nameLabel);
                setGraphic(hbox);
            });

        }
    }
}