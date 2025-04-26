package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;

import java.util.List;

/**
 * This data container will contain:
 * 1. All the players information (with ship)
 * 2. The board
 * 3.
 * */

public final class ReconnectDTO extends StateDTO {
    private String targetNickname;
    private List<PlayerJSON> players;
    private BoardJSON board;
    private StateDTO currentState;

    public ReconnectDTO() {}

    public ReconnectDTO(
            @JsonProperty("targetNickname") String targetNickname,
            @JsonProperty("players") List<PlayerJSON> players,
            @JsonProperty("board") BoardJSON board,
            @JsonProperty("currentState") StateDTO currentState
    ) {
        this.targetNickname = targetNickname;
        this.players = players;
        this.board = board;
        this.currentState = currentState;
    }

    @JsonGetter("targetNickname")
    public String getTargetNickname() {
        return targetNickname;
    }

    @JsonSetter("targetNickname")
    public ReconnectDTO setTargetNickname(String targetNickname) {
        this.targetNickname = targetNickname;
        return this;
    }

    @JsonGetter("players")
    public List<PlayerJSON> getPlayers() {
        return players;
    }

    @JsonSetter("players")
    public void setPlayers(List<PlayerJSON> players) {
        this.players = players;
    }

    @JsonGetter("board")
    public BoardJSON getBoard() {
        return board;
    }

    @JsonSetter("board")
    public void setBoard(BoardJSON board) {
        this.board = board;
    }

    @JsonGetter("currentState")
    public StateDTO getCurrentState() {
        return currentState;
    }

    @JsonSetter("currentState")
    public void setCurrentState(StateDTO currentState) {
        this.currentState = currentState;
    }

    /**
     * Accept the visitor to visit the state
     * */
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
