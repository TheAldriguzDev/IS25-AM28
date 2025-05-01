package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.GameModelv2.InsufficientPlayerState;

import java.io.Serial;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StateDTO.class, name = "StateDTO"),
        @JsonSubTypes.Type(value = AvailableGamesDTO.class, name = "AvailableGamesDTO"),
        @JsonSubTypes.Type(value = CreateGameStateDTO.class, name = "CreateGameStateDTO"),
        @JsonSubTypes.Type(value = WaitPlayersStateDTO.class, name = "WaitPlayersStateDTO"),
        @JsonSubTypes.Type(value = ReconnectDTO.class, name = "ReconnectDTO"),
        @JsonSubTypes.Type(value = TimerDTO.class, name = "TimerDTO"),
        @JsonSubTypes.Type(value = WaitingForGameConfigurationDTO.class, name = "WaitingForGameConfigurationDTO"),
        @JsonSubTypes.Type(value = ShipConstructionDTO.class, name = "ShipConstructionDTO"),
        @JsonSubTypes.Type(value = ConstructionComponentDTO.class, name = "ConstructionComponentDTO"),
        @JsonSubTypes.Type(value = ConstructionDeckDTO.class, name = "ConstructionDeckDTO"),
        @JsonSubTypes.Type(value = DisconnectedPlayerDTO.class, name = "DisconnectedPlayerDTO"),
        @JsonSubTypes.Type(value = InsufficientPlayerDTO.class, name = "InsufficientPlayerDTO")
})

@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed class StateDTO implements Serializable permits AvailableGamesDTO, CardRoundDTO, CreateGameStateDTO, EndGameDTO, FixShipDTO, DisconnectedPlayerDTO, InsufficientPlayerDTO, PopulateShipDTO, ReconnectDTO, ShipConstructionDTO, ShipConstructionEventDTO, WaitPlayersStateDTO, WaitingForGameConfigurationDTO {
    @Serial
    private static final long serialVersionUID = 1L;

    private String stateName;

    public StateDTO() {}

    public StateDTO(@JsonProperty("stateName") String stateName) {
        this.stateName = stateName;
    }

    @JsonGetter("stateName")
    public String getStateName() {
        return this.stateName;
    }

    @JsonSetter("stateName")
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * Accept the visitor to visit the state
     * */
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
