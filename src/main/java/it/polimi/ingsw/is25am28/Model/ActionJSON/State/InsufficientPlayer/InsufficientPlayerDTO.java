package it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateVisitor;

/**
 * This state will be used to notify the connected client that he's waiting for client reconnection.
 * */

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class InsufficientPlayerDTO extends StateDTO {
    private Integer countdown;

    public InsufficientPlayerDTO() {}

    public InsufficientPlayerDTO(@JsonProperty("countdown") Integer countdown) {
        this.countdown = countdown;
    }

    @JsonGetter("countdown")
    public Integer getCountdown() {
        return countdown;
    }

    @JsonSetter("countdown")
    public InsufficientPlayerDTO setCountdown(Integer countdown) {
        this.countdown = countdown;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
