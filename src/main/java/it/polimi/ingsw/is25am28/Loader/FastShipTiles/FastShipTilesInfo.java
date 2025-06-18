package it.polimi.ingsw.is25am28.Loader.FastShipTiles;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Defines the JSON structure used to parse and load saved fastShipTiles into the game, providing methods to retrieve tile-specific parameters.
 */

public class FastShipTilesInfo {
    @JsonProperty("id")
    private int id;

    @JsonProperty("direction")
    private int direction;

    @JsonProperty("row")
    private int row;

    @JsonProperty("col")
    private int col;

    @JsonGetter("id")
    public int getId() {
        return id;
    }
    @JsonSetter("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonGetter("direction")
    public int getDirection() {
        return direction;
    }
    @JsonSetter("direction")
    public void setDirection(int direction) {
        this.direction = direction;
    }

    @JsonGetter("row")
    public int getRow() {
        return row;
    }
    @JsonSetter("row")
    public void setRow(int row) {
        this.row = row;
    }

    @JsonGetter("col")
    public int getCol() {
        return col;
    }
    @JsonSetter("col")
    public void setCol(int col) {
        this.col = col;
    }
}
