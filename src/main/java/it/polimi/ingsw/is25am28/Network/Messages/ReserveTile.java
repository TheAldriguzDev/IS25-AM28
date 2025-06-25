package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public final class ReserveTile implements Message {
    private String playerNickname;
    private Integer id;

    @JsonCreator
    public ReserveTile() {}

    @JsonCreator
    public ReserveTile(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("id") Integer id
    ) {
        this.playerNickname = playerNickname;
        this.id = id;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonSetter("playerNickname")
    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    @JsonGetter("id")
    public int getId() {
        return this.id;
    }

    @JsonSetter("id")
    public void setId(Integer id) {
        this.id = id;
    }
}