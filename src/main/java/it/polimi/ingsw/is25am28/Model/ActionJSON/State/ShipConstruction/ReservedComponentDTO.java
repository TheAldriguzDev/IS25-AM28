package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

/**
 * Represents a data transfer data object that holds information about a reserved component
 *
 * * Annotations from the Jackson library are used for JSON serialization and deserialization,
 *  * ensuring that only non-null values are included in the JSON output.
 */
public final class ReservedComponentDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private Integer id;

    public ReservedComponentDTO() {}

    public ReservedComponentDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("id") Integer id) {
        this.playerNickname = playerNickname;
        this.id = id;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonSetter("playerNickname")
    public ReservedComponentDTO setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("id")
    public Integer getId() {
        return id;
    }

    @JsonSetter("id")
    public ReservedComponentDTO setId(Integer id) {
        this.id = id;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
