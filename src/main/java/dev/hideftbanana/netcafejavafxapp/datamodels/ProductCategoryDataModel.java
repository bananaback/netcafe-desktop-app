package dev.hideftbanana.netcafejavafxapp.datamodels;

import java.util.List;

import dev.hideftbanana.netcafejavafxapp.models.ProductCategory;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateProductCategoryRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductCategoryResponse;
import dev.hideftbanana.netcafejavafxapp.services.categoryservices.ProductCategoryService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductCategoryDataModel {
    private final ObservableList<ProductCategory> productCategoryList = FXCollections.observableArrayList();

    private final ObjectProperty<ProductCategory> currentProductCategory = new SimpleObjectProperty<>(null);

    public ObjectProperty<ProductCategory> currentProductCategoryProperty() {
        return currentProductCategory;
    }

    public final ProductCategory getCurrentProductCategory() {
        return currentProductCategoryProperty().get();
    }

    public final void setCurrentProductCategory(ProductCategory productCategory) {
        currentProductCategoryProperty().set(productCategory);
    }

    public ObservableList<ProductCategory> getProductCategoryList() {
        return productCategoryList;
    }

    private ProductCategoryService productCategoryService;

    public ProductCategoryDataModel() {
        this.productCategoryService = new ProductCategoryService();
    }

    public void loadData() {
        productCategoryService.getAllProductCategories()
                .thenAcceptAsync(productCategoriesResponse -> {
                    List<ProductCategoryResponse> productCategoryResponses = productCategoriesResponse
                            .getProductCategories();
                    ObservableList<ProductCategory> newProductCategoryList = FXCollections.observableArrayList();
                    for (ProductCategoryResponse productCategoryResponse : productCategoryResponses) {
                        ProductCategory productCategory = new ProductCategory(
                                productCategoryResponse.getId(),
                                productCategoryResponse.getCategoryName(),
                                productCategoryResponse.getImageLink());
                        newProductCategoryList.add(productCategory);
                    }
                    Platform.runLater(() -> {
                        productCategoryList.setAll(newProductCategoryList); // Update the UI on the JavaFX Application
                                                                            // Thread
                    });
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Handle exceptions if needed
                    return null;
                });
    }

    public void addCategory(CreateProductCategoryRequest createProductCategoryRequest) throws Exception {
        productCategoryService.createProductCategory(createProductCategoryRequest)
                .thenAccept(response -> {
                    if (response.getMessage() != null) {
                        loadData();
                    } else {
                        throw new RuntimeException("Failed to create product category: " + response.getErrorMessage());
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to create product category", throwable);
                });
    }

    public void deleteCategory(long categoryId) {
        productCategoryService.deleteProductCategory(categoryId)
                .thenAccept(response -> {
                    if (response != null && response.equals("Product category deleted successfully.")) {
                        loadData(); // Reload data after successful deletion
                    } else {
                        throw new RuntimeException("Failed to delete product category");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to delete product category", throwable);
                });
    }

    public void updateCategory(long categoryId, CreateProductCategoryRequest updateRequest) {
        productCategoryService.updateProductCategory(categoryId, updateRequest)
                .thenAccept(response -> {
                    if (response != null && response.equals("Product category updated successfully.")) {
                        loadData(); // Reload data after successful update
                    } else {
                        throw new RuntimeException("Failed to update product category");
                    }
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Throw the exception to propagate it back to the controller
                    throw new RuntimeException("Failed to update product category", throwable);
                });
    }

    // All these endpoint need to have Bearer header with token from
    // TokenManager.getAccessToken() static method to work

    // Create
    // POST http://localhost:8080/api/productcategories
    /*
     * {
     * "categoryName" : "Drinks",
     * "imageLink" : "afa36639-c89e-4ffc-bf1e-8e5eefc70470.jpg"
     * }
     */
    // Update
    // PUT http://localhost:8080/api/productcategories/1
    /*
     * {
     * "categoryName": "Drinks",
     * "imageLink": "newlink.png"
     * }
     */
    // Delete
    // DELETE http://localhost:8080/api/productcategories/1
    // Get all
    // GET http://localhost:8080/api/productcategories
    // Get by id
    // GET http://localhost:8080/api/productcategories/1

}