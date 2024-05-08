package dev.hideftbanana.netcafejavafxapp.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import dev.hideftbanana.netcafejavafxapp.customcontrols.ProductListViewCell;
import dev.hideftbanana.netcafejavafxapp.datamodels.ProductDataModel;
import dev.hideftbanana.netcafejavafxapp.models.Product;
import dev.hideftbanana.netcafejavafxapp.models.request.CreateProductRequest;
import dev.hideftbanana.netcafejavafxapp.models.responses.ImagesResponse;
import dev.hideftbanana.netcafejavafxapp.models.responses.ProductCategoryResponse;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import dev.hideftbanana.netcafejavafxapp.services.categoryservices.ProductCategoryService;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ProductController extends BaseController implements Initializable {
    private ProductDataModel productDataModel;
    private ImageCache imageCache;

    @FXML
    private ListView<Product> productListView;

    @FXML
    private Label productNameLabel;
    @FXML
    private Label productDescriptionLabel;
    @FXML
    private Label productPriceLabel;
    @FXML
    private Label productQuantityLabel;
    @FXML
    private Label productCategoryLabel;

    @FXML
    private ImageView productImagePreview;
    @FXML
    private ImageView currentProductImageViewUpdate;

    @FXML
    private TextField productNameTextField;
    @FXML
    private TextField productDescriptionTextField;
    @FXML
    private TextField productPriceTextField;
    @FXML
    private TextField productQuantityTextField;

    @FXML
    private Button updateProductButton;

    @FXML
    private VBox overviewVBox;

    @FXML
    private VBox updateVBox;

    @FXML
    private ComboBox<String> imageLinkComboBox;

    @FXML
    private ComboBox<String> categoryComboBox;

    private ProductCategoryService productCategoryService;

    private List<ProductCategoryResponse> productCategoryResponses;

    @FXML
    private void switchToUpdate() {
        overviewVBox.setVisible(false);
        updateVBox.setVisible(true);
        imageLinkComboBox.valueProperty()
                .setValue(productDataModel.getCurrentProduct().getImageLink());
        productNameTextField.setText(productDataModel.getCurrentProduct().getName());
        productDescriptionTextField.setText(productDataModel.getCurrentProduct().getDescription());
        productQuantityTextField.setText(productDataModel.getCurrentProduct().getRemainQuantity() + "");
        productPriceTextField.setText(productDataModel.getCurrentProduct().getPrice() + "");
        categoryComboBox.valueProperty().setValue(productDataModel.getCurrentProduct().getCategoryName());
    }

    @FXML
    private void switchToOverview() {
        overviewVBox.setVisible(true);
        updateVBox.setVisible(false);
    }

    @FXML
    private void deleteProduct() {
        Product selectedProduct = productDataModel.getCurrentProduct();
        if (selectedProduct != null) {
            Task<Void> deleteProductTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the deleteProduct method of the data model
                        productDataModel.deleteProduct(selectedProduct.getId());
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            deleteProductTask.setOnFailed(event -> {
                Throwable exception = deleteProductTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to delete product: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to delete product");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            deleteProductTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    // Update the UI as needed, e.g., refresh product list, switch to overview view,
                    // etc.
                    // For example:
                    productListView.setItems(productDataModel.getProductList());
                    productListView.refresh();
                });
            });

            new Thread(deleteProductTask).start();
        } else {
            // No product is selected, handle this case as needed
            // For example, show a message to the user indicating that they need to select a
            // product to delete
        }
    }

    @FXML
    private void addProduct() {
        // Create a new request object with default data
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Default Product Name");
        request.setDescription("Default Product Description");
        request.setPrice(0.0f); // Default price
        request.setRemainQuantity(0); // Default quantity
        request.setProductImageLink("86a1a96d-90ed-4283-97c2-e68fdfa576a1.jpg"); // Default image link
        request.setCategoryId(1L); // Default category ID, adjust as needed

        // Create a task to execute the addProduct method asynchronously
        Task<Void> addProductTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Call the addProduct method of the data model
                    productDataModel.addProduct(request);
                } catch (Exception e) {
                    // If an exception occurs, show an error message
                    Platform.runLater(() -> {
                        // You can show an error message to the user here
                        System.err.println("Failed to add product: " + e.getMessage());
                    });
                }
                return null;
            }
        };

        // Start the task in a background thread
        new Thread(addProductTask).start();

        Platform.runLater(() -> {
            // Update the UI as needed, e.g., refresh product list
            // For example:
            productListView.setItems(productDataModel.getProductList());
            productListView.refresh();
        });
    }

    @FXML
    private void saveProduct() {
        Product selectedProduct = productDataModel.getCurrentProduct();
        if (selectedProduct != null && productNameTextField.getText() != null
                && productDescriptionTextField.getText() != null && productPriceTextField.getText() != null
                && productQuantityTextField.getText() != null && imageLinkComboBox.getValue() != null
                && categoryComboBox.getValue() != null) {

            // Get updated data from the UI
            String updatedName = productNameTextField.getText();
            String updatedDescription = productDescriptionTextField.getText();
            float updatedPrice = Float.parseFloat(productPriceTextField.getText());
            int updatedQuantity = Integer.parseInt(productQuantityTextField.getText());
            String updatedImageLink = FxUtilTest.getComboBoxValue(imageLinkComboBox);
            String updatedCategoryName = FxUtilTest.getComboBoxValue(categoryComboBox);
            Long categoryId = null;
            for (ProductCategoryResponse c : productCategoryResponses) {
                if (c.getCategoryName().equals(updatedCategoryName)) {
                    categoryId = c.getId();
                    break;
                }
            }

            // Validate input if necessary
            // Add your validation logic here...

            // Create a request object with the updated data
            CreateProductRequest updateRequest = new CreateProductRequest();
            updateRequest.setName(updatedName);
            updateRequest.setDescription(updatedDescription);
            updateRequest.setPrice(updatedPrice);
            updateRequest.setRemainQuantity(updatedQuantity);
            updateRequest.setProductImageLink(updatedImageLink);
            updateRequest.setCategoryId(categoryId);
            // You may need to set other properties as needed

            // Create a task to execute the updateProduct method asynchronously
            Task<Void> updateProductTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Call the updateProduct method of the data model
                        productDataModel.updateProduct(selectedProduct.getId(), updateRequest);
                    } catch (Exception e) {
                        // If an exception occurs, throw it to be handled by the exception handler
                        throw e;
                    }
                    return null;
                }
            };

            // Set up an exception handler for the task
            updateProductTask.setOnFailed(event -> {
                Throwable exception = updateProductTask.getException();
                // Handle the exception appropriately, such as displaying an error message to
                // the user
                System.err.println("Failed to update product: " + exception.getMessage());
                // Handle the exception by showing an alert to the user
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to update product");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            });

            // Set up a completion handler for the task
            updateProductTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    // Update the UI as needed, e.g., refresh product list, switch to overview view,
                    // etc.
                    // For example:
                    productListView.setItems(productDataModel.getProductList());
                    productListView.refresh();
                    overviewVBox.setVisible(true);
                    updateVBox.setVisible(false);
                    // Clear input fields
                    clearInputFields();
                });
            });

            new Thread(updateProductTask).start();
        } else {
            // No product is selected or updated data is missing, handle this case as needed
            // For example, show a message to the user indicating that they need to select a
            // product
            // and provide updated data to update
        }
    }

    private void clearInputFields() {
        productNameTextField.clear();
        productDescriptionTextField.clear();
        productPriceTextField.clear();
        productQuantityTextField.clear();
        imageLinkComboBox.getSelectionModel().clearSelection();
        categoryComboBox.getSelectionModel().clearSelection();

        productNameLabel.setText("");
        productDescriptionLabel.setText("");
        productPriceLabel.setText("");
        productQuantityLabel.setText("");
        productCategoryLabel.setText("");
        productImagePreview.setImage(null);
    }

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void setDataModel(ProductDataModel productDataModel) {
        if (this.productDataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        productCategoryService = new ProductCategoryService();

        this.productListView.getStylesheets()
                .add(getClass()
                        .getResource("/dev/hideftbanana/netcafejavafxapp/css/custom_product_category_list_view.css")
                        .toExternalForm());
        this.productDataModel = productDataModel;
        this.productDataModel.loadData();

        this.productListView.setItems(this.productDataModel.getProductList());
        this.productListView.setCellFactory(
                new Callback<ListView<Product>, javafx.scene.control.ListCell<Product>>() {
                    @Override
                    public ListCell<Product> call(ListView<Product> listView) {
                        return new ProductListViewCell(imageCache);
                    }
                });

        productListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> productDataModel.setCurrentProduct(newSelection));

        productDataModel.currentProductProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != null) {

            }
            if (newValue == null) {
                imageLinkComboBox.setValue(null);
                // categoryNameTextFieldUpdate.textProperty().setValue(null);
                // validationLabel.textProperty().setValue(null);

            } else {
                updateSelectedImage();
                // categoryNameLabelPreview
                // .setText(productCategoryDataModel.getCurrentProductCategory().getCategoryName());

                overviewVBox.setVisible(true);
                updateVBox.setVisible(false);
                productNameLabel.setText(productDataModel.getCurrentProduct().getName());
                productDescriptionLabel
                        .setText(productDataModel.getCurrentProduct().getDescription());
                productPriceLabel.setText("Product price: " + productDataModel.getCurrentProduct().getPrice() + "");
                productQuantityLabel
                        .setText("Product quantity: " + productDataModel.getCurrentProduct().getRemainQuantity() + "");
                productCategoryLabel
                        .setText("Product category: " + productDataModel.getCurrentProduct().getCategoryName());
                updateSelectedImage();

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
                imageLinkComboBox.setItems(observableList);

                imageLinkComboBox.setCellFactory(param -> new ListCell<String>() {
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

                FxUtilTest.autoCompleteComboBoxPlus(imageLinkComboBox,
                        (typedText, itemToCompare) -> itemToCompare
                                .toLowerCase().contains(typedText.toLowerCase()));

                imageLinkComboBox.setConverter(new StringConverter<String>() {

                    @Override
                    public String toString(String object) {
                        return object != null ? object : "";
                    }

                    @Override
                    public String fromString(String string) {
                        return imageLinkComboBox.getItems().stream().filter(object -> object.equals(string))
                                .findFirst()
                                .orElse(null);
                    }

                });

                ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<String>(
                        imageLinkComboBox);
                comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, (KeyEvent event) -> {
                    if (event.getCode() == KeyCode.SPACE) {
                        event.consume();
                    }
                });
                imageLinkComboBox.setSkin(comboBoxListViewSkin);

                imageLinkComboBox.setVisibleRowCount(5);

            });
        });

        imageLinkComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
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
                            currentProductImageViewUpdate.setImage(image);
                        } else {
                            // Set default image or handle error
                        }
                    });
                });
            }
        });

        loadProductCategoryComboBox();

        categoryComboBox.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    String imageName = null;
                    for (ProductCategoryResponse categoryResponse : productCategoryResponses) {
                        if (categoryResponse.getCategoryName().equals(item)) {
                            imageName = categoryResponse.getImageLink();
                            break;
                        }
                    }
                    setGraphic(buildLayoutCategoryItem(imageName, item));
                } else {
                    setGraphic(null);
                }
            }
        });

        FxUtilTest.autoCompleteComboBoxPlus(categoryComboBox,
                (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()));

        categoryComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object != null ? object : "";
            }

            @Override
            public String fromString(String string) {
                return categoryComboBox.getItems().stream().filter(object -> object.equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<String>(
                categoryComboBox);
        comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
            }
        });
        categoryComboBox.setSkin(comboBoxListViewSkin);

        categoryComboBox.setVisibleRowCount(5);

        // --------------------------------------------------
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

    private void loadProductCategoryComboBox() {
        productCategoryService.getAllProductCategories()
                .thenAcceptAsync(productCategoriesResponse -> {
                    productCategoryResponses = productCategoriesResponse
                            .getProductCategories();
                    Platform.runLater(() -> {
                        ObservableList<String> categoryNames = FXCollections.observableArrayList();
                        for (ProductCategoryResponse categoryResponse : productCategoryResponses) {
                            categoryNames.add(categoryResponse.getCategoryName());
                        }
                        categoryComboBox.setItems(categoryNames);
                    });
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    // Handle exceptions if needed
                    return null;
                });
    }

    private void updateSelectedImage() {
        String imageName = productDataModel.getCurrentProduct().getImageLink();
        if (imageName != null) {
            CompletableFuture.supplyAsync(() -> {
                byte[] imageData = imageCache.get(imageName);
                return new Image(new ByteArrayInputStream(imageData));
            }).thenAcceptAsync(image -> {
                Platform.runLater(() -> {
                    if (image != null) {
                        currentProductImageViewUpdate.setImage(image);
                        productImagePreview.setImage(image);
                    } else {
                        // Set default image or handle error
                    }
                });
            });
        }
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

    private HBox buildLayoutCategoryItem(String imageName, String categoryName) {
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
        Label nameLabel = new Label(categoryName);

        // Set fixed width for the label
        nameLabel.setPrefWidth(100); // Set the desired width
        nameLabel.setWrapText(true); // Enable text wrapping if needed

        // Add the image view and label to the HBox
        hBox.getChildren().addAll(imageView, nameLabel);

        // Set alignment to center
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
