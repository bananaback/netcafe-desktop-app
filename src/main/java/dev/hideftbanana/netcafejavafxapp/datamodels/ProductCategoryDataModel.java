package dev.hideftbanana.netcafejavafxapp.datamodels;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.ProductCategory;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductCategoriesResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductCategoryResponse;
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

    public void addCategory(ProductCategory productCategory) {
        productCategory.setIsNew(true);
        productCategoryList.add(productCategory);
    }

    public void removeCategory(ProductCategory productCategory) {
        if (productCategory.getIsNew()) {
            productCategoryList.remove(productCategory);
        } else {
            // If it's an existing item, mark it as deleted in the modified list
            productCategory.setIsDeleted(true);
        }
    }

    public void loadData() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/productcategories"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                ProductCategoriesResponse productCategoriesResponse = objectMapper.readValue(response.body(),
                        ProductCategoriesResponse.class);
                List<ProductCategoryResponse> productCategoryResponses = productCategoriesResponse
                        .getProductCategories();
                productCategoryList.clear();
                for (ProductCategoryResponse productCategoryResponse : productCategoryResponses) {
                    ProductCategory productCategory = new ProductCategory(
                            productCategoryResponse.getId(),
                            productCategoryResponse.getCategoryName(),
                            productCategoryResponse.getImageLink());
                    productCategory.setValidationText("");

                    productCategoryList.add(productCategory);
                }
                for (ProductCategory productCategory : productCategoryList) {
                    System.out.println(productCategory);
                }
            } else {
                System.err.println("Failed to fetch product categories. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> saveData() {
        ProductCategory currentProductCategory = currentProductCategoryProperty().get();
        if (currentProductCategory != null) {
            CompletableFuture<Void> requestFuture = null;
            if (currentProductCategory.getIsNew()) {
                HttpRequest request = buildCreateProductCategoryRequest(currentProductCategory);
                requestFuture = sendRequestAsync(request);
            } else if (currentProductCategory.getIsModified()) {
                HttpRequest request = buildUpdateProductCategoryRequest(currentProductCategory);
                requestFuture = sendRequestAsync(request);
            } else if (currentProductCategory.getIsDeleted()) {
                HttpRequest request = buildDeleteProductCategoryRequest(currentProductCategory);
                requestFuture = sendRequestAsync(request);
            }

            if (requestFuture != null) {
                // If requestFuture is not null, then we have an asynchronous operation
                return requestFuture.thenAccept(result -> {
                    boolean delete = false;
                    if (currentProductCategory.getIsDeleted()) {
                        delete = true;
                    }
                    // Reset flags to false if the operation is successful

                    currentProductCategory.setIsNew(false);
                    currentProductCategory.setIsModified(false);
                    currentProductCategory.setIsDeleted(false);

                    if (delete && currentProductCategory.getValidationText() != "") {
                        int index = -1;
                        for (int i = 0; i < productCategoryList.size(); i++) {
                            if (productCategoryList.get(i).getId() == currentProductCategory.getId()) {
                                index = i;
                                break;
                            }
                        }
                        if (index != -1) {
                            productCategoryList.remove(index);
                            currentProductCategoryProperty().setValue(null);
                        }
                    }

                }).exceptionally(ex -> {
                    // If the operation fails, set validation text of each item to the error
                    for (ProductCategory productCategory : productCategoryList) {
                        productCategory.setValidationText("Failed to perform operation: " + ex.getMessage());
                    }
                    return null;
                });
            } else {
                // If no action is taken, return a completed future
                return CompletableFuture.completedFuture(null);
            }
        }
        // If currentProductCategory is null, return a completed future
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Void> sendRequestAsync(HttpRequest request) {
        HttpClient httpClient = HttpClient.newHttpClient();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201 || response.statusCode() == 204) {
                        // Successful response
                        return null;
                    } else {
                        throw new RuntimeException("Request failed with status code: " + response.statusCode());
                    }
                });
    }

    private HttpRequest buildCreateProductCategoryRequest(ProductCategory productCategory) {
        String requestBody = String.format("{\"categoryName\": \"%s\", \"imageLink\": \"%s\"}",
                productCategory.getCategoryName(), productCategory.getImageLink());

        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/productcategories"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private HttpRequest buildUpdateProductCategoryRequest(ProductCategory productCategory) {
        String requestBody = String.format("{\"categoryName\": \"%s\", \"imageLink\": \"%s\"}",
                productCategory.getCategoryName(), productCategory.getImageLink());

        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/productcategories/" + productCategory.getId()))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private HttpRequest buildDeleteProductCategoryRequest(ProductCategory productCategory) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/productcategories/" + productCategory.getId()))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .DELETE()
                .build();
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
