package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class MeteorShowerJSON extends ActionJSON {
    private int currMeteorIndex;
    private int diceThrowResult;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldsCoordinates;
    private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> cannonsCoordinates;

    public MeteorShowerJSON() {
        this.currMeteorIndex = 0;
        this.diceThrowResult = 0;
        this.shieldsCoordinates = new ArrayList<>();
        this.cannonsCoordinates = new ArrayList<>();
    }

    @JsonCreator
    public MeteorShowerJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("currMeteorIndex") int currMeteorIndex, // FIXME: Remove this field
            @JsonProperty("diceThrowResult") int diceThrowResult, // FIXME: REMOVE this field
            @JsonProperty("shieldsCoordinates") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldsCoordinates,
            @JsonProperty("cannonsCoordinates") List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> cannonsCoordinates
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
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getShieldsCoordinates() {
        return this.shieldsCoordinates;
    }

    @JsonSetter("shieldsCoordinates")
    public void setShieldsCoordinates(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> shieldsCoordinates) {
        this.shieldsCoordinates = shieldsCoordinates;
    }

    @JsonGetter("cannonsCoordinates")
    public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getCannonsCoordinates() {
        return this.cannonsCoordinates;
    }

    @JsonSetter("cannonsCoordinates")
    public void setCannonsCoordinates(List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> cannonsCoordinates) {
        this.cannonsCoordinates = cannonsCoordinates;
    }
}
