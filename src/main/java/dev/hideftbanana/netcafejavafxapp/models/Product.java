package dev.hideftbanana.netcafejavafxapp.models;

import javafx.beans.property.*;

public class Product {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final FloatProperty price = new SimpleFloatProperty();
    private final IntegerProperty remainQuantity = new SimpleIntegerProperty();
    private final StringProperty imageLink = new SimpleStringProperty();
    private final LongProperty categoryId = new SimpleLongProperty();
    private final StringProperty categoryName = new SimpleStringProperty(); // New property for category name

    public Product() {
    }

    public Product(long id, String name, String description, float price, int remainQuantity, String imageLink,
            long categoryId, String categoryName) {
        setId(id);
        setName(name);
        setDescription(description);
        setPrice(price);
        setRemainQuantity(remainQuantity);
        setImageLink(imageLink);
        setCategoryId(categoryId);
        setCategoryName(categoryName);
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public float getPrice() {
        return price.get();
    }

    public FloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public int getRemainQuantity() {
        return remainQuantity.get();
    }

    public IntegerProperty remainQuantityProperty() {
        return remainQuantity;
    }

    public void setRemainQuantity(int remainQuantity) {
        this.remainQuantity.set(remainQuantity);
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

    public long getCategoryId() {
        return categoryId.get();
    }

    public LongProperty categoryIdProperty() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId.set(categoryId);
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    public StringProperty categoryNameProperty() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName.set(categoryName);
    }

    @Override
    public String toString() {
        return "ID: " + getId() + "\n" +
                "Name: " + getName() + "\n" +
                "Description: " + getDescription() + "\n" +
                "Price: " + getPrice() + "\n" +
                "Remaining Quantity: " + getRemainQuantity() + "\n" +
                "Image Link: " + getImageLink() + "\n" +
                "Category ID: " + getCategoryId() + "\n" +
                "Category Name: " + getCategoryName();
    }
}
