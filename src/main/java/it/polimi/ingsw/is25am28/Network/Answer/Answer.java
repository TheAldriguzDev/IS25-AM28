package it.polimi.ingsw.is25am28.Network.Answer;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;

import java.io.Serializable;

/**
 * The Answer represent the message that is sent from the Server to the Clients
 * in order to update the clients model and refresh the UI visual.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Answer.class, name = "Answer"),
        @JsonSubTypes.Type(value = ErrorAnswer.class, name = "ErrorAnswer"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed class Answer implements Serializable permits ErrorAnswer {
    private String playerNickname;
    private StateDTO state;
    private StateDTO nextState;

    @JsonCreator
    public Answer() {}

    @JsonCreator
    public Answer(
            @JsonProperty("playerNickname") String playerNickname,
            @JsonProperty("state") StateDTO state,
            @JsonProperty("nextState") StateDTO nextState
    ) {
        this.playerNickname = playerNickname;
        this.state = state;
        this.nextState = nextState;
    }

    @JsonGetter("playerNickname")
    public String getPlayerNickname() {
        return playerNickname;
    }

    @JsonSetter("playerNickname")
    public Answer setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
        return this;
    }

    @JsonGetter("state")
    public StateDTO getState() {
        return state;
    }

    @JsonSetter("state")
    public Answer setState(StateDTO state) {
        this.state = state;
        return this;
    }

    @JsonGetter("nextState")
    public StateDTO getNextState() {
        return nextState;
    }

    @JsonSetter("nextState")
    public Answer setNextState(StateDTO nextState) {
        this.nextState = nextState;
        return this;
    }
}
