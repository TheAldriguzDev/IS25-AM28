package it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;

import java.util.List;
import java.util.Map;

public class ShipConstructionDTO extends StateJSON {
    private List<Map<String, Object>> all_components;
    private List<Integer> flipped_components;
    private List<Integer> selected_components;

    public ShipConstructionDTO() {}

    public ShipConstructionDTO(
            @JsonProperty("all_components") List<Map<String, Object>> all_components,
            @JsonProperty("flipped_components") List<Integer> flipped_components,
            @JsonProperty("selected_components") List<Integer> selected_components ) {
        this.all_components = all_components;
        this.flipped_components = flipped_components;
        this.selected_components = selected_components;
    }

    @JsonGetter("all_components")
    public List<Map<String, Object>> getAllComponents() {
        return all_components;
    }

    @JsonSetter("all_components")
    public ShipConstructionDTO setAllComponents(List<Map<String, Object>> all_components) {
        this.all_components = all_components;
        return this;
    }

    @JsonGetter("flipped_components")
    public List<Integer> getFlippedComponents() {
        return selected_components;
    }

    @JsonSetter("flipped_components")
    public ShipConstructionDTO setFlippedComponents(List<Integer> flipped_components) {
        this.flipped_components = flipped_components;
        return this;
    }

    @JsonGetter("selected_components")
    public List<Integer> getSelectedComponents() {
        return selected_components;
    }

    @JsonSetter("selected_components")
    public ShipConstructionDTO setSelectedComponents(List<Integer> selected_components) {
        this.selected_components = selected_components;
        return this;
    }
}
