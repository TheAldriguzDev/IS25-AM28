package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientStardust;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.StardustJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StardustTest {
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

    Stardust stardust;
    CardStateJSON cardState;
    ClientStardust clientStardust;

    @BeforeEach
    public void init() {

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

        board.addPlayerToBoard(p1);
        board.addPlayerToBoard(p2);
        board.addPlayerToBoard(p3);
        board.addPlayerToBoard(p4);

        stardust = new Stardust("Stardust", 2, board, 0);

    }

    @Test
    public void movementTest() {
        ActionJSON actionJSON1 = new StardustJSON("Player 1");
        ActionJSON actionJSON2 = new StardustJSON("Player 2"); // 3 connettori esposti
        ActionJSON actionJSON3 = new StardustJSON("Player 3"); // 2 connettori esposti
        ActionJSON actionJSON4 = new StardustJSON("Player 4"); // 4 connettori esposti

        // ======== STATE TESTING ======== //
        cardState = stardust.generateState();
        assertEquals("Stardust", cardState.getCardName());
        assertEquals(2, cardState.getCardLevel());
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertNull(cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TEST ======== //
        clientStardust = new ClientStardust(cardState);
        clientStardust.generateWidget().printWidget();
        // ============================= //

        stardust.initCardPlayers();

        // ======== STATE TESTING ======== //
        cardState = stardust.generateState();
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertEquals("Player 4", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TEST ======== //
        clientStardust.updateCard(cardState);
        clientStardust.generateWidget().printWidget();
        // ============================= //

        stardust.useCard(actionJSON4);
        assertFalse(stardust.hasFinished());

        // ======== STATE TESTING ======== //
        cardState = stardust.generateState();
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertTrue(cardState.getNeedsBoardUpdate());
        assertTrue(cardState.getNeedsUpdatedPositions());
        assertEquals(1, cardState.getUpdatedPositions().size());
        assertEquals(-4, cardState.getUpdatedPositions().get("Player 4"));
        assertEquals("Player 3", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TEST ======== //
        clientStardust.updateCard(cardState);
        clientStardust.generateWidget().printWidget();
        // ============================= //

        stardust.useCard(actionJSON3);
        assertFalse(stardust.hasFinished());

        // ======== STATE TESTING ======== //
        cardState = stardust.generateState();
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertTrue(cardState.getNeedsBoardUpdate());
        assertTrue(cardState.getNeedsUpdatedPositions());
        assertEquals(1, cardState.getUpdatedPositions().size());
        assertEquals(-1, cardState.getUpdatedPositions().get("Player 3"));
        assertEquals("Player 2", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TEST ======== //
        clientStardust.updateCard(cardState);
        clientStardust.generateWidget().printWidget();
        // ============================= //

        stardust.useCard(actionJSON2);
        assertFalse(stardust.hasFinished());

        // ======== STATE TESTING ======== //
        cardState = stardust.generateState();
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertTrue(cardState.getNeedsBoardUpdate());
        assertTrue(cardState.getNeedsUpdatedPositions());
        assertEquals(1, cardState.getUpdatedPositions().size());
        assertEquals(0, cardState.getUpdatedPositions().get("Player 2"));
        assertEquals("Player 1", cardState.getPlayerNickname());
        // =============================== //
        // ======== WIDGET TEST ======== //
        clientStardust.updateCard(cardState);
        clientStardust.generateWidget().printWidget();
        // ============================= //

        stardust.useCard(actionJSON1);
        assertTrue(stardust.hasFinished());

        // ======== STATE TESTING ======== //
        cardState = stardust.generateState();
        assertFalse(cardState.getNeedsShipUpdate());
        assertFalse(cardState.getNeedsPlayerUpdate());
        assertFalse(cardState.getNeedsBoardUpdate());
        assertFalse(cardState.getNeedsUpdatedPositions());
        assertEquals("Player 1", cardState.getPlayerNickname()); //TODO: Since the card has been used the getNextPlayer was not invoked, see what to do with isCardUsable
        // =============================== //
        // ======== WIDGET TEST ======== //
        clientStardust.updateCard(cardState);
        clientStardust.generateWidget().printWidget();
        // ============================= //

        assertEquals(6, p1.getCursor());
        assertEquals(0, p2.getCursor());
        assertEquals(-1, p3.getCursor());
        assertEquals(-4, p4.getCursor());
    }

    public void ship_init1(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
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
        Cannon cannon_3 = new Cannon(connectors5, 1);
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
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);

        ship.generateComponentSubLists();


//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

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
        Cannon cannon_2 = new Cannon(connectors1, 1);
        Cannon cannon_3 = new Cannon(connectors5, 1);
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
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

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
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

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
        ship.addComponent(vital_1, 7, 5);
        ship.addComponent(battery_1, 7, 7);
        ship.addComponent(storage_1, 7, 8);
        ship.addComponent(storage_2, 6, 9);

        ship.generateComponentSubLists();

//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
//        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

    }
}

