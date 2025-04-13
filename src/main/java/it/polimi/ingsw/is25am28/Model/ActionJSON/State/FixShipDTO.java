package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class FixShipDTO extends StateDTO implements Serializable  {
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
