package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ShipJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class EpidemyTest {

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

    void initCustomShip(Player player) {
        Ship ship = player.getShip();

        int[] connectors = new int[4];

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        /*
               ==== Ship Configuration (LEVEL 2) ====
            \       4       5       6       7       8
            4               a
            5               b       c       d
            6       e       f       g       h       i
            7       j       k       l       m       n
            8       o       p               q       r

            Total components = 18 (17 + 1 core)

            a = doubleCannon1 at (4, 5)
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
        ship.addComponent(doubleCannon1, 4, 5);
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

//        System.out.println("==== SHIP CONFIGURATION ====");
//        printShipGrid(ship);

        // Generating the component sub-lists right after the ship is created
        ship.generateComponentSubLists();
    }

    @Test
    void useCardWithThreePlayersAndDifferentConfigurations() {
        // Player 1 --> Has only humans onboard, has 3 cabins (thus 6 humans) all near each other
        //              When Epidemy card is used, the amount of humans should be 3
        // Player 2 --> Has 2 cabins (core + 1 normal), normal contains an alien (brown)
        //              Since they are diagonal to each other (thus not neighbours), Epidemy has no effect
        // Player 3 --> Has the same ship as player 1 (for simplicity) but the cabin eligible for alien life has an alien inside
        //              The amount of lifeforms after applying the Epidemy card should go from 5 to 2

        Board board = new BoardLevel2();

        board.newPlayer("p1", PlayerColor.RED);
        board.newPlayer("p2", PlayerColor.BLUE);
        board.newPlayer("p3", PlayerColor.YELLOW);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
        }

        Ship shipPlayer1 = board.getPlayers().get(0).getShip();
        Ship shipPlayer2 = board.getPlayers().get(1).getShip();
        Ship shipPlayer3 = board.getPlayers().get(2).getShip();

        int[] connectors = new int[4];

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Cabin cabin2 = new Cabin(connectors, false);
        Cabin cabin3 = new Cabin(connectors, false);

        shipPlayer1.removeComponent(6, 7);
        shipPlayer1.addComponent(cabin2, 6, 7);
        shipPlayer3.removeComponent(6, 7);
        shipPlayer3.addComponent(cabin3, 6, 7);

        shipPlayer1.generateComponentSubLists();
        shipPlayer2.generateComponentSubLists();
        shipPlayer3.generateComponentSubLists();

        Lifeform brownAlien = new Lifeform(LifeformType.BROWN_ALIEN);
        Lifeform purpleAlien = new Lifeform(LifeformType.PURPLE_ALIEN);
        Lifeform astronaut = new Lifeform(LifeformType.ASTRONAUT);

        // Adding all humans to player1's ship
        shipPlayer1.getCabinList().forEach(
            (Cabin c) -> {
                if (c != shipPlayer1.getComponent(6, 6)) {
                    c.addInhabitant(astronaut);
                    c.addInhabitant(astronaut);
                }
            }
        );

        // Adding all humans and an alien to player2's ship (ofc where eligible)
        // (Just for testing & simplicity) The type of alien added is based on the first vital unit found connected to a cabin
        shipPlayer2.getCabinList().forEach(
            (Cabin c) -> {
                boolean alienPlaced = false;
                Component[] neighbours = shipPlayer2.getNearestComponents(c);

                if (c != shipPlayer2.getComponent(6, 6)) {
                    for (Component neighbour : neighbours) {
                        switch (neighbour) {
                            case Vital vital -> {
                                alienPlaced = true;
                                if (vital.getVitalType() == VitalType.BROWN_VITAL) {
                                    Map<Integer, Pair<Integer, Integer>> alienCoords;
                                    ShipJSON shipJSON;

                                    alienCoords = new HashMap<>();
                                    alienCoords.put(2, new Pair<>(c.getPosition()[0], c.getPosition()[1]));
                                    shipJSON = new ShipJSON("P2", alienCoords);
                                    shipPlayer2.setChosenAliensForEligibleCabins(shipJSON);
                                }
                                else {
                                    Map<Integer, Pair<Integer, Integer>> alienCoords;
                                    ShipJSON shipJSON;

                                    alienCoords = new HashMap<>();
                                    alienCoords.put(1, new Pair<>(c.getPosition()[0], c.getPosition()[1]));
                                    shipJSON = new ShipJSON("P2", alienCoords);
                                    shipPlayer2.setChosenAliensForEligibleCabins(shipJSON);
                                }
                            }
                            case null, default -> {}
                        }
                        if (alienPlaced) break;
                    }

                    // NOTE: Core cabin is already filled with 2 humans
                    if (!alienPlaced) {
                        c.addInhabitant(astronaut);
                        c.addInhabitant(astronaut);
                    }
                }
            }
        );

        // Adding all humans and an alien to player3's ship (ofc where eligible)
        // (Just for testing & simplicity) The type of alien added is based on the first vital unit found connected to a cabin
        shipPlayer3.getCabinList().forEach(
            (Cabin c) -> {
                boolean alienPlaced = false;
                Component[] neighbours = shipPlayer3.getNearestComponents(c);

                if (c != shipPlayer3.getComponent(6, 6)) {
                    for (Component neighbour : neighbours) {
                        switch (neighbour) {
                            case Vital vital -> {
                                alienPlaced = true;
                                if (vital.getVitalType() == VitalType.BROWN_VITAL) {
                                    Map<Integer, Pair<Integer, Integer>> alienCoords;
                                    ShipJSON shipJSON;

                                    alienCoords = new HashMap<>();
                                    alienCoords.put(2, new Pair<>(c.getPosition()[0], c.getPosition()[1]));
                                    shipJSON = new ShipJSON("P3", alienCoords);
                                    shipPlayer3.setChosenAliensForEligibleCabins(shipJSON);
                                }
                                else {
                                    Map<Integer, Pair<Integer, Integer>> alienCoords;
                                    ShipJSON shipJSON;

                                    alienCoords = new HashMap<>();
                                    alienCoords.put(1, new Pair<>(c.getPosition()[0], c.getPosition()[1]));
                                    shipJSON = new ShipJSON("P3", alienCoords);
                                    shipPlayer3.setChosenAliensForEligibleCabins(shipJSON);
                                }
                            }
                            case null, default -> {}
                        }
                        if (alienPlaced) break;
                    }

                    // NOTE: Core cabin is already filled with 2 humans
                    if (!alienPlaced) {
                        c.addInhabitant(astronaut);
                        c.addInhabitant(astronaut);
                    }
                }
            }
        );

        Epidemy epidemy = new Epidemy(
                "Epidemy",
                board.getLevel(),
                board
        );

        assertEquals(6, shipPlayer1.getAllLifeforms().size());
        assertEquals(3, shipPlayer2.getAllLifeforms().size());
        assertEquals(5, shipPlayer3.getAllLifeforms().size());

        epidemy.initCardPlayers();
        epidemy.useCard();

        List<LifeformType> expectedPlayer1Lifeforms = new ArrayList<>();
        List<LifeformType> expectedPlayer2Lifeforms = new ArrayList<>();
        List<LifeformType> expectedPlayer3Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.BROWN_ALIEN);

        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);

        assertEquals(3, shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(3, shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));
        assertEquals(2, shipPlayer3.getAllLifeforms().size());
        assertTrue(shipPlayer3.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer3Lifeforms));
    }

    @Test
    void useCardWithPlayersWithSingleOccupiedCabins() {
        // Player 1 --> Has only humans onboard, has 3 cabins (core with 2 humans, the other 2 with 1 human each)
        //              all near each other. When Epidemy strikes, the amount of humans should be 1 (only 1 human remaining in the core)
        // Player 2 --> Has only humans onboard, has 3 cabins (core with 2 humans, the other 2 with 1 and 2 human each)
        //              and the cabin that has 2 humans (not the core) is distant from the others, therefore the
        //              remaining amount of humans should be 3

        Board board = new BoardLevel2();

        board.newPlayer("p1", PlayerColor.RED);
        board.newPlayer("p2", PlayerColor.BLUE);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
        }

        Ship shipPlayer1 = board.getPlayers().get(0).getShip();
        Ship shipPlayer2 = board.getPlayers().get(1).getShip();

        int[] connectors = new int[4];

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Cabin cabin2 = new Cabin(connectors, false);
        Cabin cabin3 = new Cabin(connectors, false);

        shipPlayer1.removeComponent(6, 7);
        shipPlayer1.addComponent(cabin2, 6, 7);
        shipPlayer2.removeComponent(5, 6);
        shipPlayer2.addComponent(cabin3, 5, 6);

        shipPlayer1.generateComponentSubLists();
        shipPlayer2.generateComponentSubLists();

        Lifeform astronaut = new Lifeform(LifeformType.ASTRONAUT);

        // Adding humans to player1's ship
        shipPlayer1.getCabinList().forEach(
            (Cabin c) -> {
                if (c != shipPlayer1.getCore()) {
                    c.addInhabitant(astronaut);
                }
            }
        );

        // Adding humans to player2's ship
        shipPlayer2.getCabinList().forEach(
            (Cabin c) -> {
                if (c != shipPlayer2.getCore()) {
                    c.addInhabitant(astronaut);
                }
            }
        );
        ((Cabin) shipPlayer2.getComponent(5, 6)).addInhabitant(astronaut);

        Epidemy epidemy = new Epidemy(
                "Epidemy",
                board.getLevel(),
                board
        );

        assertEquals(4, shipPlayer1.getAllLifeforms().size());
        assertEquals(5, shipPlayer2.getAllLifeforms().size());

        epidemy.initCardPlayers();
        epidemy.useCard();

        List<LifeformType> expectedPlayer1Lifeforms = new ArrayList<>();
        List<LifeformType> expectedPlayer2Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);

        assertEquals(expectedPlayer1Lifeforms.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(expectedPlayer2Lifeforms.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));
    }

    @Test
    void useCardWithHumansAndAliensAsNeighboursAndNot() {
        // Player 1 --> Has 3 cabins all connected to each other, one cabin has 1 purple alien, the others are all humans
        //              When Epidemy strikes, the number of lifeforms should go from 5 to 2
        // Player 2 --> Has 3 cabins all well distanced between each other, 2 aliens of both types, therefore
        //              when Epidemy strikes the number of lifeforms should stay at 4 (2 aliens and 2 humans)

        Board board = new BoardLevel2();

        board.newPlayer("p1", PlayerColor.RED);
        board.newPlayer("p2", PlayerColor.BLUE);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
        }

        Ship shipPlayer1 = board.getPlayers().get(0).getShip();
        Ship shipPlayer2 = board.getPlayers().get(1).getShip();

        int[] connectors = new int[4];

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Cabin cabin2 = new Cabin(connectors, false);
        Cabin cabin3 = new Cabin(connectors, false);

        shipPlayer1.removeComponent(6, 7);
        shipPlayer1.addComponent(cabin2, 6, 7);
        shipPlayer2.removeComponent(6, 8);
        shipPlayer2.addComponent(cabin3, 6, 8);

        shipPlayer1.generateComponentSubLists();
        shipPlayer2.generateComponentSubLists();

        Lifeform astronaut = new Lifeform(LifeformType.ASTRONAUT);
        Lifeform purpleAlien = new Lifeform(LifeformType.PURPLE_ALIEN);
        Lifeform brownAlien = new Lifeform(LifeformType.BROWN_ALIEN);

        ((Cabin) shipPlayer1.getComponent(6, 7)).addInhabitant(astronaut);
        ((Cabin) shipPlayer1.getComponent(6, 7)).addInhabitant(astronaut);

        Map<Integer, Pair<Integer, Integer>> alienCoords;
        ShipJSON shipJSON;

        alienCoords = new HashMap<>();
        alienCoords.put(1, new Pair<>(7, 7));
        shipJSON = new ShipJSON("P1", alienCoords);
        shipPlayer1.setChosenAliensForEligibleCabins(shipJSON);

        alienCoords = new HashMap<>();
        alienCoords.put(1, new Pair<>(7, 7));
        shipJSON = new ShipJSON("P2", alienCoords);
        shipPlayer2.setChosenAliensForEligibleCabins(shipJSON);

        alienCoords = new HashMap<>();
        alienCoords.put(2, new Pair<>(6, 8));
        shipJSON = new ShipJSON("P2", alienCoords);
        shipPlayer2.setChosenAliensForEligibleCabins(shipJSON);

        Epidemy epidemy = new Epidemy(
            "Epidemy",
            board.getLevel(),
            board
        );

        assertEquals(5, shipPlayer1.getAllLifeforms().size());
        assertEquals(4, shipPlayer2.getAllLifeforms().size());

        epidemy.initCardPlayers();
        epidemy.useCard();

        List<LifeformType> expectedPlayer1Lifeforms = new ArrayList<>();
        List<LifeformType> expectedPlayer2Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.PURPLE_ALIEN);
        expectedPlayer2Lifeforms.add(LifeformType.BROWN_ALIEN);

        assertEquals(expectedPlayer1Lifeforms.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(expectedPlayer2Lifeforms.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));
    }
}
