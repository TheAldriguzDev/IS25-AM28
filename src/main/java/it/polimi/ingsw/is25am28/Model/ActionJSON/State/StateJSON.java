package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionEventDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed class StateJSON permits CardRoundDTO, CreateGameStateJSON, EndGameDTO, FixShipDTO, PopulateShipDTO, ShipConstructionDTO, ShipConstructionEventDTO, WaitPlayersStateJSON {
    private String stateName;

    public StateJSON() {}

    public StateJSON(@JsonProperty("stateName") String stateName) {
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
}
