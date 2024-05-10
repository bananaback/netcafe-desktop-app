package dev.hideftbanana.netcafejavafxapp.services.messageservices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.MessageDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MessageService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public CompletableFuture<List<MessageDTO>> getAllMessagesForUserInTimeRange(Long userId, LocalDateTime beginTime,
            LocalDateTime endTime) {
        String apiUrl = "http://localhost:8080/api/messages/user/" + userId +
                "?beginTime=" + beginTime.toString() +
                "&endTime=" + endTime.toString();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return sendAsyncRequest(request, new TypeReference<List<MessageDTO>>() {
        });
    }

    public CompletableFuture<String> sendMessageForUser(Long userId, String messageContent) {
        String apiUrl = "http://localhost:8080/api/messages/user/" + userId + "/send";
        String requestBody = messageContent;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .uri(URI.create(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendAsyncRequestForString(request);
    }

    public CompletableFuture<String> sendMessageForAdmin(Long userId, String messageContent) {
        String apiUrl = "http://localhost:8080/api/messages/admin/" + userId + "/send";
        String requestBody = messageContent;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .uri(URI.create(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendAsyncRequestForString(request);
    }

    private <T> CompletableFuture<T> sendAsyncRequest(HttpRequest request, TypeReference<T> responseType) {
        CompletableFuture<T> future = new CompletableFuture<>();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), responseType);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                })
                .thenAcceptAsync(future::complete)
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });

        return future;
    }

    private CompletableFuture<String> sendAsyncRequestForString(HttpRequest request) {
        CompletableFuture<String> future = new CompletableFuture<>();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 201) {
                        return "Operation successful.";
                    } else {
                        try {
                            ErrorResponse errorResponse = objectMapper.readValue(response.body(), ErrorResponse.class);
                            return "Failed: " + errorResponse.getErrorMessage();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                })
                .thenAcceptAsync(future::complete)
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });

        return future;
    }
}
