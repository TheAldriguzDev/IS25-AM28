package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Player.Player;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class MeteorShowerJSON extends ActionJSON {
    private final List<Integer> diceThrowPerMeteor;
    private final Map<Player, Pair<Integer, Integer>>  shieldPerPlayer;
    private final Map<Player, Pair<Integer, Integer>>  cannonPerPlayer;

    @JsonCreator
    public MeteorShowerJSON(
            @JsonProperty("diceThrowPerMeteor") List<Integer> diceThrowPerMeteor,
            @JsonProperty("shieldPerPlayer") Map<Player, Pair<Integer, Integer>> shieldPerPlayer,
            @JsonProperty("cannonPerPlayer") Map<Player, Pair<Integer, Integer>> cannonPerPlayer
    ) {
        // "diceThrow" - To specify row and column that are potentially targeted
        // "shield" - The coordinates (row, col) of the shield to activate to defend from a small meteor
        // "shoot" - The coordinates (row, col) of the cannon to activate to defend from a big meteor

        this.diceThrowPerMeteor = diceThrowPerMeteor;
        this.shieldPerPlayer = shieldPerPlayer;
        this.cannonPerPlayer = cannonPerPlayer;
    }

    @JsonGetter("diceThrowPerMeteor")
    public int getDiceThrow(int index) {
        try {
            return this.diceThrowPerMeteor.get(index);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("ERROR: Index out of bounds");
        }
    }

    @JsonGetter("shieldPerPlayer")
    public Pair<Integer, Integer> getShieldCoordinatesPerPlayer(Player player) {
        return this.shieldPerPlayer.get(player);
    }

    @JsonGetter("cannonsPerPlayer")
    public Pair<Integer, Integer> getCannonCoordinatesPerPlayer(Player player) {
        return this.cannonPerPlayer.get(player);
    }
}
