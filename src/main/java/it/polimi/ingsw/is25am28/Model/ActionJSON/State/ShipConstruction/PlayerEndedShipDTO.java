package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PlayerEndedShipDTO extends ShipConstructionEventDTO {
    // Contains the cardName of the player that ended the ship
    private String playerNickname;
    private Integer playerCredits;
    private Integer playerCursors;

    public PlayerEndedShipDTO() {}

    public PlayerEndedShipDTO(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("playerCredits") Integer playerCredits,
            @JsonProperty("playerCursors") Integer playerCursors
    ) {
        this.playerNickname = playerNickname;
        this.playerCredits = playerCredits;
        this.playerCursors = playerCursors;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    @JsonSetter("playerNickname")
    public PlayerEndedShipDTO setPlayerNicknames(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("playerCredits")
    public Integer getPlayerCredits() {
        return this.playerCredits;
    }

    @JsonSetter("playerCredits")
    public PlayerEndedShipDTO setPlayerCredits(Integer playerCredits) {
        this.playerCredits = playerCredits;
        return this;
    }

    @JsonGetter("playerCursors")
    public Integer getPlayerCursors() {
        return this.playerCursors;
    }

    @JsonSetter("playerCursors")
    public PlayerEndedShipDTO setPlayerCursors(Integer playerCursors) {
        this.playerCursors = playerCursors;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}