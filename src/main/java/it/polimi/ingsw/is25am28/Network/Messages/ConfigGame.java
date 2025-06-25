package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

@JsonTypeName("ConfigGame")
public final class ConfigGame implements Message {
    private final String playerNickname;
    private final PlayerColor playerColor;
    private final int gameLevel;
    private final int totalPlayers;

    @JsonCreator
    public ConfigGame(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("playerColor") PlayerColor playerColor,
            @JsonProperty("gameLevel") int gameLevel,
            @JsonProperty("totalPlayers") int totalPlayers
    ) {
        this.playerNickname = playerNickname;
        this.playerColor = playerColor;
        this.gameLevel = gameLevel;
        this.totalPlayers = totalPlayers;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }
}
