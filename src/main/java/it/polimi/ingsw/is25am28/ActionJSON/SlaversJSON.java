package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import java.util.ArrayList;

public class SlaversJSON extends ActionJSON {
    private final boolean takeCredits;
    private final ArrayList<ComponentHelper<LifeformType>> crewToRemove;
    private final int numberOfDoubleCannonsActivated;

    public SlaversJSON (@JsonProperty("playerNickname") String playerNickname,
                        @JsonProperty("takeCredits") boolean takeCredits,
                        @JsonProperty("crewToRemove") ArrayList<ComponentHelper<LifeformType>> crewToRemove,
                        @JsonProperty("numberOfDoubleCannonsActivated") int numberOfDoubleCannonsActivated) {
        super(playerNickname);
        this.takeCredits = takeCredits;
        this.crewToRemove = crewToRemove;
        this.numberOfDoubleCannonsActivated = numberOfDoubleCannonsActivated;
    }

    public boolean getTakeCredits() {
        return takeCredits;
    }

    public ArrayList<ComponentHelper<LifeformType>> getCrewToRemove() {
        return crewToRemove;
    }

    public int getNumberOfDoubleCannonsActivated() {
        return numberOfDoubleCannonsActivated;
    }
}