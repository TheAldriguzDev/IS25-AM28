package it.polimi.ingsw.is25am28.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class StateJSON {
    private String stateName;

    public StateJSON() {}

    public StateJSON(@JsonProperty("stateName") String stateName) {
        this.stateName = stateName;
    }

    @JsonGetter("stateName")
    public String getStateName() {
        return this.stateName;
    }

    @JsonSetter("stateName")
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
