package it.polimi.ingsw.is25am28.Loader.Tiles;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "speed",
})
public final class Engine extends Tile {
    @JsonProperty("speed")  private Integer speed;

    @JsonGetter("speed")
    public Integer getSpeed() {
        return speed;
    }

    @JsonSetter("speed")
    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
}
