package dev.hideftbanana.netcafejavafxapp.services.orderservices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.request.UpdateOrderStatusRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.OrderResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.OrderStatusEnum;
import dev.hideftbanana.netcafejavafxapp.models.responses.OrdersResponse;

public class OrderService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OrderService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    public CompletableFuture<List<OrderResponse>> getAllOrders() {
        String apiUrl = "http://localhost:8080/api/orders";
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken()) // Add the authorization header
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<OrderResponse> orders = objectMapper.readValue(response.body(),
                                    new TypeReference<List<OrderResponse>>() {
                                    });
                            return orders;
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<OrdersResponse> getOrdersByUserId(long userId) {
        String apiUrl = "http://localhost:8080/api/orders/user/" + userId;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken()) // Add the authorization header
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            OrdersResponse ordersResponse = objectMapper.readValue(response.body(),
                                    OrdersResponse.class);
                            return ordersResponse;
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<String> updateOrderStatus(long orderId, OrderStatusEnum newStatus) {
        String apiUrl = "http://localhost:8080/api/orders/" + orderId;
        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest(newStatus);
        String requestBody = serializeToJson(updateRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .uri(URI.create(apiUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        return "Order status updated successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to update order status: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to update order status: Empty response body";
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
