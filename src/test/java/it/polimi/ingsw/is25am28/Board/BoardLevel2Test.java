package it.polimi.ingsw.is25am28.Board;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardLevel2Test {
    private Board board;

    // Initialize the players list. This represents the initial turn order
    @BeforeEach
    void init() {
        this.board = new BoardLevel2();

        this.board.buildBoard();

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);
    }

    @Test
    void test_board_creation_level_2() {

        // Check if the constructor actually set the correct size
        assertEquals(24, board.getSize());

        // Check if the initials cells are in the correct positions
        assertIterableEquals(List.of(6, 3, 1, 0), board.getInitialCells().stream().map(Cell::getIdx).toList());
    }

    // HP: The initial order of the player is the one in the init method

    /**
     * Check if the players are correctly set in the initials cells of the board and check if the cursor has been updated the current value
     * */
    @Test
    void test_board_add_players_to_board() {
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);
        }

        for (int i = 0; i < board.getPlayers().size(); i++) {
            assertEquals(board.getInitialCells().get(i), board.getPlayers().get(i).getCurrentCell());
            assertEquals(board.getInitialCells().get(i).getIdx(), board.getPlayers().get(i).getCursor());
        }
    }

    @Test
    void test_board_move_player_forwards() {
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);
        }

        board.movePlayerForward(board.getPlayers().getFirst(), 3);

        // Check if the player has been moved of 3 cells and if the cursor has been correctly updated
        assertEquals(board.getInitialCells().getFirst().getNextCell().getNextCell().getNextCell(), board.getPlayers().getFirst().getCurrentCell());
        assertEquals(board.getInitialCells().getFirst().getIdx() + 3, board.getPlayers().getFirst().getCursor());
    }

    @Test
    void test_board_move_player_backwards_with_player_to_be_skipped() {
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);
        }

        board.movePlayerBackwards(board.getPlayers().getFirst(), 3);

        // Check if the player has been moved of 4 cells and if the cursor has been correctly updated
        // 4 cells because the third cell is occupied by the second player
        assertEquals(board.getInitialCells().getFirst().getPrevCell().getPrevCell().getPrevCell().getPrevCell(), board.getPlayers().getFirst().getCurrentCell());

        assertEquals(board.getInitialCells().getFirst().getIdx() - 4, board.getPlayers().getFirst().getCursor());
    }

    @Test
    void test_board_move_player_with_doubled_player() {
        List<Player> tmpPlayers = new ArrayList<>(board.getPlayers());

        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);
        }

        board.movePlayerForward(board.getPlayers().getFirst(), 18);

        // Validate the current players positions
        board.validatePlayersPosition();

        // Check if the remaining players are in the correct order
        assertIterableEquals(
                List.of(tmpPlayers.get(0), tmpPlayers.get(1)),
                board.getPlayers(),
                "The remaining players are not in the correct order"
        );

        // Check if the players eliminated are the correct ones
        assertTrue(
                board.getEliminatedPlayers().containsAll(List.of(tmpPlayers.get(2), tmpPlayers.get(3))),
                "The eliminated players are not the expected ones"
        );

        board.getEliminatedPlayers().forEach(player ->
                assertTrue(player.isEliminated(), "The player eliminated flag has not been correctly updated")
        );
    }

    @Test
    void test_update_position_of_players_with_no_doubled_player() {
        List<Player> tmpPlayers = new ArrayList<>(board.getPlayers());

        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);
        }

        board.movePlayerForward(board.getPlayers().getLast(), 18);

        // Validate the current players positions
        board.validatePlayersPosition();

        // Check if the remaining players are in the correct order
        assertIterableEquals(
                List.of(tmpPlayers.get(3), tmpPlayers.get(0), tmpPlayers.get(1), tmpPlayers.get(2)),
                board.getPlayers(),
                "The remaining players are not in the correct order"
        );
    }
}