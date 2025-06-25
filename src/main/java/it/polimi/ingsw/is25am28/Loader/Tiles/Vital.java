package it.polimi.ingsw.is25am28.Loader.Tiles;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type"
})
public final class Vital extends Tile {
    @JsonProperty("type")   private Integer type;

    @JsonGetter("type")
    public Integer getType() {
        return type;
    }

    @JsonSetter("type")
    public void setType(Integer type) {
        this.type = type;
    }
}
