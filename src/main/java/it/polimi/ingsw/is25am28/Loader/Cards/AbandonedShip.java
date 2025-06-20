package it.polimi.ingsw.is25am28.Loader.Cards;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "credits",
        "days",
        "people",
})
public final class AbandonedShip extends Card {
    @JsonProperty("credits")
    private Integer credits;
    @JsonProperty("days")
    private Integer days;
    @JsonProperty("people")
    private Integer people;

    @JsonGetter("credits")
    public Integer getCredits() {
        return credits;
    }

    @JsonSetter("credits")
    public void setCredits(Integer credits) {
        this.credits = credits;
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
