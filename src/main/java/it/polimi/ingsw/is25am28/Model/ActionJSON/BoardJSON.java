package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.Cell;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardJSON implements Serializable {

    @JsonProperty("size")
    private int size;
    @JsonProperty("level")
    private int level;

    private List<String> playersNickname;
    private List<String> eliminatedPlayersNickname;
    @JsonProperty("startingPlayersPositions")
    private Map<String, Integer> startingPlayersPositions = new HashMap<>();

    /**
     * Default constructor used client side
     */
    public BoardJSON() {}

    /**
     * Constructor used for deserialization
     */
    @JsonCreator
    public BoardJSON(
            @JsonProperty("size") int size,
            @JsonProperty("level") int level,
            @JsonProperty("startingPlayersPositions") Map<String, Integer> startingPlayersPositions,
            @JsonProperty("playersNickname") List<String> playersNickname,
            @JsonProperty("eliminatedPlayersNickname") List<String> eliminatedPlayersNickname
    ) {
        this.size = size;
        this.level = level;
        this.startingPlayersPositions = startingPlayersPositions != null ? startingPlayersPositions : new HashMap<>();
        this.eliminatedPlayersNickname = eliminatedPlayersNickname;
    }

    public static BoardJSON fromBoard(Board board) {
        Map<String, Integer> currPlayerPositions = new HashMap<>();
        List<String> players = board.getPlayers().stream()
                .map(Player::getNickname)
                .toList();

        List<String> eliminatedPlayersNickname = board.getEliminatedPlayers().stream()
                .map(Player::getNickname)
                .toList();

        Cell head = board.getHead();
        Cell curr = board.getHead();
        do {
            if (curr.getPlayer().isPresent()) {
                currPlayerPositions.put(curr.getPlayer().get().getNickname(), curr.getIdx());
            }
            curr = curr.getNextCell();
        } while (curr != head);

        return new BoardJSON(
                board.getSize(),
                board.getLevel(),
                currPlayerPositions,
                players,
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

    @JsonGetter("startingPlayersPositions")
    public Map<String, Integer> getStartingPlayersPositions() {
        return startingPlayersPositions;
    }
}