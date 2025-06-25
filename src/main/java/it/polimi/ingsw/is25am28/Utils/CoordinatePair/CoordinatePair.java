package it.polimi.ingsw.is25am28.Utils.CoordinatePair;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

/**
 * A class representing a pair of coordinates with two integer values, i and j.
 * Useful for network communication where serialization is required.
 */
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

    /**
     * Retrieves the value of the 'i' coordinate in the coordinate pair.
     *
     * @return the integer value representing the 'i' coordinate, or null if not set.
     */
    @JsonGetter("i")
    public Integer getI() {
        return this.i;
    }

    /**
     * Sets the value of the 'i' coordinate in the coordinate pair.
     */
    @JsonSetter("i")
    public CoordinatePair setI(Integer i) {
        this.i = i;
        return this;
    }

    /**
     * Retrieves the value of the 'j' coordinate in the coordinate pair.
     *
     * @return the integer value representing the 'j' coordinate, or null if not set.
     */
    @JsonGetter("j")
    public Integer getJ() {
        return this.j;
    }

    /**
     * Sets the value of the 'j' coordinate in the coordinate pair.
     */
    @JsonSetter("j")
    public CoordinatePair setJ(Integer j) {
        this.j = j;
        return this;
    }
}
