package dev.hideftbanana.netcafejavafxapp.datamodels;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

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

    private void saveData() {

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
