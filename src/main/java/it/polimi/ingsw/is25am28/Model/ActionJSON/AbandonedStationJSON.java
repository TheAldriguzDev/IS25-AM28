package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

import java.util.ArrayList;
import java.util.List;

public class AbandonedStationJSON extends ActionJSON {
    private boolean wantToVisitStation;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<ComponentHelper<ItemColor>> itemsToBeTaken;

    /**
     * Default constructor
     * */
    public AbandonedStationJSON() {
        this.wantToVisitStation = false;
        this.itemsToBeRemoved = new ArrayList<>();
        this.itemsToBeTaken = new ArrayList<>();
    }

    public AbandonedStationJSON(@JsonProperty("playerNickname") String playerNickname,
                             @JsonProperty("wantToVisitStation") boolean wantToVisitStation,
                             @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved,
                            @JsonProperty("itemsToBeTaken") List<ComponentHelper<ItemColor>> itemsToBeTaken) {
        super(playerNickname);
        this.wantToVisitStation = wantToVisitStation;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.itemsToBeTaken = itemsToBeTaken;
    }

    public boolean getWantToVisitStation() {
        return this.wantToVisitStation;
    }

    public void setWantToVisitStation(boolean wantToVisitStation) {
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
