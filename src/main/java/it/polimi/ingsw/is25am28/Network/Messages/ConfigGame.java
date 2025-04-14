package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean validate() {
        return playerNickname != null && !playerNickname.isEmpty()
                && playerColor != null
                && (gameLevel == 0 || gameLevel == 2)
                && (totalPlayers >= 2 && totalPlayers <= 4);
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();

        if (playerNickname == null || playerNickname.isEmpty()) {
            errors.add("Your name cannot be null or empty");
        }

        if (playerColor == null) {
            errors.add("Your color cannot be null");
        }

        if (gameLevel != 0 && gameLevel != 2) {
            errors.add("Game level must be 0 or 2");
        }

        if (totalPlayers < 2 || totalPlayers > 4) {
            errors.add("Total number of players must be between 2 and 4");
        }

        return errors;
    }
}
