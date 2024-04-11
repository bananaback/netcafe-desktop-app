package dev.hideftbanana.netcafejavafxapp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.models.responses.AuthenticatedUserResponse;

public class TokenManager {
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static ScheduledExecutorService executor; // Define a ScheduledExecutorService variable

    private static Preferences preferences = Preferences.userNodeForPackage(TokenManager.class);

    public static String getAccessToken() {
        return preferences.get(ACCESS_TOKEN_KEY, null);
    }

    public static void setAccessToken(String accessToken) {
        preferences.put(ACCESS_TOKEN_KEY, accessToken);
    }

    public static String getRefreshToken() {
        return preferences.get(REFRESH_TOKEN_KEY, null);
    }

    public static void setRefreshToken(String refreshToken) {
        preferences.put(REFRESH_TOKEN_KEY, refreshToken);
    }

    public static void startTokenRefreshScheduler() {
        executor = Executors.newScheduledThreadPool(1); // Create a single-threaded executor

        Runnable task = () -> {
            String refreshToken = getRefreshToken();
            if (refreshToken != null) {
                // Construct the refresh request body
                String requestBody = "{\"refreshToken\": \"" + refreshToken + "\"}";

                // Construct the HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/auth/refresh"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                // Send the HTTP request asynchronously
                HttpClient httpClient = HttpClient.newHttpClient();
                CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(request,
                        HttpResponse.BodyHandlers.ofString());

                // Handle the response
                futureResponse.thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        // Token refresh successful
                        String responseBody = response.body();
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Parse the response body
                        AuthenticatedUserResponse refreshTokenResponse;
                        try {
                            refreshTokenResponse = objectMapper.readValue(responseBody,
                                    AuthenticatedUserResponse.class);
                            if (refreshTokenResponse.isSuccess()) {
                                // Update the access token and refresh token
                                String newAccessToken = refreshTokenResponse.getAccessToken();
                                String newRefreshToken = refreshTokenResponse.getRefreshToken();
                                setAccessToken(newAccessToken);
                                setRefreshToken(newRefreshToken);
                            } else {
                                System.out.println("Token refresh failed.");
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {
                        // Token refresh failed
                        System.out.println("Token refresh failed. Status code: " + response.statusCode());
                    }
                }).exceptionally(exception -> {
                    // Handle exceptions
                    System.out.println("Error occurred during token refresh: " + exception.getMessage());
                    return null;
                });
            } else {
                System.out.println("Refresh token not found. Unable to refresh access token.");
            }
        };

        // Schedule the task to run every 10 seconds (10000 milliseconds)
        executor.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS);
    }

    public static void stopTokenRefreshScheduler() {
        if (executor != null) {
            executor.shutdown(); // Shut down the executor
            executor = null;
        }
    }
}