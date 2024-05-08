package dev.hideftbanana.netcafejavafxapp.services.computerservices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateComputerRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ComputersResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;

public class ComputerService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ComputerService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<ComputersResponse> getAllComputers() {
        String apiUrl = "http://localhost:8080/api/computers";
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken()) // Add the authorization header
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            ComputersResponse computersResponse = objectMapper.readValue(response.body(),
                                    ComputersResponse.class);
                            return computersResponse;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed with status code: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<String> createComputer(CreateComputerRequest createRequest) {
        String apiUrl = "http://localhost:8080/api/computers";
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
                        return "Computer created successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to create computer: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to create computer: Empty response body";
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });

    }

    public CompletableFuture<String> deleteComputer(long computerId) {
        String apiUrl = "http://localhost:8080/api/computers/" + computerId;
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .DELETE()
                .uri(URI.create(apiUrl))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        return "Computer deleted successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to delete computer: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to delete computer: Empty response body";
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    }
                });
    }

    public CompletableFuture<String> updateComputer(long computerId, CreateComputerRequest updateRequest) {
        String apiUrl = "http://localhost:8080/api/computers/" + computerId;
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
                        return "Computer updated successfully.";
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                return "Failed to update computer: " + errorResponse.getErrorMessage();
                            } else {
                                return "Failed to update computer: Empty response body";
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
