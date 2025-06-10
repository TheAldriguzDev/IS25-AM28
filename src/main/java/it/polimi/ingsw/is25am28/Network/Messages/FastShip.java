package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public final class FastShip implements Message {
    private String playerNickname;

    @JsonCreator
    public FastShip(@JsonProperty("playerNickname") String playerNickname) {
        this.playerNickname = playerNickname;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }
}
