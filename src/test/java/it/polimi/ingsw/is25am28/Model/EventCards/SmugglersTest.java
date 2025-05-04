package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientSmugglers;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.SmugglersJSON;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Components.*;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersTest {
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
    Smugglers smugglers;
    ResourceBank resourceBank;
    CardStateJSON cardState;
    ClientSmugglers clientSmugglers;

    ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved1;
    ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken1;

    ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved2;
    ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken2;

    ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved3;
    ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken3;

    ArrayList<ComponentHelper<ItemColor>> itemsToBeRemoved4;
    ArrayList<ComponentHelper<ItemColor>> itemsToBeTaken4;

    ArrayList<Storage> storageList1;
    ArrayList<Storage> storageList2;
    ArrayList<Storage> storageList3;
    ArrayList<Storage> storageList4;

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

        itemsToBeRemoved1 = new ArrayList<>();
        itemsToBeTaken1 = new ArrayList<>();
        storageList1 = new ArrayList<>();

        itemsToBeRemoved2 = new ArrayList<>();
        itemsToBeTaken2 = new ArrayList<>();
        storageList2 = new ArrayList<>();

        itemsToBeRemoved3 = new ArrayList<>();
        itemsToBeTaken3 = new ArrayList<>();
        storageList3 = new ArrayList<>();

        itemsToBeRemoved4 = new ArrayList<>();
        itemsToBeTaken4 = new ArrayList<>();
        storageList4 = new ArrayList<>();

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

        resourceBank = new ResourceBank();
    }

    @Test
    public void all_players_lose() {
        System.out.println("======================== SMUGGLERS PRINT AND FUNCTION (1) TEST ==========================");


        itemsToBeRemoved2.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.GREEN));
        itemsToBeRemoved2.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));
        itemsToBeRemoved2.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.GREEN));

        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(6, 9).addItem(ItemColor.RED));
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.BLUE));
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(6, 9).addItem(ItemColor.RED));

        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(6, 9).addItem(ItemColor.RED));
        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.BLUE));
        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));
        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.GREEN));

        smugglers = new Smugglers("Smugglers", 2, 3, 5, 4, 1, 2, 1, 0, board, resourceBank);

        actionJSON1 = new SmugglersJSON("Player 1", false, itemsToBeTaken1, itemsToBeRemoved1, new ArrayList<>()); // Total FirePower: 2
        actionJSON2 = new SmugglersJSON("Player 2", false, itemsToBeTaken2, itemsToBeRemoved2, new ArrayList<>()); // Total FirePower: 2
        actionJSON3 = new SmugglersJSON("Player 3", false, itemsToBeTaken3, itemsToBeRemoved3, new ArrayList<>()); // Total FirePower: 3
        actionJSON4 = new SmugglersJSON("Player 4", false, itemsToBeTaken4, itemsToBeRemoved4, new ArrayList<>()); // Total FirePower: 3

        // ======== WIDGET TESTING ======== //
        System.out.println("Non initialized card");
        clientSmugglers = new ClientSmugglers(null, null, smugglers.generateState());
        clientSmugglers.generateWidget().printWidget();
        // ================================ //

        smugglers.initCardPlayers();

        // ======== WIDGET TESTING ======== //
        System.out.println("Player 1 input");
        clientSmugglers.updateCard(smugglers.generateState());
        clientSmugglers.generateWidget().printWidget();
        // ================================ //

        //Input gathering phase
        smugglers.useCard(new SmugglersJSON("Player 1", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        assertFalse(smugglers.hasFinished());

        // ======== WIDGET TESTING ======== //
        System.out.println("Player 2 input");
        clientSmugglers.updateCard(smugglers.generateState());
        clientSmugglers.generateWidget().printWidget();
        // ================================ //

        smugglers.useCard(new SmugglersJSON("Player 2", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        assertFalse(smugglers.hasFinished());

        // ======== WIDGET TESTING ======== //
        System.out.println("Player 3 input");
        clientSmugglers.updateCard(smugglers.generateState());
        clientSmugglers.generateWidget().printWidget();
        // ================================ //

        smugglers.useCard(new SmugglersJSON("Player 3", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        assertFalse(smugglers.hasFinished());

        // ======== WIDGET TESTING ======== //
        System.out.println("Player 4 input");
        clientSmugglers.updateCard(smugglers.generateState());
        clientSmugglers.generateWidget().printWidget();
        // ================================ //

        smugglers.useCard(new SmugglersJSON("Player 4", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        assertFalse(smugglers.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Player 1 consequence");
            clientSmugglers.updateCard(smugglers.generateState());
            clientSmugglers.generateWidget().printWidget();
            // ================================ //

            // The defeated players now need to specify what resources to get rid of
            smugglers.useCard(actionJSON1);
            assertFalse(smugglers.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Player 2 consequence");
            clientSmugglers.updateCard(smugglers.generateState());
            clientSmugglers.generateWidget().printWidget();
            // ================================ //

            smugglers.useCard(actionJSON2);
            assertFalse(smugglers.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Player 3 consequence");
            clientSmugglers.updateCard(smugglers.generateState());
            clientSmugglers.generateWidget().printWidget();
            // ================================ //

            smugglers.useCard(actionJSON3);
            assertFalse(smugglers.hasFinished());

            // ======== WIDGET TESTING ======== //
            System.out.println("Player 4 consequence");
            clientSmugglers.updateCard(smugglers.generateState());
            clientSmugglers.generateWidget().printWidget();
            // ================================ //

            smugglers.useCard(actionJSON4);
            assertTrue(smugglers.hasFinished());

        assertEquals(0, ship_1.getAvailableEnergy()); // Non avendo items, subisce -4 alle batterie -> -3 in quanto ne ha solo 3

        // System.out.println("p2 storage: ");
        for (Storage storage : ship_2.getStorageList()) {
            for(Item item : storage.getStoredItems()) {
                // System.out.println(item.toString());
            }
        }

        assertEquals(2, ship_2.getAvailableEnergy()); // Rimossi 3 items (G,G,Y) e 1 batteria


//        System.out.println("StorageList3: ");
//        for (Storage storage : storageList3) {
//            for(Item item : storage.getStoredItems()) {
//                System.out.println(item.toString());
//            }
//        }
//        System.out.println("p3 storage: ");
//        for (Storage storage : ship_3.getStorageList()) {
//            for(Item item : storage.getStoredItems()) {
//                System.out.println(item.toString());
//            }
//        }

        assertEquals(3, ship_3.getAvailableEnergy()); // Rimossi 4 items e 0 batterie
        assertEquals(ItemColor.GREEN, ship_3.getStorageList().get(1).getStoredItems().get(0).getColor());


        assertEquals(3, ship_4.getAvailableEnergy()); // Rimossi 4 items e 0 batterie
        assertEquals(0, ship_4.getStorageList().get(0).getStoredItems().size());
        assertEquals(0, ship_4.getStorageList().get(1).getStoredItems().size());


        // ======== WIDGET TESTING ======== //
        System.out.println("Last state");
        clientSmugglers.updateCard(smugglers.generateState());
        clientSmugglers.generateWidget().printWidget();
        // ================================ //
    }

    @Test void first_player_loses_second_player_ties_third_player_wins_fourth_player_does_nothing() {

        // In this test the first player will lose batteries, while the third will instead drop a green and a blue item to make space for 2 yellow items
        // The second(tie) player and the fourth(does nothing) player won't have anything changed
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.GREEN));
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.BLUE));

        itemsToBeTaken3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));
        itemsToBeTaken3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));

        List<ComponentHelper<Integer>> doubleCannonActivated = new ArrayList<>();
//        List<Integer> x = new ArrayList<>();
//        List<Integer> y = new ArrayList<>();
//        x.add(7);
//        y.add(9);
//        doubleCannonActivated.add(x);
//        doubleCannonActivated.add(y);
//        doubleCannonActivated.add(new ArrayList<>(Arrays.asList(7, 9)));
        doubleCannonActivated.add(new ComponentHelper<>(7, 9));

        actionJSON1 = new SmugglersJSON("Player 1", false, itemsToBeTaken1, itemsToBeRemoved1, new ArrayList<>()); // Total FirePower: 2
        actionJSON2 = new SmugglersJSON("Player 2", false, itemsToBeTaken2, itemsToBeRemoved2, new ArrayList<>()); // Total FirePower: 3
        actionJSON3 = new SmugglersJSON("Player 3", true, itemsToBeTaken3, itemsToBeRemoved3, doubleCannonActivated); // Total FirePower: 5
        actionJSON4 = new SmugglersJSON("Player 4", false, itemsToBeTaken4, itemsToBeRemoved4, new ArrayList<>()); // Total FirePower: 3

        smugglers = new Smugglers("Smugglers", 2, 3, 3, 2, 1, 2, 1, 0, board, resourceBank);

        ArrayList<Integer> playerPositionsBefore = new ArrayList<>();
        for (Player p : board.getPlayers()) {
            playerPositionsBefore.add(p.getCursor());
        }

        smugglers.initCardPlayers();

        // Input gathering phase
        smugglers.useCard(new SmugglersJSON("Player 1", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        assertFalse(smugglers.hasFinished());

        smugglers.useCard(new SmugglersJSON("Player 2", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        assertFalse(smugglers.hasFinished());

        smugglers.useCard(actionJSON3);
        assertFalse(smugglers.hasFinished());

        smugglers.useCard(new SmugglersJSON("Player 4", false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        assertFalse(smugglers.hasFinished());

        // The defeated players now need to specify what resources to get rid of
        smugglers.useCard(actionJSON1);
        assertTrue(smugglers.hasFinished());



        assertEquals(1, ship_1.getAvailableEnergy()); // Non avendo items, subisce -2 alle batterie -> vanno a 1

        assertEquals(3, ship_2.getAvailableEnergy()); // Non attiva cannoni doppi e non viene derubato delle batterie, il numero non varia

        assertEquals(2, ship_3.getAvailableEnergy()); // Non viene derubato ma attviva comunque un cannone doppio -> -1 alle batterie -> ne riamngono 2

        // Verfico che nello storage normale ci siano solo le casse gialle (da G,B,Y a Y,Y,Y)
        assertEquals(3, storageList3.get(0).getStoredItems().size());
        assertEquals(ItemColor.YELLOW, storageList3.get(0).getStoredItems().get(0).getColor());
        assertEquals(ItemColor.YELLOW, storageList3.get(0).getStoredItems().get(1).getColor());
        assertEquals(ItemColor.YELLOW, storageList3.get(0).getStoredItems().get(2).getColor());

        // Verifico che solo la posizione del terzo player sia cambiata
        assertEquals(playerPositionsBefore.get(0), p1.getCursor());
        assertEquals(playerPositionsBefore.get(1), p2.getCursor());
        assertEquals(playerPositionsBefore.get(2) -3 -1, p3.getCursor()); // -3 di movementSteps, -1 per il "salto" oltre il player 4
        assertEquals(playerPositionsBefore.get(3), p4.getCursor());

//        System.out.println(p3.getShip().getStorageList().get(1).getStoredItems());
//        System.out.println(storageList3.get(0).getStoredItems());


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
        Cannon cannon_1 = new Cannon(connectors2, 2);
        Cannon cannon_2 = new Cannon(connectors4, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
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

        // core + 3 cabine, 3 cannoni singoli, un vital(BROWN), una batteria da 3
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
        Cannon cannon_1 = new Cannon(connectors3, 1);
        Cannon cannon_2 = new Cannon(connectors4, 1);
        Cannon cannon_3 = new Cannon(connectors1, 1);
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

        storageList2.add(storage_1);


    }

    public void ship_init3(Ship ship) {

        // core + 3 cabine, 3 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
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
        Cabin cabin_3 = new Cabin(connectors1, false);
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

        storageList3.add(storage_1);
        storageList3.add(storage_2);


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

        storageList4.add(storage_1);
        storageList4.add(storage_2);



    }


}