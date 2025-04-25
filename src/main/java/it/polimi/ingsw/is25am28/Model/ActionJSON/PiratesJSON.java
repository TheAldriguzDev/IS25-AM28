package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PiratesJSON extends ActionJSON {
    private final boolean takeCredits;
    private final ArrayList<int []> shieldsActivatedCoordinates;
    private final ArrayList<List<Integer>> doubleCannonsToActivateCoordinates;

    public PiratesJSON(@JsonProperty("PlayerNickname") String playerNickname,
                       @JsonProperty("takeCredits") boolean takeCredits,
                       @JsonProperty("shieldsActivatedCoordinates") ArrayList<int[]> shieldsActivatedCoordinates,
                       @JsonProperty("doubleCannonsToActivateCoordinates") ArrayList<List<Integer>> doubleCannonsToActivateCoordinates) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.shieldsActivatedCoordinates = shieldsActivatedCoordinates;
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public ArrayList<int[]> getShieldsActivatedCoordinates() {
        return shieldsActivatedCoordinates;
    }

    public List<List<Integer>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }
}

