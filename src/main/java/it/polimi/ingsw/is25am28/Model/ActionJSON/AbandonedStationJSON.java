package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the players' actions in the {@code abandonedStation} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */
public class AbandonedStationJSON extends ActionJSON {
    private Boolean wantToVisitStation;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<ComponentHelper<ItemColor>> itemsToBeTaken;

    @JsonCreator
    public AbandonedStationJSON() {
        this.wantToVisitStation = false;
        this.itemsToBeRemoved = new ArrayList<>();
        this.itemsToBeTaken = new ArrayList<>();
    }

    @JsonCreator
    public AbandonedStationJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("wantToVisitStation") Boolean wantToVisitStation,
            @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved,
            @JsonProperty("itemsToBeTaken") List<ComponentHelper<ItemColor>> itemsToBeTaken
    ) {
        super(playerNickname);

        this.wantToVisitStation = wantToVisitStation;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.itemsToBeTaken = itemsToBeTaken;
    }

    @JsonGetter("wantToVisitStation")
    public Boolean getWantToVisitStation() {
        return this.wantToVisitStation;
    }

    @JsonSetter("wantToVisitStation")
    public void setWantToVisitStation(Boolean wantToVisitStation) {
        this.wantToVisitStation = wantToVisitStation;
    }

    @JsonGetter("itemsToBeRemoved")
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() {
        return this.itemsToBeRemoved;
    }

    @JsonSetter("itemsToBeRemoved")
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) {
        this.itemsToBeRemoved = itemsToBeRemoved;
    }

    public void addItemsToBeRemoved(ComponentHelper<ItemColor> itemsToBeRemoved) {
        this.itemsToBeRemoved.add(itemsToBeRemoved);
    }

    @JsonGetter("itemsToBeTaken")
    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() {
        return this.itemsToBeTaken;
   }

    @JsonSetter("itemsToBeTaken")
    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) {
        this.itemsToBeTaken = itemsToBeTaken;
    }

    public void addItemsToBeTaken(ComponentHelper<ItemColor> itemsToBeTaken) {
        this.itemsToBeTaken.add(itemsToBeTaken);
    }
}
