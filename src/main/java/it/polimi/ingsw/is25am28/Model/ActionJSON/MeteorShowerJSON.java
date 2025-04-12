package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.util.Pair;

import java.util.List;

public class MeteorShowerJSON extends ActionJSON {
    private int currMeteorIndex;
    private int diceThrowResult;
    private List<Pair<Integer, Integer>>  shieldsCoordinates;
    private List<Pair<Integer, Integer>> cannonsCoordinates;

    @JsonCreator
    public MeteorShowerJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("currMeteorIndex") int currMeteorIndex,
            @JsonProperty("diceThrowResult") int diceThrowResult,
            @JsonProperty("shieldsCoordinates") List<Pair<Integer, Integer>> shieldsCoordinates,
            @JsonProperty("cannonsCoordinates") List<Pair<Integer, Integer>> cannonsCoordinates
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
    public List<Pair<Integer, Integer>> getShieldsCoordinates() {
        return this.shieldsCoordinates;
    }

    @JsonSetter("shieldsCoordinates")
    public void setShieldsCoordinates(List<Pair<Integer, Integer>> shieldsCoordinates) {
        this.shieldsCoordinates = shieldsCoordinates;
    }

    @JsonGetter("cannonsCoordinates")
    public List<Pair<Integer, Integer>> getCannonsCoordinates() {
        return this.cannonsCoordinates;
    }

    @JsonSetter("cannonsCoordinates")
    public void setCannonsCoordinates(List<Pair<Integer, Integer>> cannonsCoordinates) {
        this.cannonsCoordinates = cannonsCoordinates;
    }
}
