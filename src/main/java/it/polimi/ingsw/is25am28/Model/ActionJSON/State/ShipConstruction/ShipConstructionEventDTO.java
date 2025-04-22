package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed class ShipConstructionEventDTO extends StateDTO permits ConstructionComponentDTO, ConstructionDeckDTO, PlayerEndedShipDTO, TimerDTO {
    private String eventType;

    public ShipConstructionEventDTO() {}

    public ShipConstructionEventDTO(@JsonProperty("eventType") String eventType) {
        this.eventType = eventType;
    }

    @JsonGetter("eventType")
    public String getEventType() {
        return eventType;
    }

    @JsonSetter("eventType")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
