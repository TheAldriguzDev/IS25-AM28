package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EndGameDTO extends StateJSON {
    private Map<String, Integer> playersCredits;
    private Map<String, Integer> playersPositionResult;

    public EndGameDTO() {}

    public EndGameDTO(
            @JsonProperty("playersCredits") Map<String, Integer> playersCredits,
            @JsonProperty("playersPositionResult") Map<String, Integer> playersPositionResult ) {
        this.playersCredits = playersCredits;
        this.playersPositionResult = playersPositionResult;
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
}
