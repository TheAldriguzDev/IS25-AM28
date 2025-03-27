package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonSetter;

import javafx.util.Pair;

import java.util.List;

public class WarZoneJSON extends ActionJSON {
    private int currPlasmaShotIndex;
    private int diceThrowResult;
    private List<Pair<Integer, Integer>> enginesToActivate;
    private List<Pair<Integer, Integer>> cannonsToActivate;
    private List<Pair<Integer, Integer>> shieldsToActivate;

    @JsonCreator
    public WarZoneJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("currPlasmaShotIndex") int currPlasmaShotIndex,
            @JsonProperty("diceThrowResult") int diceThrowResult,
            @JsonProperty("enginesToActivate") List<Pair<Integer, Integer>> enginesToActivate,
            @JsonProperty("cannonsToActivate") List<Pair<Integer, Integer>> cannonsToActivate,
            @JsonProperty("shieldsToActivate") List<Pair<Integer, Integer>> shieldsToActivate
    ) {
        super(playerNickname);

        this.currPlasmaShotIndex = currPlasmaShotIndex;
        this.diceThrowResult = diceThrowResult;
        this.enginesToActivate = enginesToActivate;
        this.cannonsToActivate = cannonsToActivate;
        this.shieldsToActivate = shieldsToActivate;
    }

    @JsonGetter("currPlasmaShotIndex")
    public int getCurrPlasmaShotIndex() {
        return this.currPlasmaShotIndex;
    }

    @JsonSetter("currPlasmaShotIndex")
    public void setCurrPlasmaShotIndex(int currPlasmaShotIndex) {
        this.currPlasmaShotIndex = currPlasmaShotIndex;
    }

    @JsonGetter("diceThrowResult")
    public int getDiceThrowResult() {
        return this.diceThrowResult;
    }

    @JsonSetter("diceThrowResult")
    public void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    @JsonSetter("enginesToActivate")
    public void setEnginesToActivate(List<Pair<Integer, Integer>> enginesToActivate) {
        this.enginesToActivate = enginesToActivate;
    }

    @JsonGetter("enginesToActivate")
    public List<Pair<Integer, Integer>> getEnginesToActivate() {
        return this.enginesToActivate;
    }

    @JsonSetter("cannonsToActivate")
    public void setCannonsToActivate(List<Pair<Integer, Integer>> cannonsToActivate) {
        this.cannonsToActivate = cannonsToActivate;
    }

    @JsonGetter("cannonsToActivate")
    public List<Pair<Integer, Integer>> getCannonsToActivate() {
        return this.cannonsToActivate;
    }

    @JsonSetter("shieldsToActivate")
    public void setShieldsToActivate(List<Pair<Integer, Integer>> shieldsPerPlayer) {
        this.shieldsToActivate = shieldsPerPlayer;
    }

    @JsonGetter("shieldsToActivate")
    public List<Pair<Integer, Integer>> getShieldsToActivate() {
        return this.shieldsToActivate;
    }
}
