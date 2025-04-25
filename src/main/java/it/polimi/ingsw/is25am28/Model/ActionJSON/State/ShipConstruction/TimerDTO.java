package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TimerDTO extends ShipConstructionEventDTO {
    // If the HourGlass can be flipped in future
    private boolean canBeFlipped;

    // If true --> it's the last HourGlass event
    private boolean hasEnded;

    // If true the DTO is created in response to onTimerEnd
    // otherwise the response is from an executed command by a player
    private boolean isServerAction;

    public TimerDTO() {}

    public TimerDTO(
            @JsonProperty("canBeFlipped") boolean canBeFlipped,
            @JsonProperty("hasEnded") boolean hasEnded,
            @JsonProperty("isServerAction") boolean isServerAction
    ) {
        this.canBeFlipped = canBeFlipped;
        this.hasEnded = hasEnded;
        this.isServerAction = isServerAction;
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

    @JsonGetter("hasEnded")
    public boolean getHasEnded() {
        return hasEnded;
    }

    @JsonSetter("hasEnded")
    public TimerDTO setHasEnded(boolean hasEnded) {
        this.hasEnded = hasEnded;
        return this;
    }

    @JsonGetter("isServerAction")
    public boolean getIsServerAction() {
        return isServerAction;
    }

    @JsonSetter("isServerAction")
    public TimerDTO setIsServerAction(boolean isServerAction) {
        this.isServerAction = isServerAction;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
