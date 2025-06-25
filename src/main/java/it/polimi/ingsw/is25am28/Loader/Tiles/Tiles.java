package it.polimi.ingsw.is25am28.Loader.Tiles;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

/**
 * Defines the JSON structure used to parse and load saved tiles into the game.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "cannon",
        "shield",
        "battery",
        "cabin",
        "engine",
        "storage",
        "structural",
        "vital"
})
public class Tiles {
    @JsonProperty("cannon")         private List<Cannon> cannon;
    @JsonProperty("shield")         private List<Shield> shield;
    @JsonProperty("battery")        private List<Battery> battery;
    @JsonProperty("cabin")          private List<Cabin> cabin;
    @JsonProperty("engine")         private List<Engine> engine;
    @JsonProperty("storage")        private List<Storage> storage;
    @JsonProperty("structural")     private List<Structural> structural;
    @JsonProperty("vital")          private List<Vital> vital;

    @JsonGetter("cannon")
    public List<Cannon> getCannon() {
        return cannon;
    }

    @JsonSetter("cannon")
    public void setCannon(List<Cannon> cannon) {
        this.cannon = cannon;
    }

    @JsonGetter("shield")
    public List<Shield> getShield() {
        return shield;
    }

    @JsonSetter("shield")
    public void setShield(List<Shield> shield) {
        this.shield = shield;
    }

    @JsonGetter("battery")
    public List<Battery> getBattery() {
        return battery;
    }

    @JsonSetter("battery")
    public void setBattery(List<Battery> battery) {
        this.battery = battery;
    }

    @JsonGetter("cabin")
    public List<Cabin> getCabin() {
        return cabin;
    }

    @JsonSetter("cabin")
    public void setCabin(List<Cabin> cabin) {
        this.cabin = cabin;
    }

    @JsonGetter("engine")
    public List<Engine> getEngine() {
        return engine;
    }

    @JsonSetter("engine")
    public void setEngine(List<Engine> engine) {
        this.engine = engine;
    }

    @JsonGetter("storage")
    public List<Storage> getStorage() {
        return storage;
    }

    @JsonSetter("storage")
    public void setStorage(List<Storage> storage) {
        this.storage = storage;
    }

    @JsonGetter("structural")
    public List<Structural> getStructural() {
        return structural;
    }

    @JsonSetter("structural")
    public void setStructural(List<Structural> structural) {
        this.structural = structural;
    }

    @JsonGetter("vital")
    public List<Vital> getVital() {
        return vital;
    }

    @JsonSetter("vital")
    public void setVital(List<Vital> vital) {
        this.vital = vital;
    }
}
