package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("NewPlayer")
public final class NewPlayer implements Message {
    private final String playerNickname;
    private final PlayerColor playerColor;
    private final int gameID;

    @JsonCreator
    public NewPlayer(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("playerColor") PlayerColor playerColor,
            @JsonProperty("gameID") int gameID
    ) {
        this.playerNickname = playerNickname;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    @Override
    public boolean validate() {
        return playerNickname != null && !playerNickname.isEmpty()
                && playerColor != null;
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();

        if (playerNickname == null || playerNickname.isEmpty()) {
            errors.add("Your cardName cannot be null or empty");
        }

        if (playerColor == null) {
            errors.add("Your color cannot be null");
        }

        return errors;
    }
}
