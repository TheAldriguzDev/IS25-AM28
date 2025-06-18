package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the players' actions in the {@code abandonedShip} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */

public class AbandonedShipJSON extends ActionJSON {
    private Boolean wantToVisitShip;
    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;

    /**
     * Default constructor
     * */
    public AbandonedShipJSON() {
        // TODO: This can break the card since there's no GUI-side check to see whether the player
        //       answered either T or F (in the case it's set to null as default).
        //       .
        //       (if you want in "YOUR ACTIONS" to see T/F iff the player actually chose something, then it
        //        needs to be set to null as default value, but then a null-check is needed GUI-side)
        //       (TUI-side null-check is already implemented in the generatePlayerActionsWidget method)
        this.wantToVisitShip = false;

        this.lifeformsToBeRemoved = new ArrayList<>();
    }

    public AbandonedShipJSON(@JsonProperty("playerNickname") String playerNickname,
                             @JsonProperty("wantToVisitShip") Boolean wantToVisitShip,
                             @JsonProperty("lifeformsToBeRemoved") List<ComponentHelper<LifeformType>> lifeformsToBeRemoved) {
        super(playerNickname);
        this.wantToVisitShip = wantToVisitShip;
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
    }

    public Boolean getWantToVisitShip() {
        return this.wantToVisitShip;
    }

    public void setWantToVisitShip(Boolean wantToVisitShip) {
        this.wantToVisitShip = wantToVisitShip;
    }

    public List<ComponentHelper<LifeformType>> getLifeformsToBeRemoved() {
        return this.lifeformsToBeRemoved;
    }

    public void setLifeformsToBeRemoved(List<ComponentHelper<LifeformType>> lifeformsToBeRemoved) {
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
    }

    public void addLifeformsToBeRemoved(ComponentHelper<LifeformType> lifeformsToBeRemoved) {
        this.lifeformsToBeRemoved.add(lifeformsToBeRemoved);
    }
}