package dev.hideftbanana.netcafejavafxapp.datamodels;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ProductCategoryListItemData {
    @FXML
    private Label productCategoryNameLabel;
    @FXML
    private ImageView productCategoryImageView;
    @FXML
    private HBox productCategoryViewBox;

    public ProductCategoryListItemData() {
        String fxmlPath;

        fxmlPath = "/dev/hideftbanana/netcafejavafxapp/fxml/default_product_category_list_item.fxml";

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        productCategoryNameLabel.setText(name);
    }

    public void setImage(Image image) {
        productCategoryImageView.setImage(image);
    }

    public HBox getBox() {
        return productCategoryViewBox;
    }

}