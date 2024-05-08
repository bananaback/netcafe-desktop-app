package dev.hideftbanana.netcafejavafxapp.models;

import javafx.beans.property.*;

public class Computer {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty configuration = new SimpleStringProperty();
    private final FloatProperty pricePerHour = new SimpleFloatProperty();
    private final ObjectProperty<Room> room = new SimpleObjectProperty<>();

    public Computer() {
    }

    public Computer(long id, String configuration, float pricePerHour, Room room) {
        setId(id);
        setConfiguration(configuration);
        setPricePerHour(pricePerHour);
        setRoom(room);
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

    public String getConfiguration() {
        return configuration.get();
    }

    public StringProperty configurationProperty() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration.set(configuration);
    }

    public float getPricePerHour() {
        return pricePerHour.get();
    }

    public FloatProperty pricePerHourProperty() {
        return pricePerHour;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour.set(pricePerHour);
    }

    public Room getRoom() {
        return room.get();
    }

    public ObjectProperty<Room> roomProperty() {
        return room;
    }

    public void setRoom(Room room) {
        this.room.set(room);
    }

    @Override
    public String toString() {
        return "ID: " + getId() + "\n" +
                "Configuration: " + getConfiguration() + "\n" +
                "Price Per Hour: " + getPricePerHour() + "\n" +
                "Room: " + getRoom();
    }
}
