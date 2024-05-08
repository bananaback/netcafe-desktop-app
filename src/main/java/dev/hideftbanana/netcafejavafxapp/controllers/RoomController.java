package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.datamodels.RoomDataModel;
import dev.hideftbanana.netcafejavafxapp.models.Room;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class RoomController extends BaseController implements Initializable {

    private ImageCache imageCache;
    private RoomDataModel roomDataModel;

    @FXML
    private ListView<Room> roomListView;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set the cell factory to display only the room names
        roomListView.setCellFactory(param -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null || room.getName() == null) {
                    setText(null);
                } else {
                    setText(room.getName());
                }
            }
        });
    }

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void setDataModel(RoomDataModel roomDataModel) {
        if (this.roomDataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.roomListView.getStylesheets()
                .add(getClass()
                        .getResource("/dev/hideftbanana/netcafejavafxapp/css/custom_product_category_list_view.css")
                        .toExternalForm());

        this.roomDataModel = roomDataModel;
        this.roomDataModel.loadData();
        this.roomListView.setItems(this.roomDataModel.getRoomList());
    }

}
