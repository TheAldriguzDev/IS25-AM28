package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientSlavers;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SlaversTest {
    Board board;
    Player p1;
    Player p2;
    Player p3;
    Player p4;
    Ship ship_1;
    Ship ship_2;
    Ship ship_3;
    Ship ship_4;
    ActionJSON actionJSON1;
    ActionJSON actionJSON2;
    ActionJSON actionJSON3;
    ActionJSON actionJSON4;

    CardStateJSON cardState;

    Slavers slavers;

    ClientSlavers clientSlavers;

    ArrayList<ComponentHelper<LifeformType>> crewToRemove1;
    ArrayList<ComponentHelper<LifeformType>>  crewToRemove2;
    ArrayList<ComponentHelper<LifeformType>>  crewToRemove3;
    ArrayList<ComponentHelper<LifeformType>>  crewToRemove4;

    List<Cabin> cabinList1;
    List<Cabin> cabinList2;
    List<Cabin> cabinList3;
    List<Cabin> cabinList4;

    @BeforeEach
    void init() {

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

        crewToRemove1 = new ArrayList<>();
        crewToRemove2 = new ArrayList<>();
        crewToRemove3 = new ArrayList<>();
        crewToRemove4 = new ArrayList<>();

        cabinList1 = new ArrayList<>();
        cabinList2 = new ArrayList<>();
        cabinList3 = new ArrayList<>();
        cabinList4 = new ArrayList<>();


        ship_1 = p1.getShip();
        ship_init1(ship_1);
        ship_2 = p2.getShip();
        ship_init2(ship_2);
        ship_3 = p3.getShip();
        ship_init3(ship_3);
        ship_4 = p4.getShip();
        ship_init4(ship_4);

        board.addPlayerToBoard(p1);
        board.addPlayerToBoard(p2);
        board.addPlayerToBoard(p3);
        board.addPlayerToBoard(p4);



    }

    @Test
    public void test_first_player_loses_crew_second_player_eliminated_third_player_defeats_slavers_and_takes_loot_fourth_player_does_not_use_the_card() {
        System.out.println("======================== SLAVERS PRINT AND FUNCTION (1) TEST ==========================");


        slavers = new Slavers("Slavers", 2, 4, 2, 4, 6, board);
        // ======== STATE TESTING ======== //
        cardState = slavers.generateState();
        assertEquals("Slavers", cardState.getCardName());
        assertEquals(2, cardState.getCardLevel());
        assertEquals(4, cardState.getRequiredFirepower());
        assertEquals(2, cardState.getMovementSteps());
        assertEquals(4, cardState.getGivenCredits());
        assertEquals(6, cardState.getTakenCrew());
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertNull(cardState.getPlayerNickname());
        assertFalse(cardState.getFirstRound());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers = new ClientSlavers(cardState, null, null);
        clientSlavers.generateWidget().printWidget();
        // ================================ //

        // ======== DATA NECESSARY TO TEST THE CARD ======== //
        ArrayList<List<Integer>> doubleCannonActivated = new ArrayList<>();

        doubleCannonActivated.add(new ArrayList<>(Arrays.asList(7, 9)));

        actionJSON1 = new SlaversJSON("Player 1", false, crewToRemove1, new ArrayList<>()); // Total FirePower: 3
        actionJSON2 = new SlaversJSON("Player 2", false, crewToRemove2, new ArrayList<>()); // Total FirePower: 3
        actionJSON3 = new SlaversJSON("Player 3", true, crewToRemove3, doubleCannonActivated); // Total FirePower: 5
        actionJSON4 = new SlaversJSON("Player 4", false, crewToRemove4, new ArrayList<>()); // Total FirePower: 3

        // Player 1 should lose all crew members except for a single Astronaut in the core
        crewToRemove1.add(new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6, 7).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(7, 6).addItem(LifeformType.BROWN_ALIEN));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6, 7).addItem(LifeformType.ASTRONAUT));
        crewToRemove1.add(new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT));

        ArrayList<Integer> occupiedSpace1Before = new ArrayList<>();
        for(Cabin cabin : cabinList1) {
            occupiedSpace1Before.add(cabin.getInhabitants().size());
        }
        // Player 2 should be eliminated
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,6).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT)));
        crewToRemove2.add((new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.ASTRONAUT)));


        Player eliminatedPlayer = board.getPlayers().get(1);

        ArrayList<Integer> playerPositionsBefore = new ArrayList<>();
        for (Player player : board.getPlayers()) {
            playerPositionsBefore.add(player.getCursor());
        }
        // ================================================= //

        slavers.initCardPlayers();

        // ======== STATE TESTING ======== //
        cardState = slavers.generateState();
        assertTrue(cardState.getFirstRound());
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertEquals("Player 1", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers.updateCard(cardState);
        clientSlavers.generateWidget().printWidget();
        // ================================ //

        // Input gathering phase
        if (!slavers.hasFinished()) {
            slavers.useCard(new SlaversJSON("Player 1", false, new ArrayList<>(), new ArrayList<>()));
        }

        // ======== STATE TESTING ======== //
        cardState = slavers.generateState();
        assertTrue(cardState.getFirstRound());
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsUpdatedBatteries());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertEquals("Player 2", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers.updateCard(cardState);
        clientSlavers.generateWidget().printWidget();
        // ================================ //

        assertFalse(slavers.hasFinished());
        if (!slavers.hasFinished()) {
            slavers.useCard(new SlaversJSON("Player 2", false, new ArrayList<>(), new ArrayList<>()));
        }

        // ======== STATE TESTING ======== //
        cardState = slavers.generateState();
        assertTrue(cardState.getFirstRound());
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsUpdatedBatteries());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertEquals("Player 3", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers.updateCard(cardState);
        clientSlavers.generateWidget().printWidget();
        // ================================ //

        assertFalse(slavers.hasFinished());
        if (!slavers.hasFinished()) {
            slavers.useCard(new SlaversJSON("Player 3", true, new ArrayList<>(), doubleCannonActivated));
        }

        // ======== STATE TESTING ======== //
            // The player 3 defeated the slavers
        cardState = slavers.generateState();
        assertTrue(cardState.getFirstRound());
        assertTrue(cardState.getNeedsShipUpdate());
        assertTrue(cardState.getNeedsUpdatedBatteries());
        assertEquals(1, cardState.getRemovedBatteries().size());
        assertEquals(1, cardState.getRemovedBatteries().get("Player 3"));
        assertTrue(cardState.getNeedsPlayerUpdate());
        assertTrue(cardState.getNeedsUpdatedCredits());
        assertEquals(1, cardState.getUpdatedCredits().size());
        assertEquals(4, cardState.getUpdatedCredits().get("Player 3"));
        assertTrue(cardState.getNeedsBoardUpdate());
        assertTrue(cardState.getNeedsUpdatedPositions());
        assertEquals(1, cardState.getUpdatedPositions().size());
        assertEquals(playerPositionsBefore.get(2) - 2 - 1, cardState.getUpdatedPositions().get("Player 3"));
        assertEquals("Player 4", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers.updateCard(cardState);
        clientSlavers.generateWidget().printWidget();
        // ================================ //

        assertFalse(slavers.hasFinished());
        if (!slavers.hasFinished()) {
            slavers.useCard(new SlaversJSON("Player 4", false, new ArrayList<>(), new ArrayList<>()));
        }
        assertFalse(slavers.hasFinished());

        // ======== STATE TESTING ======== //
        cardState = slavers.generateState();
        assertFalse(cardState.getFirstRound());
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertEquals("Player 1", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers.updateCard(cardState);
        clientSlavers.generateWidget().printWidget();
        // ================================ //

            // Phase in which the defeated players need to send the crew members they want to remove form the ship
            if (!slavers.hasFinished()) {
                slavers.useCard(new SlaversJSON("Player 1", false, crewToRemove1, new ArrayList<>()));
            }
            assertFalse(slavers.hasFinished());

        // ======== STATE TESTING ======== //
        cardState = slavers.generateState();
        assertFalse(cardState.getFirstRound());
        assertTrue(cardState.getNeedsShipUpdate());
        assertTrue(cardState.getNeedsUpdatedRemovedLifeforms());
        assertEquals(1, cardState.getRemovedLifeforms().size()); // one player in the Map
        assertEquals(6, cardState.getRemovedLifeforms().get("Player 1").size()); // 6 lifeForms taken
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertEquals("Player 2", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers.updateCard(cardState);
        clientSlavers.generateWidget().printWidget();
        // ================================ //

        if (!slavers.hasFinished()) {
            slavers.useCard(new SlaversJSON("Player 2", false, crewToRemove2, new ArrayList<>()));
        }
        assertTrue(slavers.hasFinished());

        // ======== STATE TESTING ======== //
        cardState = slavers.generateState();
        assertFalse(cardState.getFirstRound());
        assertTrue(cardState.getNeedsShipUpdate());
        assertTrue(cardState.getNeedsUpdatedRemovedLifeforms());
        assertEquals(1, cardState.getRemovedLifeforms().size()); // one player in the Map
        assertEquals(6, cardState.getRemovedLifeforms().get("Player 2").size()); // 6 lifeForms taken
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertTrue(cardState.getNeedsBoardUpdate());
        assertTrue(cardState.getNeedsUpdatedEliminatedPlayers());
        assertEquals(1, cardState.getEliminatedPlayers().size());
        assertEquals("Player 2", cardState.getEliminatedPlayers().getFirst());
        assertEquals("Player 2", cardState.getPlayerNickname()); //TODO: Since the card has been used the getNextPlayer was not invoked, see what to do with isCardUsable
        // =============================== //
        // ======== WIDGET TESTING ======== //
        clientSlavers.updateCard(cardState);
        clientSlavers.generateWidget().printWidget();
        // ================================ //




        assertEquals(occupiedSpace1Before.get(0) - 1, cabinList1.get(0).getInhabitants().size());
        assertEquals(occupiedSpace1Before.get(1) - 2, cabinList1.get(1).getInhabitants().size());
        assertEquals(occupiedSpace1Before.get(2) - 2, cabinList1.get(2).getInhabitants().size());
        assertEquals(occupiedSpace1Before.get(3) - 1, cabinList1.get(3).getInhabitants().size());

        assertEquals(board.getEliminatedPlayers().size(), 1);
        assertEquals(eliminatedPlayer, board.getEliminatedPlayers().getFirst());

        assertEquals(playerPositionsBefore.get(0), p1.getCursor());
        assertEquals(playerPositionsBefore.get(2) - 2 - 1, p3.getCursor()); // 2 steps backwards + jump over p4 // Final Position: -2
        assertEquals(playerPositionsBefore.get(3), p4.getCursor());

        assertEquals(0, p1.getCredits());

        assertEquals(4, p3.getCredits());
        assertEquals(0, p4.getCredits());

    }

    @Test
    public void test_first_three_players_tie_fourth_one_wins() {
        slavers = new Slavers("Slavers", 2, 3, 2, 4, 6, board);

        ArrayList<List<Integer>> doubleCannonActivated = new ArrayList<>();
//        List<Integer> x = new ArrayList<>();
//        List<Integer> y = new ArrayList<>();
//        x.add(7);
//        y.add(9);
//        doubleCannonActivated.add(x);
//        doubleCannonActivated.add(y);
        doubleCannonActivated.add(new ArrayList<>(Arrays.asList(7, 9)));

        actionJSON1 = new SlaversJSON("Player 1", false, crewToRemove1, new ArrayList<>()); // Total FirePower: 3
        actionJSON2 = new SlaversJSON("Player 2", false, crewToRemove2, new ArrayList<>()); // Total FirePower: 3
        actionJSON3 = new SlaversJSON("Player 3", false, crewToRemove3, new ArrayList<>()); // Total FirePower: 3
        actionJSON4 = new SlaversJSON("Player 4", true, crewToRemove4, doubleCannonActivated); // Total FirePower: 5

        ArrayList<Integer> playerPositionsBefore = new ArrayList<>();
        for (Player player : board.getPlayers()) {
            playerPositionsBefore.add(player.getCursor());
        }

        slavers.initCardPlayers();

        // Input gathering phase
        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON1);
        }
        assertFalse(slavers.hasFinished());
        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON2);
        }
        assertFalse(slavers.hasFinished());
        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON3);
        }
        assertFalse(slavers.hasFinished());
        if (!slavers.hasFinished()) {
            slavers.useCard(actionJSON4);
        }
        // There should be no players to affect
        assertTrue(slavers.hasFinished());

        assertEquals(playerPositionsBefore.get(0), p1.getCursor());
        assertEquals(playerPositionsBefore.get(1), p2.getCursor());
        assertEquals(playerPositionsBefore.get(2), p3.getCursor());
        assertEquals(playerPositionsBefore.get(3) - 2, p4.getCursor());

        assertEquals(0, p1.getCredits());
        assertEquals(0, p2.getCredits());
        assertEquals(0, p3.getCredits());
        assertEquals(4, p4.getCredits());


    }

    @Test
    void cardWidget_test() {









    }

    public void ship_init1(Ship ship) {

        // core + 3 cabine, 3 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio viene attivato

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
        connectors7.add(0);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors1, false);
        Cabin cabin_2 = new Cabin(connectors1, false);
        Cabin cabin_3 = new Cabin(connectors2, false);
        Cannon cannon_1 = new Cannon(connectors3, 2);
        Cannon cannon_2 = new Cannon(connectors4, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
        Cannon cannon_4 = new Cannon(connectors5, 1);
        Vital vital_1 = new Vital(connectors4, 0);
        Battery battery_1 = new Battery(connectors5, 3);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

          ship.addComponent(cabin_1, 6, 5);
          ship.addComponent(cabin_2, 6, 7);
          ship.addComponent(cabin_3, 7, 6);
          ship.addComponent(cannon_1, 5, 6);
          ship.addComponent(cannon_2, 6, 4);
          ship.addComponent(cannon_3, 6, 8);
          ship.addComponent(cannon_4, 7, 8);
          ship.addComponent(vital_1, 7, 5);
          ship.addComponent(battery_1, 7, 7);

          ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));






        cabinList1.add(ship.getCabinList().getFirst());
        cabinList1.add(cabin_1);
        cabinList1.add(cabin_2);
        cabinList1.add(cabin_3);

    }

    public void ship_init2(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio non viene attivato

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
        connectors7.add(0);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors2, false);
        Cabin cabin_2 = new Cabin(connectors1, false);
        Cabin cabin_3 = new Cabin(connectors6, false);
        Cannon cannon_1 = new Cannon(connectors3, 2);
        Cannon cannon_2 = new Cannon(connectors4, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
        Cannon cannon_4 = new Cannon(connectors5, 1);
        Vital vital_1 = new Vital(connectors4, 0);
        Battery battery_1 = new Battery(connectors1, 3);
        Storage storage_1 = new Storage(connectors5, 3, false);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        storage_1.storeItem(new Item(ItemColor.GREEN));
        storage_1.storeItem(new Item(ItemColor.GREEN));
        storage_1.storeItem(new Item(ItemColor.YELLOW));

        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(cabin_2, 6, 7);
        ship.addComponent(cabin_3, 7, 6);
        ship.addComponent(cannon_1, 5, 6);
        ship.addComponent(cannon_2, 6, 4);
        ship.addComponent(cannon_3, 6, 8);
        ship.addComponent(cannon_4, 7, 9);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabinList2.add(ship.getCabinList().getFirst());
        cabinList2.add(cabin_1);
        cabinList2.add(cabin_2);
        cabinList2.add(cabin_3);

    }

    public void ship_init3(Ship ship) {

        // core + 3 cabine, 3 cannoni singoli, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone

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
        connectors7.add(0);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors2, false);
        Cabin cabin_2 = new Cabin(connectors1, false);
        Cabin cabin_3 = new Cabin(connectors2, false);
        Cannon cannon_1 = new Cannon(connectors3, 1);
        Cannon cannon_2 = new Cannon(connectors1, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
        Cannon cannon_4 = new Cannon(connectors5, 2);
        Vital vital_1 = new Vital(connectors4, 0);
        Battery battery_1 = new Battery(connectors1, 3);
        Storage storage_1 = new Storage(connectors5, 3, false);
        Storage storage_2 = new Storage(connectors5, 2, true);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        storage_1.storeItem(new Item(ItemColor.BLUE));
        storage_1.storeItem(new Item(ItemColor.YELLOW));
        storage_1.storeItem(new Item(ItemColor.GREEN));

        storage_2.storeItem(new Item(ItemColor.RED));
        storage_2.storeItem(new Item(ItemColor.RED));

        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(cabin_2, 6, 7);
        ship.addComponent(cabin_3, 7, 6);
        ship.addComponent(cannon_1, 5, 6);
        ship.addComponent(cannon_2, 6, 4);
        ship.addComponent(cannon_3, 6, 8);
        ship.addComponent(cannon_4, 7, 9);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabinList3.add(ship.getCabinList().getFirst());
        cabinList3.add(cabin_1);
        cabinList3.add(cabin_2);
        cabinList3.add(cabin_3);

    }

    public void ship_init4(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone

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
        connectors7.add(0);

        List<Integer> connectors8 = new ArrayList<Integer>();
        connectors8.add(1);
        connectors8.add(0);
        connectors8.add(0);
        connectors8.add(0);

        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(connectors2, false);
        Cabin cabin_2 = new Cabin(connectors1, false);
        Cabin cabin_3 = new Cabin(connectors6, false);
        Cannon cannon_1 = new Cannon(connectors3, 1);
        Cannon cannon_2 = new Cannon(connectors2, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
        Cannon cannon_4 = new Cannon(connectors5, 2);
        Vital vital_1 = new Vital(connectors4, 0);
        Battery battery_1 = new Battery(connectors1, 3);
        Storage storage_1 = new Storage(connectors5, 3, false);
        Storage storage_2 = new Storage(connectors5, 2, true);
        //cannon_1.rotateLeft();


        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_1.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabin_3.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));

        storage_1.storeItem(new Item(ItemColor.BLUE));
        storage_1.storeItem(new Item(ItemColor.YELLOW));
        storage_1.storeItem(new Item(ItemColor.GREEN));

        storage_2.storeItem(new Item(ItemColor.RED));

        ship.addComponent(cabin_1, 6, 5);
        ship.addComponent(cabin_2, 6, 7);
        ship.addComponent(cabin_3, 7, 6);
        ship.addComponent(cannon_1, 5, 6);
        ship.addComponent(cannon_2, 6, 4);
        ship.addComponent(cannon_3, 6, 8);
        ship.addComponent(cannon_4, 7, 9);
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        cabinList4.add(ship.getCabinList().getFirst());
        cabinList4.add(cabin_1);
        cabinList4.add(cabin_2);
        cabinList4.add(cabin_3);

    }

}