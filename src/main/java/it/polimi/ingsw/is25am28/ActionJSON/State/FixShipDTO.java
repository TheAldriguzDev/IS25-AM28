package it.polimi.ingsw.is25am28.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class FixShipDTO extends StateJSON {
    private List<String> playerWithInvalidShip;

    public FixShipDTO() {}

    public FixShipDTO(
            @JsonProperty("playerWithInvalidShip") List<String> playerWithInvalidShip ) {
        this.playerWithInvalidShip = playerWithInvalidShip;
    }

    @JsonGetter("playerWithInvalidShip")
    public List<String> getPlayerWithInvalidShip() {
        return playerWithInvalidShip;
    }

    @JsonSetter("playerWithInvalidShip")
    public FixShipDTO setPlayerWithInvalidShip(List<String> playerWithInvalidShip) {
        this.playerWithInvalidShip = playerWithInvalidShip;
        return this;
    }
}
