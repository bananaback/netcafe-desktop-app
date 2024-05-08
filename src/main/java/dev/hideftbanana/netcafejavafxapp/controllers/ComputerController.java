package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.datamodels.ComputerDataModel;
import dev.hideftbanana.netcafejavafxapp.models.Computer;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateComputerRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductCategoryResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.RoomResponse;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import dev.hideftbanana.netcafejavafxapp.services.roomservices.RoomService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ComputerController extends BaseController implements Initializable {

    private ImageCache imageCache;
    private ComputerDataModel computerDataModel;

    @FXML
    private ListView<Computer> computerListView;
    @FXML
    private Button addComputerButton;
    // update v box
    @FXML
    private TextField configurationTextField;
    @FXML
    private TextField pricePerHourTextField;
    @FXML
    private ComboBox<String> roomComboBox;
    @FXML
    private Button saveUpdateButton;
    @FXML
    private Button cancelUpdateButton;

    // overview v box
    @FXML
    private Label configurationLabel;
    @FXML
    private Label pricePerHourLabel;
    @FXML
    private Label roomLabel;
    @FXML
    private Button updateComputerButton;
    @FXML
    private Button deleteComputerButton;

    @FXML
    private VBox updateVBox;
    @FXML
    private VBox overviewVBox;

    @FXML
    private void addComputer() {
        // Create a new request object with default data
        CreateComputerRequest request = new CreateComputerRequest();
        request.setConfiguration("Default Configuration");
        request.setPricePerHour(0.0f); // Default price per hour
        request.setRoomId(4L); // Default room ID, adjust as needed

        // Create a task to execute the addComputer method asynchronously
        Task<Void> addComputerTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Call the addComputer method of the data model
                    computerDataModel.addComputer(request);
                } catch (Exception e) {
                    // If an exception occurs, throw it to be handled by the exception handler
                    throw e;
                }
                return null;
            }
        };

        // Set up an exception handler for the task
        addComputerTask.setOnFailed(event -> {
            Throwable exception = addComputerTask.getException();
            // Handle the exception appropriately, such as displaying an error message to
            // the user
            System.err.println("Failed to add computer: " + exception.getMessage());
            // Handle the exception by showing an alert to the user
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to add computer");
                alert.setContentText(exception.getMessage());
                alert.showAndWait();
            });
        });

        // Set up a completion handler for the task
        addComputerTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                // Update the UI as needed, e.g., refresh computer list
                // For example:
                computerListView.setItems(computerDataModel.getComputerList());
                computerListView.refresh();
                overviewVBox.setVisible(true);
                updateVBox.setVisible(false);
            });
        });

        // Start the task in a background thread
        new Thread(addComputerTask).start();
    }

    @FXML
    private void saveComputer() {
        Computer selectedComputer = computerDataModel.getCurrentComputer();
        if (selectedComputer != null && configurationTextField.getText() != null
                && pricePerHourTextField.getText() != null && roomComboBox.getValue() != null) {

            // Get updated data from the UI
            String updatedConfiguration = configurationTextField.getText();
            float updatedPricePerHour = Float.parseFloat(pricePerHourTextField.getText());
            String updatedRoomName = roomComboBox.getValue();
            Long roomId = null;
            for (RoomResponse room : roomResponses) {
                if (room.getName().equals(updatedRoomName)) {
                    roomId = room.getId();
                    break;
                }
            }

            // Create a request object with the updated data
            CreateComputerRequest updateRequest = new CreateComputerRequest();
            updateRequest.setConfiguration(updatedConfiguration);
            updateRequest.setPricePerHour(updatedPricePerHour);
            updateRequest.setRoomId(roomId);

            // Create a task to execute the updateComputer method asynchronously
            Task<Void> updateComputerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the updateComputer method of the data model
                        computerDataModel.updateComputer(selectedComputer.getId(), updateRequest);
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            updateComputerTask.setOnFailed(event -> {
                Throwable exception = updateComputerTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to update computer: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to update computer");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            updateComputerTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    // Update the UI as needed, e.g., refresh computer list, switch to overview
                    // view,
                    // etc.
                    // For example:
                    computerListView.setItems(computerDataModel.getComputerList());
                    computerListView.refresh();
                    overviewVBox.setVisible(true);
                    updateVBox.setVisible(false);
                });
            });

            new Thread(updateComputerTask).start();
        } else {
            // No computer is selected or updated data is missing, handle this case as
            // needed
            // For example, show a message to the user indicating that they need to select a
            // computer
            // and provide updated data to update
        }
    }

    @FXML
    private void deleteComputer() {
        Computer selectedComputer = computerDataModel.getCurrentComputer();
        if (selectedComputer != null) {
            Task<Void> deleteComputerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the deleteComputer method of the data model
                        computerDataModel.deleteComputer(selectedComputer.getId());
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            deleteComputerTask.setOnFailed(event -> {
                Throwable exception = deleteComputerTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to delete computer: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to delete computer");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            deleteComputerTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    // Update the UI as needed, e.g., refresh computer list
                    // For example:
                    computerListView.setItems(computerDataModel.getComputerList());
                    computerListView.refresh();
                    overviewVBox.setVisible(true);
                    updateVBox.setVisible(false);
                });
            });

            // Start the task in a background thread
            new Thread(deleteComputerTask).start();
        } else {
            // No computer is selected, handle this case as needed
        }
    }

    @FXML
    private void switchToOverview() {
        overviewVBox.setVisible(true);
        updateVBox.setVisible(false);
    }

    @FXML
    private void switchToUpdate() {
        overviewVBox.setVisible(false);
        updateVBox.setVisible(true);

        configurationTextField.setText(computerDataModel.getCurrentComputer().getConfiguration());
        pricePerHourTextField.setText(computerDataModel.getCurrentComputer().getPricePerHour() + "");
        roomComboBox.valueProperty().setValue(computerDataModel.getCurrentComputer().getRoom().getName());

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    private RoomService roomService;
    private List<RoomResponse> roomResponses;

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void setDataModel(ComputerDataModel computerDataModel) {
        if (this.computerDataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.computerDataModel = computerDataModel;
        this.computerDataModel.loadData();

        roomService = new RoomService();

        computerListView.setItems(this.computerDataModel.getComputerList());

        computerListView.setCellFactory(param -> new ListCell<Computer>() {
            @Override
            protected void updateItem(Computer computer, boolean empty) {
                super.updateItem(computer, empty);
                if (empty || computer == null) {
                    Platform.runLater(() -> {
                        setText(null);
                    });
                } else {
                    Platform.runLater(() -> {
                        setText("Computer " + computer.getId());
                    });
                }
            }
        });

        computerListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> this.computerDataModel.setCurrentComputer(newSelection));

        this.computerDataModel.currentComputerProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {

            }
            if (newValue == null) {
                configurationTextField.textProperty().setValue(null);
                pricePerHourTextField.textProperty().setValue(null);
            } else {
                configurationLabel.setText(computerDataModel.getCurrentComputer().getConfiguration());
                pricePerHourLabel.setText(computerDataModel.getCurrentComputer().getPricePerHour() + "");
                roomLabel.setText(computerDataModel.getCurrentComputer().getRoom().getName());

                overviewVBox.setVisible(true);
                updateVBox.setVisible(false);
            }

        });

        loadRoomComboBox();
        roomComboBox.setVisibleRowCount(5);
    }

    private void loadRoomComboBox() {
        roomService.getAllRooms()
                .thenAcceptAsync(roomsResponse -> {
                    roomResponses = roomsResponse.getRooms();
                    Platform.runLater(() -> {
                        ObservableList<String> roomNames = FXCollections.observableArrayList();
                        for (RoomResponse roomResponse : roomResponses) {
                            roomNames.add(roomResponse.getName());
                        }
                        roomComboBox.setItems(roomNames);
                    });
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Handle exceptions if needed
                    return null;
                });
    }

}
