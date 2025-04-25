package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.ArrayList;
import java.util.List;

public class SlaversJSON extends ActionJSON {
    private final boolean takeCredits;
    private final ArrayList<ComponentHelper<LifeformType>> crewToRemove;
   private final ArrayList<List<Integer>> doubleCannonsToActivateCoordinates;

    public SlaversJSON (@JsonProperty("playerNickname") String playerNickname,
                        @JsonProperty("takeCredits") boolean takeCredits,
                        @JsonProperty("crewToRemove") ArrayList<ComponentHelper<LifeformType>> crewToRemove,
                        @JsonProperty("doubleCannonsToActivateCoordinates") ArrayList<List<Integer>> doubleCannonsToActivateCoordinates) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.crewToRemove = crewToRemove;
        this.doubleCannonsToActivateCoordinates = doubleCannonsToActivateCoordinates;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public ArrayList<ComponentHelper<LifeformType>> getCrewToRemove() {
        return crewToRemove;
    }


    public List<List<Integer>> getDoubleCannonsToActivateCoordinates() {
        return doubleCannonsToActivateCoordinates;
    }
}