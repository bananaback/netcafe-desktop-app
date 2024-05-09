package dev.hideftbanana.netcafejavafxapp.services.userserssionservices;

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
import dev.hideftbanana.netcafejavafxapp.models.request.ComputerIdsRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.UserSessionResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ErrorResponse;

public class UserSessionService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserSessionService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public CompletableFuture<List<UserSessionResponse>> getLatestUserSessionsByComputerIds(
            ComputerIdsRequest computerIdsRequest) {
        String apiUrl = "http://localhost:8080/api/sessions/latest";
        String requestBody = serializeToJson(computerIdsRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .header("Content-Type", "application/json")
                .uri(URI.create(apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<UserSessionResponse> userSessions = objectMapper.readValue(response.body(),
                                    new TypeReference<List<UserSessionResponse>>() {
                                    });
                            return userSessions;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse JSON response", e);
                        }
                    } else {
                        try {
                            String responseBody = response.body();
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
                                throw new RuntimeException(
                                        "Failed to get latest user sessions: " + errorResponse.getErrorMessage());
                            } else {
                                throw new RuntimeException("Failed to get latest user sessions: Empty response body");
                            }
                        } catch (Exception e) {
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
