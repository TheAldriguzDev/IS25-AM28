package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEpidemy;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Model.Connector.THREE_PIPES;
import static it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType.*;
import static org.junit.jupiter.api.Assertions.*;

class EpidemyTest {

    List<Integer> getConnectors() {
        List<Integer> connectors = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            connectors.add(THREE_PIPES.ordinal());
        }

        return connectors;
    }

    void add2PlayersToBoard(Board board) {

        Player p1 = new Player("p1", PlayerColor.RED, 2);
        Player p2 = new Player("p2", PlayerColor.BLUE, 2);

        board.newPlayer(p1);
        board.newPlayer(p2);
    }

    void add3PlayersToBoard(Board board) {

        Player p1 = new Player("p1", PlayerColor.RED, 2);
        Player p2 = new Player("p2", PlayerColor.BLUE, 2);
        Player p3 = new Player("p3", PlayerColor.YELLOW, 2);

        board.newPlayer(p1);
        board.newPlayer(p2);
        board.newPlayer(p3);
    }

    void add4PlayersToBoard(Board board) {
        Player p1 = new Player("p1", PlayerColor.RED, 2);
        Player p2 = new Player("p2", PlayerColor.BLUE, 2);
        Player p3 = new Player("p3", PlayerColor.YELLOW, 2);
        Player p4 = new Player("p3", PlayerColor.GREEN, 2);

        board.newPlayer(p1);
        board.newPlayer(p2);
        board.newPlayer(p3);
        board.newPlayer(p4);
    }

//    /**
//     * Outputs the given ship to terminal
//     * @param ship The ship to print to terminal
//     */
//    void printShipGrid(Ship ship) {
//        if (ship != null) {
//            List<Component> components = new ArrayList<>();
//            Component c;
//            int rows = ship.getGridDimensions().getKey();
//            int cols = ship.getGridDimensions().getValue();
//            int i, j, k;
//
//            System.out.print("\n\\");
//            for (i = 0; i < cols; i++) {
//                System.out.print("\t" + i);
//            }
//            System.out.print("\n");
//
//            k = 0;
//
//            for (i = 0; i < rows; i++) {
//                System.out.print(i + "\t");
//                for (j = 0; j < cols; j++) {
//                    c = ship.getComponent(i, j);
//                    if (c == null) {
//                        System.out.print("." + "\t");
//                    }
//                    else {
//                        System.out.print(k++ + "\t");
//                        components.add(c);
//                    }
//                }
//                System.out.print("\n");
//            }
//
//            int size = components.size();
//
//            System.out.println("\nFound these components:");
//
//            for (i = 0; i < size; i++) {
//                System.out.println(i + " - " + components.get(i));
//            }
//        }
//        else {
//            System.out.println("ERROR: Given ship is null");
//        }
//    }

    void initCustomShip(Player player) {
        Ship ship = player.getShip();

        List<Integer> connectors = getConnectors();

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

        Battery tripleBattery1 = new Battery(connectors, 3, "");

        Cannon singleCannon1 = new Cannon(connectors, 1, "");
        Cannon singleCannon2 = new Cannon(connectors, 1, "");
        Cannon doubleCannon1 = new Cannon(connectors, 2, "");
        Cannon doubleCannon2 = new Cannon(connectors, 2, "");

        Engine singleEngine1 = new Engine(connectors, 1, "");
        Engine singleEngine2 = new Engine(connectors, 1, "");
        Engine singleEngine3 = new Engine(connectors, 1, "");
        Engine doubleEngine1 = new Engine(connectors, 2, "");

        Cabin cabin1 = new Cabin(connectors, false, "");

        Shield shield1 = new Shield(connectors, "");

        Storage normalDoubleStorage1 = new Storage(connectors, 2, false, "");
        Storage normalTripleStorage1 = new Storage(connectors, 3, false, "");
        Storage specialSingleStorage1 = new Storage(connectors, 1, true, "");
        Storage specialDoubleStorage1 = new Storage(connectors, 2, true, "");

        Structural structural1 = new Structural(connectors, "");

        Vital purpleVital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal(), "");
        Vital brownVital1 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal(), "");

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

        add3PlayersToBoard(board);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
        }

        Ship shipPlayer1 = board.getPlayers().get(0).getShip();
        Ship shipPlayer2 = board.getPlayers().get(1).getShip();
        Ship shipPlayer3 = board.getPlayers().get(2).getShip();

        List<Integer> connectors = getConnectors();

        Cabin cabin2 = new Cabin(connectors, false, "");
        Cabin cabin3 = new Cabin(connectors, false, "");

        shipPlayer1.removeComponent(6, 7);
        shipPlayer1.addComponent(cabin2, 6, 7);
        shipPlayer3.removeComponent(6, 7);
        shipPlayer3.addComponent(cabin3, 6, 7);

        shipPlayer1.generateComponentSubLists();
        shipPlayer2.generateComponentSubLists();
        shipPlayer3.generateComponentSubLists();

        // Adding all humans to player1's ship
        shipPlayer1.getCabinList().forEach(
                (Cabin c) -> {
                    shipPlayer1.addLifeformToCabin(c.getPosition()[0], c.getPosition()[1], ASTRONAUT);
                    shipPlayer1.addLifeformToCabin(c.getPosition()[0], c.getPosition()[1], ASTRONAUT);
                }
        );

        // Adding all humans and an alien to player2's ship (ofc where eligible)
        // (Just for testing & simplicity) The type of alien added is based on the first vital unit found connected to a cabin
        shipPlayer2.getCabinList().forEach(
                (Cabin c) -> {
                    if (c != shipPlayer2.getCore()) {
                        Component[] neighbours = shipPlayer2.getNearestComponents(c);
                        boolean alienPlaced = false;

                        for (Component neighbour : neighbours) {
                            switch (neighbour) {
                                case Vital vital -> {
                                    if (vital.getVitalType() == VitalType.BROWN_VITAL) {
                                        if (shipPlayer2.getBrownAlienPosition() == null) {
                                            try {
                                                shipPlayer2.addLifeformToCabin(
                                                        c.getPosition()[0],
                                                        c.getPosition()[1],
                                                        BROWN_ALIEN
                                                );
                                                alienPlaced = true;
                                            }
                                            catch (Exception e) {
                                                // Refuse to add
                                            }
                                        }
                                        else {
                                            System.out.println("BROWN ALIEN already there!!!");
                                        }
                                    }
                                    else if (vital.getVitalType() == VitalType.PURPLE_VITAL) {
                                        if (shipPlayer2.getPurpleAlienPosition() == null) {
                                            try {
                                                shipPlayer2.addLifeformToCabin(
                                                        c.getPosition()[0],
                                                        c.getPosition()[1],
                                                        PURPLE_ALIEN
                                                );
                                                alienPlaced = true;
                                            }
                                            catch (Exception e) {
                                                // Refuse to add
                                            }
                                        }
                                        else {
                                            System.out.println("PURPLE ALIEN already there!!!");
                                        }
                                    }
                                    else {
                                        throw new IllegalArgumentException("ERROR: Vital type not recognized");
                                    }
                                }
                                case null, default -> {}
                            }
                        }

                        if (!alienPlaced) {
                            shipPlayer2.addLifeformToCabin(
                                    c.getPosition()[0],
                                    c.getPosition()[1],
                                    ASTRONAUT
                            );
                            shipPlayer2.addLifeformToCabin(
                                    c.getPosition()[0],
                                    c.getPosition()[1],
                                    ASTRONAUT
                            );
                        }
                    }
                }
        );

        // Adding all humans and an alien to player3's ship (ofc where eligible)
        // (Just for testing & simplicity) The type of alien added is based on the first vital unit found connected to a cabin
        shipPlayer3.getCabinList().forEach(
                (Cabin c) -> {
                    if (c != shipPlayer3.getCore()) {

                        Component[] neighbours = shipPlayer3.getNearestComponents(c);
                        boolean alienPlaced = false;

                        for (Component neighbour : neighbours) {
                            switch (neighbour) {
                                case Vital vital -> {
                                    if (vital.getVitalType() == VitalType.BROWN_VITAL) {
                                        if (shipPlayer3.getBrownAlienPosition() == null) {
                                            try {
                                                shipPlayer3.addLifeformToCabin(
                                                        c.getPosition()[0],
                                                        c.getPosition()[1],
                                                        BROWN_ALIEN
                                                );
                                                alienPlaced = true;
                                            }
                                            catch (Exception e) {
                                                // Refuse to add
                                            }
                                        }
                                        else {
                                            System.out.println("BROWN ALIEN already there!!!");
                                        }
                                    }
                                    else if (vital.getVitalType() == VitalType.PURPLE_VITAL) {
                                        if (shipPlayer3.getPurpleAlienPosition() == null) {
                                            try {
                                                shipPlayer3.addLifeformToCabin(
                                                        c.getPosition()[0],
                                                        c.getPosition()[1],
                                                        PURPLE_ALIEN
                                                );
                                                alienPlaced = true;
                                            }
                                            catch (Exception e) {
                                                // Refuse to add
                                            }
                                        }
                                        else {
                                            System.out.println("PURPLE ALIEN already there!!!");
                                        }
                                    }
                                    else {
                                        throw new IllegalArgumentException("ERROR: Vital type not recognized");
                                    }
                                }
                                case null, default -> {}
                            }
                        }

                        if (!alienPlaced) {
                            shipPlayer3.addLifeformToCabin(
                                    c.getPosition()[0],
                                    c.getPosition()[1],
                                    ASTRONAUT
                            );
                            shipPlayer3.addLifeformToCabin(
                                    c.getPosition()[0],
                                    c.getPosition()[1],
                                    ASTRONAUT
                            );
                        }
                    }
                }
        );

        List<LifeformType> expectedPlayer1Lifeforms;
        List<LifeformType> expectedPlayer2Lifeforms;
        List<LifeformType> expectedPlayer3Lifeforms;
        CardStateJSON state;

        Epidemy epidemy = new Epidemy(
                "Epidemy",
                board.getLevel(),
                board
                ,0,
                ""
        );

        ClientEpidemy clientEpidemy;

        assertEquals(6, shipPlayer1.getAllLifeforms().size());
        assertEquals(3, shipPlayer2.getAllLifeforms().size());
        assertEquals(5, shipPlayer3.getAllLifeforms().size());

        // ======== WIDGET TESTING ========= //
        clientEpidemy = new ClientEpidemy(epidemy.generateState());
        clientEpidemy.generateWidget().printWidget();
        // ================================= //

        epidemy.initCardPlayers();

        // ======== WIDGET TESTING ========= //
        clientEpidemy.updateCard(epidemy.generateState());
        clientEpidemy.generateWidget().printWidget();
        // ================================= //

        // (1.1) - P1 uses the card
        epidemy.useCard();

        // ======== WIDGET TESTING ========= //
        clientEpidemy.updateCard(epidemy.generateState());
        clientEpidemy.generateWidget().printWidget();
        // ================================= //

        expectedPlayer1Lifeforms = new ArrayList<>();
        expectedPlayer2Lifeforms = new ArrayList<>();
        expectedPlayer3Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.BROWN_ALIEN);

        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.BROWN_ALIEN);

        assertEquals(expectedPlayer1Lifeforms.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(expectedPlayer2Lifeforms.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));
        assertEquals(expectedPlayer3Lifeforms.size(), shipPlayer3.getAllLifeforms().size());
        assertTrue(shipPlayer3.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer3Lifeforms));

        // (1.2) - Verify that the card is not used after P2
//        state = epidemy.generateState();
        assertFalse(epidemy.hasFinished());

        // (2.1) - P2 uses the card
        epidemy.useCard();

        // ======== WIDGET TESTING ========= //
        clientEpidemy.updateCard(epidemy.generateState());
        clientEpidemy.generateWidget().printWidget();
        // ================================= //

        expectedPlayer1Lifeforms = new ArrayList<>();
        expectedPlayer2Lifeforms = new ArrayList<>();
        expectedPlayer3Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.BROWN_ALIEN);

        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.BROWN_ALIEN);

        assertEquals(expectedPlayer1Lifeforms.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(expectedPlayer2Lifeforms.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));
        assertEquals(expectedPlayer3Lifeforms.size(), shipPlayer3.getAllLifeforms().size());
        assertTrue(shipPlayer3.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer3Lifeforms));

        // (2.2) - Verify that the card is not used after P2
//        state = epidemy.generateState();
        assertFalse(epidemy.hasFinished());

        // (3) - P3 uses the card
        epidemy.useCard();

        // ======== WIDGET TESTING ========= //
        System.out.println("Last State");
        clientEpidemy.updateCard(epidemy.generateState());
        clientEpidemy.generateWidget().printWidget();
        // ================================= //

        expectedPlayer1Lifeforms = new ArrayList<>();
        expectedPlayer2Lifeforms = new ArrayList<>();
        expectedPlayer3Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.BROWN_ALIEN);

        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer3Lifeforms.add(LifeformType.ASTRONAUT);

        assertEquals(expectedPlayer1Lifeforms.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(expectedPlayer2Lifeforms.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));
        assertEquals(expectedPlayer3Lifeforms.size(), shipPlayer3.getAllLifeforms().size());
        assertTrue(shipPlayer3.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer3Lifeforms));

        // (3.2) - Verify that the card IS used after P3
//        state = epidemy.generateState();
        assertTrue(epidemy.hasFinished());

    }

    @Test
    void useCardWithPlayersWithSingleOccupiedCabins() {
        // Player 1 --> Has only humans onboard, has 3 cabins (core with 2 humans, the other 2 with 1 human each)
        //              all near each other. When Epidemy strikes, the amount of humans should be 1 (only 1 human remaining in the core)
        // Player 2 --> Has only humans onboard, has 3 cabins (core with 2 humans, the other 2 with 1 and 2 human each)
        //              and the cabin that has 2 humans (not the core) is distant from the others, therefore the
        //              remaining amount of humans should be 3

        Board board = new BoardLevel2();

        add2PlayersToBoard(board);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
        }

        Ship shipPlayer1 = board.getPlayers().get(0).getShip();
        Ship shipPlayer2 = board.getPlayers().get(1).getShip();

        List<Integer> connectors = getConnectors();

        Cabin cabin2 = new Cabin(connectors, false, "");
        Cabin cabin3 = new Cabin(connectors, false, "");

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
                board,
                0,
                ""
        );

        List<LifeformType> expectedPlayer1Lifeforms;
        List<LifeformType> expectedPlayer2Lifeforms;
        CardStateJSON state;

        assertEquals(4, shipPlayer1.getAllLifeforms().size());
        assertEquals(5, shipPlayer2.getAllLifeforms().size());

        epidemy.initCardPlayers();

        // (1.1) - P1 uses the card
        epidemy.useCard();

        expectedPlayer1Lifeforms = new ArrayList<>();
        expectedPlayer2Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);

        assertEquals(expectedPlayer1Lifeforms.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(expectedPlayer2Lifeforms.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));

        // (1.2) - Verify that the card is still in use after P1
        state = epidemy.generateState();
        assertTrue(state.getIsCardUsable());

        // (2.1) - P2 uses the card
        epidemy.useCard();

        expectedPlayer1Lifeforms = new ArrayList<>();
        expectedPlayer2Lifeforms = new ArrayList<>();

        expectedPlayer1Lifeforms.add(LifeformType.ASTRONAUT);

        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);
        expectedPlayer2Lifeforms.add(LifeformType.ASTRONAUT);

        assertEquals(expectedPlayer1Lifeforms.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer1Lifeforms));
        assertEquals(expectedPlayer2Lifeforms.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList().containsAll(expectedPlayer2Lifeforms));

        // (2.2) - Verify that the card is NOT in use after P2
        state = epidemy.generateState();
        //assertFalse(state.getIsCardUsable());
    }

    @Test
    void useCardWithHumansAndAliensAsNeighboursAndNot() {
        // Player 1 --> Has 3 cabins all connected to each other, one cabin has 1 purple alien, the others are all humans
        //              When Epidemy strikes, the number of lifeforms should go from 5 to 2
        // Player 2 --> Has 3 cabins all well distanced between each other, 2 aliens of both types, therefore
        //              when Epidemy strikes the number of lifeforms should stay at 4 (2 aliens and 2 humans)

        Board board = new BoardLevel2();

        add2PlayersToBoard(board);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
        }

        Ship shipPlayer1 = board.getPlayers().get(0).getShip();
        Ship shipPlayer2 = board.getPlayers().get(1).getShip();

        List<Integer> connectors = getConnectors();

        Cabin cabin2 = new Cabin(connectors, false, "");
        Cabin cabin3 = new Cabin(connectors, false, "");

        shipPlayer1.removeComponent(6, 7);
        shipPlayer1.addComponent(cabin2, 6, 7);
        shipPlayer2.removeComponent(6, 8);
        shipPlayer2.addComponent(cabin3, 6, 8);

        shipPlayer1.generateComponentSubLists();
        shipPlayer2.generateComponentSubLists();

        shipPlayer1.addLifeformToCabin(6, 7, ASTRONAUT);
        shipPlayer1.addLifeformToCabin(7, 7, PURPLE_ALIEN);

        shipPlayer2.addLifeformToCabin(7, 7, PURPLE_ALIEN);
        shipPlayer2.addLifeformToCabin(6, 8, BROWN_ALIEN);

        Epidemy epidemy = new Epidemy(
                "Epidemy",
                board.getLevel(),
                board,
                0,
                ""
        );

        List<LifeformType> expectedPlayer1Lifeforms;
        List<LifeformType> expectedPlayer2Lifeforms;
        CardStateJSON state;

        assertEquals(5, shipPlayer1.getAllLifeforms().size());
        assertEquals(4, shipPlayer2.getAllLifeforms().size());

        epidemy.initCardPlayers();

        // (1.1) - P1 uses the card
        epidemy.useCard();

        expectedPlayer1Lifeforms = new ArrayList<>();
        expectedPlayer2Lifeforms = new ArrayList<>();

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

        // (1.2) - Verify that the card is still in use after P1
        state = epidemy.generateState();
        assertTrue(state.getIsCardUsable());

        // (2.1) - P2 uses the card
        epidemy.useCard();

        expectedPlayer1Lifeforms = new ArrayList<>();
        expectedPlayer2Lifeforms = new ArrayList<>();

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

        // (2.2) - Verify that the card is NOT in use after P2
        state = epidemy.generateState();
        //assertFalse(state.getIsCardUsable());
    }

    @Test
    void useCardAndSkipTwoOfFourPlayers() {
        // Player 1 & 4 --> Always connected; P1 has neighbouring occupied cabins, thus epidemy will have an effect on his ship
        //                  but player 4 has all cabins disconnected from each other, thus epidemy won't have an effect on his ship
        // Player 2 & 3 --> They mirror the same ship config as P4 and P1 respectively, but they both get disconnected, thus
        //                  they should be skipped and their ships should stay the same, as if epidemy didn't have any effecto on them

        Board board = new BoardLevel2();

        add4PlayersToBoard(board);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
        }

        Ship shipPlayer1 = board.getPlayers().get(0).getShip();
        Ship shipPlayer2 = board.getPlayers().get(1).getShip();
        Ship shipPlayer3 = board.getPlayers().get(2).getShip();
        Ship shipPlayer4 = board.getPlayers().get(3).getShip();

        List<Integer> connectors = getConnectors();

        Cabin cabin2 = new Cabin(connectors, false, "");
        Cabin cabin3 = new Cabin(connectors, false, "");
        Cabin cabin4 = new Cabin(connectors, false, "");
        Cabin cabin5 = new Cabin(connectors, false, "");

        shipPlayer1.removeComponent(6, 7);
        shipPlayer1.addComponent(cabin2, 6, 7);
        shipPlayer1.addLifeformToCabin(6, 7, ASTRONAUT);
        shipPlayer1.addLifeformToCabin(6, 7, ASTRONAUT);
        shipPlayer1.addLifeformToCabin(7, 7, PURPLE_ALIEN);

        shipPlayer2.removeComponent(6, 8);
        shipPlayer2.addComponent(cabin3, 6, 8);
        shipPlayer2.addLifeformToCabin(6, 8, BROWN_ALIEN);
        shipPlayer2.addLifeformToCabin(7, 7, PURPLE_ALIEN);

        shipPlayer3.removeComponent(6, 7);
        shipPlayer3.addComponent(cabin4, 6, 7);
        shipPlayer3.addLifeformToCabin(6, 7, ASTRONAUT);
        shipPlayer3.addLifeformToCabin(6, 7, ASTRONAUT);
        shipPlayer3.addLifeformToCabin(7, 7, PURPLE_ALIEN);

        shipPlayer4.removeComponent(6, 8);
        shipPlayer4.addComponent(cabin5, 6, 8);
        shipPlayer4.addLifeformToCabin(6, 8, BROWN_ALIEN);
        shipPlayer4.addLifeformToCabin(7, 7, ASTRONAUT);
        shipPlayer4.addLifeformToCabin(7, 7, ASTRONAUT);

        shipPlayer1.generateComponentSubLists();
        shipPlayer2.generateComponentSubLists();
        shipPlayer3.generateComponentSubLists();
        shipPlayer4.generateComponentSubLists();

        Epidemy epidemy = new Epidemy(
                "Epidemy",
                board.getLevel(),
                board,
                0,
                ""
        );

        List<LifeformType> expectedLifeformsP1;
        List<LifeformType> expectedLifeformsP2;
        List<LifeformType> expectedLifeformsP3;
        List<LifeformType> expectedLifeformsP4;
        CardStateJSON state;

        expectedLifeformsP1 = new ArrayList<>();
        expectedLifeformsP2 = new ArrayList<>();
        expectedLifeformsP3 = new ArrayList<>();
        expectedLifeformsP4 = new ArrayList<>();

        expectedLifeformsP1.add(ASTRONAUT);
        expectedLifeformsP1.add(ASTRONAUT);
        expectedLifeformsP1.add(ASTRONAUT);
        expectedLifeformsP1.add(ASTRONAUT);
        expectedLifeformsP1.add(PURPLE_ALIEN);

        assertEquals(expectedLifeformsP1.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(expectedLifeformsP1.containsAll(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        expectedLifeformsP2.add(ASTRONAUT);
        expectedLifeformsP2.add(ASTRONAUT);
        expectedLifeformsP2.add(BROWN_ALIEN);
        expectedLifeformsP2.add(PURPLE_ALIEN);

        assertEquals(expectedLifeformsP2.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(expectedLifeformsP2.containsAll(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(PURPLE_ALIEN);

        assertEquals(expectedLifeformsP3.size(), shipPlayer3.getAllLifeforms().size());
        assertTrue(expectedLifeformsP3.containsAll(shipPlayer3.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(BROWN_ALIEN);

        assertEquals(expectedLifeformsP4.size(), shipPlayer4.getAllLifeforms().size());
        assertTrue(expectedLifeformsP4.containsAll(shipPlayer4.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        epidemy.initCardPlayers();

        // Disconnecting P2 and P3
        board.getPlayers().get(1).setConnected(false);
        board.getPlayers().get(2).setConnected(false);

        // (1.1) - P1 uses the card
        epidemy.useCard();

        // Verifying the expected lifeforms onboard each player's ship
        expectedLifeformsP1 = new ArrayList<>();
        expectedLifeformsP2 = new ArrayList<>();
        expectedLifeformsP3 = new ArrayList<>();
        expectedLifeformsP4 = new ArrayList<>();

        // P1 has 3 cabins close to each other, therefore 1 lifeform is subtracted from each
        // --> After epidemy, the remaining lifeforms are 2 ASTRONAUTS, 1 in Core, 1 in Cabin@(6, 7)
        expectedLifeformsP1.add(ASTRONAUT);
        expectedLifeformsP1.add(ASTRONAUT);

        assertEquals(1, shipPlayer1.getCore().getInhabitants().size());
        assertEquals(ASTRONAUT, shipPlayer1.getCore().getInhabitants().getFirst().getLifeformType());
        assertEquals(1, ((Cabin) shipPlayer1.getComponent(6, 7)).getInhabitants().size());
        assertEquals(ASTRONAUT, ((Cabin) shipPlayer1.getComponent(6, 7)).getInhabitants().getFirst().getLifeformType());
        assertEquals(0, ((Cabin) shipPlayer1.getComponent(7, 7)).getInhabitants().size());

        assertEquals(expectedLifeformsP1.size(), shipPlayer1.getAllLifeforms().size());
        assertTrue(expectedLifeformsP1.containsAll(shipPlayer1.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        // (1.2) - Verify that after P1 the card is still usable AND that the next player is P4 (since P2, P3 are marked as disconnected)
        state = epidemy.generateState();
        assertTrue(state.getIsCardUsable());
        assertEquals(board.getPlayers().getLast().getNickname(), state.getPlayerNickname());

        // P2 was disconnected, thus his lifeforms should be the same
        expectedLifeformsP2.add(ASTRONAUT);
        expectedLifeformsP2.add(ASTRONAUT);
        expectedLifeformsP2.add(BROWN_ALIEN);
        expectedLifeformsP2.add(PURPLE_ALIEN);

        assertEquals(expectedLifeformsP2.size(), shipPlayer2.getAllLifeforms().size());
        assertTrue(expectedLifeformsP2.containsAll(shipPlayer2.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        // P3 was disconnected, thus his lifeforms should be the same
        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(ASTRONAUT);
        expectedLifeformsP3.add(PURPLE_ALIEN);

        assertEquals(expectedLifeformsP3.size(), shipPlayer3.getAllLifeforms().size());
        assertTrue(expectedLifeformsP3.containsAll(shipPlayer3.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        // (4.1) - P4 uses the card
        epidemy.useCard();

        // P4 had well-distanced cabins, therefore his lifeforms should stay the same
        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(ASTRONAUT);
        expectedLifeformsP4.add(BROWN_ALIEN);

        assertEquals(expectedLifeformsP4.size(), shipPlayer4.getAllLifeforms().size());
        assertTrue(expectedLifeformsP4.containsAll(shipPlayer4.getAllLifeforms().stream().map(Lifeform::getLifeformType).toList()));

        // (4.2) - Verify that after P4 the card is marked as used
        state = epidemy.generateState();
        //assertFalse(state.getIsCardUsable());
    }
}