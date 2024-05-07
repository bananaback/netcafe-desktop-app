package dev.hideftbanana.netcafejavafxapp.services.productservices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateProductRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductsResponse;

public class ProductService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProductService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<ProductsResponse> getAllProducts() {
        String apiUrl = "http://localhost:8080/api/products";
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken()) // Add the authorization header
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            ProductsResponse productsResponse = objectMapper.readValue(response.body(),
                                    ProductsResponse.class);
                            return productsResponse;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<String> createProduct(CreateProductRequest createRequest) {
        String apiUrl = "http://localhost:8080/api/products";
        String requestBody = serializeToJson(createRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json") // Set Content-Type to application/json
                .uri(URI.create(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        return "Product created successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to create product: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to create product: Empty response body";
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });

    }

    public CompletableFuture<String> deleteProduct(long productId) {
        String apiUrl = "http://localhost:8080/api/products/" + productId;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .DELETE()
                .uri(URI.create(apiUrl))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 204) {
                        return "Product deleted successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to delete product: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to delete product: Empty response body";
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });
    }

    public CompletableFuture<String> updateProduct(long productId, CreateProductRequest updateRequest) {
        String apiUrl = "http://localhost:8080/api/products/" + productId;
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
                        return "Product updated successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to update product: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to update product: Empty response body";
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
