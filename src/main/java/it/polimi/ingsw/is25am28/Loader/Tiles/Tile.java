package it.polimi.ingsw.is25am28.Loader.Tiles;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

/**
 * Defines the abstract JSON structure for parsing tile configurations saved in a JSON file.
 * Each concrete implementation of this {@code Tile} class provides methods to retrieve tile-specific parameters.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "connectors",
})
public sealed abstract class Tile permits Battery, Cabin, Cannon, Engine, Shield, Storage, Structural, Vital {
    @JsonProperty("connectors")     private List<Integer> connectors;
    @JsonProperty("path")           private String path;

    @JsonGetter("connectors")
    public List<Integer> getConnectors() {
        return connectors;
    }

    @JsonSetter("connectors")
    public void setConnectors(List<Integer> connectors) {
        this.connectors = connectors;
    }

    @JsonGetter("path")
    public String getPath() {
        return path;
    }

    @JsonSetter("path")
    public void setPath(String path) {
        this.path = path;
    }
}
