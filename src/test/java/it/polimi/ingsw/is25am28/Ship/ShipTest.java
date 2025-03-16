package it.polimi.ingsw.is25am28.Ship;

import it.polimi.ingsw.is25am28.Components.*;

import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Exceptions.OutOfGridException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    void generateComponentSubLists() {
        int grid_rows = 5;
        int grid_cols = 5;
        Ship ship = new Ship(grid_rows, grid_cols);

        /*
                0   1   2   3   4   5
            0  vi2      cb1  ca2
            1       st5 ca1  sr1
            2   st4 ba1 cor  st1 sh1
            3       st3 vi1  en1
            4       ba2 st2      sh2
            5
         */

        Cannon cannon1 = new Cannon(1, 2, 0, null, 1);
        Cannon cannon2 = new Cannon(0, 3, 0, null, 1);
        Structural structural1 = new Structural(2, 3, 0, null);
        Vital vital1 = new Vital(3, 2, 0, null, null);
        Vital vital2 = new Vital(0, 0, 0, null, null);
        Battery battery1 = new Battery(2, 1, 0, null, 3);
        Battery battery2 = new Battery(4, 1, 0, null, 3);
        Cabin cabin1 = new Cabin(0, 2, 0, null, true);
        Storage storage1 = new Storage(1, 3, 0, null, 5, false);
        Shield shield1 = new Shield(2, 4, 0, null);
        Shield shield2 = new Shield(4, 4, 0, null);
        Engine engine1 = new Engine(3, 3, 0, null, 1);
        Structural structural2 = new Structural(4, 2, 0, null);
        Structural structural3 = new Structural(3, 1, 0, null);
        Structural structural4 = new Structural(2, 0, 0, null);
        Structural structural5 = new Structural(1, 1, 0, null);

        ship.addComponent(cannon1, cannon1.getPosition()[0], cannon1.getPosition()[1]);
        ship.addComponent(cannon2, cannon2.getPosition()[0], cannon2.getPosition()[1]);
        ship.addComponent(structural1, structural1.getPosition()[0], structural1.getPosition()[1]);
        ship.addComponent(vital1, vital1.getPosition()[0], vital1.getPosition()[1]);
        ship.addComponent(vital2, vital2.getPosition()[0], vital2.getPosition()[1]);
        ship.addComponent(battery1, battery1.getPosition()[0], battery1.getPosition()[1]);
        ship.addComponent(battery2, battery2.getPosition()[0], battery2.getPosition()[1]);
        ship.addComponent(cabin1, cabin1.getPosition()[0], cabin1.getPosition()[1]);
        ship.addComponent(storage1, storage1.getPosition()[0], storage1.getPosition()[1]);
        ship.addComponent(shield1, shield1.getPosition()[0], shield1.getPosition()[1]);
        ship.addComponent(shield2, shield2.getPosition()[0], shield2.getPosition()[1]);
        ship.addComponent(engine1, engine1.getPosition()[0], engine1.getPosition()[1]);
        ship.addComponent(structural2, structural2.getPosition()[0], structural2.getPosition()[1]);
        ship.addComponent(structural3, structural3.getPosition()[0], structural3.getPosition()[1]);
        ship.addComponent(structural4, structural4.getPosition()[0], structural4.getPosition()[1]);
        ship.addComponent(structural5, structural5.getPosition()[0], structural5.getPosition()[1]);

        assertEquals("Cabin", ship.getComponent(grid_rows/2, grid_cols/2).getClass().getSimpleName());
        assertEquals(cannon1, ship.getComponent(cannon1.getPosition()[0], cannon1.getPosition()[1]));
        assertEquals(cannon2, ship.getComponent(cannon2.getPosition()[0], cannon2.getPosition()[1]));
        assertEquals(structural1, ship.getComponent(structural1.getPosition()[0], structural1.getPosition()[1]));
        assertEquals(vital1, ship.getComponent(vital1.getPosition()[0], vital1.getPosition()[1]));
        assertEquals(vital2, ship.getComponent(vital2.getPosition()[0], vital2.getPosition()[1]));
        assertEquals(battery1, ship.getComponent(battery1.getPosition()[0], battery1.getPosition()[1]));
        assertEquals(battery2, ship.getComponent(battery2.getPosition()[0], battery2.getPosition()[1]));
        assertEquals(cabin1, ship.getComponent(cabin1.getPosition()[0], cabin1.getPosition()[1]));
        assertEquals(storage1, ship.getComponent(storage1.getPosition()[0], storage1.getPosition()[1]));
        assertEquals(shield1, ship.getComponent(shield1.getPosition()[0], shield1.getPosition()[1]));
        assertEquals(shield2, ship.getComponent(shield2.getPosition()[0], shield2.getPosition()[1]));
        assertEquals(engine1, ship.getComponent(engine1.getPosition()[0], engine1.getPosition()[1]));
        assertEquals(structural2, ship.getComponent(structural2.getPosition()[0], structural2.getPosition()[1]));
        assertEquals(structural3, ship.getComponent(structural3.getPosition()[0], structural3.getPosition()[1]));
        assertEquals(structural4, ship.getComponent(structural4.getPosition()[0], structural4.getPosition()[1]));
        assertEquals(structural5, ship.getComponent(structural5.getPosition()[0], structural5.getPosition()[1]));

        // Generating each list, thus sorting all components into their respective category
        ship.generateComponentSubLists();

        List<Battery> batteryList = new ArrayList<Battery>();
        List<Cabin> cabinList = new ArrayList<Cabin>();
        List<Cannon> cannonList = new ArrayList<Cannon>();
        List<Engine> engineList = new ArrayList<Engine>();
        List<Shield> shieldList = new ArrayList<Shield>();
        List<Storage> storageList = new ArrayList<Storage>();
        List<Vital>vitalList = new ArrayList<Vital>();

        batteryList.add(battery1);
        batteryList.add(battery2);
        cabinList.add((Cabin) ship.getComponent(grid_rows/2, grid_cols/2));
        cabinList.add(cabin1);
        cannonList.add(cannon1);
        cannonList.add(cannon2);
        engineList.add(engine1);
        shieldList.add(shield1);
        // shield2 is an isolated component, thus should not be part of the shieldList
        storageList.add(storage1);
        vitalList.add(vital1);
        // vital2 is an isolated component, thus should not be part of the shieldList


        // Verifying that all components were correctly sorted into each category
        assertEquals(batteryList, ship.getBatteryList());
        assertEquals(cabinList, ship.getCabinList());
        assertEquals(cannonList, ship.getCannonList());
        assertEquals(engineList, ship.getEngineList());
        assertEquals(shieldList, ship.getShieldList());
        assertEquals(storageList, ship.getStorageList());
        assertEquals(vitalList, ship.getVitalList());
    }

    @Test
    void getAvailableEnergy() {
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
    }

    @Test
    void getWrongComponents() {
    }

    @Test
    void validateShip() {
    }

    @Test
    void traverse() {
        int grid_rows = 5;
        int grid_cols = 5;
        Ship ship = new Ship(grid_rows, grid_cols);

        // Layer 0 - Core
        // Already added by Ship constructor

        /*
                0   1   2   3   4   5
            0           cb1
            1       st5 ca1  sr1
            2   st4 ba1  co  st1 sh1
            3       st3 vi1  en1
            4           st2
            5
         */

        // Layer 1
        Cannon cannon1 = new Cannon(1, 2, 0, null, 1);
        Structural structural1 = new Structural(2, 3, 0, null);
        Vital vital1 = new Vital(3, 2, 0, null, null);
        Battery battery1 = new Battery(2, 1, 0, null, 3);

        // Layer 2
        Cabin cabin1 = new Cabin(0, 2, 0, null, true);
        Storage storage1 = new Storage(1, 3, 0, null, 5, false);
        Shield shield1 = new Shield(2, 4, 0, null);
        Engine engine1 = new Engine(3, 3, 0, null, 1);
        Structural structural2 = new Structural(4, 2, 0, null);
        Structural structural3 = new Structural(3, 1, 0, null);
        Structural structural4 = new Structural(2, 0, 0, null);
        Structural structural5 = new Structural(1, 1, 0, null);

        // Layer 0 - Core
        // Already added by Ship constructor

        // Layer 1
        ship.addComponent(cannon1, cannon1.getPosition()[0], cannon1.getPosition()[1]);
        ship.addComponent(structural1, structural1.getPosition()[0], structural1.getPosition()[1]);
        ship.addComponent(vital1, vital1.getPosition()[0], vital1.getPosition()[1]);
        ship.addComponent(battery1, battery1.getPosition()[0], battery1.getPosition()[1]);

        // Layer 2
        ship.addComponent(cabin1, cabin1.getPosition()[0], cabin1.getPosition()[1]);
        ship.addComponent(storage1, storage1.getPosition()[0], storage1.getPosition()[1]);
        ship.addComponent(shield1, shield1.getPosition()[0], shield1.getPosition()[1]);
        ship.addComponent(engine1, engine1.getPosition()[0], engine1.getPosition()[1]);
        ship.addComponent(structural2, structural2.getPosition()[0], structural2.getPosition()[1]);
        ship.addComponent(structural3, structural3.getPosition()[0], structural3.getPosition()[1]);
        ship.addComponent(structural4, structural4.getPosition()[0], structural4.getPosition()[1]);
        ship.addComponent(structural5, structural5.getPosition()[0], structural5.getPosition()[1]);

        // Layer 0 - Core
        assertEquals("Cabin", ship.getComponent(grid_rows/2, grid_cols/2).getClass().getSimpleName());

        // Layer 1
        assertEquals(cannon1, ship.getComponent(cannon1.getPosition()[0], cannon1.getPosition()[1]));
        assertEquals(structural1, ship.getComponent(structural1.getPosition()[0], structural1.getPosition()[1]));
        assertEquals(vital1, ship.getComponent(vital1.getPosition()[0], vital1.getPosition()[1]));
        assertEquals(battery1, ship.getComponent(battery1.getPosition()[0], battery1.getPosition()[1]));

        // Layer 2
        assertEquals(cabin1, ship.getComponent(cabin1.getPosition()[0], cabin1.getPosition()[1]));
        assertEquals(storage1, ship.getComponent(storage1.getPosition()[0], storage1.getPosition()[1]));
        assertEquals(shield1, ship.getComponent(shield1.getPosition()[0], shield1.getPosition()[1]));
        assertEquals(engine1, ship.getComponent(engine1.getPosition()[0], engine1.getPosition()[1]));
        assertEquals(structural2, ship.getComponent(structural2.getPosition()[0], structural2.getPosition()[1]));
        assertEquals(structural3, ship.getComponent(structural3.getPosition()[0], structural3.getPosition()[1]));
        assertEquals(structural4, ship.getComponent(structural4.getPosition()[0], structural4.getPosition()[1]));
        assertEquals(structural5, ship.getComponent(structural5.getPosition()[0], structural5.getPosition()[1]));

        Component[] traverseCheckOrder = new Component[13];

        // Layer 0 - Core
        traverseCheckOrder[0] = ship.getComponent(grid_rows/2, grid_cols/2);

        // Layer 1
        traverseCheckOrder[1] = cannon1;
        traverseCheckOrder[2] = structural1;
        traverseCheckOrder[3] = vital1;
        traverseCheckOrder[4] = battery1;

        // Layer 2
        traverseCheckOrder[5] = cabin1;
        traverseCheckOrder[6] = storage1;
        traverseCheckOrder[7] = structural5;
        traverseCheckOrder[8] = shield1;
        traverseCheckOrder[9] = engine1;
        traverseCheckOrder[10] = structural2;
        traverseCheckOrder[11] = structural3;
        traverseCheckOrder[12] = structural4;

        AtomicInteger i = new AtomicInteger(0);

        ship.traverse(
            (Component c) -> {
                assertEquals(traverseCheckOrder[i.get()], c);
                i.set(i.get() + 1);
            }
        );
    }

    @Test
    void getNearestComponents() {
        int grid_rows = 5;
        int grid_cols = 5;
        Ship ship = new Ship(grid_rows, grid_cols);

        Battery battery = new Battery(2, 1, 0, null, 3);
        Cannon cannon = new Cannon(1, 2, 0, null, 1);
        Vital vital = new Vital(3, 2, 0, null, null);
        Structural structural = new Structural(2, 3, 0, null);
        Cabin cabin = new Cabin(4, 4, 0, null, true);

        ship.addComponent(battery, battery.getPosition()[0], battery.getPosition()[1]);
        ship.addComponent(cannon, cannon.getPosition()[0], cannon.getPosition()[1]);
        ship.addComponent(vital, vital.getPosition()[0], vital.getPosition()[1]);
        ship.addComponent(cabin, cabin.getPosition()[0], cabin.getPosition()[1]);
        ship.addComponent(structural, structural.getPosition()[0], structural.getPosition()[1]);

        assertEquals(battery, ship.getComponent(battery.getPosition()[0], battery.getPosition()[1]));
        assertEquals(cannon, ship.getComponent(cannon.getPosition()[0], cannon.getPosition()[1]));
        assertEquals(vital, ship.getComponent(vital.getPosition()[0], vital.getPosition()[1]));
        assertEquals(cabin, ship.getComponent(cabin.getPosition()[0], cabin.getPosition()[1]));
        assertEquals("Cabin", ship.getComponent(grid_rows/2, grid_cols/2).getClass().getSimpleName());

        Component[] neighbours = new Component[4];
        neighbours[0] = cannon;
        neighbours[1] = structural;
        neighbours[2] = vital;
        neighbours[3] = battery;

        Component[] neighboursToTest = ship.getNearestComponents(ship.getComponent(grid_rows/2, grid_cols/2));

        assertEquals(neighbours.length, neighboursToTest.length);

        for (int i = 0; i < neighbours.length; i++) {
            assertEquals(neighbours[i], neighboursToTest[i]);
        }
    }

    @Test
    void removeComponent() {

    }

    @Test
    void addComponent_and_getComponent() {
        Ship ship = new Ship(5, 5);

        Battery battery = new Battery(0, 0, 0, null, 3);
        Cannon cannon = new Cannon(0, 1, 0, null, 1);
        Vital vital = new Vital(0, 2, 0, null, null);
        Cabin core = new Cabin(0, 3, 0, null, true);
        Structural structural = new Structural(0, 4, 0, null);

        ship.addComponent(battery, battery.getPosition()[0], battery.getPosition()[1]);
        ship.addComponent(cannon, cannon.getPosition()[0], cannon.getPosition()[1]);
        ship.addComponent(vital, vital.getPosition()[0], vital.getPosition()[1]);
        ship.addComponent(core, core.getPosition()[0], core.getPosition()[1]);
        ship.addComponent(structural, structural.getPosition()[0], structural.getPosition()[1]);

        assertEquals(battery, ship.getComponent(battery.getPosition()[0], battery.getPosition()[1]));
        assertEquals(cannon, ship.getComponent(cannon.getPosition()[0], cannon.getPosition()[1]));
        assertEquals(vital, ship.getComponent(vital.getPosition()[0], vital.getPosition()[1]));
        assertEquals(core, ship.getComponent(core.getPosition()[0], core.getPosition()[1]));
        assertEquals(structural, ship.getComponent(structural.getPosition()[0], structural.getPosition()[1]));

        // Error cases
        assertNull(ship.getComponent(4, 4));
        assertNull(ship.getComponent(1, 0));

        try {
            ship.getComponent(5, 5);
        }
        catch (OutOfGridException e) {
            System.out.println("Out of grid exception caught");
        }
        catch (Exception e) {
            System.out.println("Other exception caught");
        }

        try {
            ship.getComponent(-14, 19);
        }
        catch (OutOfGridException e) {
            System.out.println("Out of grid exception caught");
        }
        catch (Exception e) {
            System.out.println("Other exception caught");
        }
    }
}
