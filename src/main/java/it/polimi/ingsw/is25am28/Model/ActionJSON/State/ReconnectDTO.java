package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

import java.util.List;
import java.util.Map;

/**
 * This class contains all the information necessary to reconnect a disconnected player to the game
 */
public final class ReconnectDTO extends StateDTO {
    private String targetNickname;
    private List<PlayerJSON> players;
    private BoardJSON board;
    private StateDTO currentState;
    private Map<ItemColor, Integer> resourceBank;
    private int gameLevel;

    private boolean wasInsufficientState;

    // Card list that contains the information about the deck in the game
    private List<CardStateJSON> cards;

    @JsonCreator
    public ReconnectDTO() {}

    @JsonCreator
    public ReconnectDTO(
            @JsonProperty("targetNickname") String targetNickname,
            @JsonProperty("players") List<PlayerJSON> players,
            @JsonProperty("board") BoardJSON board,
            @JsonProperty("currentState") StateDTO currentState,
            @JsonProperty("wasInsufficientState") boolean wasInsufficientState
    ) {
        this.targetNickname = targetNickname;
        this.players = players;
        this.board = board;
        this.currentState = currentState;
        this.wasInsufficientState = wasInsufficientState;
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
    public ReconnectDTO setPlayers(List<PlayerJSON> players) {
        this.players = players;
        return this;
    }

    @JsonGetter("board")
    public BoardJSON getBoard() {
        return board;
    }

    @JsonSetter("board")
    public ReconnectDTO setBoard(BoardJSON board) {
        this.board = board;
        return this;
    }

    @JsonGetter("currentState")
    public StateDTO getCurrentState() {
        return currentState;
    }

    @JsonSetter("currentState")
    public ReconnectDTO setCurrentState(StateDTO currentState) {
        this.currentState = currentState;
        return this;
    }

    @JsonSetter("resourceBank")
    public Map<ItemColor, Integer> getResourceBank() {
        return resourceBank;
    }

    @JsonGetter("resourceBank")
    public ReconnectDTO setResourceBank(Map<ItemColor, Integer> resourceBank) {
        this.resourceBank = resourceBank;
        return this;
    }

    @JsonGetter("gameLevel")
    public ReconnectDTO setGameLevel(int gameLevel) {
        this.gameLevel = gameLevel;
        return this;
    }

    @JsonGetter("gameLevel")
    public int getGameLevel() {
        return gameLevel;
    }

    @JsonSetter("cards")
    public ReconnectDTO setCards(List<CardStateJSON> cards) {
        this.cards = cards;
        return this;
    }

    @JsonGetter("cards")
    public List<CardStateJSON> getCards() {
        return cards;
    }

    @JsonGetter("wasInsufficientState")
    public boolean getWasInsufficientState() {
        return wasInsufficientState;
    }

    @JsonSetter("wasInsufficientState")
    public ReconnectDTO setWasInsufficientState(boolean wasInsufficientState) {
        this.wasInsufficientState = wasInsufficientState;
        return this;
    }

    /**
     * Accept the visitor to visit the state
     * */
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
