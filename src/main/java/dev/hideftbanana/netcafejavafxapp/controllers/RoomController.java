package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.datamodels.RoomDataModel;
import dev.hideftbanana.netcafejavafxapp.models.Room;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateRoomRequest;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RoomController extends BaseController implements Initializable {

    private ImageCache imageCache;
    private RoomDataModel roomDataModel;

    @FXML
    private Label roomNameLabelPreview;

    @FXML
    private ListView<Room> roomListView;

    @FXML
    private VBox updateVBox;
    @FXML
    private VBox overviewVBox;

    @FXML
    private TextField roomNameTextFieldUpdate;

    @FXML
    private void switchToOverview() {
        overviewVBox.setVisible(true);
        updateVBox.setVisible(false);
    }

    @FXML
    private void switchToUpdateView() {
        overviewVBox.setVisible(false);
        updateVBox.setVisible(true);

        roomNameTextFieldUpdate.setText(roomDataModel.getCurrentRoom().getName());
    }

    @FXML
    private void saveRoom() {
        Room selectedRoom = roomDataModel.getCurrentRoom();
        if (selectedRoom != null) {
            // Get updated data from the UI
            String updatedRoomName = roomNameTextFieldUpdate.getText();

            Task<Void> saveRoomTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the updateRoom method of the data model
                        CreateRoomRequest createRoomRequest = new CreateRoomRequest();
                        createRoomRequest.setName(updatedRoomName);
                        roomDataModel.updateRoom(selectedRoom.getId(), createRoomRequest);
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            saveRoomTask.setOnFailed(event -> {
                Throwable exception = saveRoomTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to save room: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to save room");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            saveRoomTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    roomListView.setItems(roomDataModel.getRoomList());
                    roomListView.refresh();
                    overviewVBox.setVisible(true);
                    updateVBox.setVisible(false);
                    roomNameLabelPreview.setText("");
                });
            });

            new Thread(saveRoomTask).start();
        } else {
            // No room is selected, handle this case as needed
            // For example, show a message to the user indicating that they need to select a
            // room to save
        }
    }

    @FXML
    private void deleteRoom() {
        Room selectedRoom = roomDataModel.getCurrentRoom();
        if (selectedRoom != null) {
            Task<Void> deleteRoomTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the deleteRoom method of the data model
                        roomDataModel.deleteRoom(selectedRoom.getId());
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            deleteRoomTask.setOnFailed(event -> {
                Throwable exception = deleteRoomTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to delete room: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to delete room");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            deleteRoomTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    roomListView.setItems(roomDataModel.getRoomList());
                    roomListView.refresh();
                    overviewVBox.setVisible(true);
                    updateVBox.setVisible(false);
                    roomNameLabelPreview.setText("");
                });
            });

            new Thread(deleteRoomTask).start();
        } else {
            // No room is selected, handle this case as needed
            // For example, show a message to the user indicating that they need to select a
            // room to delete
        }
    }

    @FXML
    private void addNewRoom() {

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void setDataModel(RoomDataModel roomDataModel) {
        if (this.roomDataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.roomDataModel = roomDataModel;
        this.roomDataModel.loadData();
        this.roomListView.setItems(this.roomDataModel.getRoomList());

        // Set the cell factory to display only the room names
        roomListView.setCellFactory(param -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null || room.getName() == null) {
                    Platform.runLater(() -> {
                        setText(null);
                    });
                } else {
                    Platform.runLater(() -> {
                        setText(room.getName());
                    });
                }
            }
        });

        roomListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> roomDataModel.setCurrentRoom(newSelection));

        roomDataModel.currentRoomProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {

            }
            if (newValue == null) {
                roomNameTextFieldUpdate.textProperty().setValue(null);
            } else {
                roomNameLabelPreview
                        .setText(roomDataModel.getCurrentRoom().getName());

                overviewVBox.setVisible(true);
                updateVBox.setVisible(false);
            }

        });
    }

}
