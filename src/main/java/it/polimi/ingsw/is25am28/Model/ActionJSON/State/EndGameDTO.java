package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;

/**
 * Represents a data transfer data object that holds information about the leaderboard at the end of the game
 * <br>
 * Annotations from the Jackson library are used for JSON serialization and deserialization,
 * ensuring that only non-null values are included in the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EndGameDTO extends StateDTO {
    private String winner;
    private Map<String, Integer> playersCredits;
    private Map<String, Integer> playersPositionResult;

    @JsonCreator
    public EndGameDTO() {}

    @JsonCreator
    public EndGameDTO(
            @JsonProperty("winner") String winner,
            @JsonProperty("playersCredits") Map<String, Integer> playersCredits,
            @JsonProperty("playersPositionResult") Map<String, Integer> playersPositionResult
    ) {
        this.playersCredits = playersCredits;
        this.playersPositionResult = playersPositionResult;
        this.winner = winner;
    }

    @JsonGetter("winner")
    public String getWinner() {
        return winner;
    }

    @JsonSetter("winner")
    public EndGameDTO setWinner(String winner) {
        this.winner = winner;
        return this;
    }

    @JsonGetter("playersCredits")
    public Map<String, Integer> getPlayersCredits() {
        return playersCredits;
    }

    @JsonSetter("playersCredits")
    public EndGameDTO setPlayersCredits(Map<String, Integer> playersCredits) {
        this.playersCredits = playersCredits;
        return this;
    }

    @JsonGetter("playersPositionResult")
    public Map<String, Integer> getPlayersPositionResult() {
        return playersPositionResult;
    }

    @JsonSetter("playersPositionResult")
    public EndGameDTO setPlayersPositionResult(Map<String, Integer> playersPositionResult) {
        this.playersPositionResult = playersPositionResult;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
