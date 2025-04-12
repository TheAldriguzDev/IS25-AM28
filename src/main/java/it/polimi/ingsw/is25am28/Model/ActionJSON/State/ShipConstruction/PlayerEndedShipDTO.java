package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public non-sealed class PlayerEndedShipDTO extends ShipConstructionEventDTO {
    private List<String> playerNicknames;

    public PlayerEndedShipDTO() {}

    public PlayerEndedShipDTO(@JsonProperty("playerNicknames") List<String> playerNicknames) {
        this.playerNicknames = playerNicknames;
    }

    @JsonGetter("playerNicknames")
    public List<String> getPlayerNicknames() {
        return this.playerNicknames;
    }

    @JsonSetter("playerNicknames")
    public PlayerEndedShipDTO setPlayerNicknames(List<String> playerNicknames) {
        this.playerNicknames = playerNicknames;
        return this;
    }
}