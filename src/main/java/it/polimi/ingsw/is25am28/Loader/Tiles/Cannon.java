package it.polimi.ingsw.is25am28.Loader.Tiles;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "force",
})
public final class Cannon extends Tile {
    @JsonProperty("force")  private Integer force;

    @JsonGetter("force")
    public Integer getForce() {
        return force;
    }

    @JsonSetter("force")
    public void setForce(Integer force) {
        this.force = force;
    }
}
