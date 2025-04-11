package it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;

public class ShipConstructionEventDTO extends StateJSON {
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
