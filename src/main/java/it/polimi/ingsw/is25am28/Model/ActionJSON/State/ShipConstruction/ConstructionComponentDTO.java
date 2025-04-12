package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public non-sealed class ConstructionComponentDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private int i;
    private int j;
    private int rotation;
    boolean isSelected;

    public ConstructionComponentDTO() {}

    public ConstructionComponentDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("i") int i,
            @JsonProperty("j") int j,
            @JsonProperty("rotation") int rotation,
            @JsonProperty("isSelected") boolean isSelected ) {
        this.playerNickname = playerNickname;
        this.i = i;
        this.j = j;
        this.isSelected = isSelected;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public ConstructionComponentDTO setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("i")
    public int getI() {
        return this.i;
    }

    @JsonSetter("i")
    public ConstructionComponentDTO setI(int i) {
        this.i = i;
        return this;
    }

    @JsonGetter("j")
    public int getJ() {
        return this.j;
    }

    @JsonSetter("j")
    public ConstructionComponentDTO setJ(int j) {
        this.j = j;
        return this;
    }

    @JsonGetter("rotation")
    public int getRotation() {
        return this.rotation;
    }

    @JsonSetter("rotation")
    public ConstructionComponentDTO setRotation(int rotation) {
        this.rotation = rotation;
        return this;
    }

    @JsonGetter("isSelected")
    public boolean isSelected() {
        return this.isSelected;
    }

    @JsonSetter("isSelected")
    public ConstructionComponentDTO setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }
}
