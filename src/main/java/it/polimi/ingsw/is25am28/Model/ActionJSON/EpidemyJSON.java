package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the players' actions in the {@code epidemy} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */
public class EpidemyJSON extends ActionJSON {
    @JsonCreator
    public EpidemyJSON() {}

    public EpidemyJSON(
            @JsonProperty("playerNickname") String playerNickname
    ) {
        super(playerNickname);
    }
}
