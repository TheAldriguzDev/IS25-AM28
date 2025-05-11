package it.polimi.ingsw.is25am28.Loader.Cards;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "days",
        "planets"
})
public final class Planet extends Card {
    @JsonProperty("level")
    private Integer level;
    @JsonProperty("days")
    private Integer days;
    @JsonProperty("planets")
    private List<ItemsInfo> planets;
    @JsonProperty("path")
    private String path;

    @JsonGetter("level")
    public Integer getLevel() {
        return level;
    }

    @JsonSetter("level")
    public void setLevel(Integer level) {
        this.level = level;
    }

    @JsonGetter("days")
    public Integer getDays() {
        return days;
    }

    @JsonSetter("days")
    public void setDays(Integer days) {
        this.days = days;
    }

    @JsonGetter("planets")
    public List<ItemsInfo> getPlanets() {
        return planets;
    }

    @JsonSetter("planets")
    public void setPlanets(List<ItemsInfo> planets) {
        this.planets = planets;
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
