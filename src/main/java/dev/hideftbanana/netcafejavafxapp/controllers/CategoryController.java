package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hideftbanana.netcafejavafxapp.TokenManager;
import dev.hideftbanana.netcafejavafxapp.customcontrols.FxUtilTest;
import dev.hideftbanana.netcafejavafxapp.customcontrols.ProductCategoryListViewCell;
import dev.hideftbanana.netcafejavafxapp.datamodels.ProductCategoryDataModel;
import dev.hideftbanana.netcafejavafxapp.models.ProductCategory;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateProductCategoryRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ImagesResponse;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javafx.util.StringConverter;

import javafx.scene.image.Image;

public class CategoryController extends BaseController implements Initializable {

    private ProductCategoryDataModel productCategoryDataModel;
    private ImageCache imageCache;

    @FXML
    private ListView<ProductCategory> categoryListView;
    @FXML
    private ImageView categoryImagePreview;
    @FXML
    private Label categoryNameLabelPreview;
    @FXML
    private ComboBox<String> categoryImageComboBoxUpdate;
    @FXML
    private TextField categoryNameTextFieldUpdate;

    @FXML
    private ImageView currentProductCategoryImageViewUpdate;

    @FXML
    private Button addButton;
    @FXML
    private Label validationLabel;

    @FXML
    private VBox overviewVBox;
    @FXML
    private VBox updateVBox;
    @FXML
    private Button updateCategoryButton;
    @FXML
    private Button deleteCategoryButton;
    @FXML
    private Label viewLabel;
    @FXML
    private Button cancelUpdateButton;
    @FXML
    private Button saveUpdateButton;

    @FXML
    private void switchToOverview() {
        overviewVBox.setVisible(true);
        updateVBox.setVisible(false);
    }

    @FXML
    private void switchToUpdateView() {
        overviewVBox.setVisible(false);
        updateVBox.setVisible(true);
        viewLabel.setText("Update category");
        categoryImageComboBoxUpdate.valueProperty()
                .setValue(productCategoryDataModel.getCurrentProductCategory().getImageLink());
        categoryNameTextFieldUpdate.setText(productCategoryDataModel.getCurrentProductCategory().getCategoryName());
    }

    @FXML
    private void deleteCategory() {
        ProductCategory selectedCategory = productCategoryDataModel.getCurrentProductCategory();
        if (selectedCategory != null) {
            Task<Void> deleteCategoryTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the addCategory method of the data model
                        productCategoryDataModel.deleteCategory(selectedCategory.getId());
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            deleteCategoryTask.setOnFailed(event -> {
                Throwable exception = deleteCategoryTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to add product category: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to delete product category");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            deleteCategoryTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    categoryListView.setItems(productCategoryDataModel.getProductCategoryList());
                    categoryListView.refresh();
                    categoryImagePreview.setImage(null);
                    categoryNameLabelPreview.setText("");
                });
            });

            new Thread(deleteCategoryTask).start();
        } else {
            // No category is selected, handle this case as needed
            // For example, show a message to the user indicating that they need to select a
            // category to delete
        }

    }

    @FXML
    private void addNewProductCategory() {
        // Create a new request object with the data from the UI
        CreateProductCategoryRequest request = new CreateProductCategoryRequest();
        request.setCategoryName("Default category name.");
        request.setImageLink("86a1a96d-90ed-4283-97c2-e68fdfa576a1.jpg");

        // Create a task to execute the addCategory method asynchronously
        Task<Void> addCategoryTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Call the addCategory method of the data model
                    productCategoryDataModel.addCategory(request);
                } catch (Exception e) {
                    // If an exception occurs, show an error message
                    Platform.runLater(() -> {
                        // You can show an error message to the user here
                        System.err.println("Failed to add product category: " + e.getMessage());
                    });
                }
                return null;
            }
        };

        // Start the task in a background thread
        new Thread(addCategoryTask).start();

        Platform.runLater(() -> {
            categoryListView.setItems(productCategoryDataModel.getProductCategoryList());
            categoryListView.refresh();
        });

    }

    @FXML
    private void updateProductCategory() {
        ProductCategory selectedCategory = productCategoryDataModel.getCurrentProductCategory();
        if (selectedCategory != null && FxUtilTest.getComboBoxValue(categoryImageComboBoxUpdate) != null
                && categoryNameTextFieldUpdate.getText() != null) {

            // Get updated data from the UI
            String updatedImageLink = FxUtilTest.getComboBoxValue(categoryImageComboBoxUpdate);
            String updatedCategoryName = categoryNameTextFieldUpdate.getText();

            // Create a request object with the updated data
            CreateProductCategoryRequest updateRequest = new CreateProductCategoryRequest();
            updateRequest.setCategoryName(updatedCategoryName);
            updateRequest.setImageLink(updatedImageLink);

            // Create a task to execute the updateCategory method asynchronously
            Task<Void> updateCategoryTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the updateCategory method of the data model
                        productCategoryDataModel.updateCategory(selectedCategory.getId(), updateRequest);
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            updateCategoryTask.setOnFailed(event -> {
                Throwable exception = updateCategoryTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to update product category: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to update product category");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            updateCategoryTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    categoryListView.setItems(productCategoryDataModel.getProductCategoryList());
                    categoryListView.refresh();
                    overviewVBox.setVisible(true);
                    updateVBox.setVisible(false);
                    categoryImagePreview.setImage(null);
                    categoryNameLabelPreview.setText("");
                });
            });

            new Thread(updateCategoryTask).start();
        } else {
            // No category is selected or updated data is missing, handle this case as
            // needed
            // For example, show a message to the user indicating that they need to select a
            // category and provide updated data to update
        }
    }

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void setDataModel(ProductCategoryDataModel productCategoryDataModel) {
        if (this.productCategoryDataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.categoryListView.getStylesheets()
                .add(getClass()
                        .getResource("/dev/hideftbanana/netcafejavafxapp/css/custom_product_category_list_view.css")
                        .toExternalForm());
        this.productCategoryDataModel = productCategoryDataModel;
        this.productCategoryDataModel.loadData();

        this.categoryListView.setItems(productCategoryDataModel.getProductCategoryList());

        this.categoryListView.setCellFactory(
                new Callback<ListView<ProductCategory>, javafx.scene.control.ListCell<ProductCategory>>() {
                    @Override
                    public ListCell<ProductCategory> call(ListView<ProductCategory> listView) {
                        return new ProductCategoryListViewCell(imageCache);
                    }
                });

        categoryListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> productCategoryDataModel.setCurrentProductCategory(newSelection));

        productCategoryDataModel.currentProductCategoryProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {

            }
            if (newValue == null) {
                categoryImageComboBoxUpdate.setValue(null);
                categoryNameTextFieldUpdate.textProperty().setValue(null);
                validationLabel.textProperty().setValue(null);
            } else {
                updateSelectedImage();
                categoryNameLabelPreview
                        .setText(productCategoryDataModel.getCurrentProductCategory().getCategoryName());

                overviewVBox.setVisible(true);
                updateVBox.setVisible(false);
            }

        });

        // ------------------------------------------------------
        CompletableFuture<List<String>> futureImageNames = loadImageNamesAsync();
        futureImageNames.thenAcceptAsync(imageNames -> {
            Platform.runLater(() -> { // Ensure UI-related code runs on the JavaFX Application Thread
                System.out.println("Received image names:");
                for (String imageName : imageNames) {
                    System.out.println(imageName);
                }
                ObservableList<String> observableList = FXCollections.observableArrayList(imageNames);
                categoryImageComboBoxUpdate.setItems(observableList);

                categoryImageComboBoxUpdate.setCellFactory(param -> new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setGraphic(buildLayout(item));
                        } else {
                            setGraphic(null);
                        }
                    }
                });

                FxUtilTest.autoCompleteComboBoxPlus(categoryImageComboBoxUpdate,
                        (typedText, itemToCompare) -> itemToCompare
                                .toLowerCase().contains(typedText.toLowerCase()));

                categoryImageComboBoxUpdate.setConverter(new StringConverter<String>() {

                    @Override
                    public String toString(String object) {
                        return object != null ? object : "";
                    }

                    @Override
                    public String fromString(String string) {
                        return categoryImageComboBoxUpdate.getItems().stream().filter(object -> object.equals(string))
                                .findFirst()
                                .orElse(null);
                    }

                });

                ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<String>(
                        categoryImageComboBoxUpdate);
                comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, (KeyEvent event) -> {
                    if (event.getCode() == KeyCode.SPACE) {
                        event.consume();
                    }
                });
                categoryImageComboBoxUpdate.setSkin(comboBoxListViewSkin);

                categoryImageComboBoxUpdate.setVisibleRowCount(5);

            });
        });

        categoryImageComboBoxUpdate.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Your event handling code here
            System.out.println("Selected item changed to: " + newVal);
            // You can perform any actions you need based on the new selected item
            String imageName = newVal;
            if (imageName != null) {
                CompletableFuture.supplyAsync(() -> {
                    byte[] imageData = imageCache.get(imageName);
                    return new Image(new ByteArrayInputStream(imageData));
                }).thenAcceptAsync(image -> {
                    Platform.runLater(() -> {
                        if (image != null) {
                            currentProductCategoryImageViewUpdate.setImage(image);
                        } else {
                            // Set default image or handle error
                        }
                    });
                });
            }
        });
        // --------------------------------------------------

    }

    private HBox buildLayout(String imageName) {
        HBox hBox = new HBox();

        // Load the image
        ImageView imageView = new ImageView();

        CompletableFuture.supplyAsync(() -> {
            byte[] imageData = imageCache.get(imageName); // Blocking call
            return new Image(new ByteArrayInputStream(imageData));
        }).thenAcceptAsync(image -> {
            Platform.runLater(() -> {
                if (image != null) {
                    imageView.setImage(image);
                } else {
                    // Set default image or handle error
                }
            });
        });

        // Set fixed width and height for the ImageView
        imageView.setFitWidth(100); // Set the desired width
        imageView.setFitHeight(100); // Set the desired height

        // Create a label for the image name
        Label nameLabel = new Label(imageName);

        // Set fixed width for the label
        nameLabel.setPrefWidth(100); // Set the desired width
        nameLabel.setWrapText(true); // Enable text wrapping if needed

        // Add the image view and label to the HBox
        hBox.getChildren().addAll(imageView, nameLabel);

        // Set alignment to center
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }

    public CompletableFuture<List<String>> loadImageNamesAsync() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/image?page=0&pageSize=100"))
                .header("Authorization", "Bearer " + TokenManager.getAccessToken())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            ImagesResponse imageNamesResponse = objectMapper.readValue(response.body(),
                                    ImagesResponse.class);
                            return imageNamesResponse.getImageNames();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return Collections.emptyList(); // Return empty list on error
                        }
                    } else {
                        System.err.println("Failed to fetch image names. Status code: " + response.statusCode());
                        return Collections.emptyList(); // Return empty list on error
                    }
                });
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    private void updateSelectedImage() {
        String imageName = productCategoryDataModel.getCurrentProductCategory().getImageLink();
        if (imageName != null) {
            CompletableFuture.supplyAsync(() -> {
                byte[] imageData = imageCache.get(imageName);
                return new Image(new ByteArrayInputStream(imageData));
            }).thenAcceptAsync(image -> {
                Platform.runLater(() -> {
                    if (image != null) {
                        // currentProductCategoryImageView.setImage(image);
                        categoryImagePreview.setImage(image);
                    } else {
                        // Set default image or handle error
                    }
                });
            });
        }
    }

}
