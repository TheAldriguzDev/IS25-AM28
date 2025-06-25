package it.polimi.ingsw.is25am28.Loader.Cards;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "items",
        "peoples",
        "days",
        "shoots",
        "actions",
})
public final class Warzone extends Card {
    @JsonProperty("items")      private Integer items;
    @JsonProperty("peoples")    private Integer peoples;
    @JsonProperty("days")       private Integer days;
    @JsonProperty("shoots")     private List<List<Integer>> shoots;
    @JsonProperty("actions")    private List<Action> actions;

    @JsonGetter("items")
    public Integer getItems() {
        return items;
    }

    @JsonSetter("items")
    public void setItems(Integer items) {
        this.items = items;
    }

    @JsonGetter("peoples")
    public Integer getPeoples() {
        return peoples;
    }

    @JsonSetter("peoples")
    public void setPeoples(Integer peoples) {
        this.peoples = peoples;
    }

    @JsonGetter("days")
    public Integer getDays() {
        return days;
    }

    @JsonSetter("days")
    public void setDays(Integer days) {
        this.days = days;
    }

    @JsonGetter("shoots")
    public List<List<Integer>> getShoots() {
        return shoots;
    }

    @JsonSetter("shoots")
    public void setShoots(List<List<Integer>> shoots) {
        this.shoots = shoots;
    }

    @JsonGetter("actions")
    public List<Action> getActions() {
        return actions;
    }

    @JsonSetter("actions")
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
