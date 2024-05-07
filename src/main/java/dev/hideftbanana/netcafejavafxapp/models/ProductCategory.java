package dev.hideftbanana.netcafejavafxapp.models;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductCategory {
    private SimpleLongProperty id = new SimpleLongProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty imageLink = new SimpleStringProperty();

    public ProductCategory() {
    }

    public ProductCategory(long id, String categoryName, String imageLink) {
        setId(id);
        setCategoryName(categoryName);
        setImageLink(imageLink);
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getCategoryName() {
        return name.get();
    }

    public StringProperty categoryNameProperty() {
        return name;
    }

    public void setCategoryName(String categoryName) {
        this.name.set(categoryName);
    }

    public String getImageLink() {
        return imageLink.get();
    }

    public StringProperty imageLinkProperty() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink.set(imageLink);
    }

    @Override
    public String toString() {
        return this.getId() + "\n" + this.getCategoryName() + "\n" + this.getImageLink();
    }
}
