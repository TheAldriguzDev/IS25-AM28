package it.polimi.ingsw.is25am28.Model.ActionJSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.Cell;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardJSON {
    private int size;
    private int level;
    private List<String> playersNickname;
    private List<String> eliminatedPlayersNickname;
    private Map<Integer, String> boardCells;

    /**
     * Default constructor, used by Jackson
     * */
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
            @JsonProperty("playerNickname") List<String> playerNickname,
            @JsonProperty("eliminatedPlayersNickname") List<String> eliminatedPlayersNickname,
            @JsonProperty("boardCells") Map<Integer, String> boardCells
    ) {
        this.size = size;
        this.level = level;
        this.playersNickname = playerNickname;
        this.eliminatedPlayersNickname = eliminatedPlayersNickname;
        this.boardCells = boardCells;
    }

    public static BoardJSON fromBoard(Board board) {
        List<String> playerNickname = board.getPlayers().stream().map(Player::getNickname).toList();
        List<String> eliminatedPlayersNickname = board.getEliminatedPlayers().stream().map(Player::getNickname).toList();

        Map<Integer, String> boardCells = new HashMap<Integer, String>();
        Cell head = board.getHead();
        Cell curr = board.getHead();
        do {
            // If the player is present set the nickname to the player in the board otherwise sets ""
            boardCells.put( curr.getIdx(), curr.getPlayer().map(Player::getNickname).orElse("") );
            curr = curr.getNextCell();
        } while (curr != head);

        return new BoardJSON(
                board.getSize(),
                board.getLevel(),
                playerNickname,
                eliminatedPlayersNickname,
                boardCells
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
}
