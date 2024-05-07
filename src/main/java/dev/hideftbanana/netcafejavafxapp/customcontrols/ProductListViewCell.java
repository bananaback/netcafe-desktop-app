package dev.hideftbanana.netcafejavafxapp.customcontrols;

import java.util.concurrent.CompletableFuture;

import dev.hideftbanana.netcafejavafxapp.datamodels.ProductListItemData;
import dev.hideftbanana.netcafejavafxapp.models.Product;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.application.Platform;
import javafx.scene.control.ListCell;
import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;

public class ProductListViewCell extends ListCell<Product> {
    private ImageCache imageCache;

    public ProductListViewCell(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    @Override
    public void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);
        if (product != null && !empty) {
            ProductListItemData data = new ProductListItemData();
            data.setName(product.getName());
            // Load image asynchronously
            loadImageAsync(product.getImageLink(), data);
            setGraphic(data.getBox());
            // Apply selected styling if the cell is selected
            if (isSelected()) {
                setStyle("-fx-background-color: #123456;");
            } else {
                setStyle(""); // Clear selected styling
            }

        } else {
            setGraphic(null);
            setText(null);
            setStyle(""); // Clear any previous styling
        }
    }

    private void loadImageAsync(String imageName, ProductListItemData data) {
        CompletableFuture.supplyAsync(() -> {
            byte[] imageData = imageCache.get(imageName); // Blocking call
            return new Image(new ByteArrayInputStream(imageData));
        }).thenAcceptAsync(image -> {
            Platform.runLater(() -> {
                if (image != null) {
                    data.setImage(image);
                } else {
                    // Set default image or handle error
                }
            });
        });
    }
}
