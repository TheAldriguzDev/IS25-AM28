package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Items.ItemColor;

import java.util.List;

public class VisitPlanetsJSON extends ActionJSON {
    private int chosenPlanetIndex;
    private List<ComponentHelper<ItemColor>> itemsToDrop;
    private List<ComponentHelper<ItemColor>> itemsToTake;

    @JsonCreator
    public VisitPlanetsJSON(
            @JsonProperty("chosenPlanetIndex") int chosenPlanetIndex,
            @JsonProperty("itemsToDrop") List<ComponentHelper<ItemColor>> itemsToDrop,
            @JsonProperty("itemsToTake") List<ComponentHelper<ItemColor>> itemsToTake
    ) {
        this.chosenPlanetIndex = chosenPlanetIndex;
        this.itemsToDrop = itemsToDrop;
        this.itemsToTake = itemsToTake;
    }

    @JsonGetter("chosenPlanetIndex")
    public int getChosenPlanetIndex() {
        return this.chosenPlanetIndex;
    }

    @JsonSetter("chosenPlanetIndex")
    public void setChosenPlanetIndex(int chosenPlanetIndex) {
        this.chosenPlanetIndex = chosenPlanetIndex;
    }

    @JsonGetter("itemsToDrop")
    public List<ComponentHelper<ItemColor>> getItemsToDrop() {
        return this.itemsToDrop;
    }

    @JsonSetter("itemsToDrop")
    public void setItemsToDrop(List<ComponentHelper<ItemColor>> itemsToDrop) {
        this.itemsToDrop = itemsToDrop;
    }

    @JsonGetter("itemsToTake")
    public List<ComponentHelper<ItemColor>> getItemsToTake() {
        return this.itemsToTake;
    }

    @JsonSetter("itemsToTake")
    public void setItemsToTake(List<ComponentHelper<ItemColor>> itemsToTake) {
        this.itemsToTake = itemsToTake;
    }
}
