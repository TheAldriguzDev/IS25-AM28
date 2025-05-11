package it.polimi.ingsw.is25am28.Loader.Cards;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "days",
        "storage",
        "cannons",
        "penalty"
})
public final class Smuggler extends Card {
    @JsonProperty("days")
    private Integer days;
    @JsonProperty("storage")
    private ItemsInfo storage;
    @JsonProperty("cannons")
    private Integer cannons;
    @JsonProperty("penalty")
    private Integer penalty;

    @JsonProperty("days")
    public Integer getDays() {
        return days;
    }

    @JsonProperty("days")
    public void setDays(Integer days) {
        this.days = days;
    }

    @JsonProperty("storage")
    public ItemsInfo getStorage() {
        return storage;
    }

    @JsonProperty("storage")
    public void setStorage(ItemsInfo storage) {
        this.storage = storage;
    }

    @JsonProperty("cannons")
    public Integer getCannons() {
        return cannons;
    }

    @JsonProperty("cannons")
    public void setCannons(Integer cannons) {
        this.cannons = cannons;
    }

    @JsonProperty("penalty")
    public Integer getPenalty() {
        return penalty;
    }

    @JsonProperty("penalty")
    public void setPenalty(Integer penalty) {
        this.penalty = penalty;
    }
}
