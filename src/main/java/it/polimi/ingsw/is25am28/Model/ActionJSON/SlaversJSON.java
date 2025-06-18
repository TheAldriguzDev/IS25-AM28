package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the players' actions in the {@code slavers} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */
public class SlaversJSON extends ActionJSON {
    private boolean takeCredits;
    private List<ComponentHelper<LifeformType>> crewToRemove;
    private List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates;

    public SlaversJSON() {
        this.takeCredits = false;
        this.crewToRemove = new ArrayList<>();
        this.doubleCannonsToActivateCoordinates = new ArrayList<>();
    }

    public SlaversJSON (
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("takeCredits") boolean takeCredits,
            @JsonProperty("crewToRemove") List<ComponentHelper<LifeformType>> crewToRemove,
            @JsonProperty("doubleCannonsToActivateCoordinates") List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates
    ) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.crewToRemove = crewToRemove;
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

    @JsonGetter("crewToRemove")
    public List<ComponentHelper<LifeformType>> getCrewToRemove() {
        return crewToRemove;
    }

    @JsonSetter("crewToRemove")
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) {
        this.crewToRemove = crewToRemove;
    }

    @JsonGetter("doubleCannonsToActivateCoordinates")
    public List<Pair<CoordinatePair, CoordinatePair>> getDoubleCannonsToActivateCoordinates() {
        return this.doubleCannonsToActivateCoordinates;
    }

    @JsonSetter("doubleCannonsToActivateCoordinates")
    public void setDoubleCannonsToActivateCoordinates(List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivateCoordinates) {
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }
}
