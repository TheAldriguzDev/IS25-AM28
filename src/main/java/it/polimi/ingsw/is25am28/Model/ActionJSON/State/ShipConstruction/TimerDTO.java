package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

import com.fasterxml.jackson.annotation.*;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TimerDTO extends ShipConstructionEventDTO {
    // If the HourGlass can be flipped in the future
    private boolean canBeFlipped;

    // If true --> it's the last HourGlass event
    private boolean hasEnded;

    // If true the DTO is created in response to onTimerEnd
    // otherwise the response is from an executed command by a player
    private boolean isServerAction;

    private boolean isTimeFlowing;

    @JsonCreator
    public TimerDTO() {}

    @JsonCreator
    public TimerDTO(
            @JsonProperty("canBeFlipped") boolean canBeFlipped,
            @JsonProperty("hasEnded") boolean hasEnded,
            @JsonProperty("isServerAction") boolean isServerAction,
            @JsonProperty("isTimeFlowing") boolean isTimeFlowing
    ) {
        this.canBeFlipped = canBeFlipped;
        this.hasEnded = hasEnded;
        this.isServerAction = isServerAction;
        this.isTimeFlowing = isTimeFlowing;
    }

    @JsonGetter("canBeFlipped")
    public boolean getCanBeFlipped() {
        return canBeFlipped;
    }

    @JsonSetter("canBeFlipped")
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

    @JsonGetter("isTimeFlowing")
    public boolean isTimeFlowing() {
        return this.isTimeFlowing;
    }

    @JsonSetter("isTimeFlowing")
    public TimerDTO setIsTimeFlowing(boolean isTimeFlowing) {
        this.isTimeFlowing = isTimeFlowing;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
