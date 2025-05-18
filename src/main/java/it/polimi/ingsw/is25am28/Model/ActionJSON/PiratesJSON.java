package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class PiratesJSON extends ActionJSON {
    private boolean takeCredits;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldsActivatedCoordinates;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleCannonsToActivateCoordinates;

    public PiratesJSON() {
        this.takeCredits = false;
        this.shieldsActivatedCoordinates = new ArrayList<>();
        this.doubleCannonsToActivateCoordinates = new ArrayList<>();
    }

    public PiratesJSON(
            @JsonProperty("PlayerNickname") String playerNickname,
            @JsonProperty("takeCredits") boolean takeCredits,
            @JsonProperty("shieldsActivatedCoordinates") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldsActivatedCoordinates,
            @JsonProperty("doubleCannonsToActivateCoordinates") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> doubleCannonsToActivateCoordinates
    ) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.shieldsActivatedCoordinates = shieldsActivatedCoordinates;
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

    @JsonGetter("takeCredits")
    public boolean getTakeCredits() {
        return takeCredits;
    }

    @JsonSetter("takeCredits")
    public void setTakeCredits(boolean takeCredits) {
        this.takeCredits = takeCredits;
    }

    @JsonGetter("shieldsActivatedCoordinates")
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getShieldsActivatedCoordinates() {
        return shieldsActivatedCoordinates;
    }

    @JsonSetter("shieldsActivatedCoordinates")
    public void setShieldsActivatedCoordinates(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldsActivatedCoordinates) {
        this.shieldsActivatedCoordinates = shieldsActivatedCoordinates;
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
