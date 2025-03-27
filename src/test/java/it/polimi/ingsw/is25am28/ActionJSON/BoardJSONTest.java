package it.polimi.ingsw.is25am28.ActionJSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class BoardJSONTest {
    Board board;

    /**
     * Method used to initialize:
     * 1. The board
     * 2. The players and add them to the board
     * */
    @BeforeEach
    void init() {
        this.board = new BoardLevel2();

        this.board.buildBoard();

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);

        // Add the players to the board and create the ships for each player
        // All the players have the same amount of Astronauts (that is 6) --> So they can play the card
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);
        }

        // To be sure we validate the players positions
        board.validatePlayersPosition();
    }

    @Test
    void test_board_json_serialization() throws Exception {
        BoardJSON boardJSON = BoardJSON.fromBoard(board);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(boardJSON);

        assertTrue(json.contains("\"size\":24"));
        assertTrue(json.contains("\"level\":2"));
        assertTrue(json.contains("\"eliminatedPlayersNickname\":[]"));
        assertTrue(json.contains("\"boardCells\":{\"0\":\"Player 4\",\"1\":\"Player 3\",\"2\":\"\",\"3\":\"Player 2\",\"4\":\"\",\"5\":\"\",\"6\":\"Player 1\",\"7\":\"\",\"8\":\"\",\"9\":\"\",\"10\":\"\",\"11\":\"\",\"12\":\"\",\"13\":\"\",\"14\":\"\",\"15\":\"\",\"16\":\"\",\"17\":\"\",\"18\":\"\",\"19\":\"\",\"20\":\"\",\"21\":\"\",\"22\":\"\",\"23\":\"\"}"));
        assertTrue(json.contains("playersNickname\":[\"Player 1\",\"Player 2\",\"Player 3\",\"Player 4\"]"));
    }

    @Test
    public void testFinalJsonDeserialization() throws Exception {
        String json = "{\"size\":24,\"level\":2,\"eliminatedPlayersNickname\":[],\"boardCells\":{\"0\":\"Player 4\",\"1\":\"Player 3\",\"2\":\"\",\"3\":\"Player 2\",\"4\":\"\",\"5\":\"\",\"6\":\"Player 1\",\"7\":\"\",\"8\":\"\",\"9\":\"\",\"10\":\"\",\"11\":\"\",\"12\":\"\",\"13\":\"\",\"14\":\"\",\"15\":\"\",\"16\":\"\",\"17\":\"\",\"18\":\"\",\"19\":\"\",\"20\":\"\",\"21\":\"\",\"22\":\"\",\"23\":\"\"},\"playersNickname\":[\"Player 1\",\"Player 2\",\"Player 3\",\"Player 4\"]}";

        // Create ObjectMapper and register modules if needed
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());

        // Deserialize JSON to BoardJSON object
        BoardJSON boardJSON = mapper.readValue(json, BoardJSON.class);

        // Assert board size and level
        assertEquals(24, boardJSON.getSize(), "Board size should be 24");
        assertEquals(2, boardJSON.getLevel(), "Board level should be 2");

        // Assert eliminatedPlayersNickname is empty
        assertTrue(boardJSON.getEliminatedPlayersNickname().isEmpty(), "Eliminated players list should be empty");

        // Assert playersNickname list
        assertEquals(Arrays.asList("Player 1", "Player 2", "Player 3", "Player 4"), boardJSON.getPlayersNickname(), "Players nickname list does not match");

        // Assert boardCells mapping
        Map<Integer, String> expectedCells = new HashMap<>();
        expectedCells.put(0, "Player 4");
        expectedCells.put(1, "Player 3");
        expectedCells.put(2, "");
        expectedCells.put(3, "Player 2");
        expectedCells.put(4, "");
        expectedCells.put(5, "");
        expectedCells.put(6, "Player 1");
        for (int i = 7; i < 24; i++) {
            expectedCells.put(i, "");
        }
        assertEquals(expectedCells, boardJSON.getBoardCells(), "Board cells mapping does not match expected values");
    }
}