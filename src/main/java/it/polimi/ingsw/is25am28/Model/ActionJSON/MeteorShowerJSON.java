package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class MeteorShowerJSON extends ActionJSON {
    private Integer currMeteorIndex;
    private Integer diceThrowResult;
    private List<Pair<CoordinatePair, CoordinatePair>> shieldsCoordinates;
    private List<Pair<CoordinatePair, CoordinatePair>> cannonsCoordinates;

    @JsonCreator
    public MeteorShowerJSON() {
        this.currMeteorIndex = null;
        this.diceThrowResult = null;
        this.shieldsCoordinates = new ArrayList<>();
        this.cannonsCoordinates = new ArrayList<>();
    }

    @JsonCreator
    public MeteorShowerJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("currMeteorIndex") Integer currMeteorIndex,
            @JsonProperty("diceThrowResult") Integer diceThrowResult,
            @JsonProperty("shieldsCoordinates") List<Pair<CoordinatePair, CoordinatePair>> shieldsCoordinates,
            @JsonProperty("cannonsCoordinates") List<Pair<CoordinatePair, CoordinatePair>> cannonsCoordinates
    ) {
        super(playerNickname);
        this.currMeteorIndex = currMeteorIndex;
        this.diceThrowResult = diceThrowResult;
        this.shieldsCoordinates = shieldsCoordinates;
        this.cannonsCoordinates = cannonsCoordinates;
    }

    @JsonGetter("currMeteorIndex")
    public Integer getCurrMeteorIndex() {
        return this.currMeteorIndex;
    }

    @JsonSetter("currMeteorIndex")
    public void setCurrMeteorIndex(Integer currMeteorIndex) {
        this.currMeteorIndex = currMeteorIndex;
    }

    @JsonGetter("diceThrowResult")
    public Integer getDiceThrowResult() {
        return this.diceThrowResult;
    }

    @JsonSetter("diceThrowResult")
    public void setDiceThrowResult(Integer diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    @JsonGetter("shieldsCoordinates")
    public List<Pair<CoordinatePair, CoordinatePair>> getShieldsCoordinates() {
        return this.shieldsCoordinates;
    }

    @JsonSetter("shieldsCoordinates")
    public void setShieldsCoordinates(List<Pair<CoordinatePair, CoordinatePair>> shieldsCoordinates) {
        this.shieldsCoordinates = shieldsCoordinates;
    }

    @JsonGetter("cannonsCoordinates")
    public List<Pair<CoordinatePair, CoordinatePair>> getCannonsCoordinates() {
        return this.cannonsCoordinates;
    }

    @JsonSetter("cannonsCoordinates")
    public void setCannonsCoordinates(List<Pair<CoordinatePair, CoordinatePair>> cannonsCoordinates) {
        this.cannonsCoordinates = cannonsCoordinates;
    }
}
