package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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

    @JsonCreator
    public AbandonedShipJSON() {
        this.wantToVisitShip = false;
        this.lifeformsToBeRemoved = new ArrayList<>();
    }

    @JsonCreator
    public AbandonedShipJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("wantToVisitShip") Boolean wantToVisitShip,
            @JsonProperty("lifeformsToBeRemoved") List<ComponentHelper<LifeformType>> lifeformsToBeRemoved
    ) {
        super(playerNickname);

        this.wantToVisitShip = wantToVisitShip;
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
    }

    @JsonGetter("wantToVisitShip")
    public Boolean getWantToVisitShip() {
        return this.wantToVisitShip;
    }

    @JsonSetter("wantToVisitShip")
    public void setWantToVisitShip(Boolean wantToVisitShip) {
        this.wantToVisitShip = wantToVisitShip;
    }

    @JsonGetter("lifeformsToBeRemoved")
    public List<ComponentHelper<LifeformType>> getLifeformsToBeRemoved() {
        return this.lifeformsToBeRemoved;
    }

    @JsonSetter("lifeformsToBeRemoved")
    public void setLifeformsToBeRemoved(List<ComponentHelper<LifeformType>> lifeformsToBeRemoved) {
        this.lifeformsToBeRemoved = lifeformsToBeRemoved;
    }

    public void addLifeformsToBeRemoved(ComponentHelper<LifeformType> lifeformsToBeRemoved) {
        this.lifeformsToBeRemoved.add(lifeformsToBeRemoved);
    }
}