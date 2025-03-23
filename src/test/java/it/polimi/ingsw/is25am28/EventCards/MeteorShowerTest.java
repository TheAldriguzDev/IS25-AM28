package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.Meteor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.*;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class MeteorShowerTest {

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

    Ship initCustomShip() {
        Ship ship = new Ship(1);

        int[] connectors = new int[4];

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        /*
               ==== Ship Configuration (LEVEL 1) ====
            \       4       5       6       7       8
            4                       a
            5               b       c       d
            6       e       f       g       h       i
            7       j       k       l       m       n
            8       o       p               q       r

            Total components = 18 (17 + 1 core)

            a = doubleCannon1 at (4, 6)
            b = singleCannon1 at (5, 5)
            c = specialDoubleStorage1 at (5, 6)
            d = singleCannon2 at (5, 7)
            e = shield1 at (6, 4)
            f = normalTripleStorage1 at (6, 5)
            g = CORE (6, 6)
            h = specialSingleStorage1 at (6, 7)
            i = doubleCannon2 at (6, 8)
            j = tripleBattery1 at (7, 4)
            k = normalDoubleStorage1 at (7, 5)
            l = purpleVital1 at (7, 6)
            m = cabin1 at (7, 7)
            n = brownVital1 at (7, 8)
            o = singleEngine1 at (9, 4)
            p = singleEngine2 at (9, 5)
            q = doubleEngine1 at (9, 7)
            r = singleEngine3 at (9, 8)
        */

        Battery tripleBattery1 = new Battery(connectors, 3);

        Cannon singleCannon1 = new Cannon(connectors, 1);
        Cannon singleCannon2 = new Cannon(connectors, 1);
        Cannon doubleCannon1 = new Cannon(connectors, 2);
        Cannon doubleCannon2 = new Cannon(connectors, 2);

        Engine singleEngine1 = new Engine(connectors, 1);
        Engine singleEngine2 = new Engine(connectors, 1);
        Engine singleEngine3 = new Engine(connectors, 1);
        Engine doubleEngine1 = new Engine(connectors, 2);

        Cabin cabin1 = new Cabin(connectors, false);

        Shield shield1 = new Shield(connectors);

        Storage normalDoubleStorage1 = new Storage(connectors, 2, false);
        Storage normalTripleStorage1 = new Storage(connectors, 3, false);
        Storage specialSingleStorage1 = new Storage(connectors, 1, true);
        Storage specialDoubleStorage1 = new Storage(connectors, 2, true);

        Structural structural1 = new Structural(connectors);

        Vital purpleVital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
        Vital brownVital1 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

        // Adding the components created above
        ship.addComponent(doubleCannon1, 4, 6);
        ship.addComponent(singleCannon1, 5, 5);
        ship.addComponent(specialDoubleStorage1, 5, 6);
        ship.addComponent(singleCannon2, 5, 7);
        ship.addComponent(shield1, 6, 4);
        ship.addComponent(normalTripleStorage1, 6, 5);
        ship.addComponent(specialSingleStorage1, 6, 7);
        ship.addComponent(doubleCannon2, 6, 8);
        ship.addComponent(tripleBattery1, 7, 4);
        ship.addComponent(normalDoubleStorage1, 7, 5);
        ship.addComponent(purpleVital1, 7, 6);
        ship.addComponent(cabin1, 7, 7);
        ship.addComponent(brownVital1, 7, 8);
        ship.addComponent(singleEngine1, 8, 4);
        ship.addComponent(singleEngine2, 8, 5);
        ship.addComponent(doubleEngine1, 8, 7);
        ship.addComponent(singleEngine3, 8, 8);

        System.out.println("==== SHIP CONFIGURATION ====");
        printShipGrid(ship);

        // Generating the component sub-lists right after the ship is created
        ship.generateComponentSubLists();

        return ship;
    }

    /**
     * NOTE: This test works only if in MeteorShower the random
     *       generator is instantiated with seed=0
     *       (because otherwise I can't know where the meteors will come from)
     */
    @Test
    void useCard_goingThroughAnEntireMeteorSequence() {
        Board board = new BoardLevel2();
        List<Player> playerList;

        assertFalse(true, "SET THE RANDOM GENERATOR SEED TO 0 BEFORE TESTING");

        board.newPlayer("p1", PlayerColor.RED);
        board.newPlayer("p2", PlayerColor.GREEN);

        Ship shipP1 = initCustomShip();
        Ship shipP2 = initCustomShip();

        playerList = board.getPlayers();

        playerList.get(0).setShip(shipP1);
        playerList.get(1).setShip(shipP2);

        List<List<Integer>> meteorSequence = new ArrayList<>();
        List<Integer> meteorDescriptor;

        // [1, 0] - Small Meteor, pointing up (comes from the bottom in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(0);
        meteorSequence.add(meteorDescriptor);

        // [1, 1] - Small Meteor, pointing right (comes from the left in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(1);
        meteorSequence.add(meteorDescriptor);

        // [1, 2] - Small Meteor, pointing down (comes from the top in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(2);
        meteorSequence.add(meteorDescriptor);

        // [1, 3] - Small Meteor, pointing left (comes from the right in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(3);
        meteorSequence.add(meteorDescriptor);

        // [2, 0] - Big Meteor, pointing up (comes from the bottom in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(0);
        meteorSequence.add(meteorDescriptor);

        // [2, 1] - Big Meteor, pointing right (comes from the left in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(1);
        meteorSequence.add(meteorDescriptor);

        // [2, 2] - Big Meteor, pointing down (comes from the top in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(2);
        meteorSequence.add(meteorDescriptor);

        // [2, 3] - Big Meteor, pointing left (comes from the right in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(3);
        meteorSequence.add(meteorDescriptor);

        // MeteorShower card with all 8 possible meteor configurations
        MeteorShower meteorShower = new MeteorShower(
                "Meteor Shower",
                2,
                meteorSequence,
                board
        );

        // Initializing the internal player list
        meteorShower.initCardPlayers();

        MeteorShowerJSON meteorShowerJSON;
        Map<Player, Pair<Integer, Integer>> shieldPerPlayer;
        Map<Player, Pair<Integer, Integer>> cannonPerPlayer;
        int energyP1 = playerList.get(0).getShip().getAvailableEnergy();
        int energyP2 = playerList.get(1).getShip().getAvailableEnergy();

        // ======== Meteor 1 of 8 (Small, Bottom) ========
        // Player 1 --> Turns on the shield, but it's not oriented correctly, therefore still loses 1 energy
        // Player 2 --> Doesn't turn on the shield, doesn't lose energy
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), new Pair<>(6, 4));
        shieldPerPlayer.put(playerList.get(1), null);
        cannonPerPlayer.put(playerList.get(0), null);
        cannonPerPlayer.put(playerList.get(1), null);

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());

        energyP1--; // P1 activated his only shield
        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 1 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 1 ====");
        printShipGrid(shipP2);

        // ======== Meteor 2 of 8 (Small, Left) ========
        // Player 1 --> Does nothing
        // Player 2 --> Does nothing
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), null);
        shieldPerPlayer.put(playerList.get(1), null);
        cannonPerPlayer.put(playerList.get(0), null);
        cannonPerPlayer.put(playerList.get(1), null);

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());

        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 2 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 2 ====");
        printShipGrid(shipP2);

        // ======== Meteor 3 of 8 (Small, Top) ========
        // Player 1 --> Activates shield, which should protect him from damage since it's oriented correctly
        // Player 2 --> Does nothing
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), new Pair<>(6, 4));
        shieldPerPlayer.put(playerList.get(1), null);
        cannonPerPlayer.put(playerList.get(0), null);
        cannonPerPlayer.put(playerList.get(1), null);

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());

        energyP1--; // P1 activated his shield
        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 3 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 3 ====");
        printShipGrid(shipP2);

        // ======== Meteor 4 of 8 (Small, Right) ========
        // Player 1 --> Use shield again
        // Player 2 --> Use shield
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), new Pair<>(6, 4));
        shieldPerPlayer.put(playerList.get(1), new Pair<>(6, 4));
        cannonPerPlayer.put(playerList.get(0), null);
        cannonPerPlayer.put(playerList.get(1), null);

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());

        energyP1--; // P1 activated his shield
        energyP2--; // P2 activated his shield
        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 4 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 4 ====");
        printShipGrid(shipP2);

        // ======== Meteor 5 of 8 (Big, Bottom) ========
        // Player 1 --> Shoots a single cannon (shouldn't consume battery)
        // Player 2 --> Does nothing
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), new Pair<>(0, 0));
        shieldPerPlayer.put(playerList.get(1), new Pair<>(0, 0));
        cannonPerPlayer.put(playerList.get(0), new Pair<>(5, 7));
        cannonPerPlayer.put(playerList.get(1), new Pair<>(0, 0));

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());

        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        energyP2 = 0; // This meteor detaches the player2's ship branch with the batteries, therefore the count is 0
        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 5 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 5 ====");
        printShipGrid(shipP2);

        // ======== Meteor 6 of 8 (Big, Left) ========
        // Player 1 --> Does nothing
        // Player 2 --> Does nothing
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), new Pair<>(0, 0));
        shieldPerPlayer.put(playerList.get(1), new Pair<>(0, 0));
        cannonPerPlayer.put(playerList.get(0), new Pair<>(0, 0));
        cannonPerPlayer.put(playerList.get(1), new Pair<>(0, 0));

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());

        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 6 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 6 ====");
        printShipGrid(shipP2);

        // ======== Meteor 7 of 8 (Big, Top) ========
        // Player 1 --> Shoots a double cannon, even though p1 has zero energy
        // Player 2 --> Shoots a double cannon, should consume 1 energy
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), new Pair<>(0, 0));
        shieldPerPlayer.put(playerList.get(1), new Pair<>(0, 0));
        cannonPerPlayer.put(playerList.get(0), new Pair<>(6, 8));
        cannonPerPlayer.put(playerList.get(1), new Pair<>(6, 8));

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        try {
            meteorShower.useCard(meteorShowerJSON);
        }
        catch (IllegalStateException e) {
            assertEquals(0, playerList.get(0).getShip().getAvailableEnergy());
        }

        assertFalse(meteorShower.hasFinished());

        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 7 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 7 ====");
        printShipGrid(shipP2);

        // ======== Meteor 8 of 8 (Big, Right) ========
        // Player 1 -->
        // Player 2 -->
        shieldPerPlayer = new HashMap<>();
        cannonPerPlayer = new HashMap<>();
        shieldPerPlayer.put(playerList.get(0), new Pair<>(0, 0));
        shieldPerPlayer.put(playerList.get(1), new Pair<>(0, 0));
        cannonPerPlayer.put(playerList.get(0), new Pair<>(0, 0));
        cannonPerPlayer.put(playerList.get(1), new Pair<>(0, 0));

        System.out.println("[BEFORE] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[BEFORE] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        meteorShowerJSON = new MeteorShowerJSON(shieldPerPlayer, cannonPerPlayer);
        meteorShower.useCard(meteorShowerJSON);
        assertTrue(meteorShower.hasFinished());

        System.out.println("[AFTER] Player1 Energy: " + energyP1 + " | actualEnergy: " + playerList.get(0).getShip().getAvailableEnergy());
        System.out.println("[AFTER] Player2 Energy: " + energyP2 + " | actualEnergy: " + playerList.get(1).getShip().getAvailableEnergy());

        assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());

        System.out.println("\n\t ==== SHIP Player1 after Meteor 8 ====");
        printShipGrid(shipP1);
        System.out.println("\n\t ==== SHIP Player2 after Meteor 8 ====");
        printShipGrid(shipP2);
    }

    @Test
    void generateState() {
    }
}