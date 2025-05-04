package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PiratesJSON extends ActionJSON {
    private final boolean takeCredits;
    private final List<ComponentHelper<Integer>> shieldsActivatedCoordinates;
    private final List<ComponentHelper<Integer>> doubleCannonsToActivateCoordinates;

    public PiratesJSON(@JsonProperty("PlayerNickname") String playerNickname,
                       @JsonProperty("takeCredits") boolean takeCredits,
                       @JsonProperty("shieldsActivatedCoordinates") List<ComponentHelper<Integer>> shieldsActivatedCoordinates,
                       @JsonProperty("doubleCannonsToActivateCoordinates") List<ComponentHelper<Integer>> doubleCannonsToActivateCoordinates) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.shieldsActivatedCoordinates = shieldsActivatedCoordinates;
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public List<ComponentHelper<Integer>> getShieldsActivatedCoordinates() {
        return shieldsActivatedCoordinates;
    }

    public List<ComponentHelper<Integer>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }
}

