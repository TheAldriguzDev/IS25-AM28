package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientOpenSpace;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.OpenSpaceJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Model.Connector.*;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceTest {
    Board board;

    /**
     * Method used to initialize:
     * 1. The board
     * 2. The players and add them to the board
     * 3. The players ship
     * */
    @BeforeEach
    void init() {
        this.board = new BoardLevel2();

        this.board.buildBoard();

        List<Player> players = new ArrayList<Player>();

        players.add(new Player("Player 1", PlayerColor.RED, 2));
        players.add(new Player("Player 2", PlayerColor.BLUE, 2));
        players.add(new Player("Player 3", PlayerColor.GREEN, 2));
        players.add(new Player("Player 4", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            this.board.newPlayer(player);
        }

        // Add the players to the board and create the ships for each player
        // Player1 - Player3 - Player4 have the same ship
        // Player2's ship has no engine power — it has a brown alien that gives +2 power but since there is no engine power, the boost cannot be applied
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);

            List<Integer> connectors = new ArrayList<Integer>();

            // Default connector is THREE_PIPES
            for (int i = 0; i < 4; i++) {
                connectors.add(THREE_PIPES.ordinal());
            }

            if (player.getNickname().equals("Player 1") || player.getNickname().equals("Player 3") || player.getNickname().equals("Player 4")) {
                Cannon singleCannon1 = new Cannon(connectors, 1);
                Cannon singleCannon2 = new Cannon(connectors, 1);

                Shield shield1 = new Shield(connectors);

                Storage specialStorage1 = new Storage(connectors, 1, true);
                Storage specialStorage2 = new Storage(connectors, 2, true);

                Storage storage1 = new Storage(connectors, 3, false);
                Storage storage2 = new Storage(connectors, 2, false);

                Battery battery1 = new Battery(connectors, 2);
                Battery battery2 = new Battery(connectors, 2);
                Battery battery3 = new Battery(connectors, 2);

                Structural structural1 = new Structural(connectors);
                Structural structural2 = new Structural(connectors);
                Structural structural3 = new Structural(connectors);

                Cabin cabin1 = new Cabin(connectors, false);
                cabin1.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));
                Cabin cabin2 = new Cabin(connectors, false);
                cabin2.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

                Vital vital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
                Vital vital2 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

                Engine normalEngine1 = new Engine(connectors, 1);
                Engine normalEngine2 = new Engine(connectors, 1);
                Engine normalEngine3 = new Engine(connectors, 1);

                Engine doubleEngine = new Engine(connectors, 2);

                player.getShip().addComponent(singleCannon1, 5, 6);
                player.getShip().addComponent(singleCannon2, 5, 4);
                player.getShip().addComponent(shield1, 5, 7);

                player.getShip().addComponent(specialStorage1, 6, 3);
                player.getShip().addComponent(specialStorage2, 6, 9);

                player.getShip().addComponent(battery1, 6, 4);
                player.getShip().addComponent(battery2, 6, 8);

                player.getShip().addComponent(structural1, 6, 5);
                player.getShip().addComponent(structural2, 6, 7);

                player.getShip().addComponent(structural3, 7, 3);
                player.getShip().addComponent(cabin1, 7, 4);
                player.getShip().addComponent(vital1, 7, 5);
                player.getShip().addComponent(cabin2, 7, 6);
                player.getShip().addComponent(vital2, 7, 7);
                player.getShip().addComponent(battery3, 7, 8);

                player.getShip().addComponent(normalEngine1, 8, 3);
                player.getShip().addComponent(normalEngine2, 8, 4);
                player.getShip().addComponent(normalEngine3, 8, 5);

                player.getShip().addComponent(storage1, 8, 7);
                player.getShip().addComponent(storage2, 8, 8);
                player.getShip().addComponent(doubleEngine, 8, 9);
            } else {
                Cannon singleCannon1 = new Cannon(connectors, 1);
                Cannon singleCannon2 = new Cannon(connectors, 1);

                Shield shield1 = new Shield(connectors);

                Storage specialStorage1 = new Storage(connectors, 1, true);
                Storage specialStorage2 = new Storage(connectors, 2, true);

                Storage storage1 = new Storage(connectors, 3, false);
                Storage storage2 = new Storage(connectors, 2, false);

                Battery battery1 = new Battery(connectors, 2);
                Battery battery2 = new Battery(connectors, 2);
                Battery battery3 = new Battery(connectors, 2);

                Structural structural1 = new Structural(connectors);
                Structural structural2 = new Structural(connectors);
                Structural structural3 = new Structural(connectors);

                Cabin cabin1 = new Cabin(connectors, false);
                cabin1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                cabin1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                Cabin cabin2 = new Cabin(connectors, false);
                cabin2.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

                Vital vital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
                Vital vital2 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

                // Engine normalEngine1 = new Engine(connectors, 1);
                // Engine normalEngine2 = new Engine(connectors, 1);
                // Engine normalEngine3 = new Engine(connectors, 1);

                // Engine doubleEngine = new Engine(connectors, 2);

                player.getShip().addComponent(singleCannon1, 5, 6);
                player.getShip().addComponent(singleCannon2, 5, 4);
                player.getShip().addComponent(shield1, 5, 7);

                player.getShip().addComponent(specialStorage1, 6, 3);
                player.getShip().addComponent(specialStorage2, 6, 9);

                player.getShip().addComponent(battery1, 6, 4);
                player.getShip().addComponent(battery2, 6, 8);

                player.getShip().addComponent(structural1, 6, 5);
                player.getShip().addComponent(structural2, 6, 7);

                player.getShip().addComponent(structural3, 7, 3);
                player.getShip().addComponent(cabin1, 7, 4);
                player.getShip().addComponent(vital1, 7, 5);
                player.getShip().addComponent(cabin2, 7, 6);
                player.getShip().addComponent(vital2, 7, 7);
                player.getShip().addComponent(battery3, 7, 8);

                // player.getShip().addComponent(normalEngine1, 8, 3);
                // player.getShip().addComponent(normalEngine2, 8, 4);
                // player.getShip().addComponent(normalEngine3, 8, 5);

                player.getShip().addComponent(storage1, 8, 7);
                player.getShip().addComponent(storage2, 8, 8);
                // player.getShip().addComponent(doubleEngine, 8, 9);
            }

            player.getShip().generateComponentSubLists();
        }

        // To be sure we validate the players positions
        board.validatePlayersPosition();
    }

    @Test
    void test_open_space_with_1_players_eliminated_and_3_players_with_all_the_engines_supply() {
        // Create the card that will be used in the simulation
        OpenSpace openSpaceCard = new OpenSpace("openSpace", 2, board);
        CardStateJSON cardState;
        ClientOpenSpace clientOpenSpace;

        // ======== STATE TESTING ======== //
        cardState = openSpaceCard.generateState();
        assertEquals("openSpace", cardState.getCardName());
        assertEquals(2, cardState.getCardLevel());
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertNull(cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientOpenSpace = new ClientOpenSpace(cardState);
        clientOpenSpace.generateWidget().printWidget();
        // ================================ //


        // Init the players that will use the card
        openSpaceCard.initCardPlayers();

        List<Integer> tmpCursors = new ArrayList<>();
        Player tmpPlayer = this.board.getPlayers().get(1);

        // Use the card for each player.
        // We expect that p1 - p3 - p4 will be able to use the card, instead the p2 will be eliminated since he doesn't have any power (the alien boost can't be used)
        int count = 0;
        ArrayList<Player> players = new ArrayList<>(board.getPlayers());
        for (Player player : players) {
            // Add the initial cursor of each player
            tmpCursors.add(player.getCursor());

            // Set the JSON data to use the card
            OpenSpaceJSON openSpaceJSON = new OpenSpaceJSON();
            openSpaceJSON.setPlayerNickname(player.getNickname());

            openSpaceJSON.setUsedEnergy(1);

            // ======== STATE TESTING ======== //
            cardState = openSpaceCard.generateState();
            assertFalse(cardState.getNeedsPlayerUpdate());
            switch (count) {
                case 0 -> { // State after initCard
                    assertFalse(cardState.getNeedsShipUpdate());
                    assertFalse(cardState.getNeedsBoardUpdate());

                    assertFalse(cardState.getNeedsUpdatedEliminatedPlayers());
                }
                case 1 -> { // State relative to player 1
                    assertTrue(cardState.getNeedsShipUpdate());
                    assertTrue(cardState.getNeedsBoardUpdate());
                    assertTrue(cardState.getNeedsUpdatedPositions());
                    assertEquals(1, cardState.getUpdatedPositions().size());
                    assertEquals(13, cardState.getUpdatedPositions().get("Player 1"));
                    assertTrue(cardState.getNeedsUpdatedBatteries());
                    assertEquals(1, cardState.getRemovedBatteries().size());
                    assertEquals(1, cardState.getRemovedBatteries().get("Player 1"));
                    assertFalse(cardState.getNeedsUpdatedEliminatedPlayers());
                }
                case 2 -> { // State relative to player 2
                    assertTrue(cardState.getNeedsShipUpdate());
                    assertTrue(cardState.getNeedsBoardUpdate());
                    assertFalse(cardState.getNeedsUpdatedPositions());
                    assertTrue(cardState.getNeedsUpdatedEliminatedPlayers());
                    assertEquals(1, cardState.getEliminatedPlayers().size());
                    assertEquals("Player 2", cardState.getEliminatedPlayers().getFirst());
                }
                case 3 -> { // State relative to player 3
                    assertTrue(cardState.getNeedsShipUpdate());
                    assertTrue(cardState.getNeedsBoardUpdate());
                    assertTrue(cardState.getNeedsUpdatedPositions());
                    assertEquals(1, cardState.getUpdatedPositions().size());
                    assertEquals(8, cardState.getUpdatedPositions().get("Player 3"));
                    assertTrue(cardState.getNeedsUpdatedBatteries());
                    assertEquals(1, cardState.getRemovedBatteries().size());
                    assertEquals(1, cardState.getRemovedBatteries().get("Player 3"));
                    assertFalse(cardState.getNeedsUpdatedEliminatedPlayers());
                }
            }
            assertEquals(player.getNickname(), cardState.getPlayerNickname());
            // =============================== //
            count++;
            // ======== WIDGET TESTING ======== //
            clientOpenSpace.updateCard(cardState);
            clientOpenSpace.generateWidget().printWidget();
            // ================================ //

            // Use the card
            openSpaceCard = (OpenSpace) openSpaceCard.useCard(openSpaceJSON);
            if (openSpaceCard.hasFinished()) break;
        }

        // ======== STATE TESTING ======== //
        cardState = openSpaceCard.generateState();
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertTrue(cardState.getNeedsShipUpdate());
        assertTrue(cardState.getNeedsBoardUpdate());
        assertTrue(cardState.getNeedsUpdatedPositions());
        assertEquals(1, cardState.getUpdatedPositions().size());
        assertEquals(7, cardState.getUpdatedPositions().get("Player 4"));
        assertTrue(cardState.getNeedsUpdatedBatteries());
        assertEquals(1, cardState.getRemovedBatteries().size());
        assertEquals(1, cardState.getRemovedBatteries().get("Player 4"));
        assertFalse(cardState.getNeedsUpdatedEliminatedPlayers());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientOpenSpace.updateCard(cardState);
        clientOpenSpace.generateWidget().printWidget();
        // ================================ //

        // All the types of power have been tested
        assertEquals(tmpCursors.get(0) + 7, players.get(0).getCursor());
        assertEquals(tmpCursors.get(2) + 7, players.get(2).getCursor());
        assertEquals(tmpCursors.get(3) + 7, players.get(3).getCursor());

        assertEquals(board.getEliminatedPlayers().size(), 1);
        assertEquals(tmpPlayer, this.board.getEliminatedPlayers().getFirst());

        // The power that initially was 6, now has been decreased to 5
        assertEquals(5, this.board.getPlayers().getFirst().getShip().getAvailableEnergy());
    }

    // Test case con potenza dichiarata a zero --> Eliminare il player
}