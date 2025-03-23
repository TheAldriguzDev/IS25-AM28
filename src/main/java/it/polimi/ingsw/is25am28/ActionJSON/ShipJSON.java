package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.util.Pair;

import java.util.Map;

public class ShipJSON extends ActionJSON {
    // Each type of alien (expressed as an integer) is mapped to the cabin that will house it.
    // (alien life eligibility check for that cabin is performed client-side)
    private Map<Integer, Pair<Integer, Integer>> chosenAliens;

    @JsonCreator
    public ShipJSON() {
        // Creates an empty ShipJSON, to fill afterwards
    }

    @JsonCreator
    public ShipJSON(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("chosenAliens") Map<Integer, Pair<Integer, Integer>> chosenAliens
    ) {
        super(playerNickname);
        this.chosenAliens = chosenAliens;
    }

    @JsonGetter("chosenAliens")
    public Map<Integer, Pair<Integer, Integer>> getChosenAliens() {
        return this.chosenAliens;
    }

    @JsonSetter("chosenAliens")
    public void setChosenAliens(Map<Integer, Pair<Integer, Integer>> chosenAliens) {
        this.chosenAliens = chosenAliens;
    }
}
