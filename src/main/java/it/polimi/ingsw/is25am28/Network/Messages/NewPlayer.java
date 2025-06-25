package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

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

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonGetter("playerColor")
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    @JsonGetter("gameID")
    public int getGameID() {
        return gameID;
    }
}
