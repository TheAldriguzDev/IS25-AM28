package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActionJSON {
    protected String playerNickname;

    /**
     * Default constructor used client side
     * */
    public ActionJSON() {}

    /**
     * Constructor that initialize the JSON with a nickname, used mainly serverside
     * */
    @JsonCreator
    public ActionJSON(@JsonProperty("playerNickname")  String playerNickname) {
        this.playerNickname = playerNickname;
    }

    /**
     * Returns the player Nickname
     * */
    @JsonGetter("playerNickname")
    public String getPlayerNickname() throws IllegalStateException {
        if (this.playerNickname == null || this.playerNickname.isEmpty()) {
            return null;
        }

        return this.playerNickname;
    }

    /**
     * Set the playerNickname to the given data
     * */
    public void setPlayerNickname(String playerNickname) throws IllegalStateException {
        if (playerNickname == null || playerNickname.isEmpty()) {
            throw new IllegalStateException("playerNickname cannot be null or empty");
        }

        this.playerNickname = playerNickname;
    }
}