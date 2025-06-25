package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

/**
 * Represents a data transfer data object that holds information about a component that has been removed to fix a ship
 * <br>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
public final class FixedComponentDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private int i;
    private int j;
    private boolean isShipFixed;

    @JsonCreator
    public FixedComponentDTO() {}

    @JsonCreator
    public FixedComponentDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("i") int i,
            @JsonProperty("j") int j,
            @JsonProperty("isShipFixed") boolean isShipFixed
    ) {
        this.playerNickname = playerNickname;
        this.i = i;
        this.j = j;
        this.isShipFixed = isShipFixed;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public FixedComponentDTO setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("i")
    public int getI() {
        return this.i;
    }

    @JsonSetter("i")
    public FixedComponentDTO setI(int i) {
        this.i = i;
        return this;
    }

    @JsonGetter("j")
    public int getJ() {
        return this.j;
    }

    @JsonSetter("j")
    public FixedComponentDTO setJ(int j) {
        this.j = j;
        return this;
    }

    @JsonGetter("isShipFixed")
    public boolean isShipFixed() {
        return this.isShipFixed;
    }

    @JsonSetter("isShipFixed")
    public FixedComponentDTO setShipFixed(boolean isShipFixed) {
        this.isShipFixed = isShipFixed;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
