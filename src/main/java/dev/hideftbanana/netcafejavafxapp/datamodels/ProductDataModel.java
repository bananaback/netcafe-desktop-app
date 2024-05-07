package dev.hideftbanana.netcafejavafxapp.datamodels;

import dev.hideftbanana.netcafejavafxapp.models.Product;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateProductRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductResponse;
import dev.hideftbanana.netcafejavafxapp.services.productservices.ProductService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductDataModel {
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    private final ObjectProperty<Product> currentProduct = new SimpleObjectProperty<>(null);

    public ObjectProperty<Product> currentProductProperty() {
        return currentProduct;
    }

    public final Product getCurrentProduct() {
        return currentProductProperty().get();
    }

    public final void setCurrentProduct(Product product) {
        currentProductProperty().set(product);
    }

    public ObservableList<Product> getProductList() {
        return productList;
    }

    private ProductService productService;

    public ProductDataModel() {
        this.productService = new ProductService();
    }

    public void loadData() {
        productService.getAllProducts()
                .thenAcceptAsync(productsResponse -> {
                    ObservableList<Product> newProductList = FXCollections.observableArrayList();
                    for (ProductResponse productResponse : productsResponse.getProducts()) {
                        Product product = new Product(
                                productResponse.getId(),
                                productResponse.getName(),
                                productResponse.getDescription(),
                                productResponse.getPrice(),
                                productResponse.getRemainQuantity(),
                                productResponse.getImageLink(),
                                productResponse.getProductCategory().getId(),
                                productResponse.getProductCategory().getCategoryName());
                        newProductList.add(product);
                    }
                    Platform.runLater(() -> {
                        productList.setAll(newProductList); // Update the UI on the JavaFX Application Thread
                    });
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Handle exceptions if needed
                    return null;
                });
    }

    public void addProduct(CreateProductRequest createProductRequest) throws Exception {
        productService.createProduct(createProductRequest)
                .thenAccept(response -> {
                    if (response != null && response.equals("Product created successfully.")) {
                        loadData();
                    } else {
                        throw new RuntimeException("Failed to create product: " + response);
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to create product", throwable);
                });
    }

    public void deleteProduct(long productId) {
        productService.deleteProduct(productId)
                .thenAccept(response -> {
                    if (response != null && response.equals("Product deleted successfully.")) {
                        loadData(); // Reload data after successful deletion
                    } else {
                        throw new RuntimeException("Failed to delete product");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to delete product", throwable);
                });
    }

    public void updateProduct(long productId, CreateProductRequest updateRequest) {
        productService.updateProduct(productId, updateRequest)
                .thenAccept(response -> {
                    if (response != null && response.equals("Product updated successfully.")) {
                        loadData(); // Reload data after successful update
                    } else {
                        throw new RuntimeException("Failed to update product");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to update product", throwable);
                });
    }
}
