package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import java.util.ArrayList;
import java.util.List;

public class SmugglersJSON extends ActionJSON {
    private boolean takeLoot;
    private ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken;
    private ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<ComponentHelper<Integer>> doubleCannonsToActivateCoordinates;

    public SmugglersJSON() {}

    public SmugglersJSON(@JsonProperty("playerNickname") String playerNickname,
                         @JsonProperty("takeLoot") boolean takeLoot,
                         @JsonProperty("itemsToBeTaken") ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken,
                         @JsonProperty("itemsToBeRemoved") ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved,
                         @JsonProperty("doubleCannonsToActivateCoordinates") List<ComponentHelper<Integer>> doubleCannonsToActivateCoordinates) {
        super(playerNickname);
        this.takeLoot = takeLoot;
        this.itemsToBeTaken = itemsToBeTaken;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

    @JsonGetter("takeLoot")
    public boolean getTakeLoot() {
        return takeLoot;
    }

    @JsonSetter
    public void setTakeLoot(boolean takeLoot) {
        this.takeLoot = takeLoot;
    }

    @JsonGetter("itemsToBeTaken")
    public ArrayList<ComponentHelper<ItemColor>> getItemsToBeTaken() {
        return itemsToBeTaken;
    }

    @JsonSetter("itemsToBeTaken")
    public void setItemsToBeTaken(ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken) {
        this.itemsToBeTaken = itemsToBeTaken;
    }

    @JsonGetter("itemsToBeRemoved")
    public ArrayList<ComponentHelper<ItemColor>> getItemsToBeRemoved() {
        return itemsToBeRemoved;
    }

    @JsonSetter("itemsToBeRemoved")
    public void setItemsToBeRemoved(ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved) {
        this.itemsToBeRemoved = itemsToBeRemoved;
    }

    @JsonGetter("doubleCannonsToActivateCoordinates")
    public List<ComponentHelper<Integer>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }

    @JsonSetter("doubleCannonsToActivateCoordinates")
    public void setDoubleCannonsToActivateCoordinates(List<ComponentHelper<Integer>> doubleCannonsToActivateCoordinates) {
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

}