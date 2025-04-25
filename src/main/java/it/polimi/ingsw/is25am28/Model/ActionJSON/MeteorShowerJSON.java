package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class MeteorShowerJSON extends ActionJSON {
    private int currMeteorIndex;
    private int diceThrowResult;
    private List<List<Integer>>  shieldsCoordinates;
    private List<List<Integer>> cannonsCoordinates;

    @JsonCreator
    public MeteorShowerJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("currMeteorIndex") int currMeteorIndex,
            @JsonProperty("diceThrowResult") int diceThrowResult,
            @JsonProperty("shieldsCoordinates") List<List<Integer>> shieldsCoordinates,
            @JsonProperty("cannonsCoordinates") List<List<Integer>> cannonsCoordinates
    ) {
        super(playerNickname);
        this.currMeteorIndex = currMeteorIndex;
        this.diceThrowResult = diceThrowResult;
        this.shieldsCoordinates = shieldsCoordinates;
        this.cannonsCoordinates = cannonsCoordinates;
    }

    @JsonGetter("currMeteorIndex")
    public int getCurrMeteorIndex() {
        return this.currMeteorIndex;
    }

    @JsonSetter("currMeteorIndex")
    public void setCurrMeteorIndex(int currMeteorIndex) {
        this.currMeteorIndex = currMeteorIndex;
    }

    @JsonGetter("diceThrowResult")
    public int getDiceThrowResult() {
        return this.diceThrowResult;
    }

    @JsonSetter("diceThrowResult")
    public void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    @JsonGetter("shieldsCoordinates")
    public List<List<Integer>> getShieldsCoordinates() {
        return this.shieldsCoordinates;
    }

    @JsonSetter("shieldsCoordinates")
    public void setShieldsCoordinates(List<List<Integer>> shieldsCoordinates) {
        this.shieldsCoordinates = shieldsCoordinates;
    }

    @JsonGetter("cannonsCoordinates")
    public List<List<Integer>> getCannonsCoordinates() {
        return this.cannonsCoordinates;
    }

    @JsonSetter("cannonsCoordinates")
    public void setCannonsCoordinates(List<List<Integer>> cannonsCoordinates) {
        this.cannonsCoordinates = cannonsCoordinates;
    }
}
