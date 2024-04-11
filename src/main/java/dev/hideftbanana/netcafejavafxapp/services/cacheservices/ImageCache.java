package dev.hideftbanana.netcafejavafxapp.services.cacheservices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.responses.ImageResponse;

public class ImageCache extends SimpleCache<String, byte[]> {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:8080/api/image/pic/";
    private final String cacheFolderPath = "src/main/resources/images/";

    public ImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    void put(String key, byte[] value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value of image element cache should not be null.");
        }
        cache.put(key, value);
    }

    @Override
    public byte[] get(String key) {
        byte[] imageData = cache.get(key); // Check in memory cache first

        if (imageData == null) {
            // Not in memory, check file system
            File imageFile = new File(getCacheFilePath(key));
            try {
                imageData = Files.readAllBytes(imageFile.toPath()); // Read from file system
                cache.put(key, imageData); // Update cache for future use
            } catch (IOException e) {

                // Not in file system, download asynchronously from URL
                CompletableFuture<byte[]> futureImageData = CompletableFuture.supplyAsync(() -> {
                    try {
                        return downloadImage(key);
                    } catch (IOException e1) {
                        // Handle download error
                        e1.printStackTrace();
                        return null;
                    }
                });

                // Return the CompletableFuture for the caller to handle
                return futureImageData.join(); // This returns null if downloadImage throws an exception
            }
        }

        return imageData;
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    private byte[] downloadImage(String key) throws IOException {
        URL imageUrl = new URL(baseUrl + key);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Open a connection to the URL
            connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setRequestMethod("POST");

            // Add the Authorization header with the access token
            String accessToken = TokenManager.getAccessToken();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            // Read the response from the connection
            inputStream = connection.getInputStream();

            // Parse the JSON response using Jackson Databind
            ImageResponse imageResponse = objectMapper.readValue(inputStream, ImageResponse.class);

            // Extract the value of the "imageBytes" key
            byte[] downloadedData = imageResponse.getImageBytes();

            // Write the downloaded data to the file system cache
            Files.write(Paths.get(getCacheFilePath(key)), downloadedData);

            // Perform additional step here, if the images in the folder exceed a limit like
            // 100 images, remove the oldest-accessed image
            File cacheFolder = new File(cacheFolderPath);
            File[] imageFiles = cacheFolder.listFiles();
            if (imageFiles != null && imageFiles.length > 100) {
                // Delete the oldest-accessed image
                deleteOldestImage(imageFiles);
            }

            return downloadedData;
        } finally {
            // Close the input stream
            if (inputStream != null) {
                inputStream.close();
            }

            // Close the output stream
            if (outputStream != null) {
                outputStream.close();
            }

            // Disconnect the connection
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void deleteOldestImage(File[] imageFiles) {
        // Find the oldest-accessed image
        File oldestImage = imageFiles[0];
        for (int i = 1; i < imageFiles.length; i++) {
            if (imageFiles[i].lastModified() < oldestImage.lastModified()) {
                oldestImage = imageFiles[i];
            }
        }

        // Delete the oldest-accessed image
        if (oldestImage.delete()) {
            System.out.println("Oldest-accessed image deleted successfully.");
        } else {
            System.err.println("Failed to delete oldest-accessed image.");
        }
    }

    private String getCacheFilePath(String key) {
        return cacheFolderPath + key; // Modify if cache directory differs
    }

}
