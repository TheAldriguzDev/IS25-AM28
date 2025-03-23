package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Player.Player;
import javafx.util.Pair;

import java.util.Map;

public class MeteorShowerJSON extends ActionJSON {
    private Map<Player, Pair<Integer, Integer>>  shieldPerPlayer;
    private Map<Player, Pair<Integer, Integer>> cannonPerPlayer;

    @JsonCreator
    public MeteorShowerJSON(
            @JsonProperty("shieldsPerPlayer") Map<Player, Pair<Integer, Integer>> shieldPerPlayer,
            @JsonProperty("cannonsPerPlayer") Map<Player, Pair<Integer, Integer>> cannonPerPlayer
    ) {
        this.shieldPerPlayer = shieldPerPlayer;
        this.cannonPerPlayer = cannonPerPlayer;
    }

    @JsonSetter("shieldsPerPlayer")
    public void setShieldsPerPlayer(Map<Player, Pair<Integer, Integer>> shieldPerPlayer) {
        this.shieldPerPlayer = shieldPerPlayer;
    }

    @JsonGetter("shieldsPerPlayer")
    public Pair<Integer, Integer> getShieldsCoordinatesPerPlayer(Player player) {
        return this.shieldPerPlayer.get(player);
    }

    @JsonSetter("cannonPerPlayer")
    public void setCannonsPerPlayer(Map<Player, Pair<Integer, Integer>> cannonPerPlayer) {
        this.cannonPerPlayer = cannonPerPlayer;
    }

    @JsonGetter("cannonPerPlayer")
    public Pair<Integer, Integer> getCannonsCoordinatesPerPlayer(Player player) {
        return this.cannonPerPlayer.get(player);
    }
}
