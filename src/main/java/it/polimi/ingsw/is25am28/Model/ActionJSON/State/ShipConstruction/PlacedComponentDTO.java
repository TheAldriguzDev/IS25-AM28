package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

/**
 * Represents a data transfer data object that holds information about a component that has been placed
 * <br>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
public final class PlacedComponentDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private Integer id;
    private Integer i;
    private Integer j;
    private Integer rotation;

    @JsonCreator
    public PlacedComponentDTO() {}

    @JsonCreator
    public PlacedComponentDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("id") Integer id,
            @JsonProperty("i") Integer i,
            @JsonProperty("j") Integer j,
            @JsonProperty("rotation")Integer rotation
    ) {
        this.playerNickname = playerNickname;
        this.id = id;
        this.i = i;
        this.j = j;
        this.rotation = rotation;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonSetter("playerNickname")
    public PlacedComponentDTO setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("id")
    public Integer getId() {
        return id;
    }

    @JsonSetter("id")
    public PlacedComponentDTO setId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonGetter("i")
    public Integer getI() {
        return i;
    }

    @JsonSetter("i")
    public PlacedComponentDTO setI(Integer i) {
        this.i = i;
        return this;
    }

    @JsonGetter("j")
    public Integer getJ() {
        return j;
    }

    @JsonSetter("j")
    public PlacedComponentDTO setJ(Integer j) {
        this.j = j;
        return this;
    }

    @JsonGetter("rotation")
    public Integer getRotation() {
        return rotation;
    }

    @JsonSetter("rotation")
    public PlacedComponentDTO setRotation(Integer rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
