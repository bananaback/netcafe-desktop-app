package dev.hideftbanana.netcafejavafxapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import dev.hideftbanana.netcafejavafxapp.customcontrols.ProductCategoryListViewCell;
import dev.hideftbanana.netcafejavafxapp.datamodels.ProductCategoryDataModel;
import dev.hideftbanana.netcafejavafxapp.models.ProductCategory;
import dev.hideftbanana.netcafejavafxapp.services.cacheservices.ImageCache;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CategoryController extends BaseController implements Initializable {

    private ProductCategoryDataModel productCategoryDataModel;
    private ImageCache imageCache;

    @FXML
    private ListView<ProductCategory> categoryListView;

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
                    }
                });
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
