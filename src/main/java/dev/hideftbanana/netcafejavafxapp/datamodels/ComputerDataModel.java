package dev.hideftbanana.netcafejavafxapp.datamodels;

import dev.hideftbanana.netcafejavafxapp.models.Computer;
import dev.hideftbanana.netcafejavafxapp.models.Room;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateComputerRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ComputerResponse;
import dev.hideftbanana.netcafejavafxapp.services.computerservices.ComputerService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ComputerDataModel {
    private final ObservableList<Computer> computerList = FXCollections.observableArrayList();

    private final ObjectProperty<Computer> currentComputer = new SimpleObjectProperty<>(null);

    public ObjectProperty<Computer> currentComputerProperty() {
        return currentComputer;
    }

    public final Computer getCurrentComputer() {
        return currentComputerProperty().get();
    }

    public final void setCurrentComputer(Computer computer) {
        currentComputerProperty().set(computer);
    }

    public ObservableList<Computer> getComputerList() {
        return computerList;
    }

    private ComputerService computerService;

    public ComputerDataModel() {
        this.computerService = new ComputerService();
    }

    public void loadData() {
        computerService.getAllComputers()
                .thenAcceptAsync(computersResponse -> {
                    ObservableList<Computer> newComputerList = FXCollections.observableArrayList();
                    for (ComputerResponse computerResponse : computersResponse.getComputers()) {
                        Computer computer = new Computer();
                        computer.setId(computerResponse.getId());
                        computer.setConfiguration(computerResponse.getConfiguration());
                        computer.setPricePerHour(computerResponse.getPricePerHour());
                        // You might need to map the room details as well
                        Room room = new Room();
                        room.setId(computerResponse.getRoom().getId());
                        room.setName(computerResponse.getRoom().getName());
                        computer.setRoom(room);
                        newComputerList.add(computer);
                    }
                    Platform.runLater(() -> {
                        computerList.setAll(newComputerList); // Update the UI on the JavaFX Application Thread
                    });
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Handle exceptions if needed
                    return null;
                });
    }

    public void addComputer(CreateComputerRequest createComputerRequest) throws Exception {
        computerService.createComputer(createComputerRequest)
                .thenAccept(response -> {
                    if (response != null && response.equals("Computer created successfully.")) {
                        loadData();
                    } else {
                        throw new RuntimeException("Failed to create computer: " + response);
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to create computer", throwable);
                });
    }

    public void deleteComputer(long computerId) {
        computerService.deleteComputer(computerId)
                .thenAccept(response -> {
                    if (response != null) {
                        loadData(); // Reload data after successful deletion
                    } else {
                        throw new RuntimeException("Failed to delete computer");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to delete computer", throwable);
                });
    }

    public void updateComputer(long computerId, CreateComputerRequest updateRequest) {
        computerService.updateComputer(computerId, updateRequest)
                .thenAccept(response -> {
                    if (response != null && response.equals("Computer updated successfully.")) {
                        loadData(); // Reload data after successful update
                    } else {
                        throw new RuntimeException("Failed to update computer");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to update computer", throwable);
                });
    }
}