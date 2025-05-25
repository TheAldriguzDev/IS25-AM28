package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

import java.util.ArrayList;
import java.util.List;

public class AbandonedStationJSON extends ActionJSON {
    private Boolean wantToVisitStation;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<ComponentHelper<ItemColor>> itemsToBeTaken;

    /**
     * Default constructor
     * */
    public AbandonedStationJSON() {
        // TODO: This can break the card since there's no GUI-side check to see whether the player
        //       answered either T or F (in the case it's set to null as default).
        //       .
        //       (if you want in "YOUR ACTIONS" to see T/F iff the player actually chose something, then it
        //        needs to be set to null as default value, but then a null-check is needed GUI-side)
        //       (TUI-side null-check is already implemented in the generatePlayerActionsWidget method)
        this.wantToVisitStation = false;

        this.itemsToBeRemoved = new ArrayList<>();
        this.itemsToBeTaken = new ArrayList<>();
    }

    public AbandonedStationJSON(@JsonProperty("playerNickname") String playerNickname,
                             @JsonProperty("wantToVisitStation") Boolean wantToVisitStation,
                             @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved,
                            @JsonProperty("itemsToBeTaken") List<ComponentHelper<ItemColor>> itemsToBeTaken) {
        super(playerNickname);
        this.wantToVisitStation = wantToVisitStation;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.itemsToBeTaken = itemsToBeTaken;
    }

    public Boolean getWantToVisitStation() {
        return this.wantToVisitStation;
    }

    public void setWantToVisitStation(Boolean wantToVisitStation) {
        this.wantToVisitStation = wantToVisitStation;
    }

    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() {
        return this.itemsToBeRemoved;
    }

    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) {
        this.itemsToBeRemoved = itemsToBeRemoved;
    }

    public void addItemsToBeRemoved(ComponentHelper<ItemColor> itemsToBeRemoved) {
        this.itemsToBeRemoved.add(itemsToBeRemoved);
    }

    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() {
        return this.itemsToBeTaken;
   }

    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) {
        this.itemsToBeTaken = itemsToBeTaken;
    }

    public void addItemsToBeTaken(ComponentHelper<ItemColor> itemsToBeTaken) {
        this.itemsToBeTaken.add(itemsToBeTaken);
    }
}
