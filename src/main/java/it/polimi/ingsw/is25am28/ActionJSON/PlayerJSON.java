package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.List;
import java.util.Map;

public class PlayerJSON {
    private String nickname;
    private String color;
    private int cursor;
    private int credits;
    private int lostPieces;
    private boolean hasLost;
    private boolean isConnected;
    private List<Map<String, Object>> ship;

    /**
     * Default constructor used client side
     * */
    public PlayerJSON() {}

    /**
     * Constructor that initializes the JSON with a given ship.
     * This is used mainly server-side to prepare the data for serialization.
     */
    @JsonCreator
    public PlayerJSON(
                @JsonProperty("nickname") String nickname,
                @JsonProperty("color") String color,
                @JsonProperty("cursors") int cursor,
                @JsonProperty("credits") int credits,
                @JsonProperty("lostPieces") int lostPieces,
                @JsonProperty("hasLost") boolean hasLost,
                @JsonProperty("isConnected") boolean isConnected,
                @JsonProperty("ship") List<Map<String, Object>> ship
            ) {
        this.nickname = nickname;
        this.color = color;
        this.cursor = cursor;
        this.credits = credits;
        this.lostPieces = lostPieces;
        this.hasLost = hasLost;
        this.isConnected = isConnected;
        this.ship = ship;
    }

    /**
     * Constructor that initializes the JSON without a given ship.
     * This is used mainly server-side to prepare the data for serialization.
     */
    @JsonCreator
    public PlayerJSON(
                @JsonProperty("nickname") String nickname,
                @JsonProperty("color") String color,
                @JsonProperty("cursors") int cursor,
                @JsonProperty("credits") int credits,
                @JsonProperty("lostPieces") int lostPieces,
                @JsonProperty("hasLost") boolean hasLost,
                @JsonProperty("isConnected") boolean isConnected
        ) {
        this.nickname = nickname;
        this.color = color;
        this.cursor = cursor;
        this.credits = credits;
        this.lostPieces = lostPieces;
        this.hasLost = hasLost;
        this.isConnected = isConnected;
    }

    /**
     * PlayerJSON builder that creates the data that needs to be sent to the client.
     * If the withBoard is true the player ship will be added, otherwise the PlayerJSON will include only the player information
     * */
    public static PlayerJSON fromPlayer(Player player, boolean withBoard) {
        if (withBoard) {
            return new PlayerJSON(
                    player.getNickname(),
                    player.getColor(),
                    player.getCursor(),
                    player.getCredits(),
                    player.getLostPieces(),
                    player.isEliminated(),
                    player.isConnected(),
                    player.getShip().generateState()
            );
        } else {
            return new PlayerJSON(
                    player.getNickname(),
                    player.getColor(),
                    player.getCursor(),
                    player.getCredits(),
                    player.getLostPieces(),
                    player.isEliminated(),
                    player.isConnected()
            );
        }
    }

}
