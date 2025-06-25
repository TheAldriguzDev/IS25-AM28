package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

/**
 * A DTO representing the state for creating a game.
 * This class is an extension of StateDTO and includes details specific to the creation of a game,
 * such as available colors for selection and already used nicknames.
 * <br>
 * This class is immutable and serializable.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateGameStateDTO extends StateDTO {
    List<String> availableColors;
    List<String> usedNicknames;

    @JsonCreator
    public CreateGameStateDTO() {}

    @JsonCreator
    public CreateGameStateDTO(
            @JsonProperty("availableColors") List<String> availableColors,
            @JsonProperty("usedNicknames") List<String> usedNicknames
    ) {
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
