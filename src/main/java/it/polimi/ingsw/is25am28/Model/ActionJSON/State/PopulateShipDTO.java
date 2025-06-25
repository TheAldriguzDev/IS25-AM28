package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a data transfer object that holds information about the players that need to populate their ships.
 * <br>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PopulateShipDTO extends StateDTO implements Serializable  {
    private List<String> playersReady;

    @JsonCreator
    public PopulateShipDTO() {}

    @JsonCreator
    public PopulateShipDTO(
            @JsonProperty("playersReady") List<String> playersReady
    ) {
        this.playersReady = playersReady;
    }

    @JsonGetter("playersReady")
    public List<String> getPlayersReady() {
        return playersReady;
    }

    @JsonSetter("playersReady")
    public PopulateShipDTO setPlayersReady(List<String> playersReady) {
        this.playersReady = playersReady;

        return this;
    }

    @Override
    public void accept(StateVisitor visitor) {
        try {
            visitor.visit(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
