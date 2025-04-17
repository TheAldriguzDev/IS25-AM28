package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DisconnectedPlayerDTO extends StateDTO implements Serializable {
    List<String> disconnectedPlayers;

    public DisconnectedPlayerDTO() {}

    public DisconnectedPlayerDTO(@JsonProperty("disconnectedPlayers") List<String> disconnectedPlayers) {
        this.disconnectedPlayers = disconnectedPlayers;
    }

    /**
     * @return a list of Strings that contains the disconnected player list
     * */
    @JsonGetter("disconnectedPlayers")
    public List<String> getDisconnectedPlayers() {
        return this.disconnectedPlayers;
    }

    @JsonSetter("disconnectedPlayers")
    public void setDisconnectedPlayers(List<String> disconnectedPlayers) {
        this.disconnectedPlayers = disconnectedPlayers;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
