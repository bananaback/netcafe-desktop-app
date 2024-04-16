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
import dev.hideftbanana.netcafejavafxapp.models.responses.ImagesResponse;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
    private TextField categoryNameTextField;
    @FXML
    private ImageView currentProductCategoryImageView;
    @FXML
    private ComboBox<String> categoryImageComboBox;

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

        productCategoryDataModel.currentProductCategoryProperty()
                .addListener((obs, oldProductCategory, newProductCategory) -> {
                    if (newProductCategory == null) {
                        categoryListView.getSelectionModel().clearSelection();
                    } else {
                        categoryListView.getSelectionModel().select(newProductCategory);
                        categoryNameTextField.setText(newProductCategory.getCategoryName());
                        CompletableFuture.supplyAsync(() -> {
                            byte[] imageData = imageCache.get(newProductCategory.getImageLink()); // Blocking call
                            return new Image(new ByteArrayInputStream(imageData));
                        }).thenAcceptAsync(image -> {
                            Platform.runLater(() -> {
                                if (image != null) {
                                    currentProductCategoryImageView.setImage(image);
                                } else {
                                    // Set default image or handle error
                                }
                            });
                        });
                    }
                });

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
                .uri(URI.create("http://localhost:8080/api/image"))
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
        CompletableFuture<List<String>> futureImageNames = loadImageNamesAsync();
        futureImageNames.thenAcceptAsync(imageNames -> {
            Platform.runLater(() -> { // Ensure UI-related code runs on the JavaFX Application Thread
                System.out.println("Received image names:");
                for (String imageName : imageNames) {
                    System.out.println(imageName);
                }
                ObservableList<String> observableList = FXCollections.observableArrayList(imageNames);
                categoryImageComboBox.setItems(observableList);

                categoryImageComboBox.setCellFactory(param -> new ListCell<String>() {
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

                FxUtilTest.autoCompleteComboBoxPlus(categoryImageComboBox, (typedText, itemToCompare) -> itemToCompare
                        .toLowerCase().contains(typedText.toLowerCase()));

                categoryImageComboBox.setConverter(new StringConverter<String>() {

                    @Override
                    public String toString(String object) {
                        return object != null ? object : "";
                    }

                    @Override
                    public String fromString(String string) {
                        return categoryImageComboBox.getItems().stream().filter(object -> object.equals(string))
                                .findFirst()
                                .orElse(null);
                    }

                });

                ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<String>(
                        categoryImageComboBox);
                comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, (KeyEvent event) -> {
                    if (event.getCode() == KeyCode.SPACE) {
                        event.consume();
                    }
                });
                categoryImageComboBox.setSkin(comboBoxListViewSkin);

                categoryImageComboBox.setVisibleRowCount(5);
            });
        });
    }

}
