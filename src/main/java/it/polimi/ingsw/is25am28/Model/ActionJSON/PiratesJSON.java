package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class PiratesJSON extends ActionJSON {
    private boolean takeCredits;
    private List<Pair<CoordinatePair, CoordinatePair>> shieldsActivatedCoordinates;
    private List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates;

    public PiratesJSON() {
        this.takeCredits = false;
        this.shieldsActivatedCoordinates = new ArrayList<>();
        this.doubleCannonsToActivateCoordinates = new ArrayList<>();
    }

    public PiratesJSON(
            @JsonProperty("PlayerNickname") String playerNickname,
            @JsonProperty("takeCredits") boolean takeCredits,
            @JsonProperty("shieldsActivatedCoordinates") List<Pair<CoordinatePair, CoordinatePair>> shieldsActivatedCoordinates,
            @JsonProperty("doubleCannonsToActivateCoordinates") List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates
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
    public List<Pair<CoordinatePair, CoordinatePair>> getShieldsActivatedCoordinates() {
        return shieldsActivatedCoordinates;
    }

    @JsonSetter("shieldsActivatedCoordinates")
    public void setShieldsActivatedCoordinates(List<Pair<CoordinatePair, CoordinatePair>> shieldsActivatedCoordinates) {
        this.shieldsActivatedCoordinates = shieldsActivatedCoordinates;
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
