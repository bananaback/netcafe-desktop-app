package dev.hideftbanana.netcafejavafxapp;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setWidth(AppConfig.LOGIN_STAGE_WIDTH);
        stage.setHeight(AppConfig.LOGIN_STAGE_HEIGHT);
        stage.initStyle(StageStyle.UNDECORATED);
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchingScene("/dev/hideftbanana/netcafejavafxapp/fxml/admin_login_page.fxml");
        sceneManager.showRootStage();
    }

    public static void main(String[] args) {
        launch();
    }

}
