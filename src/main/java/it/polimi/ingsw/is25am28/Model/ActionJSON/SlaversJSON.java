package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.ArrayList;
import java.util.List;

public class SlaversJSON extends ActionJSON {
    private boolean takeCredits;
    private boolean isPlayerDefeated;
    private List<ComponentHelper<LifeformType>> crewToRemove;
    private List<ComponentHelper<Void>> doubleCannonsToActivateCoordinates;

    public SlaversJSON() {
        this.takeCredits = false;
        this.crewToRemove = new ArrayList<>();
        this.doubleCannonsToActivateCoordinates = new ArrayList<>();
    }

    public SlaversJSON (@JsonProperty("playerNickname") String playerNickname,
                        @JsonProperty("takeCredits") boolean takeCredits,
                        @JsonProperty("crewToRemove") List<ComponentHelper<LifeformType>> crewToRemove,
                        @JsonProperty("doubleCannonsToActivateCoordinates") List<ComponentHelper<Void>> doubleCannonsToActivateCoordinates) {
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
    public List<ComponentHelper<Void>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }

    @JsonSetter("doubleCannonsToActivateCoordinates")
    public void setDoubleCannonsToActivateCoordinates(List<ComponentHelper<Void>> doubleCannonsToActivateCoordinates) {
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }
}