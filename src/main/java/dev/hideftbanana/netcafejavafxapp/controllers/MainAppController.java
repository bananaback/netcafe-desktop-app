package dev.hideftbanana.netcafejavafxapp.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.Animation;
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

import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;

public class MainAppController extends BaseController implements Initializable {

    @FXML
    private HBox topHBox;
    @FXML
    private HBox bottomHBox;
    @FXML
    private HBox centerHBox;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button servicesButton;
    @FXML
    private Button monitorButton;
    @FXML
    private Button resourcesButton;
    private FadeTransition fadeTransition;

    private ImageCache imageCache;

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/welcome_center_section.fxml");

        fadeElementsIn();

        // Add event handlers to buttons
        dashboardButton.setOnAction(event -> handleButtonSelection(dashboardButton));
        servicesButton.setOnAction(event -> handleButtonSelection(servicesButton));
        monitorButton.setOnAction(event -> handleButtonSelection(monitorButton));
        resourcesButton.setOnAction(event -> handleButtonSelection(resourcesButton));
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

        // Select the clicked button
        selectedButton.setStyle("-fx-background-color: #197B1D;");

        // Replace the center section with the appropriate FXML content
        if (selectedButton == resourcesButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/images.fxml");
        } else if (selectedButton == dashboardButton) {
            replaceCenterWithFXML("/dev/hideftbanana/netcafejavafxapp/fxml/welcome_center_section.fxml");
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
            }
            newCenter.setOpacity(0); // Set opacity to 0 initially

            // Cancel any ongoing fade transition
            if (fadeTransition != null && fadeTransition.getStatus() == Animation.Status.RUNNING) {
                fadeTransition.stop();
            }

            // Add newCenter to centerHBox
            centerHBox.getChildren().clear();
            centerHBox.getChildren().add(newCenter);
            centerHBox.setAlignment(Pos.CENTER);

            // Create a new fade transition for the new center content
            fadeTransition = new FadeTransition(Duration.seconds(2), newCenter);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
