package dev.hideftbanana.netcafejavafxapp.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.models.responses.ImageUploadResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ImagesResponse;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImageController extends BaseController implements Initializable {
    @FXML
    private FlowPane imageFlowPane;
    @FXML
    private Button loadButton;
    @FXML
    private Button uploadButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label pageLabel;
    @FXML
    private VBox imageGalleryVBox;
    @FXML
    private VBox imageDetailsVBox;
    @FXML
    private ImageView detailedImageView;
    @FXML
    private Label imageTitleLabel;
    @FXML
    private Button copyNameButton;
    @FXML
    private Button deleteImageButton;
    @FXML
    private Button exitButton;

    private ImageCache imageCache;
    private int pageSize = 10;
    private int page = 0;
    private int totalPages = 1;
    private final String cacheFolderPath = "src/main/resources/images/";

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
        System.out.println("Image cache passed to ImageController!!!");
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(imageFlowPane.getScene().getWindow());
        if (selectedFile != null) {
            System.out.println(selectedFile);
            // Get access token from TokenManager
            String accessToken = TokenManager.getAccessToken();

            // Create HttpClient
            HttpClient httpClient = HttpClient.newHttpClient();

            // Get the URI for the endpoint
            URI uri = getUploadUri();

            // Prepare the multi-part form data
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            HttpRequest.BodyPublisher bodyPublisher;
            try {
                bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(
                        createMultipartBody(selectedFile, boundary));
                // Build the request
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .POST(bodyPublisher)
                        .build();

                // Send the HTTP POST request asynchronously
                httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(this::handleUploadResponse)
                        .exceptionally(this::handleException);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            loadImages();
        }
    }

    private byte[] createMultipartBody(File file, String boundary) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

        // Add file part
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName())
                .append("\"\r\n");
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
        writer.append("\r\n");
        writer.flush();
        Files.copy(file.toPath(), outputStream);
        outputStream.flush();

        // Add boundary
        writer.append("\r\n").append("--").append(boundary).append("--").append("\r\n");
        writer.flush();

        return outputStream.toByteArray();
    }

    private void showMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Method to handle the response when uploading an image
    private void handleUploadResponse(HttpResponse<String> httpResponse) {
        // Check if the response status code is OK (200)
        if (httpResponse.statusCode() == 200) {
            try {
                // Parse the JSON response using Jackson ObjectMapper
                ObjectMapper objectMapper = new ObjectMapper();
                ImageUploadResponse uploadResponse = objectMapper.readValue(httpResponse.body(),
                        ImageUploadResponse.class);

                // Access the message and download link from the uploadResponse object
                String message = uploadResponse.getMessage();
                String downloadLink = uploadResponse.getDownloadLink();

                showMessage("Image uploaded successfully!\n" + "Message: " + message + "\n" + "Download Link: "
                        + downloadLink);
                // Display the message as needed
                System.out.println("Message: " + message);

                // You can use the download link if needed
                System.out.println("Download Link: " + downloadLink);

                // You can perform further actions based on the response if needed
            } catch (IOException e) {
                // Handle IOException
                e.printStackTrace();
            }
        } else {
            // Print error message if response status code is not 200
            System.out.println("Failed to upload image. Status code: " + httpResponse.statusCode());
            showMessage("Failed to upload image. Status code: " + httpResponse.statusCode());
            // You can handle the failure scenario as needed
        }
    }

    @FXML
    private void loadImages() {
        loadButton.setDisable(true);
        // Get access token from TokenManager
        String accessToken = TokenManager.getAccessToken();

        // Create HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Get the URI for the endpoint
        URI uri = getUri();

        // Create the HTTP GET request with Authorization header
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Authorization", "Bearer " + accessToken) // Add Authorization header
                .build();

        // Send the HTTP GET request asynchronously
        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenAccept(this::handleResponse)
                .exceptionally(this::handleException);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        prevButton.setDisable(true);
        nextButton.setDisable(true);
        imageGalleryVBox.setVisible(true);
        imageDetailsVBox.setVisible(false);
    }

    // Method to handle the response when it is received
    private void handleResponse(HttpResponse<String> httpResponse) {
        // Check if the response status code is OK (200)
        if (httpResponse.statusCode() == 200) {
            try {
                // Parse the JSON response using Jackson ObjectMapper
                ObjectMapper objectMapper = new ObjectMapper();
                ImagesResponse imagesResponse = objectMapper.readValue(httpResponse.body(), ImagesResponse.class);

                List<String> imageNames = imagesResponse.getImageNames();
                // Now you can access the image names and size from the response object
                System.out.println("Image Names: " + imageNames);
                System.out.println("Total Size: " + imagesResponse.getSize());

                // Calculate the total number of pages
                int totalImages = imagesResponse.getSize();
                totalPages = (int) Math.ceil((double) totalImages / pageSize);
                System.out.println("Total images:" + totalImages);
                System.out.println("Total pages: " + totalPages);

                // Calculate the current page
                int currentPage = page + 1; // Since page is zero-based

                // Update the page label
                Platform.runLater(() -> {
                    pageLabel.setText(currentPage + "/" + totalPages);
                });

                updatePagingButtons();
                renderImages(imageNames);
            } catch (IOException e) {
                // Handle IOException
                e.printStackTrace();
            }
        } else {
            // Print error message if response status code is not 200
            System.out.println("Failed to fetch image names. Status code: " + httpResponse.statusCode());
        }
        loadButton.setDisable(false);
    }

    @FXML
    private void gotoPreviousPage() {
        if (page > 0) {
            page--;
        }
        loadImages();
    }

    @FXML
    private void gotoNextPage() {
        if (page < totalPages) {
            page++;
            loadImages();
        }
    }

    private void updatePagingButtons() {
        Platform.runLater(() -> {
            // Disable prevButton when on the first page
            prevButton.setDisable(page == 0);

            // Disable nextButton when on the last page
            nextButton.setDisable(page >= totalPages - 1);
        });
    }

    // Method to handle exceptions occurred during the HTTP request
    private Void handleException(Throwable throwable) {
        // Handle exceptions
        throwable.printStackTrace();
        loadButton.setDisable(false);
        return null;
    }

    // Get URI object for the endpoint
    private URI getUri() {
        try {
            // Construct the URI with page and page size parameters
            return new URI("http://localhost:8080/api/image?page=" + page + "&pageSize=" + pageSize);
        } catch (URISyntaxException e) {
            // Handle URISyntaxException
            e.printStackTrace();
            return null;
        }
    }

    // Get URI object for the upload endpoint
    private URI getUploadUri() {
        try {
            // Construct the URI for the upload endpoint
            return new URI("http://localhost:8080/api/image/pic");
        } catch (URISyntaxException e) {
            // Handle URISyntaxException
            e.printStackTrace();
            return null;
        }
    }

    private void renderImages(List<String> imageNames) {
        // Clear existing images in the imageFlowPane
        Platform.runLater(() -> imageFlowPane.getChildren().clear());

        // Fetch image data asynchronously for each image name
        List<CompletableFuture<Void>> futures = imageNames.stream()
                .map(imageName -> CompletableFuture.runAsync(() -> {
                    System.err.println("getting image data on a new thread");
                    byte[] imageData = imageCache.get(imageName);
                    if (imageData != null) {
                        try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
                            Image image = new Image(inputStream);
                            ImageView imageView = new ImageView(image);

                            // Set the fixed width and height of the ImageView
                            double fixedWidth = 200; // Set your desired fixed width
                            double fixedHeight = 150; // Set your desired fixed height

                            // Get the dimensions of the image
                            double imageWidth = image.getWidth();
                            double imageHeight = image.getHeight();

                            // Calculate the scaling factors
                            double scaleX = fixedWidth / imageWidth;
                            double scaleY = fixedHeight / imageHeight;

                            // Choose the smaller scaling factor to ensure the image fits within the fixed
                            // size
                            double scale = Math.min(scaleX, scaleY);

                            // Set the fit width and height of the ImageView
                            imageView.setFitWidth(imageWidth * scale);
                            imageView.setFitHeight(imageHeight * scale);

                            // Preserve aspect ratio
                            imageView.setPreserveRatio(true);

                            // Create a StackPane to hold the ImageView and set its background color to
                            // white
                            StackPane stackPane = new StackPane();
                            stackPane.setPrefSize(fixedWidth, fixedHeight);
                            stackPane.setStyle("-fx-background-color: white");
                            stackPane.getChildren().add(imageView);

                            // Add the StackPane to the imageFlowPane
                            Platform.runLater(() -> {
                                imageFlowPane.getChildren().add(stackPane);

                                // Add event listener for image view click
                                stackPane.setOnMouseClicked(event -> {
                                    // Handle image click event
                                    displayImageDetails(imageName, image);
                                });
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Image data is null");
                    }
                }))
                .collect(Collectors.toList());

        // Combine all futures into a single future and handle any exceptions
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        combinedFuture.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private void displayImageDetails(String imageName, Image image) {
        // Here you can display the image details using the imageName and image
        // For example, you can show them in a separate view or dialog
        // You can access the imageName and image and display them as needed
        System.out.println("Image Name: " + imageName);
        // Display image details view with imageName and image
        // You can show this view in a separate dialog or change the visibility of an
        // existing view
        // For demonstration purposes, let's just print the image name and size
        detailedImageView.setImage(image);
        imageTitleLabel.setText(imageName);
        imageDetailsVBox.setVisible(true);
        imageGalleryVBox.setVisible(false);
    }

    @FXML
    private void copyImageName() {
        copyToClipboard(imageTitleLabel.getText());
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        showMessage("Copy image name successfully.\n" + text);
    }

    @FXML
    private void deleteImage() {
        String imageName = imageTitleLabel.getText(); // Get the image name
        if (imageName != null && !imageName.isEmpty()) {
            // Remove the image from the cache
            imageCache.remove(imageName);
            // Optionally, delete the image from the file system
            // Delete the image from the file system
            String imagePath = getCacheFilePath(imageName);
            try {
                Files.deleteIfExists(Paths.get(imagePath));
                showMessage("Image deleted successfully: " + imageName);
            } catch (IOException e) {
                showMessage("Failed to delete image: " + imageName);
                e.printStackTrace();
            }
            // Remove the image from Firebase Storage
            // Make HttpClient call to delete the image
            String accessToken = TokenManager.getAccessToken();
            HttpClient httpClient = HttpClient.newHttpClient();
            URI uri = getDeleteImageUri(imageName); // Construct URI for Firebase Storage delete endpoint
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(this::handleFirebaseStorageDeleteResponse)
                    .exceptionally(this::handleException);

            loadImages();
            exitImageDetail();
            showMessage("Image deleted successfully: " + imageName);
        } else {
            showMessage("No image selected.");
        }

    }

    // Get URI object for the delete image endpoint
    private URI getDeleteImageUri(String imageName) {
        try {
            // Construct the URI for the delete image endpoint
            return new URI("http://localhost:8080/api/image/pic/" + imageName);
        } catch (URISyntaxException e) {
            // Handle URISyntaxException
            e.printStackTrace();
            return null;
        }
    }

    // Method to handle the response when deleting image from Firebase Storage
    private void handleFirebaseStorageDeleteResponse(HttpResponse<String> httpResponse) {
        // Check if the response status code is OK (200)
        if (httpResponse.statusCode() == 200) {
            showMessage("Image deleted from Firebase Storage successfully.");
        } else {
            showMessage("Failed to delete image from Firebase Storage. Status code: " + httpResponse.statusCode());
        }
    }

    @FXML
    private void exitImageDetail() {
        imageDetailsVBox.setVisible(false);
        imageGalleryVBox.setVisible(true);
    }

    private String getCacheFilePath(String key) {
        return cacheFolderPath + key; // Modify if cache directory differs
    }

}
