package it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;

public class TimerDTO extends ShipConstructionEventDTO {
    private boolean hasBeenFlipped; // If the clock has been flipped
    private boolean canBeFlipped; // if the clock can be flipped in future

    public TimerDTO() {}

    public TimerDTO(
            @JsonProperty("canBeFlipped") boolean canBeFlipped,
            @JsonProperty("hasBeenFlipped") boolean hasBeenFlipped
    ) {
        this.canBeFlipped = canBeFlipped;
        this.hasBeenFlipped = hasBeenFlipped;
    }

    @JsonGetter("canBeFlipped")
    public boolean getCanBeFlipped() {
        return canBeFlipped;
    }

    @JsonSetter
    public TimerDTO setCanBeFlipped(boolean canBeFlipped) {
        this.canBeFlipped = canBeFlipped;
        return this;
    }

    @JsonGetter("hasBeenFlipped")
    public boolean getHasBeenFlipped() {
        return hasBeenFlipped;
    }

    @JsonSetter("hasBeenFlipped")
    public TimerDTO setHasBeenFlipped(boolean hasBeenFlipped) {
        this.hasBeenFlipped = hasBeenFlipped;
        return this;
    }
}
