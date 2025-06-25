package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the players' actions in the {@code stardust} card in JSON format.
 * The class is designed to be serialized and deserialized using Jackson annotations
 */
public class StardustJSON extends ActionJSON {
    @JsonCreator
    public StardustJSON() {}

    @JsonCreator
    public StardustJSON (
            @JsonProperty("playerNickname") String playerNickname
    ) {
        super(playerNickname);
    }
}