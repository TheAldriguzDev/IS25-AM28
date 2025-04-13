package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionEventDTO;

import java.io.Serial;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed class StateDTO implements Serializable permits CardRoundDTO, CreateGameStateDTO, EndGameDTO, FixShipDTO, PopulateShipDTO, ShipConstructionDTO, ShipConstructionEventDTO, WaitPlayersStateDTO, WaitingForGameConfigurationDTO {
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
