package dev.hideftbanana.netcafejavafxapp.services.userservices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.UserInfoResponse;

public class UserService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<UserInfoResponse> getUserInfoById(long userId) {
        String apiUrl = "http://localhost:8080/api/users/" + userId;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            UserInfoResponse userInfoResponse = objectMapper.readValue(response.body(),
                                    UserInfoResponse.class);
                            return userInfoResponse;
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else if (response.statusCode() == 400) {
                        throw new NoSuchElementException("User not found");
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<List<UserInfoResponse>> getAllUserInfo() {
        String apiUrl = "http://localhost:8080/api/users";
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<UserInfoResponse> userInfoResponses = objectMapper.readValue(response.body(),
                                    new TypeReference<List<UserInfoResponse>>() {
                                    });
                            return userInfoResponses;
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<String> updateUserInfo(long userId, UserInfoResponse userInfoResponse) {
        String apiUrl = "http://localhost:8080/api/users/" + userId;
        String requestBody = serializeToJson(userInfoResponse);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .uri(URI.create(apiUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        return "User info updated successfully.";
                    } else if (response.statusCode() == 400) {
                        throw new NoSuchElementException("User not found");
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to update user info: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to update user info: Empty response body";
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
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
