package dev.hideftbanana.netcafejavafxapp.services.orderitemservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hideftbanana.netcafejavafxapp.models.responses.OrderItemsResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class OrderItemService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OrderItemService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<OrderItemsResponse> getAllOrderItemsOfOrder(Long orderId) {
        String apiUrl = "http://localhost:8080/api/orderitems/order/" + orderId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), OrderItemsResponse.class);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }
}