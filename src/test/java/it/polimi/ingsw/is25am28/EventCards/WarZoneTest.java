package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.ShipJSON;
import it.polimi.ingsw.is25am28.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.EventCards.HazardEntities.PlasmaShot;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Ship.Ship;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.is25am28.Lifeform.LifeformType.BROWN_ALIEN;
import static it.polimi.ingsw.is25am28.Lifeform.LifeformType.PURPLE_ALIEN;
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

    List<ComponentHelper<Integer>> doubleCannons_empty = new ArrayList<>();

    ResourceBank resourceBank;

    WarZone warzone;

    @BeforeEach
    void init() {
        board = new BoardLevel2();
        board.buildBoard();

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);

        List<Player> players = board.getPlayers();
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

        warzone.initCardPlayers();

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

        // Player order: 1, 3, 4, 2

        //Lowest enginePower action : input

            // actionJson del player 1
            actionJSON = new WarZoneJSON("Player 1", 1, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // actionJSON del player 3
            actionJSON = new WarZoneJSON("Player 3", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // actionJSON del player 4

            actionJSON = new WarZoneJSON("Player 4", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // actionJSON del player 2
            actionJSON = new WarZoneJSON("Player 2", 1, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_empty);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

                // Player 4 has to send lifeform to remove
                actionJSON = new WarZoneJSON("Player 4", 0, lifeformsToRemove_4, new ArrayList<>(), new ArrayList<>(), doubleCannons_1);
                warzone.useCard(actionJSON);
                assertFalse(warzone.hasFinished());

                assertEquals(1, board.getEliminatedPlayers().size());
                assertEquals(eliminatedPlayers.get(0), board.getEliminatedPlayers().get(0));

        //Lowest Firepower action
            // actionJson del player 1
            actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_1);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // actionJSON del player 3
            actionJSON = new WarZoneJSON("Player 3", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_3);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

            // actionJSON del player 2
            actionJSON = new WarZoneJSON("Player 2", 0, lifeformsToRemove_empty, new ArrayList<>(), new ArrayList<>(), doubleCannons_2);
            warzone.useCard(actionJSON);
            assertFalse(warzone.hasFinished());

                // Player 1 has to send shields to activate for each plasmashot
                // Player 1 will block the first small plasmashot from behing, the second one, headet to the ship's core, will eliminate him

                actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), shieldsToActivate1, doubleCannons_empty);
                warzone.useCard(actionJSON);
                assertFalse(warzone.hasFinished());


                actionJSON = new WarZoneJSON("Player 1", 0, lifeformsToRemove_empty, new ArrayList<>(), shieldsToActivate1, doubleCannons_empty);
                warzone.useCard(actionJSON);
                assertTrue(warzone.hasFinished());







        assertEquals(2, board.getEliminatedPlayers().size());
        assertEquals(eliminatedPlayers.get(1), board.getEliminatedPlayers().get(1));






//        while(warZone.hasFinished()) {
//            warZone = 0;
//        }
    }

    /*Assuming batteries are being used:
    * Firepower = 1
    * EnginePower = 2 (2 from doubleBoosters)
    * Batteries = 3
    * Crew = 6
    *
    * This is the ship with the lowest firepower*/
    public void ship_init1(Ship ship) {
        Cabin cabin_1 = new Cabin(new int[] {0, 1, 1, 1}, false);
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        Cabin cabin_2 = new Cabin(new int[] {0, 0, 0, 1}, false);
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        Engine double_booster = new Engine(new int[] {1, 0, 0, 0}, 2);

        Battery battery = new Battery(new int[] {0, 1, 1, 1}, 3);

        Shield shield = new Shield(new int[] {0, 0, 0, 1});
        shield.rotateRight(); // Covers right and lower side

        Cannon single_cannon = new Cannon(new int[] {0, 1, 0, 0}, 1);

        ship.addComponent(cabin_1, 6, 7);
        ship.addComponent(cabin_2, 6, 8);
        ship.addComponent(double_booster, 7, 7);
        ship.addComponent(battery, 6, 5);
        ship.addComponent(shield, 7, 5);
        ship.addComponent(single_cannon, 6, 4);

        ship.generateComponentSubLists();

        lifeformsToRemove_1 = new ArrayList<>();
        lifeformsToRemove_1.add(new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT));
        lifeformsToRemove_1.add(new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT));

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
        Cabin cabin_1 = new Cabin(new int[] {0, 1, 0, 1}, false);
        //cabin_1.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));

        Vital purple_vital = new Vital(new int[] {0, 1, 0, 0}, 0); // 0 -> purple, 1 -> brown

        Battery battery_1 = new Battery(new int[] {0, 0, 1, 0}, 3);

        Engine double_booster = new Engine(new int[] {1, 0, 0, 0}, 2);

        Shield shield = new Shield(new int[] {0, 1, 0, 1});

        Engine single_booster = new Engine(new int[] {0, 0, 0, 1}, 1);

        Battery battery_2 = new Battery(new int[] {0, 1, 1, 1}, 3);

        Cannon double_cannon = new Cannon(new int[] {0, 1, 0, 0}, 2);

        Map<Integer, Pair<Integer, Integer>> alienCoords;
        ShipJSON shipJSON;

        ship.addComponent(cabin_1, 6, 7);
        ship.addComponent(purple_vital, 6, 8);
        ship.addComponent(battery_1, 5, 6);
        ship.addComponent(double_booster, 7, 6);
        ship.addComponent(shield, 7, 7);
        ship.addComponent(single_booster, 7, 8);
        ship.addComponent(battery_2, 6, 5);
        ship.addComponent(double_cannon, 6, 4);

        ship.generateComponentSubLists();

        alienCoords = new HashMap<>();
        alienCoords.put(PURPLE_ALIEN.ordinal(), new Pair<>(6, 7));
        shipJSON = new ShipJSON("Player 2", alienCoords);
        ship.setChosenAliensForEligibleCabins(shipJSON);

        doubleCannons_2.add(new ComponentHelper<>(6, 4));

        lifeformsToRemove_2 = new ArrayList<>();
        lifeformsToRemove_2.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));
        lifeformsToRemove_2.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));
    }


    /*Assuming batteries are being used:
    * Firepower = 1
    * EnginePower = 3 (1 from singleBooster, 2 from brown alien)
    * Batteries = 0
    * Crew = 3
    *
    * This ship will not be affected by the WarZone since its lowestCrewCount and lowestFirepower are also present in ships ahead of it*/
    public void ship_init3(Ship ship) {
        Cannon single_cannon = new Cannon(new int[] {0, 0, 1, 0}, 1);

        Engine single_booster = new Engine(new int[] {1, 0, 0, 1}, 1);

        Cabin cabin_1 = new Cabin(new int[] {0, 1, 1, 0}, false);
        //cabin_1.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        Vital brown_vital = new Vital(new int[] {1, 1, 0, 0}, 1);

        Map<Integer, Pair<Integer, Integer>> alienCoords;
        ShipJSON shipJSON;

        ship.addComponent(single_cannon, 5, 6);
        ship.addComponent(single_booster, 7, 6);
        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(brown_vital, 7, 5);

        ship.generateComponentSubLists();

        alienCoords = new HashMap<>();
        alienCoords.put(BROWN_ALIEN.ordinal(), new Pair<>(6, 5));
        shipJSON = new ShipJSON("Player 3", alienCoords);
        ship.setChosenAliensForEligibleCabins(shipJSON);

        lifeformsToRemove_3 = new ArrayList<>();
        lifeformsToRemove_3.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));
        lifeformsToRemove_3.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));
    }

    /*Assuming batteries are being used:
    * Firepower = 4 (2 from double cannon, 2 from purple alien)
    * EnginePower = 1
    * Batteries = 3
    * Crew = 3 (2 humans, 1 purple alien)
    *
    * This is the ship with the lowest enginePower*/
    public void ship_init4(Ship ship) {
        Battery battery = new Battery(new int[] {0, 1, 0, 1}, 3);

        Cannon double_cannon = new Cannon(new int[] {0, 0, 0, 1}, 2);

        Engine single_booster = new Engine(new int[] {1, 0, 0, 0}, 1);

        Cabin cabin_1 = new Cabin(new int[] {1, 1, 0, 1}, false);
        //cabin_1.addInhabitant(new Lifeform(LifeformType.PURPLE_ALIEN));

        Vital purple_vital = new Vital(new int[] {0, 1, 1, 0}, 0);

        Storage special_storage = new Storage(new int[] {0, 0, 1, 0}, 2, true);
        special_storage.storeItem(new Item(ItemColor.RED));

        Storage normal_storage = new Storage(new int[] {1, 0, 0, 0}, 3, false);
        normal_storage.storeItem(new Item(ItemColor.YELLOW));
        normal_storage.storeItem(new Item(ItemColor.BLUE));
        normal_storage.storeItem(new Item(ItemColor.GREEN));

        Map<Integer, Pair<Integer, Integer>> alienCoords;
        ShipJSON shipJSON;

        ship.addComponent(battery, 6, 7);
        ship.addComponent(double_cannon, 6, 8);
        ship.addComponent(single_booster, 7, 6);
        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(special_storage, 5, 5);
        ship.addComponent(purple_vital, 6, 4);
        ship.addComponent(normal_storage, 7, 4);

        ship.generateComponentSubLists();

        alienCoords = new HashMap<>();
        alienCoords.put(PURPLE_ALIEN.ordinal(), new Pair<>(6, 5));
        shipJSON = new ShipJSON("Player 4", alienCoords);
        ship.setChosenAliensForEligibleCabins(shipJSON);

        doubleCannons_4.add(new ComponentHelper<>(6, 8));

        lifeformsToRemove_4 = new ArrayList<>();
        lifeformsToRemove_4.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));
        lifeformsToRemove_4.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));
    }

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