package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

public final class FixedComponentDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private int i;
    private int j;
    private boolean isShipFixed;

    public FixedComponentDTO() {}

    public FixedComponentDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("i") int i,
            @JsonProperty("j") int j,
            @JsonProperty("isShipFixed") boolean isShipFixed) {
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
