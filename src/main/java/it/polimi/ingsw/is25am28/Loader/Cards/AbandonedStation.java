package it.polimi.ingsw.is25am28.Loader.Cards;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "blue",
        "green",
        "yellow",
        "red",
        "days",
        "people",
})
public final class AbandonedStation extends Card {
    @JsonProperty("blue")       private Integer blue;
    @JsonProperty("green")      private Integer green;
    @JsonProperty("yellow")     private Integer yellow;
    @JsonProperty("red")        private Integer red;
    @JsonProperty("days")       private Integer days;
    @JsonProperty("people")     private Integer people;

    @JsonGetter("blue")
    public Integer getBlue() {
        return blue;
    }

    @JsonSetter("blue")
    public void setBlue(Integer blue) {
        this.blue = blue;
    }

    @JsonGetter("green")
    public Integer getGreen() {
        return green;
    }

    @JsonSetter("green")
    public void setGreen(Integer green) {
        this.green = green;
    }

    @JsonGetter("yellow")
    public Integer getYellow() {
        return yellow;
    }

    @JsonSetter("yellow")
    public void setYellow(Integer yellow) {
        this.yellow = yellow;
    }

    @JsonGetter("red")
    public Integer getRed() {
        return red;
    }

    @JsonSetter("red")
    public void setRed(Integer red) {
        this.red = red;
    }

    @JsonGetter("days")
    public Integer getDays() {
        return days;
    }

    @JsonSetter("days")
    public void setDays(Integer days) {
        this.days = days;
    }

    @JsonGetter("people")
    public Integer getPeople() {
        return people;
    }

    @JsonSetter("people")
    public void setPeople(Integer people) {
        this.people = people;
    }
}
