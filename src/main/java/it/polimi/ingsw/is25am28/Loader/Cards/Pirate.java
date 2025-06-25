package it.polimi.ingsw.is25am28.Loader.Cards;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "firepower",
        "shoots",
        "days",
        "credits"
})
public final class Pirate extends Card {
    @JsonProperty("firepower")      private Integer firepower;
    @JsonProperty("shoots")         private List<List<Integer>> shoots;
    @JsonProperty("days")           private Integer days;
    @JsonProperty("credits")        private Integer credits;

    @JsonGetter("firepower")
    public Integer getFirepower() {
        return firepower;
    }

    @JsonSetter("firepower")
    public void setFirepower(Integer firepower) {
        this.firepower = firepower;
    }

    @JsonGetter("shoots")
    public List<List<Integer>> getShoots() {
        return shoots;
    }

    @JsonSetter("shoots")
    public void setShoots(List<List<Integer>> shoots) {
        this.shoots = shoots;
    }

    @JsonGetter("days")
    public Integer getDays() {
        return days;
    }

    @JsonSetter("days")
    public void setDays(Integer days) {
        this.days = days;
    }

    @JsonGetter("credits")
    public Integer getCredits() {
        return credits;
    }

    @JsonSetter("credits")
    public void setCredits(Integer credits) {
        this.credits = credits;
    }
}
