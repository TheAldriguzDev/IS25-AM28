package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PiratesJSON extends ActionJSON {
    private final boolean takeCredits;
    private final ArrayList<int []> shieldsActivatedCoordinates;
    private final ArrayList<Pair<Integer, Integer>> doubleCannonsToActivateCoordinates;

    public PiratesJSON(@JsonProperty("PlayerNickname") String playerNickname,
                       @JsonProperty("takeCredits") boolean takeCredits,
                       @JsonProperty("shieldsActivatedCoordinates") ArrayList<int[]> shieldsActivatedCoordinates,
                       @JsonProperty("doubleCannonsToActivateCoordinates") ArrayList<Pair<Integer, Integer>> doubleCannonsToActivateCoordinates) {
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

    public List<Pair<Integer, Integer>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }
}

