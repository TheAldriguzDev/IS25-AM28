package it.polimi.ingsw.is25am28.Loader.Cards;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "days",
        "cannons",
        "penalty",
        "credits"
})
public final class Slaver extends Card {
    @JsonProperty("days")       private Integer days;
    @JsonProperty("cannons")    private Integer cannons;
    @JsonProperty("penalty")    private Integer penalty;
    @JsonProperty("credits")    private Integer credits;

    @JsonGetter("days")
    public Integer getDays() {
        return days;
    }

    @JsonSetter("days")
    public void setDays(Integer days) {
        this.days = days;
    }

    @JsonGetter("cannons")
    public Integer getCannons() {
        return cannons;
    }

    @JsonSetter("cannons")
    public void setCannons(Integer cannons) {
        this.cannons = cannons;
    }

    @JsonGetter("penalty")
    public Integer getPenalty() {
        return penalty;
    }

    @JsonSetter("penalty")
    public void setPenalty(Integer penalty) {
        this.penalty = penalty;
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
