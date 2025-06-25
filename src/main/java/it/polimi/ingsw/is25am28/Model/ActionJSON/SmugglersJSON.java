package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the players' actions in the {@code smugglers} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */
public class SmugglersJSON extends ActionJSON {
    private boolean takeLoot;
    private List<ComponentHelper<ItemColor>> itemsToBeTaken;
    private List<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private List<CoordinatePair> batteriesToBeStolen;
    private List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates;

    @JsonCreator
    public SmugglersJSON() {
        this.takeLoot = false;
        this.itemsToBeTaken = new ArrayList<>();
        this.itemsToBeRemoved = new ArrayList<>();
        this.batteriesToBeStolen = new ArrayList<>();
        this.doubleCannonsToActivateCoordinates = new ArrayList<>();
    }

    @JsonCreator
    public SmugglersJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("takeLoot") boolean takeLoot,
            @JsonProperty("itemsToBeTaken") List<ComponentHelper<ItemColor>> itemsToBeTaken,
            @JsonProperty("itemsToBeRemoved") List<ComponentHelper<ItemColor>> itemsToBeRemoved,
            @JsonProperty("batteriesToBeStolen") List<CoordinatePair> batteriesToBeStolen,
            @JsonProperty("doubleCannonsToActivateCoordinates") List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates
    ) {
        super(playerNickname);

        this.takeLoot = takeLoot;
        this.itemsToBeTaken = itemsToBeTaken;
        this.itemsToBeRemoved = itemsToBeRemoved;
        this.batteriesToBeStolen = batteriesToBeStolen;
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

    @JsonGetter("takeLoot")
    public boolean getTakeLoot() {
        return takeLoot;
    }

    @JsonSetter("takeLoot")
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

    @JsonGetter("batteriesToBeStolen")
    public List<CoordinatePair> getBatteriesToBeStolen() {
        return this.batteriesToBeStolen;
    }

    @JsonSetter("batteriesToBeStolen")
    public void setBatteriesToBeStolen(List<CoordinatePair> batteriesToBeStolen) {
        this.batteriesToBeStolen = batteriesToBeStolen;
    }

    @JsonGetter("doubleCannonsToActivateCoordinates")
    public List<Pair<CoordinatePair, CoordinatePair>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }

    @JsonSetter("doubleCannonsToActivateCoordinates")
    public void setDoubleCannonsToActivateCoordinates(List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates) {
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }
}
