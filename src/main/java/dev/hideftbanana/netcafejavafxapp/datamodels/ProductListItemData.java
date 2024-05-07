package dev.hideftbanana.netcafejavafxapp.datamodels;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ProductListItemData {
    @FXML
    private Label productNameLabel;
    @FXML
    private ImageView productImageView;
    @FXML
    private HBox productViewBox;

    public ProductListItemData() {
        String fxmlPath;

        fxmlPath = "/dev/hideftbanana/netcafejavafxapp/fxml/default_product_list_item.fxml";

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        productNameLabel.setText(name);
    }

    public void setImage(Image image) {
        productImageView.setImage(image);
    }

    public HBox getBox() {
        return productViewBox;
    }

}
