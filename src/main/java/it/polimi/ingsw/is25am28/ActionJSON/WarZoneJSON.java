package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonSetter;
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

    @JsonSetter("enginesPerPlayer")
    public void setEnginesPerPlayer(Map<Player, Integer> enginesPerPlayer) {
        this.enginesPerPlayer = enginesPerPlayer;
    }

    @JsonGetter("enginesPerPlayer")
    public int getEngineAmountPerPlayer(Player player) {
        return this.enginesPerPlayer.get(player);
    }

    @JsonSetter("cannonsPerPlayer")
    public void setCannonsPerPlayer(Map<Player, Integer> cannonsPerPlayer) {
        this.cannonsPerPlayer = cannonsPerPlayer;
    }

    @JsonGetter("cannonsPerPlayer")
    public int getCannonAmountPerPlayer(Player player) {
        return this.cannonsPerPlayer.get(player);
    }

    @JsonSetter("shieldsPerPlayer")
    public void setShieldsPerPlayer(Map<Player, List<Pair<Integer, Integer>>> shieldsPerPlayer) {
        this.shieldsPerPlayer = shieldsPerPlayer;
    }

    @JsonGetter("shieldsPerPlayer")
    public List<Pair<Integer, Integer>> getShieldCoordinatesPerPlayer(Player player) {
        return this.shieldsPerPlayer.get(player);
    }
}
