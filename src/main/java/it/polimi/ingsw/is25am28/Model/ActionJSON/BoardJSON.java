package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.Cell;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardJSON implements Serializable {
    private int size;
    private int level;
    private Cell boardHead;
    private List<String> playersNickname;
    private List<String> eliminatedPlayersNickname;
    private Map<Integer, String> boardCells;
    private Map<String, Integer> startingPlayersPositions = new HashMap<>();

    /**
     * Default constructor used client side
     */
    public BoardJSON() {}

    /**
     * Constructor that initializes the JSON with a Map of cells.
     * This is used mainly server-side to prepare the data for serialization.
     */
    @JsonCreator
    public BoardJSON(
            @JsonProperty("size") int size,
            @JsonProperty("level") int level,
            @JsonProperty("startingPlayersPositions") Map<String, Integer> startingPlayersPositions,
            @JsonProperty("EliminatedPlayers") List<String> eliminatedPlayersNickname
    ) {
        this.size = size;
        this.level = level;
        this.startingPlayersPositions = startingPlayersPositions;
        this.eliminatedPlayersNickname = eliminatedPlayersNickname;
    }

    public static BoardJSON fromBoard(Board board) {
        Map<String, Integer> currPlayerPositions = new HashMap<>();
        List<String> eliminatedPlayersNickname = board.getEliminatedPlayers().stream().map(Player::getNickname).toList();

        Cell head = board.getHead();
        Cell curr = board.getHead();
        do {
            if(curr.getPlayer().isPresent()) {
                currPlayerPositions.put(curr.getPlayer().get().getNickname(), curr.getIdx());
            }
            curr = curr.getNextCell();
        } while (curr != head);

        return new BoardJSON(
                board.getSize(),
                board.getLevel(),
                currPlayerPositions,
                eliminatedPlayersNickname
        );
    }

    @JsonGetter("size")
    public int getSize() {
        return size;
    }

    @JsonGetter("level")
    public int getLevel() {
        return level;
    }

    @JsonGetter("playersNickname")
    public List<String> getPlayersNickname() {
        return playersNickname;
    }

    @JsonGetter("eliminatedPlayersNickname")
    public List<String> getEliminatedPlayersNickname() {
        return eliminatedPlayersNickname;
    }

    @JsonGetter("boardCells")
    public Map<Integer, String> getBoardCells() {
        return boardCells;
    }

    @JsonGetter("head")
    public Cell getHead() {
        return boardHead;
    }

    @JsonGetter("startingPlayersPositions")
    public Map<String, Integer> getStartingPlayersPositions() {
        return startingPlayersPositions;
    }
}
