package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class SplashScreenController extends BaseController implements Initializable {

    @FXML
    private Label introLabel;

    private FadeTransition fadeTransition;
    private TranslateTransition translateTransition;
    private PauseTransition pauseTransition;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Opacity Transition
        fadeTransition = new FadeTransition(Duration.seconds(2), introLabel);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        // Margin Transition
        translateTransition = new TranslateTransition(Duration.seconds(1.5), introLabel);
        translateTransition.setFromY(1000);
        translateTransition.setToY(0);

        // Introduce a delay of 1 second
        pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            fadeOutAndMoveLabel();
        });

        // Start the animations
        fadeTransition.play();
        translateTransition.play();
        pauseTransition.play();
    }

    private void fadeOutAndMoveLabel() {
        // Fade out the introLabel
        FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(2), introLabel);
        fadeOutTransition.setFromValue(1);
        fadeOutTransition.setToValue(0);

        // Move the introLabel out of the visible area
        TranslateTransition moveTransition = new TranslateTransition(Duration.seconds(2), introLabel);
        moveTransition.setFromY(0);
        moveTransition.setToY(-1000);

        // Cleanup
        fadeOutTransition.setOnFinished(event -> {
            clearTransitions();
            sceneManager.setStageSize(1440, 1024);
            sceneManager.switchingScene("/dev/hideftbanana/netcafejavafxapp/fxml/admin_main_2.fxml");
        });

        // Start the animations
        fadeOutTransition.play();
        moveTransition.play();
    }

    private void clearTransitions() {
        // Stop ongoing animations
        if (fadeTransition != null) {
            fadeTransition.stop();
        }
        if (translateTransition != null) {
            translateTransition.stop();
        }
        if (pauseTransition != null) {
            pauseTransition.stop();
        }
    }
}
