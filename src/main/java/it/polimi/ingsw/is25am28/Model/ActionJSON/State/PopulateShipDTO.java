package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;


/**
 * Represents a data transfer object that holds information about the players that need to populate their ships.
 *
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PopulateShipDTO extends StateDTO implements Serializable  {
    private List<String> playersReady;

    public PopulateShipDTO() {}

    public PopulateShipDTO(@JsonProperty("playersReady") List<String> playersReady) {
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
