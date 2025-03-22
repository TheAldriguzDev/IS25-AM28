package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am28.Player.Player;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class WarZoneJSON extends ActionJSON {
    private Map<Player, Integer> enginesPerPlayer;
    private Map<Player, Integer> cannonsPerPlayer;
    private Map<Player, List<Pair<Integer, Integer>>> shieldsPerPlayer;

    @JsonCreator
    public WarZoneJSON(
            @JsonProperty("engines") Map<Player, Integer> enginesPerPlayer,
            @JsonProperty("cannons") Map<Player, Integer> cannonsPerPlayer,
            @JsonProperty("shields") Map<Player, List<Pair<Integer, Integer>>> shieldsPerPlayer
    ) {
        this.enginesPerPlayer = enginesPerPlayer;
        this.cannonsPerPlayer = cannonsPerPlayer;
        this.shieldsPerPlayer = shieldsPerPlayer;
    }

    @JsonGetter("enginesPerPlayer")
    public int getEngineAmountPerPlayer(Player player) {
        return this.enginesPerPlayer.get(player);
    }

    @JsonGetter("cannonsPerPlayer")
    public int getCannonAmountPerPlayer(Player player) {
        return this.cannonsPerPlayer.get(player);
    }

    @JsonGetter("shieldsPerPlayer")
    public List<Pair<Integer, Integer>> getShieldCoordinatesPerPlayer(Player player) {
        return this.shieldsPerPlayer.get(player);
    }
}
