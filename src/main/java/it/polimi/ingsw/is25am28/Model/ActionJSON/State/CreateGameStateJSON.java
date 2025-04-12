package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateGameStateJSON extends StateJSON {
    List<String> availableColors;
    List<String> usedNicknames;

    public CreateGameStateJSON() {}

    public CreateGameStateJSON(
            @JsonProperty("availableColors") List<String> availableColors,
            @JsonProperty("usedNicknames") List<String> usedNicknames) {
        this.availableColors = availableColors;
        this.usedNicknames = usedNicknames;
    }

    @JsonGetter("availableColors")
    public List<String> getAvailableColors() {
        return this.availableColors;
    }

    @JsonSetter("availableColors")
    public void setAvailableColors(List<String> availableColors) {
        this.availableColors = availableColors;
    }

    @JsonGetter("usedNicknames")
    public List<String> getUsedNicknames() {
        return this.usedNicknames;
    }

    @JsonSetter("usedNicknames")
    public void setUsedNicknames(List<String> usedNicknames) {
        this.usedNicknames = usedNicknames;
    }
}
