package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Player.Player;

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
     * Default constructor used client side.
     */
    public PlayerJSON() {}

    /**
     * Constructor that initializes the JSON with a given ship.
     * This is used mainly server-side to prepare the data for serialization.
     */
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
     * PlayerJSON builder that creates the data that needs to be sent to the client.
     * If the withBoard is true the player ship will be added, otherwise the PlayerJSON will include only the player information.
     */
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
                    player.isConnected(),
                    null
            );
        }
    }

    // Metodi getter per permettere la serializzazione da parte di Jackson
    public String getNickname() {
        return nickname;
    }

    public String getColor() {
        return color;
    }

    public int getCursor() {
        return cursor;
    }

    public int getCredits() {
        return credits;
    }

    public int getLostPieces() {
        return lostPieces;
    }

    public boolean isHasLost() {
        return hasLost;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<Map<String, Object>> getShip() {
        return ship;
    }
}
