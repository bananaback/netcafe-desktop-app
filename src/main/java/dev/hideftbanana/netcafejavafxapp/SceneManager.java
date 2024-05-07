package dev.hideftbanana.netcafejavafxapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.hideftbanana.netcafejavafxapp.controllers.BaseController;
import dev.hideftbanana.netcafejavafxapp.controllers.MainAppController;
import dev.hideftbanana.netcafejavafxapp.datamodels.ProductCategoryDataModel;
import dev.hideftbanana.netcafejavafxapp.datamodels.ProductDataModel;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SceneManager {
    private final Stage rootStage;
    private final ImageCache imageCache;
    private final ProductCategoryDataModel productCategoryDataModel;
    private ProductDataModel productDataModel;

    public SceneManager(Stage rootStage, ImageCache imageCache, ProductCategoryDataModel productCategoryDataModel,
            ProductDataModel productDataModel) {
        if (rootStage == null || imageCache == null || productCategoryDataModel == null || productDataModel == null) {
            throw new IllegalArgumentException("Root stage, image cache and data models must not be null.");
        }
        this.rootStage = rootStage;
        this.imageCache = imageCache;
        this.productCategoryDataModel = productCategoryDataModel;
        this.productDataModel = productDataModel;
    }

    private final Map<String, Scene> scenes = new HashMap<>();

    public void switchingScene(String url) {
        Scene scene = scenes.computeIfAbsent(url, u -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(u));
            loader.setControllerFactory(param -> {
                try {
                    Object controller = param.getDeclaredConstructor().newInstance();
                    System.out.println(controller.getClass());
                    if (controller instanceof MainAppController) {
                        MainAppController mainAppController = (MainAppController) controller;
                        mainAppController.setImageCache(imageCache);
                        mainAppController.setProductCategoryDataModel(productCategoryDataModel);
                        mainAppController.setProductDataModel(productDataModel);
                        System.out.println("Image cache passed to MainAppController!!!");
                    }
                    return controller;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            try {
                Pane p = loader.load();
                BaseController controller = loader.getController();
                controller.setSceneManager(this);
                return new Scene(p);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        rootStage.setScene(scene);
    }

    public void showRootStage() {
        this.rootStage.show();
    }

    public void setStageSize(double x, double y) {
        rootStage.setWidth(x);
        rootStage.setHeight(y);
        rootStage.centerOnScreen();
    }

    public void setMinSize(double x, double y) {
        rootStage.setMinWidth(x);
        rootStage.setMinHeight(y);
        rootStage.centerOnScreen();
    }

    public void closeStage() {
        this.rootStage.close();
    }

    public void setFullScreen(boolean b) {
        this.rootStage.setFullScreen(b);
    }

}