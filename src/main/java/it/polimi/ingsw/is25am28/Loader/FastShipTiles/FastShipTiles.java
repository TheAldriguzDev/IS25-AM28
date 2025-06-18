package it.polimi.ingsw.is25am28.Loader.FastShipTiles;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

/**
 * Defines the JSON structure used to parse and load the tiles of a certain ship to create with the fastShip command
 */

@JsonInclude(JsonInclude.Include.NON_NULL)

public class FastShipTiles {
    @JsonProperty("0")
    private List<FastShipTilesInfo> firstShipTiles;
    @JsonProperty("1")
    private List<FastShipTilesInfo> secondShipTiles;
    @JsonProperty("2")
    private List<FastShipTilesInfo> thirdShipTiles;

    @JsonGetter("0")
    public List<FastShipTilesInfo> getFirstShipTiles() {
        return firstShipTiles;
    }
    @JsonGetter("1")
    public List<FastShipTilesInfo> getSecondShipTiles() {
        return secondShipTiles;
    }
    @JsonGetter("2")
    public List<FastShipTilesInfo> getThirdShipTiles() {
        return thirdShipTiles;
    }

    @JsonSetter("0")
    public void setFirstShipTiles(List<FastShipTilesInfo> firstShipTiles) {
        this.firstShipTiles = firstShipTiles;
    }
    @JsonSetter("1")
    public void setSecondShipTiles(List<FastShipTilesInfo> secondShipTiles) {
        this.secondShipTiles = secondShipTiles;
    }
    @JsonSetter("2")
    public void setThirdShipTiles(List<FastShipTilesInfo> thirdShipTiles) {
        this.thirdShipTiles = thirdShipTiles;
    }

    public List<FastShipTilesInfo> getFastShipTilesInfo(int index) throws IllegalArgumentException {
        return switch (index) {
            case 0 -> firstShipTiles;
            case 1 -> secondShipTiles;
            case 2 -> thirdShipTiles;
            default -> throw new IllegalArgumentException();
        };
    }
}
