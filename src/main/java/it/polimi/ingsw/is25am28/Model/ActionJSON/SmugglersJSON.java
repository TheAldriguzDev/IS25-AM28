package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class SmugglersJSON extends ActionJSON {
    private boolean takeLoot;
    private List<ComponentHelper<ItemColor>> itemsToBeTaken;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleCannonsToActivateCoordinates;

    public SmugglersJSON() {
        this.takeLoot = false;
        this.itemsToBeTaken = new ArrayList<>();
        this.itemsToBeRemoved = new ArrayList<>();
        this.doubleCannonsToActivateCoordinates = new ArrayList<>();
    }

    public SmugglersJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("takeLoot") boolean takeLoot,
            @JsonProperty("itemsToBeTaken") List<ComponentHelper<ItemColor>> itemsToBeTaken,
            @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved,
            @JsonProperty("doubleCannonsToActivateCoordinates") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleCannonsToActivateCoordinates
    ) {
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
    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() {
        return itemsToBeTaken;
    }

    @JsonSetter("itemsToBeTaken")
    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) {
        this.itemsToBeTaken = itemsToBeTaken;
    }

    @JsonGetter("itemsToBeRemoved")
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() {
        return itemsToBeRemoved;
    }

    @JsonSetter("itemsToBeRemoved")
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) {
        this.itemsToBeRemoved = itemsToBeRemoved;
    }

    @JsonGetter("doubleCannonsToActivateCoordinates")
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }

    @JsonSetter("doubleCannonsToActivateCoordinates")
    public void setDoubleCannonsToActivateCoordinates(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleCannonsToActivateCoordinates) {
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

}