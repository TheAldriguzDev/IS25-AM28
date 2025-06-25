package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a data transfer object that holds information about the players that need to fix their ships.
 * <br>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class FixShipDTO extends StateDTO implements Serializable  {
    private List<String> playerWithInvalidShip;

    @JsonCreator
    public FixShipDTO() {}

    @JsonCreator
    public FixShipDTO(
            @JsonProperty("playerWithInvalidShip") List<String> playerWithInvalidShip
    ) {
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

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
