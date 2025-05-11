package it.polimi.ingsw.is25am28.Loader.Cards;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "blue",
        "green",
        "yellow",
        "red"
})
public class ItemsInfo {
    public ItemsInfo() {}

    @JsonProperty("blue")
    private int blue;
    @JsonProperty("green")
    private int green;
    @JsonProperty("yellow")
    private int yellow;
    @JsonProperty("red")
    private int red;

    @JsonGetter("blue")
    public int getBlue() {
        return blue;
    }

    @JsonSetter("blue")
    public void setBlue(int blue) {
        this.blue = blue;
    }

    @JsonGetter("green")
    public int getGreen() {
        return green;
    }

    @JsonSetter("green")
    public void setGreen(int green) {
        this.green = green;
    }

    @JsonGetter("yellow")
    public int getYellow() {
        return yellow;
    }

    @JsonSetter("yellow")
    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    @JsonGetter("red")
    public int getRed() {
        return red;
    }

    @JsonSetter("red")
    public void setRed(int red) {
        this.red = red;
    }
}
