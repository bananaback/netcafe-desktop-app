package dev.hideftbanana.netcafejavafxapp.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductCategory {
    private SimpleLongProperty id = new SimpleLongProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty imageLink = new SimpleStringProperty();

    private SimpleBooleanProperty isNew = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isModified = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isDeleted = new SimpleBooleanProperty(false);
    private SimpleStringProperty validationTextProperty = new SimpleStringProperty("");

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

    public Boolean getIsNew() {
        return isNew.get();
    }

    public BooleanProperty isNewProperty() {
        return isNew;
    }

    public boolean getIsModified() {
        return isModified.get();
    }

    public BooleanProperty isModifiedProperty() {
        return isModified;
    }

    public void setIsModified(boolean isModified) {
        this.isModified.set(isModified);
    }

    public boolean getIsDeleted() {
        return isDeleted.get();
    }

    public BooleanProperty isDeletedProperty() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted.set(isDeleted);
    }

    public void setIsNew(Boolean isNew) {
        this.isNew.set(isNew);
    }

    public String getValidationText() {
        return validationTextProperty.get();
    }

    public StringProperty valiationTextProperty() {
        return validationTextProperty;
    }

    public void setValidationText(String text) {
        this.validationTextProperty.set(text);
    }

    @Override
    public String toString() {
        return this.getId() + "\n" + this.getCategoryName() + "\n" + this.getImageLink();
    }
}
