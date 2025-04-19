package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public final class SelectTile implements Message {
    private String playerNickname;
    private int i;
    private int j;

    public SelectTile() {}

    public SelectTile(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("i") int i,
            @JsonProperty("j") int j
    ) {
        this.playerNickname = playerNickname;
        this.i = i;
        this.j = j;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonSetter("playerNickname")
    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    @JsonGetter("i")
    public int getI() {
        return i;
    }

    @JsonSetter("i")
    public void setI(int i) {
        this.i = i;
    }

    @JsonGetter("j")
    public int getJ() {
        return j;
    }

    @JsonSetter("j")
    public void setJ(int j) {
        this.j = j;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public List<String> getErrors() {
        return List.of();
    }
}
