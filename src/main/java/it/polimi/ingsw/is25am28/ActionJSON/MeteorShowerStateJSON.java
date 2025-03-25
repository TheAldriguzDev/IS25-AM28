package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.*;
import javafx.util.Pair;

public class MeteorShowerStateJSON extends CardStateJSON{
    private int currMeteorIndex;
    private int diceThrowResult;
    private Pair<Integer, Integer> currMeteorDescriptor;

    @JsonCreator
    public MeteorShowerStateJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("isCardUsable") boolean isCardUsable,
            @JsonProperty("currMeteorIndex") int currMeteorIndex,
            @JsonProperty("diceThrowResult") int diceThrowResult,
            @JsonProperty("currMeteorDescriptor") Pair<Integer, Integer> currMeteorDescriptor
    ) {
        super(playerNickname, cardName, cardLevel, isCardUsable);
        this.currMeteorIndex = currMeteorIndex;
        this.diceThrowResult = diceThrowResult;
        this.currMeteorDescriptor = currMeteorDescriptor;
    }

    @JsonSetter("currMeteorIndex")
    public void setCurrMeteorIndex(int currMeteorIndex) {
        this.currMeteorIndex = currMeteorIndex;
    }

    @JsonGetter("currMeteorIndex")
    public int getCurrMeteorIndex() {
        return this.currMeteorIndex;
    }

    @JsonSetter("diceThrowResult")
    public void setDiceThrowResult(int diceThrowResult) {
        this.diceThrowResult = diceThrowResult;
    }

    @JsonGetter("diceThrowResult")
    public int getDiceThrowResult() {
        return this.diceThrowResult;
    }

    @JsonSetter("currMeteorDescriptor")
    public void setCurrMeteorDescriptor(Pair<Integer, Integer> currMeteorDescriptor) {
        this.currMeteorDescriptor = currMeteorDescriptor;
    }

    @JsonGetter("currMeteorDescriptor")
    public Pair<Integer, Integer> getCurrMeteorDescriptor() {
        return this.currMeteorDescriptor;
    }
}
