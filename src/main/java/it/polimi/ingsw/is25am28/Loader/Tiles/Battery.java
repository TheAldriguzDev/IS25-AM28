package it.polimi.ingsw.is25am28.Loader.Tiles;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "capacity",
})
public final class Battery extends Tile {
    @JsonProperty("capacity")
    private Integer capacity;

    @JsonGetter("capacity")
    public Integer getCapacity() {
        return capacity;
    }

    @JsonSetter("capacity")
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
