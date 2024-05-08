package dev.hideftbanana.netcafejavafxapp.models;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();

    public Room() {
    }

    public Room(long id, String name) {
        setId(id);
        setName(name);
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

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
