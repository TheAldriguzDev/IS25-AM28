package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.*;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed class ShipConstructionEventDTO extends StateDTO permits ConstructionComponentDTO, ConstructionDeckDTO, FixedComponentDTO, PlacedComponentDTO, PlayerEndedShipDTO, PopulateShipComponentDTO, ReservedComponentDTO, TimerDTO {
    private String eventType;

    @JsonCreator
    public ShipConstructionEventDTO() {}

    @JsonCreator
    public ShipConstructionEventDTO(
            @JsonProperty("eventType") String eventType
    ) {
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
