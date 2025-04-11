package it.polimi.ingsw.is25am28.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class PopulateShipDTO extends StateJSON {
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
