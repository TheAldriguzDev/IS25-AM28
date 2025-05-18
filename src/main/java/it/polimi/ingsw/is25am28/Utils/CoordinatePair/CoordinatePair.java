package it.polimi.ingsw.is25am28.Utils.CoordinatePair;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

public class CoordinatePair implements Serializable {
    @JsonProperty private Integer i;
    @JsonProperty private Integer j;

    @JsonCreator
    public CoordinatePair() {}

    @JsonCreator
    public CoordinatePair(
            @JsonProperty("i") Integer i,
            @JsonProperty("j") Integer j
    ) {
        this.i = i;
        this.j = j;
    }

    @JsonGetter("i")
    public Integer getI() {
        return this.i;
    }

    @JsonSetter("i")
    public CoordinatePair setI(Integer i) {
        this.i = i;
        return this;
    }

    @JsonGetter("j")
    public Integer getJ() {
        return this.j;
    }

    @JsonSetter("j")
    public CoordinatePair setJ(Integer j) {
        this.j = j;
        return this;
    }
}
