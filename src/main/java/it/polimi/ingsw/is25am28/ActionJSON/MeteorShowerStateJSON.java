package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class MeteorShowerStateJSON extends CardStateJSON{
    private int currMeteor;

    @JsonCreator
    public MeteorShowerStateJSON() {
        super();
    }

    @JsonSetter("currMeteor")
    public void setCurrMeteor(int currMeteor) {
        this.currMeteor = currMeteor;
    }

    @JsonGetter("currMeteor")
    public int getCurrMeteor() {
        return this.currMeteor;
    }
}
