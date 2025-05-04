package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import java.util.ArrayList;
import java.util.List;

public class SmugglersJSON extends ActionJSON {
    private final boolean takeLoot;
    private final ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken;
    private final ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved;
    private final List<ComponentHelper<Integer>> doubleCannonsToActivateCoordinates;


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

    public boolean getTakeLoot() {
        return takeLoot;
    }

    public ArrayList<ComponentHelper<ItemColor>> getItemsToBeTaken() {
        return itemsToBeTaken;
    }

    public ArrayList<ComponentHelper<ItemColor>> getItemsToBeRemoved() {
        return itemsToBeRemoved;
    }

    public List<ComponentHelper<Integer>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }
}