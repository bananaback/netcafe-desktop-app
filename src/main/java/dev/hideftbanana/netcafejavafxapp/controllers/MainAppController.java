package dev.hideftbanana.netcafejavafxapp.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.datamodels.ComputerDataModel;
import dev.hideftbanana.netcafejavafxapp.datamodels.ProductCategoryDataModel;
import dev.hideftbanana.netcafejavafxapp.datamodels.ProductDataModel;
import dev.hideftbanana.netcafejavafxapp.datamodels.RoomDataModel;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;

public class MainAppController extends BaseController implements Initializable {

    @FXML
    private HBox topHBox;
    @FXML
    private HBox bottomHBox;
    @FXML
    private HBox centerHBox;
    @FXML
    private HBox rightHBox;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button servicesButton;
    @FXML
    private Button monitorButton;
    @FXML
    private Button resourcesButton;
    @FXML
    private Button categoriesButton;
    @FXML
    private Button productsButton;
    @FXML
    private Button roomsButton;
    @FXML
    private Button computersButton;

    private ImageCache imageCache;
    private ProductCategoryDataModel productCategoryDataModel;
    private ProductDataModel productDataModel;
    private RoomDataModel roomDataModel;
    private ComputerDataModel computerDataModel;

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/welcome_center_section.fxml");
        rightHBox.setPrefWidth(0);

        fadeElementsIn();

        // Add event handlers to buttons
        dashboardButton.setOnAction(event -> handleButtonSelection(dashboardButton));
        servicesButton.setOnAction(event -> handleButtonSelection(servicesButton));
        monitorButton.setOnAction(event -> handleButtonSelection(monitorButton));
        resourcesButton.setOnAction(event -> handleButtonSelection(resourcesButton));
        categoriesButton.setOnAction(event -> handleButtonSelection(categoriesButton));
        productsButton.setOnAction(event -> handleButtonSelection(productsButton));
        roomsButton.setOnAction(event -> handleButtonSelection(roomsButton));
        computersButton.setOnAction(event -> handleButtonSelection(computersButton));

    }

    private void fadeElementsIn() {
        FadeTransition fadeTransitionTop = new FadeTransition(Duration.seconds(2), topHBox);
        fadeTransitionTop.setFromValue(0);
        fadeTransitionTop.setToValue(1);
        fadeTransitionTop.play();

        FadeTransition fadeTransitionCenter = new FadeTransition(Duration.seconds(2), centerHBox);
        fadeTransitionCenter.setFromValue(0);
        fadeTransitionCenter.setToValue(1);
        fadeTransitionCenter.play();

        FadeTransition fadeTransitionBottom = new FadeTransition(Duration.seconds(2), bottomHBox);
        fadeTransitionBottom.setFromValue(0);
        fadeTransitionBottom.setToValue(1);
        fadeTransitionBottom.play();
    }

    private void handleButtonSelection(Button selectedButton) {
        // Deselect all buttons
        dashboardButton.setStyle("-fx-background-color: #1C1A1A;");
        servicesButton.setStyle("-fx-background-color: #1C1A1A;");
        monitorButton.setStyle("-fx-background-color: #1C1A1A;");
        resourcesButton.setStyle("-fx-background-color: #1C1A1A;");
        categoriesButton.setStyle("-fx-background-color: #1C1A1A;");
        productsButton.setStyle("-fx-background-color: #1C1A1A;");
        roomsButton.setStyle("-fx-background-color: #1C1A1A;");
        computersButton.setStyle("-fx-background-color: #1C1A1A;");

        // Select the clicked button
        selectedButton.setStyle("-fx-background-color: #197B1D;");

        // Replace the center section with the appropriate FXML content
        if (selectedButton == resourcesButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/images.fxml");
            rightHBox.getChildren().clear();
        } else if (selectedButton == dashboardButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/welcome_center_section.fxml");
            rightHBox.getChildren().clear();
        } else if (selectedButton == servicesButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/service_management.fxml");
            rightHBox.getChildren().clear();
        } else if (selectedButton == categoriesButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/category_management.fxml");
            rightHBox.getChildren().clear();
        } else if (selectedButton == productsButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/product_management.fxml");
            rightHBox.getChildren().clear();
        } else if (selectedButton == roomsButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/room_management.fxml");
            rightHBox.getChildren().clear();
        } else if (selectedButton == computersButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/computer_management.fxml");
            rightHBox.getChildren().clear();
        } else if (selectedButton == monitorButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/monitor_center.fxml");
        }
    }

    private void replaceCenterWithFXML(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent newCenter = loader.load();
            BaseController controller = loader.getController();
            if (controller instanceof ImageController) {
                ImageController imageController = (ImageController) controller;
                imageController.setImageCache(imageCache);
            } else if (controller instanceof CategoryController) {
                CategoryController categoryController = (CategoryController) controller;
                categoryController.setImageCache(imageCache);
                categoryController.setDataModel(productCategoryDataModel);
            } else if (controller instanceof ProductController) {
                ProductController productController = (ProductController) controller;
                productController.setImageCache(imageCache);
                productController.setDataModel(productDataModel);
            } else if (controller instanceof RoomController) {
                RoomController roomController = (RoomController) controller;
                roomController.setImageCache(imageCache);
                roomController.setDataModel(roomDataModel);
            } else if (controller instanceof ComputerController) {
                ComputerController computerController = (ComputerController) controller;
                computerController.setImageCache(imageCache);
                computerController.setDataModel(computerDataModel);
            } else if (controller instanceof MonitorController) {
                MonitorController monitorController = (MonitorController) controller;
                monitorController.setImageCache(imageCache);
            } else if (controller instanceof UserController) {

            }

            // Add newCenter to centerHBox
            centerHBox.getChildren().clear();
            centerHBox.getChildren().add(newCenter);
            centerHBox.setAlignment(Pos.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProductCategoryDataModel(ProductCategoryDataModel productCategoryDataModel) {
        this.productCategoryDataModel = productCategoryDataModel;
    }

    public void setProductDataModel(ProductDataModel productDataModel) {
        this.productDataModel = productDataModel;
    }

    public void setRoomDataModel(RoomDataModel roomDataModel) {
        this.roomDataModel = roomDataModel;
    }

    public void setComputerDataModel(ComputerDataModel computerDataModel) {
        this.computerDataModel = computerDataModel;
    }
}
