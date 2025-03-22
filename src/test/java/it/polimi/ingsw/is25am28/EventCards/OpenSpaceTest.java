package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.OpenSpaceJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Connector.*;

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

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);

        // Add the players to the board and create the ships for each player
        // Player1 - Player3 - Player4 have the same ship
        // Player2 has a different ship without any form of engine power
        for (Player player : board.getPlayers()) {
            board.addPlayerToBoard(player);

            int[] connectors = new int[4];

            for (int i = 0; i < 4; i++) {
                connectors[i] = THREE_PIPES.ordinal();
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
                cabin2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
                cabin2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

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

    /**
     * Outputs the given ship to terminal
     * @param ship The ship to print to terminal
     */
    void printShipGrid(Ship ship) {
        if (ship != null) {
            List<Component> components = new ArrayList<>();
            Component c;
            int rows = ship.getGridDimensions().getKey();
            int cols = ship.getGridDimensions().getValue();
            int i, j, k;

            System.out.print("\n\\");
            for (i = 0; i < cols; i++) {
                System.out.print("\t" + i);
            }
            System.out.print("\n");

            k = 0;

            for (i = 0; i < rows; i++) {
                System.out.print(i + "\t");
                for (j = 0; j < cols; j++) {
                    c = ship.getComponent(i, j);
                    if (c == null) {
                        System.out.print("." + "\t");
                    }
                    else {
                        System.out.print(k++ + "\t");
                        components.add(c);
                    }
                }
                System.out.print("\n");
            }

            int size = components.size();

            System.out.println("\nFound these components:");

            for (i = 0; i < size; i++) {
                System.out.println(i + " - " + components.get(i));
            }
        }
        else {
            System.out.println("ERROR: Given ship is null");
        }
    }

    @Test
    void test_open_space_with_1_players_eliminated_and_3_players_with_all_the_engines_supply() {
        // Create the card that will be used in the simulation
        OpenSpace openSpaceCard = new OpenSpace("openSpace", 2, board);

        // Init the players that will use the card
        openSpaceCard.initCardPlayers();

        List<Integer> tmpCursors = new ArrayList<>();
        Player tmpPlayer = this.board.getPlayers().get(1);

        // Use the card for each player.
        // We expect that p1 - p3 - p4 will be able to use the card, instead the p2 will be eliminated since he doesn't have any power
        ArrayList<Player> players = new ArrayList<>(board.getPlayers());
        for (Player player : players) {
            // Add the initial cursor of each player
            tmpCursors.add(player.getCursor());

            // Set the JSON data to use the card
            OpenSpaceJSON openSpaceJSON = new OpenSpaceJSON();
            openSpaceJSON.setPlayerNickname(player.getNickname());

            openSpaceJSON.setUsedEnergy(1);

            // Use the card
            openSpaceCard = (OpenSpace) openSpaceCard.useCard(openSpaceJSON);
            if (openSpaceCard.hasFinished()) break;
        }

        // All the types of power has been tested
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