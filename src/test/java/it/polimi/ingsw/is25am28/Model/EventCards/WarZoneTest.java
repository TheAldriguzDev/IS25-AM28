package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientWarZone;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Connector;
import it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType.*;
import static org.junit.jupiter.api.Assertions.*;

class WarZoneTest {
    Board board;
    Player p1;
    Player p2;
    Player p3;
    Player p4;
    Ship ship_1;
    Ship ship_2;
    Ship ship_3;
    Ship ship_4;

    // All THREE_PIPES connectors
    List<Integer> connectorsThree = new ArrayList<Integer>();

    List<ComponentHelper<Integer>> doubleCannons_1 = new ArrayList<>();
    List<ComponentHelper<Integer>> doubleCannons_2 = new ArrayList<>();
    List<ComponentHelper<Integer>> doubleCannons_3 = new ArrayList<>();
    List<ComponentHelper<Integer>> doubleCannons_4 = new ArrayList<>();

    List<ComponentHelper<LifeformType>> lifeformsToRemove_empty = new ArrayList<>();
    List<ComponentHelper<LifeformType>> lifeformsToRemove_4;
    List<ComponentHelper<LifeformType>> lifeformsToRemove_3;
    List<ComponentHelper<LifeformType>> lifeformsToRemove_2;
    List<ComponentHelper<LifeformType>> lifeformsToRemove_1;

    List<ComponentHelper<Integer>> getShieldsToActivate_empty = new ArrayList<>();
    List<ComponentHelper<Integer>> shieldsToActivate1;
    List<ComponentHelper<Integer>> shieldsToActivate2;

    List<ComponentHelper<Integer>> doubleCannons_empty = new ArrayList<>();

    ResourceBank resourceBank;

    WarZone warzone;

    @BeforeEach
    void init() {
        // All THREE_PIPES connectors
        connectorsThree.add(Connector.THREE_PIPES.ordinal());
        connectorsThree.add(Connector.THREE_PIPES.ordinal());
        connectorsThree.add(Connector.THREE_PIPES.ordinal());
        connectorsThree.add(Connector.THREE_PIPES.ordinal());

        board = new BoardLevel2();
        board.buildBoard();

        List<Player> players = new ArrayList<Player>();

        players.add(new Player("Player 1", PlayerColor.RED, 2));
        players.add(new Player("Player 2", PlayerColor.BLUE, 2));
        players.add(new Player("Player 3", PlayerColor.GREEN, 2));
        players.add(new Player("Player 4", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            this.board.newPlayer(player);
        }

        players = board.getPlayers();
        p1 = players.get(0);
        p2 = players.get(1);
        p3 = players.get(2);
        p4 = players.get(3);

        ship_1 = p1.getShip();
        ship_init1(ship_1);
        ship_2 = p2.getShip();
        ship_init2(ship_2);
        ship_3 = p3.getShip();
        ship_init3(ship_3);
        ship_4 = p4.getShip();
        ship_init4(ship_4);

        resourceBank = new ResourceBank();

        board.addPlayerToBoard(p1);
        board.addPlayerToBoard(p2);
        board.addPlayerToBoard(p3);
        board.addPlayerToBoard(p4);
    }

    /*In this test:
    * The lowest firepower player should be eliminated by a plasmaShot (ship 1)
    * The lowest enginePower player should be eliminated due to the absence of humans onboard (ship 4)
    * The lowest crewCount player should go back 3 positions (+ jumps over other players) (ship 2)
    * the third player (ship 3) should not be affected*/
    @Test
    public void test_against_WarZoneCard_1() {
        // WarZone card 1 initialization
        List<PlasmaShot> shootingSequence = new ArrayList<>();
        shootingSequence.add(new PlasmaShot(1, 2)); // dal basso, piccolo
        shootingSequence.add(new PlasmaShot(2, 2)); // dal basso, grande

        List<WarZoneActionConsequencePair> consequencePairs = new ArrayList<>();
        consequencePairs.add(new WarZoneActionConsequencePair(WarZoneAction.fromInteger(2), WarZoneConsequence.fromInteger(1))); // LowestCrewCount -> movementSteps
        consequencePairs.add(new WarZoneActionConsequencePair(WarZoneAction.fromInteger(1), WarZoneConsequence.fromInteger(0))); // LowestEnginePower -> removeCrew
        consequencePairs.add(new WarZoneActionConsequencePair(WarZoneAction.fromInteger(0), WarZoneConsequence.fromInteger(2))); // LowestFirepower -> PlasmaShots

        warzone = new WarZone("WarZone", 2, board, resourceBank, 3, 2, 0, shootingSequence, consequencePairs);
        // end of WarZone Card 1 Initialization

        // Used energy is the energy use by the double engines

        ClientWarZone clientWarZone;

        // ======== WIDGET TESTING ======== //
        System.out.println("Before initialization");
        clientWarZone = new ClientWarZone(warzone.generateState());
        clientWarZone.generateWidget().printWidget();
        // ================================ //

        warzone.initCardPlayers();

        // ======== WIDGET TESTING ======== //
        System.out.println("Movement steps will be applied after leader ack");
        clientWarZone.updateCard(warzone.generateState());
        clientWarZone.generateWidget().printWidget();
        // ================================ //

        ActionJSON actionJSON;

        ArrayList<Player> eliminatedPlayers = new ArrayList<>();
        eliminatedPlayers.add(p4);
        eliminatedPlayers.add(p1);


        //Lowest crew member action
        // actionJson del player 1
            actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());
            assertEquals(-2, p2.getCursor());

            // ======== WIDGET TESTING ======== //
            System.out.println("Engine power input p1");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

        // Player order: 1, 3, 4, 2

        //Lowest enginePower action : input

            // actionJson del player 1
            actionJSON = new WarZoneJSON("Player 1", 1, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Engine power input p3");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

            // actionJSON del player 3
            actionJSON = new WarZoneJSON("Player 3", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Engine power input p2");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

            // actionJSON del player 4

            actionJSON = new WarZoneJSON("Player 4", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Engine power input p4");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

            // actionJSON del player 2
            actionJSON = new WarZoneJSON("Player 2", 1, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Engine power consequence p4");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

            // Player 4 has to send lifeform to remove
            actionJSON = new WarZoneJSON("Player 4", 0, lifeformsToRemove_4, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Firepower input p1");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

            assertEquals(1, board.getEliminatedPlayers().size());
            assertEquals(eliminatedPlayers.get(0), board.getEliminatedPlayers().get(0));

        //Lowest Firepower action

            // actionJson del player 1
            actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_1);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Firepower input p3");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

            // actionJSON del player 3
            actionJSON = new WarZoneJSON("Player 3", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_3);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Firepower input p2");
            clientWarZone.updateCard(warzone.generateState());
            clientWarZone.generateWidget().printWidget();
            // ================================ //

            // actionJSON del player 2
            actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_2);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

                // ======== WIDGET TESTING ======== //
                System.out.println("Shields input p1");
                clientWarZone.updateCard(warzone.generateState());
                clientWarZone.generateWidget().printWidget();
                // ================================ //

                // Player 1 has to send shields to activate for each plasmashot
                // Player 1 will block the first small plasmashot from behing, the second one, headet to the ship's core, will eliminate him
                warzone.forceDiceThrow(6); // on column 6 // will be blocked by the shield
                actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), shieldsToActivate1, doubleCannons_empty);
                warzone.useCard(actionJSON);
                assertFalse(warzone.hasFinished());

                // ======== WIDGET TESTING ======== //
                System.out.println("Shields input p1");
                clientWarZone.updateCard(warzone.generateState());
                clientWarZone.generateWidget().printWidget();
                // ================================ //

                assertEquals(1, board.getEliminatedPlayers().size()); // player 1 not eliminated
                //assertEquals(); for the batteries // Batteries consumed by shields already tested in the other test

                warzone.forceDiceThrow(6);
                actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
                warzone.useCard(actionJSON);
                assertTrue(warzone.hasFinished());

                // ======== WIDGET TESTING ======== //
                System.out.println("Last State");
                clientWarZone.updateCard(warzone.generateState());
                clientWarZone.generateWidget().printWidget();
                // ================================ //

        assertEquals(2, board.getEliminatedPlayers().size());
        assertEquals(eliminatedPlayers.get(1), board.getEliminatedPlayers().get(1));
    }

    /*In this test:
    * The lowest firepower player should go back 4 positions (+ jumps over other players) (ship 1)
    * The lowest enginePower player should lose 3 items (starting from the most valuable ones) (ship 4)
    * The lowest crewCount player should be exposed to 4 plasmaShots (ship 2)*/
    @Test
    public void test_against_WarZoneCard_2() {
        // WarZone card 2 initialization
        List<PlasmaShot> shootingSequence = new ArrayList<>();
        shootingSequence.add(new PlasmaShot(1, 0)); // dall'alto, piccolo
        shootingSequence.add(new PlasmaShot(1, 1)); // da destra, piccolo
        shootingSequence.add(new PlasmaShot(2, 2)); // dal basso, grande
        shootingSequence.add(new PlasmaShot(1, 3)); // da sinistra, piccolo

        List<WarZoneActionConsequencePair> consequencePairs = new ArrayList<>();
        consequencePairs.add(new WarZoneActionConsequencePair(WarZoneAction.fromInteger(0), WarZoneConsequence.fromInteger(1))); // firepower -> movementSteps
        consequencePairs.add(new WarZoneActionConsequencePair(WarZoneAction.fromInteger(1), WarZoneConsequence.fromInteger(3))); // enginePower -> items
        consequencePairs.add(new WarZoneActionConsequencePair(WarZoneAction.fromInteger(2), WarZoneConsequence.fromInteger(2))); // crewCount -> shootingSequence

        warzone = new WarZone("WarZone", 2, board, resourceBank, 4, 0, 3, shootingSequence, consequencePairs);
        // end of WarZone card 2 initialization

        warzone.initCardPlayers();

        // Used energy is the energy use by the double engines

        ActionJSON actionJSON;

        // Lowest firepower action // the consequence (movementSteps) will be applied instantly once all 4 players have sent their data
        // player 1
        actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

        // player 2
        actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_2);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

        // player 3
        actionJSON = new WarZoneJSON("Player 3", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

        // player 4
        actionJSON = new WarZoneJSON("Player 4", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_4);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

        assertEquals(-1, p1.getCursor());
        assertEquals(3, p2.getCursor());
        assertEquals(1, p3.getCursor());
        assertEquals(0, p4.getCursor());

        assertEquals(3, p1.getShip().getAvailableEnergy()); // did not use any batteries | BASELINE: 3
        assertEquals(5, p2.getShip().getAvailableEnergy()); // did use 1 battery for the doubleCannon | BASELINE: 6
        assertEquals(0, p3.getShip().getAvailableEnergy()); // did not use any batteries | BASELINE: 0
        assertEquals(2, p4.getShip().getAvailableEnergy()); // did use 1 battery for the doubleCannon | BASELINE: 3

        // player order should now be 2,3,4,1, no need to verify that since the card will automatically throw an error on a wrong nickname if the order of the data sent in the next paragraph is wrong

        // Lowest enginePower
        // player 2
        actionJSON = new WarZoneJSON("Player 2", 1, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

        // player 3
        actionJSON = new WarZoneJSON("Player 3", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

        // player 3
        actionJSON = new WarZoneJSON("Player 4", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

        // player 4
        actionJSON = new WarZoneJSON("Player 1", 1, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

            // now the player 4 need to send the data regarding what items he wants to drop
            // player 4
            List<ComponentHelper<ItemColor>> itemsToBeRemoved = new ArrayList<>();
            itemsToBeRemoved.add(new ComponentHelper<ItemColor>(5, 5).addItem(ItemColor.RED));
            itemsToBeRemoved.add(new ComponentHelper<ItemColor>(7, 4).addItem(ItemColor.YELLOW));
            itemsToBeRemoved.add(new ComponentHelper<ItemColor>(7, 4).addItem(ItemColor.BLUE));

            actionJSON = new WarZoneJSON("Player 4", 0, lifeformsToRemove_empty, itemsToBeRemoved, new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // Verifying storage changes
            assertEquals(0, p4.getShip().getStorageList().get(0).getStoredItems().size()); // Special storage is now empty
            assertEquals(1, p4.getShip().getStorageList().get(1).getStoredItems().size()); // Normal storage should have only 1 item left
            assertEquals(ItemColor.GREEN, p4.getShip().getStorageList().get(1).getStoredItems().getFirst().getColor()); // That item should be a blue item

            // Verifying changes to the battery counts
            assertEquals(2, p1.getShip().getAvailableEnergy()); // did use 1 battery for the doubleBooster | BASELINE: 3
            assertEquals(4, p2.getShip().getAvailableEnergy()); // did use 1 battery for the doubleBooster | BASELINE: 5
            assertEquals(0, p3.getShip().getAvailableEnergy()); // did not use any batteries | BASELINE: 0
            assertEquals(2, p4.getShip().getAvailableEnergy()); // did not use any batteries | BASELINE: 2

        // Lowest crewCount
        // player 2 // Teh lowest crewCount is determined instantly, no need to send information
        actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
        warzone.useCard(actionJSON);
        assertFalse(warzone.hasFinished());

            // The lowest crewCount player (player 2) must send data about which shields to activate (4 times since there are 4 shots) // will not get destroyed
            // First shot
            warzone.forceDiceThrow(6); // aimed at the battery pack in 5, 6
            shieldsToActivate2 = new ArrayList<>();
            shieldsToActivate2.add(new ComponentHelper<>(7, 7)); // protection for top and right sides
            actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), shieldsToActivate2, doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());
            assertEquals(3, p2.getShip().getAvailableEnergy()); // did use one battery for 1 shield

            // Second shot
            warzone.forceDiceThrow(6); // aimed at the purple vital in 6, 8
            shieldsToActivate2 = new ArrayList<>();
            shieldsToActivate2.add(new ComponentHelper<>(7, 7)); // protection for top and right sides
            actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), shieldsToActivate2, doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());
            assertEquals(2, p2.getShip().getAvailableEnergy()); // did use one battery for 1 shield

            // Third shot
            warzone.forceDiceThrow(4); // aimed at the doubleCannon in 6, 4
            shieldsToActivate2 = new ArrayList<>();
            shieldsToActivate2.add(new ComponentHelper<>(7, 5)); // protection for bottom and left sides (in this case it will not work as the shot is big)
            actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), shieldsToActivate2, doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());
            assertEquals(1, p2.getShip().getAvailableEnergy()); // did use one battery for 1 shield

            // Fourth shot
            warzone.forceDiceThrow(7); // aimed at the shield in 7, 5
            shieldsToActivate2 = new ArrayList<>();
            shieldsToActivate2.add(new ComponentHelper<>(7, 5)); // protection for top and right sides
            actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), shieldsToActivate2, doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertTrue(warzone.hasFinished());
            assertEquals(0, p2.getShip().getAvailableEnergy()); // did use one battery for 1 shield

            assertNotNull(p2.getShip().getComponent(5, 6));
            assertNotNull(p2.getShip().getComponent(6, 8));
            assertNull(p2.getShip().getComponent(6, 4));
            assertNotNull(p2.getShip().getComponent(7, 5));
    }


    /*Assuming batteries are being used:
    * Firepower = 1
    * EnginePower = 2 (2 from doubleBoosters)
    * Batteries = 3
    * Crew = 6
    *
    * This is the ship with the lowest firepower*/
    public void ship_init1(Ship ship) {
        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(1);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors4 = new ArrayList<Integer>();
        connectors4.add(0);
        connectors4.add(1);
        connectors4.add(0);
        connectors4.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        List<Integer> connectors6 = new ArrayList<Integer>();
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);

        List<Integer> connectors7 = new ArrayList<Integer>();
        connectors7.add(0);
        connectors7.add(1);
        connectors7.add(1);
        connectors7.add(1);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        Cabin cabin_1 = new Cabin(connectors7, false);
//        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        Cabin cabin_2 = new Cabin(connectors5, false);
//        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        Engine double_booster = new Engine(connectors8, 2);

        Battery battery = new Battery(connectors7, 3);

        Shield shield = new Shield(connectors5);
        shield.rotateRight(); // Covers right and lower side

        Cannon single_cannon = new Cannon(connectors4, 1);

        ship.addComponent(cabin_1, 6, 7);
        ship.addComponent(cabin_2, 6, 8);
        ship.addComponent(double_booster, 7, 7);
        ship.addComponent(battery, 6, 5);
        ship.addComponent(shield, 7, 5);
        ship.addComponent(single_cannon, 6, 4);

        ship.addLifeformToCabin(6, 7, ASTRONAUT);
        ship.addLifeformToCabin(6, 7, ASTRONAUT);
        ship.addLifeformToCabin(6, 8, ASTRONAUT);
        ship.addLifeformToCabin(6, 8, ASTRONAUT);

        ship.generateComponentSubLists();

        lifeformsToRemove_1 = new ArrayList<>();
        lifeformsToRemove_1.add(new ComponentHelper<LifeformType>(6,7).addItem(ASTRONAUT));
        lifeformsToRemove_1.add(new ComponentHelper<LifeformType>(6,7).addItem(ASTRONAUT));

        shieldsToActivate1 = new ArrayList<>();
        shieldsToActivate1.add(new ComponentHelper<>(7, 5));
    }

    /*Assuming batteries are being used:
    * Firepower = 4 (2 from double cannon, 2 from purple alien)
    * EnginePower = 3 (2 from double booster, 1 from single booster)
    * Batteries = 6
    * Crew = 3 (2 humans, 1 purple alien)
    *
    * This is the ship with the lowest crew count*/
    public void ship_init2(Ship ship) {
        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(1);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors4 = new ArrayList<Integer>();
        connectors4.add(0);
        connectors4.add(1);
        connectors4.add(0);
        connectors4.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        List<Integer> connectors6 = new ArrayList<Integer>();
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);

        List<Integer> connectors7 = new ArrayList<Integer>();
        connectors7.add(0);
        connectors7.add(1);
        connectors7.add(1);
        connectors7.add(1);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        Cabin cabin_1 = new Cabin(connectors1, false);
        //cabin_1.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));

        Vital purple_vital = new Vital(connectors4, VitalType.PURPLE_VITAL.ordinal());

        Battery battery_1 = new Battery(connectors3, 3);

        Engine double_booster = new Engine(connectors8, 2);

        Shield shield_top_right = new Shield(connectors1);

        Shield shield_bottom_left = new Shield(connectors4);
        shield_bottom_left.rotateLeft();
        shield_bottom_left.rotateLeft();

        Engine single_booster = new Engine(connectors5, 1);

        Battery battery_2 = new Battery(connectors7, 3);

        Cannon double_cannon = new Cannon(connectors4, 2);

        ship.addComponent(cabin_1, 6, 7);
        ship.addComponent(purple_vital, 6, 8);
        ship.addComponent(battery_1, 5, 6);
        ship.addComponent(double_booster, 7, 6);
        ship.addComponent(shield_top_right, 7, 7);
        ship.addComponent(single_booster, 7, 8);
        ship.addComponent(battery_2, 6, 5);
        ship.addComponent(shield_bottom_left, 7, 5);
        ship.addComponent(double_cannon, 6, 4);

        ship.generateComponentSubLists();

        ship.addLifeformToCabin(6, 7, PURPLE_ALIEN);

        doubleCannons_2.add(new ComponentHelper<>(6, 4));

        lifeformsToRemove_2 = new ArrayList<>();
        lifeformsToRemove_2.add(new ComponentHelper<LifeformType>(6,6).addItem(ASTRONAUT));
        lifeformsToRemove_2.add(new ComponentHelper<LifeformType>(6,6).addItem(ASTRONAUT));

//        shieldsToActivate2 = new ArrayList<>();
//        shieldsToActivate2.add(new ComponentHelper<>(7, 7)); // top right shield
//        shieldsToActivate2.add(new ComponentHelper<>(7, 5)); // bottom left shield
    }


    /*Assuming batteries are being used:
    * Firepower = 1
    * EnginePower = 3 (1 from singleBooster, 2 from brown alien)
    * Batteries = 0
    * Crew = 3
    *
    * This ship will not be affected by the WarZone since its lowestCrewCount and lowestFirepower are also present in ships ahead of it*/
    public void ship_init3(Ship ship) {
        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(0);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors4 = new ArrayList<Integer>();
        connectors4.add(0);
        connectors4.add(1);
        connectors4.add(0);
        connectors4.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        List<Integer> connectors6 = new ArrayList<Integer>();
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);

        List<Integer> connectors7 = new ArrayList<Integer>();
        connectors7.add(0);
        connectors7.add(1);
        connectors7.add(1);
        connectors7.add(1);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        List<Integer> connectors9 = new ArrayList<Integer>();
        connectors9.add(1);
        connectors9.add(0);
        connectors9.add(0);
        connectors9.add(1);

        List<Integer> connectors10 = new ArrayList<Integer>();
        connectors10.add(0);
        connectors10.add(1);
        connectors10.add(1);
        connectors10.add(0);

        Cannon single_cannon = new Cannon(connectors3, 1);

        Engine single_booster = new Engine(connectors9, 1);

        Cabin cabin_1 = new Cabin(connectors10, false);
        //cabin_1.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        Vital brown_vital = new Vital(connectors2, VitalType.BROWN_VITAL.ordinal());

        ship.addComponent(single_cannon, 5, 6);
        ship.addComponent(single_booster, 7, 6);
        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(brown_vital, 7, 5);

        ship.generateComponentSubLists();

        ship.addLifeformToCabin(6, 5, BROWN_ALIEN);

        lifeformsToRemove_3 = new ArrayList<>();
        lifeformsToRemove_3.add(new ComponentHelper<LifeformType>(6,6).addItem(ASTRONAUT));
        lifeformsToRemove_3.add(new ComponentHelper<LifeformType>(6,6).addItem(ASTRONAUT));
    }

    /*Assuming batteries are being used:
    * Firepower = 4 (2 from double cannon, 2 from purple alien)
    * EnginePower = 1
    * Batteries = 3
    * Crew = 3 (2 humans, 1 purple alien)
    *
    * This is the ship with the lowest enginePower*/
    public void ship_init4(Ship ship) {
        List<Integer> connectors1 = new ArrayList<Integer>();
        connectors1.add(0);
        connectors1.add(1);
        connectors1.add(0);
        connectors1.add(1);

        List<Integer> connectors2 = new ArrayList<Integer>();
        connectors2.add(1);
        connectors2.add(1);
        connectors2.add(0);
        connectors2.add(1);

        List<Integer> connectors3 = new ArrayList<Integer>();
        connectors3.add(0);
        connectors3.add(0);
        connectors3.add(1);
        connectors3.add(0);

        List<Integer> connectors4 = new ArrayList<Integer>();
        connectors4.add(0);
        connectors4.add(1);
        connectors4.add(0);
        connectors4.add(0);

        List<Integer> connectors5 = new ArrayList<Integer>();
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(0);
        connectors5.add(1);

        List<Integer> connectors6 = new ArrayList<Integer>();
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);
        connectors6.add(1);

        List<Integer> connectors7 = new ArrayList<Integer>();
        connectors7.add(0);
        connectors7.add(1);
        connectors7.add(1);
        connectors7.add(1);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        List<Integer> connectors9 = new ArrayList<Integer>();
        connectors9.add(1);
        connectors9.add(0);
        connectors9.add(0);
        connectors9.add(1);

        List<Integer> connectors10 = new ArrayList<Integer>();
        connectors10.add(0);
        connectors10.add(1);
        connectors10.add(1);
        connectors10.add(0);

        Battery battery = new Battery(connectors1, 3);

        Cannon double_cannon = new Cannon(connectors5, 2);

        Engine single_booster = new Engine(connectors8, 1);

        Cabin cabin_1 = new Cabin(connectors2, false);
        //cabin_1.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));

        Vital purple_vital = new Vital(connectors10, VitalType.PURPLE_VITAL.ordinal());

        Storage special_storage = new Storage(connectors3, 2, true);
        special_storage.storeItem(new Item(ItemColor.RED));

        Storage normal_storage = new Storage(connectors8, 3, false);
        normal_storage.storeItem(new Item(ItemColor.YELLOW));
        normal_storage.storeItem(new Item(ItemColor.BLUE));
        normal_storage.storeItem(new Item(ItemColor.GREEN));

        ship.addComponent(battery, 6, 7);
        ship.addComponent(double_cannon, 6, 8);
        ship.addComponent(single_booster, 7, 6);
        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(special_storage, 5, 5);
        ship.addComponent(purple_vital, 6, 4);
        ship.addComponent(normal_storage, 7, 4);

        ship.generateComponentSubLists();

        ship.addLifeformToCabin(6, 5, PURPLE_ALIEN);

        doubleCannons_4.add(new ComponentHelper<>(6, 8));

        lifeformsToRemove_4 = new ArrayList<>();
        lifeformsToRemove_4.add(new ComponentHelper<LifeformType>(6,6).addItem(ASTRONAUT));
        lifeformsToRemove_4.add(new ComponentHelper<LifeformType>(6,6).addItem(ASTRONAUT));
    }

    // For debugging purposes
    public void numOfLifeForms(Player player) {
        AtomicInteger numOfLifeForms = new AtomicInteger(0);
        player.getShip().traverse(
                (Component c) -> {
                    if (c.getClass() == Cabin.class) {
                        for (Lifeform l : ((Cabin) c).getInhabitants()) {
                            numOfLifeForms.incrementAndGet();
                        }
                    };
                }
        );
        System.out.println("NumOfLifeForms of player: " + player.getNickname() + " : " + numOfLifeForms.get());
    }
}