package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a data transfer data object that holds information about the available games
 * <br>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
public final class AvailableGamesDTO extends StateDTO implements Serializable {
    private List<String> usedNicknames;
    private List<GameInfoDTO> availableGames;

    @JsonCreator
    public AvailableGamesDTO() {}

    @JsonCreator
    public AvailableGamesDTO(
            @JsonProperty("usedNicknames") List<String> usedNicknames,
            @JsonProperty("availableGames") List<GameInfoDTO> availableGames
    ) {
        this.usedNicknames = usedNicknames;
        this.availableGames = availableGames;
    }

    @JsonGetter("usedNicknames")
    public List<String> getUsedNicknames() {
        return usedNicknames;
    }

    @JsonSetter("usedNicknames")
    public AvailableGamesDTO setUsedNicknames(List<String> usedNicknames) {
        this.usedNicknames = usedNicknames;
        return this;
    }

    @JsonGetter("availableGames")
    public List<GameInfoDTO> getAvailableGames() {
        return availableGames;
    }

    @JsonSetter("availableGames")
    public AvailableGamesDTO setAvailableGames(List<GameInfoDTO> availableGames) {
        this.availableGames = availableGames;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
