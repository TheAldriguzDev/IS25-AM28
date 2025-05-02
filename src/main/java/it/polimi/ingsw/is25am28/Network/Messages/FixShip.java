package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

public final class FixShip implements Message {
    private static final int GRID_SIZE = 12;

    private String playerNickname;
    private int i;
    private int j;;

    public FixShip(
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
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public FixShip setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("i")
    public int getI() {
        return this.i;
    }

    @JsonSetter("i")
    public FixShip setI(int i) {
        this.i = i;
        return this;
    }

    @JsonGetter("j")
    public int getJ() {
        return this.j;
    }

    @JsonSetter("j")
    public FixShip setJ(int j) {
        this.j = j;
        return this;
    }

    @Override
    public boolean validate() {
        return (this.playerNickname != null && !this.playerNickname.isEmpty());
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();

        if (this.playerNickname == null || this.playerNickname.isEmpty()) {
            errors.add("The 'playerNickname' field cannot be empty nor null.");
        }

        return errors;
    }
}
