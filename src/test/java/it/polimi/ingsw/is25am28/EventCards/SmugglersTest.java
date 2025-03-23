package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.SmugglersJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Ship.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

        board.newPlayer("Player 1", PlayerColor.RED);
        board.newPlayer("Player 2", PlayerColor.BLUE);
        board.newPlayer("Player 3", PlayerColor.GREEN);
        board.newPlayer("Player 4", PlayerColor.YELLOW);

        List<Player> players = board.getPlayers();
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
        smugglers = new Smugglers("Smugglers", 2, 3, 5, 4, 1, 2, 1, 0, board, resourceBank);

        actionJSON1 = new SmugglersJSON("Player 1", false, itemsToBeTaken1, itemsToBeRemoved1, 0); // Total FirePower: 2
        actionJSON2 = new SmugglersJSON("Player 2", false, itemsToBeTaken2, itemsToBeRemoved2, 0); // Total FirePower: 2
        actionJSON3 = new SmugglersJSON("Player 3", false, itemsToBeTaken3, itemsToBeRemoved3, 0); // Total FirePower: 3
        actionJSON4 = new SmugglersJSON("Player 4", false, itemsToBeTaken4, itemsToBeRemoved4, 0); // Total FirePower: 3

        smugglers.initCardPlayers();

        smugglers.useCard(actionJSON1);
        assertFalse(smugglers.hasFinished());

        smugglers.useCard(actionJSON2);
        assertFalse(smugglers.hasFinished());

        smugglers.useCard(actionJSON3);
        assertFalse(smugglers.hasFinished());

        smugglers.useCard(actionJSON4);
        assertTrue(smugglers.hasFinished());


        assertEquals(0, ship_1.getAvailableEnergy()); // Non avendo items, subisce -4 alle batterie -> -3 in quanto ne ha solo 3

        System.out.println("p2 storage: ");
        for (Storage storage : ship_2.getStorageList()) {
            for(Item item : storage.getStoredItems()) {
                System.out.println(item.toString());
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









    }


    public void ship_init1(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio viene attivato


        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 2);
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 0}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 0, 0, 1}, 3);
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

        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));




    }

    public void ship_init2(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone
        // Il cannone doppio non viene attivato


        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 1, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 2);
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 0}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 1, 0, 1}, 3);
        Storage storage_1 = new Storage(new int[] {0, 0, 0, 1}, 3, false);
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

        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        storageList2.add(storage_1);

        itemsToBeRemoved2.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.GREEN));
        itemsToBeRemoved2.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));
        itemsToBeRemoved2.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.GREEN));


    }

    public void ship_init3(Ship ship) {

        // core + 3 cabine, 3 cannoni singoli, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone



        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 1);
        Cannon cannon_2 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 1, 0, 1}, 3);
        Storage storage_1 = new Storage(new int[] {0, 0, 0, 1}, 3, false);
        Storage storage_2 = new Storage(new int[] {0, 0, 0, 1}, 2, true);
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

        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        storageList3.add(storage_1);
        storageList3.add(storage_2);

        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(6, 9).addItem(ItemColor.RED));
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.BLUE));
        itemsToBeRemoved3.add(new ComponentHelper<ItemColor>(6, 9).addItem(ItemColor.RED));



    }

    public void ship_init4(Ship ship) {

        // core + 3 cabine, 2 cannoni singoli, un cannone doppio, un vital(BROWN), una batteria da 3
        // 2 + 2 + 2 = umani + 1 alieno marrone


        //Cabin core = new Cabin(connectors, true);
        Cabin cabin_1 = new Cabin(new int[] {1, 1, 0, 1}, false);
        Cabin cabin_2 = new Cabin(new int[] {0, 1, 0, 1}, false);
        Cabin cabin_3 = new Cabin(new int[] {1, 1, 1, 1}, false);
        Cannon cannon_1 = new Cannon(new int[] {0, 0, 1, 0}, 1);
        Cannon cannon_2 = new Cannon(new int[] {1, 1, 0, 1}, 1);
        Cannon cannon_3 = new Cannon(new int[] {0, 1, 0, 1}, 1);
        Vital vital_1 = new Vital(new int[] {0, 1, 0, 0}, 0);
        Battery battery_1 = new Battery(new int[] {0, 1, 0, 1}, 3);
        Storage storage_1 = new Storage(new int[] {0, 0, 0, 1}, 3, false);
        Storage storage_2 = new Storage(new int[] {0, 0, 0, 1}, 2, true);
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

        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.getCabinList().getFirst().addInhabitant(new Lifeform(LifeformType.ASTRONAUT));

        storageList4.add(storage_1);
        storageList4.add(storage_2);

        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(6, 9).addItem(ItemColor.RED));
        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.BLUE));
        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.YELLOW));
        itemsToBeRemoved4.add(new ComponentHelper<ItemColor>(7, 8).addItem(ItemColor.GREEN));

    }


}