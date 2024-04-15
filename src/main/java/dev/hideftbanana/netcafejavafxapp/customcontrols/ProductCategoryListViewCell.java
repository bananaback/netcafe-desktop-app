package dev.hideftbanana.netcafejavafxapp.customcontrols;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import dev.hideftbanana.netcafejavafxapp.datamodels.ProductCategoryListItemData;
import dev.hideftbanana.netcafejavafxapp.models.ProductCategory;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.application.Platform;
import javafx.scene.control.ListCell;
import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;

public class ProductCategoryListViewCell extends ListCell<ProductCategory> {
    private ImageCache imageCache;

    public ProductCategoryListViewCell(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    @Override
    public void updateItem(ProductCategory productCategory, boolean empty) {
        super.updateItem(productCategory, empty);
        if (productCategory != null) {
            ProductCategoryListItemData data = new ProductCategoryListItemData();
            data.setName(productCategory.getCategoryName());
            // Load image asynchronously
            loadImageAsync(productCategory.getImageLink(), data);
            setGraphic(data.getBox());
        }
    }

    private void loadImageAsync(String imageName, ProductCategoryListItemData data) {
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