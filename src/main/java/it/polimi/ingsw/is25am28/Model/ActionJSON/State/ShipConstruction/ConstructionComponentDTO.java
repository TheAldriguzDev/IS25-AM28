package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ConstructionComponentDTO extends ShipConstructionEventDTO {
    private String playerNickname;
    private Integer id;
    private Integer rotation;
    boolean isSelected;

    public ConstructionComponentDTO() {}

    public ConstructionComponentDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("id") Integer id,
            @JsonProperty("rotation") Integer rotation,
            @JsonProperty("isSelected") boolean isSelected ) {
        this.playerNickname = playerNickname;
        this.id = id;
        this.rotation = rotation;
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

    @JsonGetter("id")
    public Integer getId() {
        return this.id;
    }

    @JsonSetter("id")
    public ConstructionComponentDTO setId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonGetter("rotation")
    public Integer getRotation() {
        return this.rotation;
    }

    @JsonSetter("rotation")
    public ConstructionComponentDTO setRotation(Integer rotation) {
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

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
