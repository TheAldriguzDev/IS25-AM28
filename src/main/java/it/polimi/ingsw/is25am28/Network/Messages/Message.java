package it.polimi.ingsw.is25am28.Network.Messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConfigGame.class, name = "ConfigGame"),
        @JsonSubTypes.Type(value = NewPlayer.class, name = "NewPlayer")
})
public sealed interface Message extends Serializable permits ConfigGame, NewPlayer {

    /**
     * @return true if the message it's correct, otherwise it will return false
     * */
    public boolean validate();

    /**
     * @return a list of strings that will indicate the errors in the message
     * */
    public List<String> getErrors();
}
