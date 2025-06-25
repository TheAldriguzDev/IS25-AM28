package it.polimi.ingsw.is25am28.Loader.Cards;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "meteors"
})
public final class Meteor extends Card {
    @JsonProperty("meteors")    private List<List<Integer>> meteors;

    @JsonGetter("meteors")
    public List<List<Integer>> getMeteors() {
        return meteors;
    }

    @JsonSetter("meteors")
    public void setMeteors(List<List<Integer>> meteors) {
        this.meteors = meteors;
    }
}
