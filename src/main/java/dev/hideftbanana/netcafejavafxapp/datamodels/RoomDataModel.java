package dev.hideftbanana.netcafejavafxapp.datamodels;

import dev.hideftbanana.netcafejavafxapp.models.Room;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateRoomRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.RoomResponse;
import dev.hideftbanana.netcafejavafxapp.services.roomservices.RoomService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RoomDataModel {
    private final ObservableList<Room> roomList = FXCollections.observableArrayList();
    private final ObjectProperty<Room> currentRoom = new SimpleObjectProperty<>(null);
    private final RoomService roomService;

    public RoomDataModel() {
        this.roomService = new RoomService();
    }

    public ObjectProperty<Room> currentRoomProperty() {
        return currentRoom;
    }

    public final Room getCurrentRoom() {
        return currentRoomProperty().get();
    }

    public final void setCurrentRoom(Room room) {
        currentRoomProperty().set(room);
    }

    public ObservableList<Room> getRoomList() {
        return roomList;
    }

    public void loadData() {
        roomService.getAllRooms()
                .thenAcceptAsync(roomsResponse -> {
                    ObservableList<Room> newRoomList = FXCollections.observableArrayList();
                    for (RoomResponse r : roomsResponse.getRooms()) {
                        Room room = new Room();
                        room.setId(r.getId());
                        room.setName(r.getName());
                        newRoomList.add(room);
                    }
                    roomList.setAll(newRoomList); // Update the UI on the JavaFX Application Thread
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Handle exceptions if needed
                    return null;
                });
    }

    public void addRoom(CreateRoomRequest createRoomRequest) {
        roomService.addRoom(createRoomRequest)
                .thenAccept(response -> {
                    if (response != null && response.equals("Room added successfully.")) {
                        loadData(); // Reload data after successful addition
                    } else {
                        throw new RuntimeException("Failed to add room");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to add room", throwable);
                });
    }

    public void deleteRoom(long roomId) {
        roomService.deleteRoom(roomId)
                .thenAccept(response -> {
                    if (response != null && response.equals("Room deleted successfully.")) {
                        loadData(); // Reload data after successful deletion
                    } else {
                        throw new RuntimeException("Failed to delete room");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to delete room", throwable);
                });
    }

    public void updateRoom(long roomId, CreateRoomRequest updateRequest) {
        roomService.updateRoom(roomId, updateRequest)
                .thenAccept(response -> {
                    if (response != null && response.equals("Room updated successfully.")) {
                        loadData(); // Reload data after successful update
                    } else {
                        throw new RuntimeException("Failed to update room");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to update room", throwable);
                });
    }
}
