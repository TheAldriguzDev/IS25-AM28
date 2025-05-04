package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientMeteorShower;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.is25am28.Model.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class MeteorShowerTest {
    Board board = new BoardLevel2();
    MeteorShower meteorShower;
    ClientMeteorShower clientMeteorShower;

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

        List<Integer> connectors = new ArrayList<Integer>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add(THREE_PIPES.ordinal());
        }

        Battery tripleBattery1 = new Battery(connectors, 6);

        Cannon singleCannon1 = new Cannon(connectors, 1);
        Cannon singleCannon2 = new Cannon(connectors, 1);
        Cannon singleCannon3 = new Cannon(connectors, 1);
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
        ship.addComponent(doubleCannon1, 7, 3);
        ship.addComponent(singleCannon3, 7, 9);
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

        // Generating the component sub-lists right after the ship is created
        ship.generateComponentSubLists();

        // Verifying that the ship is correctly built according to
        // ship building rules and each component's positioning rules
        assertTrue(ship.validateShip());
    }

    @BeforeEach
    void init() {
        // Initializes a board with two players
        // Each player has the same ship (for simplicity)
        // The meteor sequence is composed of all 8 possible combinations (4 directions * 2 sizes = 8 meteor configs)

        Player p1 = new Player("p1", PlayerColor.RED, 2);
        Player p2 = new Player("p2", PlayerColor.GREEN, 2);

        board.newPlayer(p1);
        board.newPlayer(p2);

        board.addPlayerToBoard(p1);
        board.addPlayerToBoard(p2);

        for (Player player : board.getPlayers()) {
            initCustomShip(player);
//            System.out.println("==== SHIP CONFIGURATION ====");
//            printShipGrid(player.getShip());
        }

        List<List<Integer>> meteorSequence = new ArrayList<>();
        List<Integer> meteorDescriptor;

        // [1, 0] - Small Meteor, pointing up (comes from the bottom in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(2);
        meteorSequence.add(meteorDescriptor);

        // [1, 1] - Small Meteor, pointing right (comes from the left in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(3);
        meteorSequence.add(meteorDescriptor);

        // [1, 2] - Small Meteor, pointing down (comes from the top in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(0);
        meteorSequence.add(meteorDescriptor);

        // [1, 3] - Small Meteor, pointing left (comes from the right in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(1);
        meteorDescriptor.add(1);
        meteorSequence.add(meteorDescriptor);

        // [2, 0] - Big Meteor, pointing up (comes from the bottom in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(2);
        meteorSequence.add(meteorDescriptor);

        // [2, 1] - Big Meteor, pointing right (comes from the left in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(3);
        meteorSequence.add(meteorDescriptor);

        // Meteor 7 of 9 --> Testing if active shields shouldn't block the big meteor
        // [2, 2] - Big Meteor, pointing down (comes from the top in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(0);
        meteorSequence.add(meteorDescriptor);

        // Meteor 8 of 9 --> Testing destruction with both single and double cannons
        // [2, 2] - Big Meteor, pointing down (comes from the top in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(0);
        meteorSequence.add(meteorDescriptor);

        // [2, 3] - Big Meteor, pointing left (comes from the right in the ship)
        meteorDescriptor = new ArrayList<>(2);
        meteorDescriptor.add(2);
        meteorDescriptor.add(1);
        meteorSequence.add(meteorDescriptor);

        // MeteorShower card with all 8 possible meteor configurations
        this.meteorShower = new MeteorShower(
                "Meteor Shower",
                2,
                meteorSequence,
                board
        );
    }

    /**
     * NOTE: This test works only if in MeteorShower the random
     *       generator is instantiated with seed=0
     *       (because otherwise I can't know where the meteors will come from)
     */
    @Test
    void useCard_goingThroughAnEntireMeteorSequence() {

        // ======== WIDGET TESTING ======== //
        System.out.println("Non initialized card");
        clientMeteorShower = new ClientMeteorShower(meteorShower.generateState());
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        // Initializing the internal player list
        this.meteorShower.initCardPlayers();

        MeteorShowerJSON meteorShowerJSON;
        CardStateJSON meteorShowerStateJSON;
        List<Player> playerList = this.board.getPlayers();
        List<ComponentHelper<Integer>> shieldsCoordinates;
        List<ComponentHelper<Integer>> cannonsCoordinates;
        int energyP1 = playerList.get(0).getShip().getAvailableEnergy();
        int energyP2 = playerList.get(1).getShip().getAvailableEnergy();
        int currMeteorIndex = 0;

        final AtomicInteger expectedShipP1ComponentCount = new AtomicInteger(0);
        final AtomicInteger expectedShipP2ComponentCount = new AtomicInteger(0);
        final AtomicInteger actualShipP1ComponentCount = new AtomicInteger(0);
        final AtomicInteger actualShipP2ComponentCount = new AtomicInteger(0);

        // Counting the component amount on the ship
        playerList.get(0).getShip().traverse(
            (Component c) -> {
                expectedShipP1ComponentCount.getAndIncrement();
            }
        );

        // Counting the component amount on the ship
        playerList.get(1).getShip().traverse(
            (Component c) -> {
                expectedShipP2ComponentCount.getAndIncrement();
            }
        );



        // ======== Meteor 1 of 9 (Small, Bottom) ========
        // Player 1 --> Turns on the shield, but it's not oriented correctly, therefore still loses 1 energy
        //              Also gives a wrong pair of coordinates, these will not be of a shield and thus energy shouldn't be consumed
        // Player 2 --> Does nothing
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 1 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 1 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
//        assertNull(meteorShowerStateJSON.getPreviousPlayerRemovedComponents().getKey());
//        assertNull(meteorShowerStateJSON.getPreviousPlayerRemovedComponents().getValue());

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

//        System.out.println("\n\t ======== STATE - Meteor 1 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        //shieldsCoordinates.add(new Pair<>(6, 4));   // Shield, 1 energy should be consumed
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // Shield, 1 energy should be consumed
        shieldsCoordinates.add(new ComponentHelper<>(6, 4));   // Shield, 1 energy should be consumed
        //shieldsCoordinates.add(new Pair<>(0, 0));   // Wrong component, no energy should be consumed
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(0, 0)));   // Wrong component, no energy should be consumed
        shieldsCoordinates.add(new ComponentHelper<>(0, 0));   // Wrong component, no energy should be consumed
        cannonsCoordinates.add(null);
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP1--;
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 1 aftermath --> Meteor hits and removes 1 component
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 1 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has a component removed
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 1 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 1 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
        //assertEquals("p1", meteorShowerStateJSON.getPreviousPlayerRemovedComponents().getKey());
        assertEquals(1, meteorShowerStateJSON.getRemovedComponents().get("p1").size());

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields selected
        cannonsCoordinates.add(null);   // P2 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 1 aftermath --> Meteor hits and removes 1 component
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 1 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
            (Component c) -> {
                actualShipP2ComponentCount.getAndIncrement();
            }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());



        // ======== Meteor 2 of 9 (Small, Left) ========
        // Player 1 --> Does nothing
        // Player 2 --> Does nothing
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 2 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 2 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
//        assertEquals("p2", meteorShowerStateJSON.getPreviousPlayerRemovedComponents().getKey());
        assertEquals(1, meteorShowerStateJSON.getRemovedComponents().get("p2").size());

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

//        System.out.println("\n\t ======== STATE - Meteor 2 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        shieldsCoordinates.add(null);   // P1 no shields selected
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 2 aftermath --> Meteor hits and removes 1 component
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 2 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has a component removed
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 2 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 2 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
//        assertEquals("p1", meteorShowerStateJSON.getPreviousPlayerRemovedComponents().getKey());
        assertEquals(1, meteorShowerStateJSON.getRemovedComponents().get("p1").size());

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields selected
        cannonsCoordinates.add(null);   // P2 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 2 aftermath --> Meteor hits and takes away 1 component
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 2 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());



        // ======== Meteor 3 of 9 (Small, Top) ========
        // Player 1 --> Activates shield, which should protect him from damage since it's oriented correctly
        // Player 2 --> Does nothing, but gives 3 wrong components in the cannonList (nothing should happen)
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 3 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 3 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
//        assertEquals("p2", meteorShowerStateJSON.getPreviousPlayerRemovedComponents().getKey());
        assertEquals(1, meteorShowerStateJSON.getRemovedComponents().get("p2").size());

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

//        System.out.println("\n\t ======== STATE - Meteor 3 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        //shieldsCoordinates.add(new Pair<>(6, 4));   // P1 activates shield
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // P1 activates shield
        shieldsCoordinates.add(new ComponentHelper<Integer>(6, 4));   // P1 activates shield
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP1--;
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 3 aftermath --> Meteor is blocked by shield, 1 energy removed
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 3 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has a component removed
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 3 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 3 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
//        assertEquals("p1", meteorShowerStateJSON.getPreviousPlayerRemovedComponents().getKey());
        assertNull(meteorShowerStateJSON.getPreviousPlayerRemovedComponents());

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields selected
        //cannonsCoordinates.add(new Pair<>(6, 4));   // P2 chooses 3 non-cannon components, equivalent to doing nothing
//        cannonsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // P2 chooses 3 non-cannon components, equivalent to doing nothing
        cannonsCoordinates.add(new ComponentHelper<>(6, 4));   // P2 chooses 3 non-cannon components, equivalent to doing nothing
        //cannonsCoordinates.add(new Pair<>(0, 0));
//        cannonsCoordinates.add(new ArrayList<>(Arrays.asList(0, 0)));
        cannonsCoordinates.add(new ComponentHelper<>(0, 0));
        //cannonsCoordinates.add(new Pair<>(6, 6));
//        cannonsCoordinates.add(new ArrayList<>(Arrays.asList(6, 6)));
        cannonsCoordinates.add(new ComponentHelper<>(6, 6));
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 3 aftermath --> Meteor hits and takes away 2 components
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 3 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());



        // ======== Meteor 4 of 9 (Small, Right) ========
        // Player 1 --> Use shield again
        // Player 2 --> Use shield, plus activates for no reason a single cannon and a double cannon, wasting 1 energy
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 4 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 4 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

//        System.out.println("\n\t ======== STATE - Meteor 4 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        //shieldsCoordinates.add(new Pair<>(6, 4));   // P1 activates the shield
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // P1 activates the shield
        shieldsCoordinates.add(new ComponentHelper<>(6, 4));   // P1 activates the shield
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP1--;
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 4 aftermath --> Meteor is reflected by the shield
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 4 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has a component removed
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 4 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 4 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        //shieldsCoordinates.add(new Pair<>(6, 4));   // P2 activates the shield
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // P2 activates the shield
        shieldsCoordinates.add(new ComponentHelper<>(6, 4));   // P2 activates the shield
        //cannonsCoordinates.add(new Pair<>(5, 7));   // P2 activates a single and a double cannon for no reason, thus wasting 1 energy
//        cannonsCoordinates.add(new ArrayList<>(Arrays.asList(5, 7)));   // P2 activates a single and a double cannon for no reason, thus wasting 1 energy
        cannonsCoordinates.add(new ComponentHelper<>(5, 7));   // P2 activates a single and a double cannon for no reason, thus wasting 1 energy
        //cannonsCoordinates.add(new Pair<>(6, 8));
//        cannonsCoordinates.add(new ArrayList<>(Arrays.asList(6, 8)));
        cannonsCoordinates.add(new ComponentHelper<>(6, 8));
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP2--;
            energyP2--;
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 4 aftermath --> Meteor is reflected by the shield
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 4 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());



        // ======== Meteor 5 of 9 (Big, Bottom) ========
        // Player 1 --> Does nothing
        // Player 2 --> Does nothing
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 5 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 5 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

//        System.out.println("\n\t ======== STATE - Meteor 5 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        shieldsCoordinates.add(null);   // P1 no shields selected
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 5 aftermath --> Meteor hits and removes 1 component
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 5 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has a component removed
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 5 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 5 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields activated
        cannonsCoordinates.add(null);   // P2 no cannons activated
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 5 aftermath --> Meteor hits and removes a branch of 5 component
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 5 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        expectedShipP2ComponentCount.getAndDecrement();
        expectedShipP2ComponentCount.getAndDecrement();
        expectedShipP2ComponentCount.getAndDecrement();
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());



        // ======== Meteor 6 of 9 (Big, Left) ========
        // Player 1 --> Does nothing
        // Player 2 --> Does nothing
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 6 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 6 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

//        System.out.println("\n\t ======== STATE - Meteor 6 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        shieldsCoordinates.add(null);   // P1 no shields selected
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 6 aftermath --> Meteor hits and removes 1 component
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 6 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has a component removed
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 6 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 6 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields activated
        cannonsCoordinates.add(null);   // P2 no cannons activated
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 6 aftermath --> Meteor hits the ship and removes 1 component
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 6 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());



        // ======== Meteor 7 of 9 (Big, Top) ========
        // Player 1 --> Activates shield, meteor should still pass through and destroy something
        // Player 2 --> Activates shield, meteor should still pass through and destroy something
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 7 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 7 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(8);

//        System.out.println("\n\t ======== STATE - Meteor 7 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        //shieldsCoordinates.add(new Pair<>(6, 4));   // P1 activates the shield
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // P1 activates the shield
        shieldsCoordinates.add(new ComponentHelper<>(6, 4));   // P1 activates the shield
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (energyP1 > 0 && !playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP1--;
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 7 aftermath --> Shield doesn't stop big meteor; a component is destroyed
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 7 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has a component removed
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 7 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 7 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        //shieldsCoordinates.add(new Pair<>(6, 4));   // P2 activates the shield
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // P2 activates the shield
        shieldsCoordinates.add(new ComponentHelper<>(6, 4));   // P2 activates the shield
        cannonsCoordinates.add(null);   // P2 no cannons activated
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (energyP2 > 0 && !playerList.get(1).getShip().getBatteryList().isEmpty() && playerList.get(1).getShip().getAvailableEnergy() > 0) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP2--;
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 7 aftermath --> Shield doesn't stop big meteor; a component is destroyed
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 7 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());


        // ======== Meteor 8 of 9 (Big, Top) ========
        // Player 1 --> Activates a single cannon to destroy the big meteor
        // Player 2 --> Activates a double cannon to destroy the big meteor
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 8 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 8 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(9);

//        System.out.println("\n\t ======== STATE - Meteor 8 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        shieldsCoordinates.add(null);   // P1 no shield activated
        //cannonsCoordinates.add(new Pair<>(7, 9));   // P1 shoots at meteor with single cannon
//        cannonsCoordinates.add(new ArrayList<>(Arrays.asList(7, 9)));   // P1 shoots at meteor with single cannon
        cannonsCoordinates.add(new ComponentHelper<>(7, 9));   // P1 shoots at meteor with single cannon
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (energyP1 > 0 && !playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 8 aftermath --> Single cannon shoots but is not on the same column as the meteor, therefore 1 component is destroyed
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 8 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has the same components as before
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 8 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 8 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(9);

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shield activated
        //cannonsCoordinates.add(new Pair<>(6, 8));   // P2 shoots at meteor with double cannon, but it gets destroyed due to no energy left
//        cannonsCoordinates.add(new ArrayList<>(Arrays.asList(6, 8)));   // P2 shoots at meteor with double cannon, but it gets destroyed due to no energy left
        cannonsCoordinates.add(new ComponentHelper<>(6, 8));   // P2 shoots at meteor with double cannon, but it gets destroyed due to no energy left
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (energyP2 > 0 && !playerList.get(1).getShip().getBatteryList().isEmpty() && playerList.get(1).getShip().getAvailableEnergy() > 0) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP2--;
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 8 aftermath --> No energy (the battery was destroyed), the double cannon is destroyed
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 8 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());



        // ======== Meteor 9 of 9 (Big, Right) ========
        // Player 1 --> Does nothing
        // Player 2 --> Does nothing
        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 9 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 9 - Player 1 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

//        System.out.println("\n\t ======== STATE - Meteor 9 ========");
//        System.out.println("\t\t currMeteorIndex = " + meteorShowerStateJSON.getCurrMeteorIndex());
//        System.out.println("\t\t diceThrowResult = " + meteorShowerStateJSON.getDiceThrowResult());
//        System.out.println("\t\t currMeteorDescriptor = [" + meteorShowerStateJSON.getCurrMeteorDescriptor().getKey() + ", " + meteorShowerStateJSON.getCurrMeteorDescriptor().getValue() + "]");

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        shieldsCoordinates.add(null);   // P1 no shields selected
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (energyP1 > 0 && !playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 9 aftermath --> Meteor misses
//        System.out.println("\n\t ==== SHIP Player1 after Meteor 9 ====");
//        printShipGrid(playerList.get(0).getShip());

        // Checking that Player1's ship has the same component amount
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 9 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();
        // ======== WIDGET TESTING ======== //
        System.out.println("Meteor 9 - Player 2 card state");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields activated
        cannonsCoordinates.add(null);   // P2 no cannons activated
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertTrue(meteorShower.hasFinished()); // Finally the last state is generated and the card is completed
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (energyP2 > 0 && !playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 9 aftermath --> Meteor misses
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 9 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has the same component amount
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
            (Component c) -> {
                actualShipP2ComponentCount.getAndIncrement();
            }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());

        // ======== WIDGET TESTING ======== //
        System.out.println("Last State");
        clientMeteorShower .updateCard(meteorShowerStateJSON);
        clientMeteorShower.generateWidget().printWidget();
        // ================================ //
    }

    @Test
    void useCard_threePlayersButOneIsDisconnected() {

        // Initializing the internal player list
        this.meteorShower.initCardPlayers();

        MeteorShowerJSON meteorShowerJSON;
        CardStateJSON meteorShowerStateJSON;
        List<Player> playerList;
        List<ComponentHelper<Integer>> shieldsCoordinates;
        List<ComponentHelper<Integer>> cannonsCoordinates;

        Player player3 = new Player("p3", PlayerColor.RED, 2);
        this.board.newPlayer(player3);
        this.board.addPlayerToBoard(player3);

        playerList = this.board.getPlayers();

        initCustomShip(this.board.getPlayers().get(2));

        int energyP1 = playerList.get(0).getShip().getAvailableEnergy();
        int energyP2 = playerList.get(1).getShip().getAvailableEnergy();
        int energyP3 = playerList.get(2).getShip().getAvailableEnergy();
        int currMeteorIndex = 0;

        final AtomicInteger expectedShipP1ComponentCount = new AtomicInteger(0);
        final AtomicInteger expectedShipP2ComponentCount = new AtomicInteger(0);
        final AtomicInteger expectedShipP3ComponentCount = new AtomicInteger(0);
        final AtomicInteger actualShipP1ComponentCount = new AtomicInteger(0);
        final AtomicInteger actualShipP2ComponentCount = new AtomicInteger(0);
        final AtomicInteger actualShipP3ComponentCount = new AtomicInteger(0);

        // Counting the component amount on P1's ship
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    expectedShipP1ComponentCount.getAndIncrement();
                }
        );

        // Counting the component amount on P2's ship
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    expectedShipP2ComponentCount.getAndIncrement();
                }
        );

        // Counting the component amount on P3's ship
        playerList.get(2).getShip().traverse(
            (Component c) -> {
                expectedShipP3ComponentCount.getAndIncrement();
            }
        );

        this.meteorShower.initCardPlayers();

        // ======== Meteor 1 of 9 (Small, Bottom) ========
        // Player 1 --> Turns on the shield, but it's not oriented correctly, therefore still loses 1 energy
        //              Also gives a wrong pair of coordinates, these will not be of a shield and thus energy shouldn't be consumed
        // Player 2 --> Does nothing
        // Player 3 --> Does nothing

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 1 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        //shieldsCoordinates.add(new Pair<>(6, 4));   // Shield, 1 energy should be consumed
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(6, 4)));   // Shield, 1 energy should be consumed
        shieldsCoordinates.add(new ComponentHelper<>(6, 4));   // Shield, 1 energy should be consumed
        //shieldsCoordinates.add(new Pair<>(0, 0));   // Wrong component, no energy should be consumed
//        shieldsCoordinates.add(new ArrayList<>(Arrays.asList(0, 0)));   // Wrong component, no energy should be consumed
        shieldsCoordinates.add(new ComponentHelper<>(0, 0));   // Wrong component, no energy should be consumed
        cannonsCoordinates.add(null);
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            energyP1--;
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 1 aftermath --> Meteor hits and removes 1 component

        // Checking that Player1's ship has a component removed
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 1 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields selected
        cannonsCoordinates.add(null);   // P2 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 1 aftermath --> Meteor hits and removes 1 component
//        System.out.println("\n\t ==== SHIP Player2 after Meteor 1 ====");
//        printShipGrid(playerList.get(1).getShip());

        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 1 - Player 3 card state
        meteorShowerStateJSON = meteorShower.generateState();

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

        // Player 3 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
//        shieldsCoordinates.add(null)      // P3 does nothing
//        cannonsCoordinates.add(null);     // P3 does nothing
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(2).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 3
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 3 energy check
        if (!playerList.get(2).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP3, playerList.get(2).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP3 > 0) { energyP3 = 0; }
            assertEquals(energyP3, playerList.get(2).getShip().getAvailableEnergy());
        }

        // Player3 - Meteor 1 aftermath --> Meteor hits and removes 1 component
        // Checking that Player3's ship has a component removed
        expectedShipP3ComponentCount.getAndDecrement();
        actualShipP3ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
            (Component c) -> {
                actualShipP3ComponentCount.getAndIncrement();
            }
        );
        assertEquals(expectedShipP3ComponentCount.get(), actualShipP3ComponentCount.get());



        // Simulating P3 disconnecting before meteor sequence
        this.board.getPlayers().get(2).setConnected(false);

        // Now we'll test that the meteor sequence will iterate only upon the currently connected players
        // (which are P1 and P2 since P3 was disconnected above)



        // ======== Meteor 2 of 9 (Small, Left) ========
        // Player 1 --> Does nothing
        // Player 2 --> Does nothing
        // Player 3 --> Gets disconnected before his turn, therefore no meteors should hit him

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 2 - Player 1 card state
        meteorShowerStateJSON = meteorShower.generateState();

        // Altering the seed result to try to shoot a big meteor
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

        // Player 1 response
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        shieldsCoordinates.add(null);   // P1 no shields selected
        cannonsCoordinates.add(null);   // P1 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(0).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 1
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 1 energy check
        if (!playerList.get(0).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP1 > 0) { energyP1 = 0; }
            assertEquals(energyP1, playerList.get(0).getShip().getAvailableEnergy());
        }

        // Player1 - Meteor 2 aftermath --> Meteor hits and removes 1 component
        // Checking that Player1's ship has a component removed
        expectedShipP1ComponentCount.getAndDecrement();
        actualShipP1ComponentCount.set(0);
        playerList.get(0).getShip().traverse(
                (Component c) -> {
                    actualShipP1ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP1ComponentCount.get(), actualShipP1ComponentCount.get());

        // Meteor 2 - Player 2 card state
        meteorShowerStateJSON = meteorShower.generateState();

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Player 2 response
        shieldsCoordinates.add(null);   // P2 no shields selected
        cannonsCoordinates.add(null);   // P2 no cannons selected
        meteorShowerJSON = new MeteorShowerJSON(
                playerList.get(1).getNickname(),
                meteorShowerStateJSON.getCurrMeteorIndex(),
                meteorShowerStateJSON.getDiceThrowResult(),
                shieldsCoordinates,
                cannonsCoordinates
        );

        // Using card on player 2
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        meteorShower.useCard(meteorShowerJSON);
        assertFalse(meteorShower.hasFinished());
        currMeteorIndex = meteorShowerStateJSON.getCurrMeteorIndex();
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());

        // Player 2 energy check
        if (!playerList.get(1).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP2 > 0) { energyP2 = 0; }
            assertEquals(energyP2, playerList.get(1).getShip().getAvailableEnergy());
        }

        // Player2 - Meteor 2 aftermath --> Meteor hits and takes away 1 component
        // Checking that Player2's ship has a component removed
        expectedShipP2ComponentCount.getAndDecrement();
        actualShipP2ComponentCount.set(0);
        playerList.get(1).getShip().traverse(
                (Component c) -> {
                    actualShipP2ComponentCount.getAndIncrement();
                }
        );
        assertEquals(expectedShipP2ComponentCount.get(), actualShipP2ComponentCount.get());

        shieldsCoordinates = new ArrayList<>();
        cannonsCoordinates = new ArrayList<>();

        // Meteor 1 - Player 3 card state
        meteorShowerStateJSON = meteorShower.generateState();

        // Altering the seed result to try to shoot a big meteor\
        // coming from the top with a single cannon
        meteorShowerStateJSON.setDiceThrowResult(6);

        // Since P3 is disconnected, the meteorIndex should go to the next meteor
        currMeteorIndex++;
        assertEquals(currMeteorIndex, meteorShowerStateJSON.getCurrMeteorIndex());
        assertFalse(meteorShower.hasFinished());

        // Player 3 energy check
        if (!playerList.get(2).getShip().getBatteryList().isEmpty()) {
            // If the ship still has batteries, then evaluate the energy amount
            assertEquals(energyP3, playerList.get(2).getShip().getAvailableEnergy());
        }
        else {
            // Else the ship should have 0 energy
            if (energyP3 > 0) { energyP3 = 0; }
            assertEquals(energyP3, playerList.get(2).getShip().getAvailableEnergy());
        }

        // Player3 - Meteor 1 aftermath --> Meteor hits and removes 1 component
        // BUT since P3 is disconnected, the meteor is skipped and thus no components should be removed

        // Checking that Player3's ship has the same components as before (because he got skipped due to being disconnected)
        actualShipP3ComponentCount.set(0);
        playerList.get(2).getShip().traverse(
            (Component c) -> {
                actualShipP3ComponentCount.getAndIncrement();
            }
        );
        assertEquals(expectedShipP3ComponentCount.get(), actualShipP3ComponentCount.get());
    }
}
