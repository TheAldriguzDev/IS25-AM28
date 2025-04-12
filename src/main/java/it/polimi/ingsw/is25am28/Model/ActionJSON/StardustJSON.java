package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StardustJSON extends ActionJSON {

    public StardustJSON (@JsonProperty("playerNickname") String playerNickname) {
        super(playerNickname);
    }
}