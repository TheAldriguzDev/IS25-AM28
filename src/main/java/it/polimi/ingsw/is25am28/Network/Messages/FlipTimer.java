package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public final class FlipTimer implements Message {
    private String playerNickname;

    @JsonCreator
    public FlipTimer(@JsonProperty("playerNickname") String playerNickname) {
        this.playerNickname = playerNickname;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonSetter("playerNickname")
    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }
}
