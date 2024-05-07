package dev.hideftbanana.netcafejavafxapp.services.categoryservices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateProductCategoryRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.CreateProductCategoryResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductCategoriesResponse;

public class ProductCategoryService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProductCategoryService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<ProductCategoriesResponse> getAllProductCategories() {
        String apiUrl = "http://localhost:8080/api/productcategories";
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken()) // Add the authorization header
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            ProductCategoriesResponse categoryList = objectMapper.readValue(response.body(),
                                    ProductCategoriesResponse.class);
                            return categoryList;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<CreateProductCategoryResponse> createProductCategory(
            CreateProductCategoryRequest createRequest) {
        String apiUrl = "http://localhost:8080/api/productcategories";
        String requestBody = serializeToJson(createRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json") // Set Content-Type to application/json
                .uri(URI.create(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    System.out.println(response.statusCode());
                    if (response.statusCode() == 200) {
                        return new CreateProductCategoryResponse("Product category created successfully.", null);
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return new CreateProductCategoryResponse(null, errorResponse.getErrorMessage());
                            } else {
                                return new CreateProductCategoryResponse(null, "Empty response body");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });

    }

    public CompletableFuture<String> deleteProductCategory(long categoryId) {
        String apiUrl = "http://localhost:8080/api/productcategories/" + categoryId;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .DELETE()
                .uri(URI.create(apiUrl))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 204) {
                        return "Product category deleted successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to delete product category: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to delete product category: Empty response body";
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });
    }

    public CompletableFuture<String> updateProductCategory(long categoryId,
            CreateProductCategoryRequest updateRequest) {
        String apiUrl = "http://localhost:8080/api/productcategories/" + categoryId;
        String requestBody = serializeToJson(updateRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json") // Set Content-Type to application/json
                .uri(URI.create(apiUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        return "Product category updated successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to update product category: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to update product category: Empty response body";
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });
    }

    private String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

}