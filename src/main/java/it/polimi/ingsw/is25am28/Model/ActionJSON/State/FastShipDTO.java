package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class FastShipDTO extends StateDTO {
    private String targetNickname;
    private List<Map<String, Object>> ship;

    @JsonCreator
    public FastShipDTO() {}

    @JsonCreator
    public FastShipDTO(
        @JsonProperty("targetNickname") String targetNickname,
        @JsonProperty("ship") List<Map<String, Object>> ship
    ) {
        this.targetNickname = targetNickname;
        this.ship = ship;
    }
    
    @JsonGetter("targetNickname")
    public String getTargetNickname() {
        return this.targetNickname;
    }

    @JsonSetter("targetNickname")
    public FastShipDTO setTargetNickname(String targetNickname) {
        this.targetNickname = targetNickname;
        return this;
    }

    @JsonGetter("ship")
    public List<Map<String, Object>> getShip() {
        return this.ship;
    }

    @JsonSetter("ship")
    public FastShipDTO setShip(List<Map<String, Object>> ship) {
        this.ship = ship;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
