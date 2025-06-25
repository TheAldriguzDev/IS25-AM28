package it.polimi.ingsw.is25am28.Loader.Tiles;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "capacity",
        "special"
})
public final class Storage extends Tile {
    @JsonProperty("capacity")   private Integer capacity;
    @JsonProperty("special")    private Boolean special;

    @JsonGetter("capacity")
    public Integer getCapacity() {
        return capacity;
    }

    @JsonSetter("capacity")
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @JsonGetter("special")
    public Boolean getSpecial() {
        return special;
    }

    @JsonSetter("special")
    public void setSpecial(Boolean special) {
        this.special = special;
    }
}
