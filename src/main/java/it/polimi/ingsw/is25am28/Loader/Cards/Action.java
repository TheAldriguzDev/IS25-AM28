package it.polimi.ingsw.is25am28.Loader.Cards;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "action",
        "consequence"
})
public final class Action {
    @JsonProperty("action")         private Integer action;
    @JsonProperty("consequence")    private Integer consequence;

    @JsonGetter("action")
    public Integer getAction() {
        return action;
    }

    @JsonSetter("action")
    public void setAction(Integer action) {
        this.action = action;
    }

    @JsonGetter("consequence")
    public Integer getConsequence() {
        return consequence;
    }

    @JsonSetter("consequence")
    public void setConsequence(Integer consequence) {
        this.consequence = consequence;
    }
}
