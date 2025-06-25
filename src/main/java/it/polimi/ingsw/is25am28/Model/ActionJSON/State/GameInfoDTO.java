package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object representing the details of a game.
 * This class is used to encapsulate game-related information including
 * the game ID, level, total players, actual players currently in the game,
 * and the list of available colors used in the game.
 * <p>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
public class GameInfoDTO implements Serializable {
    private int id;
    private int level;
    private int totalPlayers;
    private int actualPlayers;
    private List<String> availableColors;

    @JsonCreator
    public GameInfoDTO() {}

    @JsonCreator
    public GameInfoDTO(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("totalPlayers") int totalPlayers,
            @JsonProperty("actualPlayers") int actualPlayers,
            @JsonProperty("availableColors") List<String> availableColors
    ) {
        this.id = id;
        this.level = level;
        this.totalPlayers = totalPlayers;
        this.actualPlayers = actualPlayers;
        this.availableColors = availableColors;
    }

    @JsonGetter("id")
    public int getId() {
        return id;
    }

    @JsonSetter("id")
    public GameInfoDTO setId(int id) {
        this.id = id;
        return this;
    }

    @JsonGetter("level")
    public int getLevel() {
        return level;
    }

    @JsonSetter("level")
    public GameInfoDTO setLevel(int level) {
        this.level = level;
        return this;
    }

    @JsonGetter("totalPlayers")
    public int getTotalPlayers() {
        return totalPlayers;
    }

    @JsonSetter("totalPlayers")
    public GameInfoDTO setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        return this;
    }

    @JsonGetter("actualPlayers")
    public int getActualPlayers() {
        return actualPlayers;
    }

    @JsonSetter("actualPlayers")
    public GameInfoDTO setActualPlayers(int actualPlayers) {
        this.actualPlayers = actualPlayers;
        return this;
    }

    @JsonGetter("availableColors")
    public List<String> getAvailableColors() {
        return availableColors;
    }

    @JsonSetter("availableColors")
    public GameInfoDTO setAvailableColors(List<String> availableColors) {
        this.availableColors = availableColors;
        return this;
    }
}
