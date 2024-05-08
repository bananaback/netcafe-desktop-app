package dev.hideftbanana.netcafejavafxapp.services.roomservices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateRoomRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.RoomsResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;

public class RoomService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RoomService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<RoomsResponse> getAllRooms() {
        String apiUrl = "http://localhost:8080/api/rooms";
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), RoomsResponse.class);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<String> addRoom(CreateRoomRequest createRequest) {
        String apiUrl = "http://localhost:8080/api/rooms";
        String requestBody = serializeToJson(createRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .uri(URI.create(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        return "Room added successfully.";
                    } else {
                        try {
                            ErrorResponse errorResponse = objectMapper.readValue(response.body(), ErrorResponse.class);
                            return "Failed to add room: " + errorResponse.getErrorMessage();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });
    }

    public CompletableFuture<String> deleteRoom(long roomId) {
        String apiUrl = "http://localhost:8080/api/rooms/" + roomId;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .DELETE()
                .uri(URI.create(apiUrl))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 204) {
                        return "Room deleted successfully.";
                    } else {
                        try {
                            ErrorResponse errorResponse = objectMapper.readValue(response.body(), ErrorResponse.class);
                            return "Failed to delete room: " + errorResponse.getErrorMessage();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });
    }

    public CompletableFuture<String> updateRoom(long roomId, CreateRoomRequest updateRequest) {
        String apiUrl = "http://localhost:8080/api/rooms/" + roomId;
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
                        return "Room updated successfully.";
                    } else {
                        try {
                            ErrorResponse errorResponse = objectMapper.readValue(response.body(), ErrorResponse.class);
                            return "Failed to update room: " + errorResponse.getErrorMessage();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });
    }

    private String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
