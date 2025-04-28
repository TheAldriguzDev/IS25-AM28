package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

import java.util.Map;

public class VisitPlanetsStateJSON extends CardStateJSON {
    private Map<Integer, Map<ItemColor, Integer>> availablePlanets;

    @JsonCreator
    public VisitPlanetsStateJSON(
            @JsonProperty("id") int id,
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("cardName") String cardName,
            @JsonProperty("cardLevel") int cardLevel,
            @JsonProperty("isCardUsable") boolean isCardUsable,
            @JsonProperty("availablePlanets") Map<Integer, Map<ItemColor, Integer>> availablePlanets
    ) {
        super(id, playerNickname, cardName, cardLevel, isCardUsable);
        this.availablePlanets = availablePlanets;
    }

    @JsonGetter("availablePlanets")
    public Map<Integer, Map<ItemColor, Integer>> getAvailablePlanets() {
        return this.availablePlanets;
    }

    @JsonSetter("availablePlanets")
    public void setAvailablePlanets(Map<Integer, Map<ItemColor, Integer>> availablePlanets) {
        this.availablePlanets = availablePlanets;
    }
}
