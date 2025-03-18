package it.polimi.ingsw.is25am28.Ship;

import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Exceptions.ExistingComponentException;
import it.polimi.ingsw.is25am28.Exceptions.NullComponentException;
import it.polimi.ingsw.is25am28.Exceptions.OutOfGridException;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

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

    @Test
    void generateComponentSubLists() {
        Ship ship = new Ship(2);

        int[] connectors = new int[4];

        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery battery = new Battery(connectors, 3);
        Battery battery2 = new Battery(connectors, 3);
        Cabin cabin = new Cabin(connectors, false);
        Cannon cannon = new Cannon(connectors, 1);
        Engine engine = new Engine(connectors, 1);
        Engine engine2 = new Engine( connectors, 2);
        Shield shield = new Shield( connectors);
        Storage storage = new Storage(connectors, 3, false);
        Structural structural = new Structural(connectors);
        Vital vital = new Vital(connectors, 0);

        System.out.println("==== BEFORE ====");
        printShipGrid(ship);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

        System.out.println("==== AFTER ====");
        printShipGrid(ship);

        List<Engine> engineList = new ArrayList<Engine>();
        engineList.add(engine);
        engineList.add(engine2);

        List<Cabin> cabinList = new ArrayList<Cabin>();
        cabinList.add((Cabin) ship.getComponent(6, 6)); // Core is a cabin, thus appears here
        cabinList.add(cabin);

        ship.generateComponentSubLists();

        // Verifying each sublist's size
        assertEquals(1, ship.getBatteryList().size());
        assertEquals(2, ship.getCabinList().size());
        assertEquals(0, ship.getCannonList().size());
        assertEquals(2, ship.getEngineList().size());
        assertEquals(1, ship.getShieldList().size());
        assertEquals(0, ship.getStorageList().size());
        assertEquals(0, ship.getVitalList().size());

        // Verifying each sublist
        assertEquals(battery, ship.getBatteryList().getFirst());
        assertTrue(ship.getCabinList().containsAll(cabinList));
        assertTrue(ship.getCannonList().isEmpty());
        assertTrue(ship.getEngineList().containsAll(engineList));
        assertEquals(shield, ship.getShieldList().getFirst());
        assertTrue(ship.getStorageList().isEmpty());
        assertTrue(ship.getVitalList().isEmpty());
    }

    @Test
    void getGridDimensions() {
    }

    @Test
    void getShipDimensionsByDifficulty() {
    }

    @Test
    void getOffsetsByDifficulty() {
    }

    @Test
    void getDifficultyLevel() {
    }

    @Test
    void getBatteryList() {
    }

    @Test
    void getCabinList() {
    }

    @Test
    void getCannonList() {
    }

    @Test
    void getEngineList() {
    }

    @Test
    void getShieldList() {
    }

    @Test
    void getStorageList() {
    }

    @Test
    void getVitalList() {
    }

    @Test
    void getDoubleEngines() {
    }

    @Test
    void getDoubleCannons() {
    }

    @Test
    void getAvailableEnergy() {
    }

    @Test
    void consumeEnergy() {
    }

    @Test
    void getAllLifeforms() {
    }

    @Test
    void getFirePower() {
    }

    @Test
    void getEnginePower() {
    }

    @Test
    void getAllItems() {
    }

    @Test
    void getAllItemValue() {
        Ship ship = new Ship(1);

        int[] connectors = new int[4];

        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Storage leftStorage = new Storage(connectors, 3, false);
        Storage rightStorage = new Storage(connectors, 3, false);
        Storage topStorage = new Storage(connectors, 2, true);
        Storage disconnectedStorage = new Storage(connectors, 3, false);

        Item red = new Item(ItemColor.RED);
        Item yellow = new Item(ItemColor.YELLOW);
        Item green = new Item(ItemColor.GREEN);
        Item blue = new Item(ItemColor.BLUE);

        leftStorage.storeItem(green);
        leftStorage.storeItem(yellow);
        leftStorage.storeItem(yellow);

        rightStorage.storeItem(green);
        rightStorage.storeItem(green);
        rightStorage.storeItem(blue);

        topStorage.storeItem(red);
        topStorage.storeItem(yellow);

        disconnectedStorage.storeItem(blue);
        disconnectedStorage.storeItem(yellow);

        ship.addComponent(leftStorage, 6, 5);
        ship.addComponent(rightStorage, 6, 7);
        ship.addComponent(topStorage, 5, 6);
        ship.addComponent(disconnectedStorage, 7, 7);

        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
        printShipGrid(ship);

        ship.generateComponentSubLists();

        List<Storage> storageList = new ArrayList<>();

        storageList.add(topStorage);
        storageList.add(rightStorage);
        storageList.add(leftStorage);

        int expectedTotalValue = storageList.stream()
                .flatMap(s -> s.getStoredItems().stream())
                        .mapToInt(Item::getValue)
                        .sum();

        assertEquals(storageList, ship.getStorageList());
        assertTrue(ship.getAllItems().containsAll((List<Item>) storageList.stream().flatMap(s -> s.getStoredItems().stream()).toList()));
        assertEquals(expectedTotalValue, ship.getAllItemValue());

    }

    @Test
    void getWrongComponents() {
    }

    @Test
    void getGridRow() {
        Ship ship = new Ship(2);

        int[] connectors = new int[4];

        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery battery = new Battery(connectors, 3);
        Battery battery2 = new Battery(connectors, 3);
        Cabin cabin = new Cabin(connectors, false);
        Cannon cannon = new Cannon(connectors, 1);
        Engine engine = new Engine(connectors, 1);
        Engine engine2 = new Engine( connectors, 2);
        Shield shield = new Shield( connectors);
        Storage storage = new Storage(connectors, 3, false);
        Structural structural = new Structural(connectors);
        Vital vital = new Vital(connectors, 0);

        Cabin core = (Cabin) ship.getComponent(6, 6);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(battery2, 5, 5);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
        printShipGrid(ship);

        int i;
        int indexRow1 = 6;
        int indexRow2 = 4;
        int indexRow3 = 3;
        Component[] row1 = new Component[12];
        Component[] row2 = new Component[12];
        Component[] row3 = new Component[12];

        for (i = 0; i < 12; i++) {
            row1[i] = null;
            row2[i] = null;
            row3[i] = null;
        }

        row1[5] = shield;
        row1[6] = core;
        row1[7] = battery;

        row2[5] = storage;

        row3[2] = structural;

        assertTrue(Arrays.stream(row1).toList().containsAll(Arrays.stream(ship.getGridRow(indexRow1)).toList()));
        assertTrue(Arrays.stream(row2).toList().containsAll(Arrays.stream(ship.getGridRow(indexRow2)).toList()));
        assertTrue(Arrays.stream(row3).toList().containsAll(Arrays.stream(ship.getGridRow(indexRow3)).toList()));
    }

    @Test
    void getGridColumn() {
        Ship ship = new Ship(2);

        int[] connectors = new int[4];

        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery battery = new Battery(connectors, 3);
        Battery battery2 = new Battery(connectors, 3);
        Cabin cabin = new Cabin(connectors, false);
        Cannon cannon = new Cannon(connectors, 1);
        Engine engine = new Engine(connectors, 1);
        Engine engine2 = new Engine( connectors, 2);
        Shield shield = new Shield( connectors);
        Storage storage = new Storage(connectors, 3, false);
        Structural structural = new Structural(connectors);
        Vital vital = new Vital(connectors, 0);

        Cabin core = (Cabin) ship.getComponent(6, 6);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(battery2, 5, 5);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
        printShipGrid(ship);

        int i;
        int indexCol1 = 5;
        int indexCol2 = 4;
        int indexCol3 = 2;
        Component[] column1 = new Component[12];
        Component[] column2 = new Component[12];
        Component[] column3 = new Component[12];

        for (i = 0; i < 12; i++) {
            column1[i] = null;
            column2[i] = null;
            column3[i] = null;
        }

        column1[4] = storage;
        column1[5] = battery2;
        column1[6] = shield;

        column2[8] = cannon;

        column3[2] = vital;
        column3[3] = structural;

        assertTrue(Arrays.stream(column1).toList().containsAll(Arrays.stream(ship.getGridColumn(indexCol1)).toList()));
        assertTrue(Arrays.stream(column2).toList().containsAll(Arrays.stream(ship.getGridColumn(indexCol2)).toList()));
        assertTrue(Arrays.stream(column3).toList().containsAll(Arrays.stream(ship.getGridColumn(indexCol3)).toList()));
    }

    @Test
    void validateShip() {
    }

    @Test
    void traverse() {
        Ship ship = new Ship(2);

        int[] connectors = new int[4];
        int i;
        
        for (i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery battery = new Battery(connectors, 3);
        Battery battery2 = new Battery(connectors, 3);
        Cabin cabin = new Cabin(connectors, false);
        Cannon cannon = new Cannon(connectors, 1);
        Engine engine = new Engine(connectors, 1);
        Engine engine2 = new Engine( connectors, 2);
        Shield shield = new Shield( connectors);
        Storage storage = new Storage(connectors, 3, false);
        Structural structural = new Structural(connectors);
        Vital vital = new Vital(connectors, 0);

        Cabin core = (Cabin) ship.getComponent(6, 6);

        System.out.println("==== BEFORE ====");
        printShipGrid(ship);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(battery2, 5, 5);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

        System.out.println("==== AFTER ====");
        printShipGrid(ship);

        List<Component> expectedVisited = new ArrayList<>();
        List<Component> actualVisited = new ArrayList<>();

        expectedVisited.add(core);
        expectedVisited.add(cabin);
        expectedVisited.add(battery);
        expectedVisited.add(engine);
        expectedVisited.add(shield);
        expectedVisited.add(battery2);
        expectedVisited.add(engine2);
        expectedVisited.add(storage);

        ship.traverse(actualVisited::add);

        int size = expectedVisited.size();
        System.out.println("\tsize: " + size + " | actualVisited.size(): " + actualVisited.size());

        for (i = 0; i < size; i++) {
            assertEquals(expectedVisited.get(i), actualVisited.get(i));
        }

        assertFalse(actualVisited.contains(vital));
        assertFalse(actualVisited.contains(structural));
        assertFalse(actualVisited.contains(cannon));

    }

    @Test
    void getNearestComponents() {
        Ship ship = new Ship(2);

        int[] connectors = new int[4];

        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery battery = new Battery(connectors, 3);
        Battery battery2 = new Battery(connectors, 3);
        Cabin cabin = new Cabin(connectors, false);
        Cannon cannon = new Cannon(connectors, 1);
        Engine engine = new Engine(connectors, 1);
        Engine engine2 = new Engine( connectors, 2);
        Shield shield = new Shield( connectors);
        Storage storage = new Storage(connectors, 3, false);
        Structural structural = new Structural(connectors);
        Vital vital = new Vital(connectors, 0);

        Cabin core = (Cabin) ship.getComponent(6, 6);

        System.out.println("==== BEFORE ====");
        printShipGrid(ship);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

        System.out.println("==== AFTER ====");
        printShipGrid(ship);

        // Asserting the shield's neighbours
        assertEquals(null, ship.getNearestComponents(shield)[0]);
        assertEquals(core, ship.getNearestComponents(shield)[1]);
        assertEquals(null, ship.getNearestComponents(shield)[2]);
        assertEquals(null, ship.getNearestComponents(shield)[3]);

        // Asserting the core's neighbours
        assertEquals(cabin, ship.getNearestComponents(core)[0]);
        assertEquals(battery, ship.getNearestComponents(core)[1]);
        assertEquals(engine, ship.getNearestComponents(core)[2]);
        assertEquals(shield, ship.getNearestComponents(core)[3]);

        // Asserting the engine2's neighbours
        assertEquals(battery, ship.getNearestComponents(engine2)[0]);
        assertEquals(null, ship.getNearestComponents(engine2)[1]);
        assertEquals(null, ship.getNearestComponents(engine2)[2]);
        assertEquals(engine, ship.getNearestComponents(engine2)[3]);
    }

    @Test
    void addComponentThenGetComponent() {
        Ship ship = new Ship(2);

        int[] connectors = new int[4];

        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery battery = new Battery(connectors, 3);
        Battery battery2 = new Battery(connectors, 3);
        Cabin cabin = new Cabin(connectors, false);
        Cannon cannon = new Cannon(connectors, 1);
        Engine engine = new Engine(connectors, 1);
        Engine engine2 = new Engine( connectors, 2);
        Shield shield = new Shield( connectors);
        Storage storage = new Storage(connectors, 3, false);
        Structural structural = new Structural(connectors);
        Vital vital = new Vital(connectors, 0);

        System.out.println("==== BEFORE ====");
        printShipGrid(ship);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

        System.out.println("==== AFTER ====");
        printShipGrid(ship);

        // All these components should be added
        assertEquals(ship.getComponent(6, 7), battery);
        assertEquals(ship.getComponent(5, 6), cabin);
        assertEquals(ship.getComponent(8, 4), cannon);
        assertEquals(ship.getComponent(7, 6), engine);
        assertEquals(ship.getComponent(6, 5), shield);
        assertEquals(ship.getComponent(4, 5), storage);
        assertEquals(ship.getComponent(3, 2), structural);
        assertEquals(ship.getComponent(2, 2), vital);

        // No components are in these coordinates
        assertNull(ship.getComponent(1, 1));
        assertNull(ship.getComponent(2, 4));
        assertNull(ship.getComponent(7, 7));
        assertNull(ship.getComponent(5, 7));

        // Testing edge cases
        // (1) - OutOfGridException
        try {
            ship.addComponent(engine, -1, 0);
        }
        catch (OutOfGridException e) {
            System.out.println("OutOfGridException CAUGHT (1)");
        }

        try {
            ship.addComponent(engine, 2, -1);
        }
        catch (OutOfGridException e) {
            System.out.println("OutOfGridException CAUGHT (2)");
        }

        try {
            ship.addComponent(engine, -22, -7);
        }
        catch (OutOfGridException e) {
            System.out.println("OutOfGridException CAUGHT (3)");
        }

        // (2) - NullComponentException
        try {
            ship.addComponent(null, 3, 0);
        }
        catch (NullComponentException e) {
            System.out.println("NullComponentException CAUGHT");
        }

        // (3) - ExistingComponentException
        try {
            ship.addComponent(engine, 6, 6);
        }
        catch (ExistingComponentException e) {
            System.out.println("ExistingComponentException CAUGHT");
        }
    }

    @Test
    void removeComponent() {
        Ship ship = new Ship(3);

        int[] connectors = new int[4];

        for (int i = 0; i < 4; i++) {
            connectors[i] = THREE_PIPES.ordinal();
        }

        Battery battery = new Battery(connectors, 3);
        Battery battery2 = new Battery(connectors, 3);
        Cabin cabin = new Cabin(connectors, false);
        Cannon cannon = new Cannon(connectors, 1);
        Engine engine = new Engine(connectors, 1);
        Engine engine2 = new Engine( connectors, 2);
        Shield shield = new Shield( connectors);
        Storage storage = new Storage(connectors, 3, false);
        Structural structural = new Structural(connectors);
        Vital vital = new Vital(connectors, 0);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
        printShipGrid(ship);

        // Removing components
        try {
            ship.removeComponent(-1, -1);
            throw new Exception("Should not have removed it");
        }
        catch (OutOfGridException e) {
            System.out.println("OutOfGridException CAUGHT");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            ship.removeComponent(11, 12);
            throw new Exception("Should not have removed it");
        }
        catch (OutOfGridException e) {
            System.out.println("OutOfGridException CAUGHT");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Removing the vital
        ship.removeComponent(2, 2);
        assertNull(ship.getComponent(2, 2));

        // Removing the cannon
        ship.removeComponent(8, 4);
        assertNull(ship.getComponent(8, 4));
    }
}