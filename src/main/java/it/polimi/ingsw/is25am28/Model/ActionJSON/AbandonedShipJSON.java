package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import java.util.ArrayList;
import java.util.List;

public class AbandonedShipJSON extends ActionJSON {
    private boolean wantToVisitShip;
    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;

    /**
     * Default constructor
     * */
    public AbandonedShipJSON() {
        this.wantToVisitShip = false;
        this.lifeformsToBeRemoved = new ArrayList<>();
    }

    public AbandonedShipJSON(@JsonProperty("playerNickname") String playerNickname,
                             @JsonProperty("wantToVisitShip") boolean wantToVisitShip,
                             @JsonProperty("lifeformsToBeRemoved") List<ComponentHelper<LifeformType>> lifeformsToBeRemoved) {
        super(playerNickname);
        this.wantToVisitShip = wantToVisitShip;
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
    }

    public boolean getWantToVisitShip() {
        return this.wantToVisitShip;
    }

    public void setWantToVisitShip(boolean wantToVisitShip) {
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