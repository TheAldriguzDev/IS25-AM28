package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PopulateShipDTO extends StateJSON {
    private List<String> playersReady;

    public PopulateShipDTO() {}

    public PopulateShipDTO(@JsonProperty("playersReady") List<String> playersReady) {
        this.playersReady = playersReady;
    }

    @JsonGetter("playersReady")
    public List<String> getPlayersReady() {
        return playersReady;
    }

    @JsonSetter("playersReady")
    public PopulateShipDTO setPlayersReady(List<String> playersReady) {
        this.playersReady = playersReady;

        return this;
    }
}
