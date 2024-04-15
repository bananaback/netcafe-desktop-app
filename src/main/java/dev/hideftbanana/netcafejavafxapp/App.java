package dev.hideftbanana.netcafejavafxapp;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import dev.hideftbanana.netcafejavafxapp.datamodels.ProductCategoryDataModel;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setWidth(AppConfig.LOGIN_STAGE_WIDTH);
        stage.setHeight(AppConfig.LOGIN_STAGE_HEIGHT);
        stage.setMinWidth(AppConfig.LOGIN_STAGE_MIN_WIDTH);
        stage.setMinHeight(AppConfig.LOGIN_STAGE_MIN_HEIGHT);

        stage.setOnCloseRequest(event -> {
            TokenManager.stopTokenRefreshScheduler();
            // Additional cleanup code if needed
        });

        // stage.initStyle(StageStyle.UNDECORATED);
        ImageCache imageCache = new ImageCache(100);
        ProductCategoryDataModel productCategoryDataModel = new ProductCategoryDataModel();
        SceneManager sceneManager = new SceneManager(stage, imageCache, productCategoryDataModel);
        sceneManager.switchingScene("/dev/hideftbanana/netcafejavafxapp/fxml/admin_login_page.fxml");
        sceneManager.showRootStage();

        TokenManager.startTokenRefreshScheduler();
    }

    public static void main(String[] args) {
        launch();
    }

}
