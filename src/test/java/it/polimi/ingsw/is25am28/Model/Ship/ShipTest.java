package it.polimi.ingsw.is25am28.Model.Ship;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Model.Exceptions.*;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;

import javafx.util.Pair;

import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.is25am28.Model.Components.VitalType.BROWN_VITAL;
import static it.polimi.ingsw.is25am28.Model.Components.VitalType.PURPLE_VITAL;
import static it.polimi.ingsw.is25am28.Model.Connector.*;
import static it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType.BROWN_ALIEN;
import static it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType.PURPLE_ALIEN;
import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

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

    Ship initCustomShip() {
        Ship ship = new Ship(1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        /*
               ==== Ship Configuration (LEVEL 1) ====
            \       4       5       6       7       8
            4                       a
            5               b       c       d
            6       e       f       g       h       i
            7       j       k       l       m       n
            8       o       p               q       r

            Total components = 18 (17 + 1 core)

            a = doubleCannon1 at (4, 6)
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
        ship.addComponent(doubleCannon1, 4, 6);
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

        return ship;
    }

    @Test
    void generateComponentSubLists() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

//        System.out.println("==== BEFORE ====");
//        printShipGrid(ship);

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

//        System.out.println("==== AFTER ====");
//        printShipGrid(ship);

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
    void generateComponentSubLists_testingInitShipForDuplicateComponents() {
        Ship ship = new Ship(1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        /*
               ==== Ship Configuration (LEVEL 1) ====
            \       4       5       6       7       8
            4                       a
            5               b       c       d
            6       e       f       g       h       i
            7       j       k       l       m       n
            8       o       p               q       r

            Total components = 18 (17 + 1 core)

            a = doubleCannon1 at (4, 6)
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
        ship.addComponent(doubleCannon1, 4, 6);
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

        List<Battery> expectedBatteryList = new ArrayList<>();
        List<Cannon> expectedCannonList = new ArrayList<>();
        List<Engine> expectedEngineList = new ArrayList<>();
        List<Cabin> expectedCabinList = new ArrayList<>();
        List<Shield> expectedShieldList = new ArrayList<>();
        List<Storage> expectedStorageList = new ArrayList<>();
        List<Vital> expectedVitalList = new ArrayList<>();

        expectedBatteryList.add(tripleBattery1);

        expectedCannonList.add(singleCannon1);
        expectedCannonList.add(singleCannon2);
        expectedCannonList.add(doubleCannon1);
        expectedCannonList.add(doubleCannon2);

        expectedEngineList.add(singleEngine1);
        expectedEngineList.add(singleEngine2);
        expectedEngineList.add(singleEngine3);
        expectedEngineList.add(doubleEngine1);

        expectedCabinList.add(cabin1);
        expectedCabinList.add((Cabin) ship.getComponent(6, 6));   // Core

        expectedShieldList.add(shield1);

        expectedStorageList.add(normalDoubleStorage1);
        expectedStorageList.add(normalTripleStorage1);
        expectedStorageList.add(specialSingleStorage1);
        expectedStorageList.add(specialDoubleStorage1);

        expectedVitalList.add(purpleVital1);
        expectedVitalList.add(brownVital1);

        // Verifying each sub-list's size matches
        assertEquals(expectedBatteryList.size(), ship.getBatteryList().size());
        assertEquals(expectedCannonList.size(), ship.getCannonList().size());
        assertEquals(expectedEngineList.size(), ship.getEngineList().size());
        assertEquals(expectedCabinList.size(), ship.getCabinList().size());
        assertEquals(expectedShieldList.size(), ship.getShieldList().size());
        assertEquals(expectedStorageList.size(), ship.getStorageList().size());
        assertEquals(expectedVitalList.size(), ship.getVitalList().size());

        // Verifying the content of each sub-list is the same
        assertTrue(ship.getBatteryList().containsAll(expectedBatteryList));
        assertTrue(ship.getCannonList().containsAll(expectedCannonList));
        assertTrue(ship.getEngineList().containsAll(expectedEngineList));
        assertTrue(ship.getCabinList().containsAll(expectedCabinList));
        assertTrue(ship.getShieldList().containsAll(expectedShieldList));
        assertTrue(ship.getStorageList().containsAll(expectedStorageList));
        assertTrue(ship.getVitalList().containsAll(expectedVitalList));
    }

    @Test
    void getAllDoubleComponents() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Cannon doubleCannon = new Cannon(connectors, 2, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 1, "");
        Engine doubleEngine = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

        Cabin core = (Cabin) ship.getComponent(6, 6);

//        System.out.println("==== BEFORE ====");
//        printShipGrid(ship);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(doubleCannon, 8, 6);
        ship.addComponent(doubleCannon, 8, 7);
        ship.addComponent(doubleCannon, 11, 11);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(doubleEngine, 5, 7);
        ship.addComponent(doubleEngine, 10, 10);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

//        System.out.println("==== AFTER ====");
//        printShipGrid(ship);

        ship.generateComponentSubLists();

        List<Cannon> expectedDoubleCannons = new ArrayList<>();
        List<Engine> expectedDoubleEngines = new ArrayList<>();

        expectedDoubleCannons.add(doubleCannon);
        expectedDoubleCannons.add(doubleCannon);

        expectedDoubleEngines.add(doubleEngine);

        assertTrue(ship.getDoubleCannons().containsAll(expectedDoubleCannons));
        assertTrue(ship.getDoubleEngines().containsAll(expectedDoubleEngines));
    }

    @Test
    void getAvailableEnergyBeforeAndAfterConsumption() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

        Cabin core = (Cabin) ship.getComponent(6, 6);

//        System.out.println("==== BEFORE ====");
//        printShipGrid(ship);

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

//        System.out.println("==== AFTER ====");
//        printShipGrid(ship);

        ship.generateComponentSubLists();

        // Before consumption
        assertEquals(6, ship.getAvailableEnergy());

        // After consumption
        ship.consumeEnergy(2);
        assertEquals(4, ship.getAvailableEnergy());

        // Overconsumption
        InsufficientEnergyException iee = assertThrowsExactly(InsufficientEnergyException.class, () -> ship.consumeEnergy(20));

//        try {
//            ship.consumeEnergy(20);
//        }
//        catch (InsufficientEnergyException e) {
//            System.out.println("InsufficientEnergyException CAUGHT");
//        }
//        catch (Exception e) {
//            fail("Wrong exception thrown");
//        }
//

        int prevEnergy = ship.getAvailableEnergy();

        // Illegal value, ship's total energy remains the same
        ship.consumeEnergy(-20);
        assertEquals(prevEnergy, ship.getAvailableEnergy());
    }

    @Test
    void addLifeformToCabin_addingPurpleAlienTest() {
        Ship ship = initCustomShip();

//        Map<Integer, Pair<Integer, Integer>> chosenAliens = new HashMap<>();
//
//        chosenAliens.put(1, new Pair<Integer, Integer>(7, 7));
//
//        ShipJSON shipJson = new ShipJSON("p1", chosenAliens);
//
//        ship.setChosenAliensForEligibleCabins(shipJson);

        ship.addLifeformToCabin(7, 7, PURPLE_ALIEN);

        assertEquals(LifeformType.PURPLE_ALIEN.ordinal(), ((Cabin) ship.getComponent(7, 7)).getInhabitants().getFirst().getLifeformType().ordinal());
        assertEquals(0, ((Cabin) ship.getComponent(7, 7)).getAvailableSpace());
    }

    @Test
    void getFirePower() {
        List<ComponentHelper<Void>> doubleCannonCoords;
        int expectedFirePower;
        Ship ship;

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        // Case 0 - No cannons + no purple alien on board
        ship = new Ship(-1);
        expectedFirePower = 0;
        assertEquals(expectedFirePower, ship.getFirePower(null));

        // Case 1 - No cannons, + purple alien on board
        ship.addComponent(new Cabin(connectors, false, ""), 6, 7);
        ship.addComponent(new Vital(connectors, PURPLE_VITAL.ordinal(), ""), 6, 8);

//        alienCoords = new HashMap<>();
//        alienCoords.put(PURPLE_ALIEN.ordinal(), new Pair<>(6, 7));
//        shipJSON = new ShipJSON("p1", alienCoords);
//        ship.setChosenAliensForEligibleCabins(shipJSON);

        ship.addLifeformToCabin(6, 7, PURPLE_ALIEN);

        expectedFirePower = 0;
        assertEquals(expectedFirePower, ship.getFirePower(null));

        // Initializing a custom ship (it has 2 single cannons and 2 double cannons)
        ship = initCustomShip();

        // Case 2 - baseline firepower + no purple alien on board
        doubleCannonCoords = new ArrayList<>();
        expectedFirePower = 2;
        assertEquals(expectedFirePower, ship.getFirePower(doubleCannonCoords));

//        alienCoords = new HashMap<>();
//        alienCoords.put(PURPLE_ALIEN.ordinal(), new Pair<>(7, 7));
//        shipJSON = new ShipJSON("p1", alienCoords);
//        ship.setChosenAliensForEligibleCabins(shipJSON);

        ship.addLifeformToCabin(7, 7, PURPLE_ALIEN);

        // Case 3 - baseline engine power + purple alien on board
        doubleCannonCoords = new ArrayList<>();
        expectedFirePower = 4;
        assertEquals(expectedFirePower, ship.getFirePower(doubleCannonCoords));

        // Case 4 - total engine power + purple alien on board
        doubleCannonCoords = new ArrayList<>();
        //doubleCannonCoords.add(new Pair<>(4, 6));
//        doubleCannonCoords.add(new ArrayList<>(Arrays.asList(4, 6)));
        doubleCannonCoords.add(new ComponentHelper<>(4, 6));
        //doubleCannonCoords.add(new Pair<>(6, 8));
//        doubleCannonCoords.add(new ArrayList<>(Arrays.asList(6, 8)));
        doubleCannonCoords.add(new ComponentHelper<>(6, 8));
        expectedFirePower = 8;
        assertEquals(expectedFirePower, ship.getFirePower(doubleCannonCoords));

        // Burning all energy
        ship.consumeEnergy(ship.getAvailableEnergy());

        // Case 5 - no energy available + purple alien on board
        doubleCannonCoords = new ArrayList<>();
        //doubleCannonCoords.add(new Pair<>(4, 6));
//        doubleCannonCoords.add(new ArrayList<>(Arrays.asList(4, 6)));
        doubleCannonCoords.add(new ComponentHelper<>(4, 6));
        //doubleCannonCoords.add(new Pair<>(6, 8));
//        doubleCannonCoords.add(new ArrayList<>(Arrays.asList(6, 8)));
        doubleCannonCoords.add(new ComponentHelper<>(6, 8));
        expectedFirePower = 4;
        assertEquals(expectedFirePower, ship.getFirePower(doubleCannonCoords));
    }

    @Test
    void getEnginePower() {
        int expectedEnginePower, batteries;
        Ship ship;

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        // Case 0 - No engines + no brown alien on board
        ship = new Ship(-1);
        batteries = 0;
        expectedEnginePower = 0;
        assertEquals(expectedEnginePower, ship.getEnginePower(batteries));

        // Case 1 - No engines, + brown alien on board
        ship.addComponent(new Cabin(connectors, false, ""), 6, 7);
        ship.addComponent(new Vital(connectors, BROWN_VITAL.ordinal(), ""), 6, 8);

//        alienCoords = new HashMap<>();
//        alienCoords.put(BROWN_ALIEN.ordinal(), new Pair<>(6, 7));
//        shipJSON = new ShipJSON("p1", alienCoords);
//        ship.setChosenAliensForEligibleCabins(shipJSON);

        ship.addLifeformToCabin(6, 7, BROWN_ALIEN);

        batteries = 0;
        expectedEnginePower = 0;
        assertEquals(expectedEnginePower, ship.getEnginePower(batteries));

        // Initializing a custom ship (it has 3 single engines and 1 double engine)
        ship = initCustomShip();

        // Case 2 - baseline engine power + no brown alien on board
        batteries = 0;
        expectedEnginePower = 3;
        assertEquals(expectedEnginePower, ship.getEnginePower(batteries));

//        alienCoords = new HashMap<>();
//        alienCoords.put(BROWN_ALIEN.ordinal(), new Pair<>(7, 7));
//        shipJSON = new ShipJSON("p1", alienCoords);
//        ship.setChosenAliensForEligibleCabins(shipJSON);

        ship.addLifeformToCabin(7, 7, BROWN_ALIEN);

        // Case 3 - baseline engine power + brown alien on board
        batteries = 0;
        expectedEnginePower = 5;
        assertEquals(expectedEnginePower, ship.getEnginePower(batteries));

        // Case 4 - total engine power + brown alien on board
        batteries = 1;
        expectedEnginePower = 7;
        assertEquals(expectedEnginePower, ship.getEnginePower(batteries));

        // Burning all energy
        ship.consumeEnergy(ship.getAvailableEnergy());

        // Case 5 - no energy available + brown alien on board
        batteries = 1;
        expectedEnginePower = 5;
        assertEquals(expectedEnginePower, ship.getEnginePower(batteries));
    }

    @Test
    void addLifeformToCabin() {

    }

    @Test
    void removeLifeformFromCabin() {

    }

    @Test
    void getExposedConnectorAmount() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");
        Vital vital2 = new Vital(connectors, 0, "");

        Cabin core = (Cabin) ship.getComponent(6, 6);

//        System.out.println("==== BEFORE ====");
//        printShipGrid(ship);

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
        ship.addComponent(vital, 3, 5);

//        System.out.println("==== AFTER ====");
//        printShipGrid(ship);

        // Expected from the ship created above
        // (counted by looking at the ship printed to terminal)
        int expectedAmountOfExposedComponents = 16;

        assertEquals(expectedAmountOfExposedComponents, ship.getExposedConnectorAmount());
    }

    @Test
    void getAllItemsAndTheirValue() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Storage leftStorage = new Storage(connectors, 3, false, "");
        Storage rightStorage = new Storage(connectors, 3, false, "");
        Storage topStorage = new Storage(connectors, 2, true, "");
        Storage disconnectedStorage = new Storage(connectors, 3, false, "");

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
        ship.addComponent(disconnectedStorage, 7, 8);

//        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
//        printShipGrid(ship);

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
        Ship ship = new Ship(-1);


        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        List<Integer> connector1 = new ArrayList<>();

        connector1.add( ZERO_PIPES.ordinal() );
        connector1.add( THREE_PIPES.ordinal() );
        connector1.add( TWO_PIPES.ordinal() );
        connector1.add( TWO_PIPES.ordinal() );

        List<Integer> connector2 = new ArrayList<>();

        connector2.add( ONE_PIPE.ordinal() );
        connector2.add( TWO_PIPES.ordinal() );
        connector2.add( ONE_PIPE.ordinal() );
        connector2.add( THREE_PIPES.ordinal() );


        List<Integer> connector3 = new ArrayList<>();

        connector3.add( THREE_PIPES.ordinal() );
        connector3.add( ZERO_PIPES.ordinal() );
        connector3.add( TWO_PIPES.ordinal() );
        connector3.add( ZERO_PIPES.ordinal() );

        List<Integer> connector4 = new ArrayList<>();

        connector4.add( TWO_PIPES.ordinal() );
        connector4.add( THREE_PIPES.ordinal() );
        connector4.add( TWO_PIPES.ordinal() );
        connector4.add( THREE_PIPES.ordinal() );

        List<Integer> connector5 = new ArrayList<>();

        connector5.add( ONE_PIPE.ordinal() );
        connector5.add( ZERO_PIPES.ordinal() );
        connector5.add( ZERO_PIPES.ordinal() );
        connector5.add( THREE_PIPES.ordinal() );


        Battery battery = new Battery(connector3, 3, "");
        Cabin cabin = new Cabin(connector4, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connector4, 1, "");
        Engine engine2 = new Engine(connector5, 2, "");
        Shield shield = new Shield(connector2, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connector2, "");
        Vital vital = new Vital(connectors, 0, "");

        Cabin core = (Cabin) ship.getComponent(6, 6);

        // Adding the components created above
        ship.addComponent(battery, 6, 7); // On the ship
        ship.addComponent(cabin, 5, 6); // On the ship
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6); // On the ship
        ship.addComponent(engine2, 7, 7); // On the ship
        ship.addComponent(shield, 6, 5); // On the ship
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

//        System.out.println("==== SHIP CONFIGURATION ====");
//        printShipGrid(ship);

        List<Component> wrongs = ship.removeComponent(0, 0);

//        if (!wrongs.isEmpty()) {
//            System.out.println("\nWRONG COMPONENTS FOUND:");
//            for (Component c : wrongs) {
//                System.out.println(" - " + c.toString() + " at coordinates (" + c.getPosition()[0] + ", " + c.getPosition()[1] + ")");
//            }
//        }

        List<Component> expectedWrongs = new ArrayList<>();
        expectedWrongs.add(vital);
        expectedWrongs.add(structural);
        expectedWrongs.add(storage);
        expectedWrongs.add(cannon);
        expectedWrongs.add(battery);

        assertTrue(wrongs.containsAll(expectedWrongs));
        assertTrue(ship.validateShip());

        // Fixing the ship and performing the validity check again
        // Battery moved from (6, 7) to (4, 6)
        // engine2 moved in (7, 7) to (8, 5)
        // Adding structural at (7, 5) with connector layout of connector

        ship.removeComponent(6, 7);
        ship.removeComponent(7, 7);
        ship.addComponent(battery, 4, 6);
        ship.addComponent(engine2, 8, 5);
        ship.addComponent(structural, 7, 5);

//        System.out.println("\n==== SHIP CONFIGURATION (after moving some components (see the test code)) ====");
//        printShipGrid(ship);

        wrongs = ship.removeComponent(0, 0);

//        if (!wrongs.isEmpty()) {
//            System.out.println("\nWRONG COMPONENTS FOUND:");
//            for (Component c : wrongs) {
//                System.out.println(" - " + c.toString() + " at coordinates (" + c.getPosition()[0] + ", " + c.getPosition()[1] + ")");
//            }
//        }

        assertTrue(wrongs.isEmpty());
        assertTrue(ship.validateShip());
    }

    @Test
    void getGridRow() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

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

//        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
//        printShipGrid(ship);

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
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

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

//        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
//        printShipGrid(ship);

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
        Ship ship = new Ship(-1);

        assertTrue(ship.validateShip());

        ship = initCustomShip();

        assertTrue(ship.validateShip());
    }

    @Test
    void traverse() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();
        int i;

        // Default connector is THREE_PIPES
        for (i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

        Cabin core = (Cabin) ship.getComponent(6, 6);

//        System.out.println("==== BEFORE ====");
//        printShipGrid(ship);

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

//        System.out.println("==== AFTER ====");
//        printShipGrid(ship);

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
//        System.out.println("\tsize: " + size + " | actualVisited.size(): " + actualVisited.size());

        for (i = 0; i < size; i++) {
            assertEquals(expectedVisited.get(i), actualVisited.get(i));
        }

        assertFalse(actualVisited.contains(vital));
        assertFalse(actualVisited.contains(structural));
        assertFalse(actualVisited.contains(cannon));

    }

    @Test
    void traverseInTheCorrectOrder() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 2, "");
        Battery battery3 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(battery2, 4, 4);
        ship.addComponent(battery3, 5, 5);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

//        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
//        printShipGrid(ship);

        List<Component> actualVisitingOrder = new ArrayList<Component>();
        List<Component> expectedVisitingOrder = new ArrayList<Component>();

        // Layer 0
        expectedVisitingOrder.add(ship.getComponent(6, 6));

        // Layer 1
        expectedVisitingOrder.add(cabin);
        expectedVisitingOrder.add(battery);
        expectedVisitingOrder.add(engine);
        expectedVisitingOrder.add(shield);

        // Layer 2
        expectedVisitingOrder.add(battery3);
        expectedVisitingOrder.add(engine2);

        // Layer 3
        expectedVisitingOrder.add(storage);

        // Layer 4
        expectedVisitingOrder.add(battery2);

        ship.traverse(actualVisitingOrder::add);

        assertEquals(expectedVisitingOrder.size(), actualVisitingOrder.size());

        for (int i = 0; i < actualVisitingOrder.size(); i++) {
//            System.out.println("Checking index " + i);
            assertEquals(expectedVisitingOrder.get(i), actualVisitingOrder.get(i));
        }
    }

    @Test
    void getNearestComponents() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

        Cabin core = (Cabin) ship.getComponent(6, 6);

//        System.out.println("==== BEFORE ====");
//        printShipGrid(ship);

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

//        System.out.println("==== AFTER ====");
//        printShipGrid(ship);

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
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

//        System.out.println("==== BEFORE ====");
//        printShipGrid(ship);

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

//        System.out.println("==== AFTER ====");
//        printShipGrid(ship);

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
        OutOfGridException ooge = assertThrowsExactly(OutOfGridException.class, () -> ship.addComponent(engine, -1, 0));

//        try {
//            ship.addComponent(engine, -1, 0);
//        }
//        catch (OutOfGridException e) {
//            System.out.println("OutOfGridException CAUGHT (1)");
//        }
//        catch (Exception e) {
//            fail("Wrong exception caught");
//        }

        ooge = assertThrowsExactly(OutOfGridException.class, () -> ship.addComponent(engine, 2, -1));

//        try {
//            ship.addComponent(engine, 2, -1);
//        }
//        catch (OutOfGridException e) {
//            System.out.println("OutOfGridException CAUGHT (2)");
//        }
//        catch (Exception e) {
//            fail("Wrong exception caught");
//        }

        ooge = assertThrowsExactly(OutOfGridException.class, () -> ship.addComponent(engine, -22, -7));

//        try {
//            ship.addComponent(engine, -22, -7);
//        }
//        catch (OutOfGridException e) {
//            System.out.println("OutOfGridException CAUGHT (3)");
//        }
//        catch (Exception e) {
//            fail("Wrong exception caught");
//        }

        // (2) - NullComponentException
        NullComponentException nce = assertThrowsExactly(NullComponentException.class, () -> ship.addComponent(null, 3, 0));

//        try {
//            ship.addComponent(null, 3, 0);
//        }
//        catch (NullComponentException e) {
//            System.out.println("NullComponentException CAUGHT");
//        }
//        catch (Exception e) {
//            fail("Wrong exception caught");
//        }

        // (3) - ExistingComponentException
        ExistingComponentException ece = assertThrowsExactly(ExistingComponentException.class, () -> ship.addComponent(engine, 6, 6));

//        try {
//            ship.addComponent(engine, 6, 6);
//        }
//        catch (ExistingComponentException e) {
//            System.out.println("ExistingComponentException CAUGHT");
//        }
//        catch (Exception e) {
//            fail("Wrong exception caught");
//        }
    }

    @Test
    void removeComponent() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Battery battery = new Battery(connectors, 3, "");
        Battery battery2 = new Battery(connectors, 2, "");
        Battery battery3 = new Battery(connectors, 3, "");
        Cabin cabin = new Cabin(connectors, false, "");
        Cannon cannon = new Cannon(connectors, 1, "");
        Engine engine = new Engine(connectors, 1, "");
        Engine engine2 = new Engine( connectors, 2, "");
        Shield shield = new Shield( connectors, "");
        Storage storage = new Storage(connectors, 3, false, "");
        Structural structural = new Structural(connectors, "");
        Vital vital = new Vital(connectors, 0, "");

        // Adding the components created above
        ship.addComponent(battery, 6, 7);
        ship.addComponent(battery2, 4, 4);
        ship.addComponent(battery3, 5, 5);
        ship.addComponent(cabin, 5, 6);
        ship.addComponent(cannon, 8, 4);
        ship.addComponent(engine, 7, 6);
        ship.addComponent(engine2, 7, 7);
        ship.addComponent(shield, 6, 5);
        ship.addComponent(storage, 4, 5);
        ship.addComponent(structural, 3, 2);
        ship.addComponent(vital, 2, 2);

//        System.out.println("==== CURRENT SHIP CONFIGURATION ====");
//        printShipGrid(ship);

        // Removing components
        OutOfGridException ooge = assertThrowsExactly(OutOfGridException.class, () -> ship.removeComponent(-1, -1));

//        try {
//            ship.removeComponent(-1, -1);
//            throw new Exception("Should not have removed it");
//        }
//        catch (OutOfGridException e) {
//            System.out.println("OutOfGridException CAUGHT");
//        }
//        catch (Exception e) {
//            fail("Wrong exception caught");
//        }

        ooge = assertThrowsExactly(OutOfGridException.class, () -> ship.removeSingleComponent(11, 12));

//        try {
//            ship.removeComponent(11, 12);
//            throw new Exception("Should not have removed it");
//        }
//        catch (OutOfGridException e) {
//            System.out.println("OutOfGridException CAUGHT");
//        }
//        catch (Exception e) {
//            fail("Wrong exception caught");
//        }

        ship.recreateShipGrid();
        List<Component> removed;

//        System.out.println("AFTER REMOVING ILLEGAL COMPONENTS");
//        printShipGrid(ship);

        // Removing the vital
        removed = ship.removeComponent(2, 2);
        assertNull(ship.getComponent(2, 2));
        assertEquals(0, removed.size());

        // Removing the cannon
        removed = ship.removeComponent(8, 4);
        assertNull(ship.getComponent(8, 4));
        assertEquals(0, removed.size());

        // Removing battery at (5, 5), which should detach the storage at (4, 5) and
        // the battery at (4, 4), thus these 3 components should be present in the removed list
        List<Component> expectedRemoved = new ArrayList<>();
        expectedRemoved.add(storage);
        expectedRemoved.add(battery2);
        expectedRemoved.add(battery3);

        removed = ship.removeComponent(5, 5);

//        System.out.println("AFTER REMOVING HANGING BRANCH");
//        printShipGrid(ship);

        assertEquals(expectedRemoved.size(), removed.size());
        assertTrue(removed.containsAll(expectedRemoved));

        // Trying to remove a single component that exists
        // This list should only contain battery
        expectedRemoved = new ArrayList<>();
        expectedRemoved.add(battery);

        removed = ship.removeComponent(6, 7);

//        printShipGrid(ship);

        assertEquals(expectedRemoved.size(), removed.size());
        assertTrue(removed.containsAll(expectedRemoved));

        // Trying to remove a single component that exists
        // This list should contain engine and engine2 (engine2 was on a hanging branch)
        expectedRemoved = new ArrayList<>();
        expectedRemoved.add(engine);
        expectedRemoved.add(engine2);

        removed = ship.removeComponent(7, 6);

//        printShipGrid(ship);

        assertEquals(expectedRemoved.size(), removed.size());
        assertTrue(removed.containsAll(expectedRemoved));
    }

    @Test
    void addingBothAliensToTheShip() {
        List<Integer> connectors = new ArrayList<>();
        Ship ship;

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add(THREE_PIPES.ordinal());
        }

        // (1) - Adding both aliens
        ship = initCustomShip();
        ship.removeComponent(6, 8);
        ship.addComponent(new Cabin(connectors, false, ""), 6, 8);  // Adding another cabin to the brown vital unit
        ship.generateComponentSubLists();

        ship.addLifeformToCabin(7, 7, PURPLE_ALIEN);
        ship.addLifeformToCabin(6, 8, BROWN_ALIEN);

        // Expecting to have 2 aliens only onboard of the ship
        assertEquals(2, ship.getAllLifeforms().stream().filter(l -> (l.getLifeformType() == BROWN_ALIEN || l.getLifeformType() == LifeformType.PURPLE_ALIEN)).toList().size());
    }

    @Test
    void addingTheWrongAlienToAnAlienLifeEligibleCabin() {
        Map<Integer, Pair<Integer, Integer>> chosenAliens;
        Ship ship;

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add(THREE_PIPES.ordinal());
        }

        // Adding purpleAlien to a brownAlien-eligible cabin (the one @ coords (6, 8))
        ship = initCustomShip();
        ship.removeComponent(6, 8);
        ship.addComponent(new Cabin(connectors, false, ""), 6, 8);  // Adding another cabin to the brown vital unit
        ship.generateComponentSubLists();

        NoSupportVitalFoundException nsvfe = assertThrows(NoSupportVitalFoundException.class, () -> { ship.addLifeformToCabin(6, 8, PURPLE_ALIEN); });
        assertEquals(0, ((Cabin) ship.getComponent(6, 8)).getInhabitants().size());
    }

    @Test
    void removingVitalUnitDoesNotRemoveAlien() {
        // Testing all cases where if a vital unit is destroyed, then
        // any aliens that were in neighbouring cabins need to check if they
        // now have any other neighbouring vital units of their same type, otherwise
        // they cannot live in their cabins anymore and thus will be removed

        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Cabin cabin = new Cabin(connectors, false, "");
        Vital purpleVital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal(), "");
        Vital purpleVital2 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal(), "");
        Vital brownVital1 = new Vital(connectors, BROWN_VITAL.ordinal(), "");
        Vital brownVital2 = new Vital(connectors, BROWN_VITAL.ordinal(), "");

        ship.addComponent(cabin, 6, 7);
        ship.addComponent(purpleVital1, 6, 8);
        ship.addComponent(purpleVital2, 5, 7);
        ship.addComponent(brownVital1, 7, 7);

        // Case 1 - Removing the vital unit has no problem (PURPLE alien, PURPLE vital)
        // (i.e.: another vital unit is now supporting the current alien)
        ship.addLifeformToCabin(6, 7, PURPLE_ALIEN);
        ship.removeComponent(5, 7);
        assertEquals(1, cabin.getInhabitants().size());
        assertEquals(PURPLE_ALIEN, cabin.getInhabitants().getFirst().getLifeformType());
        ship.addComponent(purpleVital2, 5, 7);

        // Case 2 - Removing the vital unit has no problem (PURPLE alien, BROWN vital)
        // (i.e.: another vital unit is now supporting the current alien)
        ship.removeComponent(7, 7);
        assertEquals(1, cabin.getInhabitants().size());
        assertEquals(PURPLE_ALIEN, cabin.getInhabitants().getFirst().getLifeformType());
        ship.addComponent(brownVital1, 7, 7);

        // Case 3 - Removing both purple vital units should remove the purpleAlien in the cabin
        // (i.e.: there are no purpleVitals to support the purpleAlien)
        ship.removeComponent(5, 7);
        ship.removeComponent(6, 8);
        assertTrue(cabin.getInhabitants().isEmpty());
        ship.addComponent(purpleVital2, 5, 7);

        // Substituting a purple vital with a brown vital
        // and adding a brownAlien to the cabin
        ship.addComponent(brownVital2, 6, 8);
        ship.addLifeformToCabin(6, 7, BROWN_ALIEN);

        // Case 4 - Removing the vital unit has no problem (BROWN alien, BROWN vital)
        // (i.e.: another vital unit is now supporting the current alien)
        ship.removeComponent(6, 8);
        assertEquals(1, cabin.getInhabitants().size());
        assertEquals(BROWN_ALIEN, cabin.getInhabitants().getFirst().getLifeformType());
        ship.addComponent(brownVital2, 6, 8);

        // Case 5 - Removing the vital unit has no problem (BROWN alien, PURPLE vital)
        // (i.e.: another vital unit is now supporting the current alien)
        ship.removeComponent(5, 7);
        assertEquals(1, cabin.getInhabitants().size());
        assertEquals(BROWN_ALIEN, cabin.getInhabitants().getFirst().getLifeformType());
        ship.addComponent(purpleVital2, 5, 7);

        // Case 6 - Removing both brown vital units should remove the brownAlien in the cabin
        // (i.e.: there are no brownVitals to support the brownAlien)
        ship.removeComponent(7, 7);
        ship.removeComponent(6, 8);
        assertTrue(cabin.getInhabitants().isEmpty());
    }

    @Test
    void removingVitalUnitDoesNotRemoveHumans() {
        Ship ship = new Ship(-1);

        List<Integer> connectors = new ArrayList<>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add( THREE_PIPES.ordinal() );
        }

        Cabin cabin = new Cabin(connectors, false, "");
        Vital purpleVital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal(), "");
        Vital purpleVital2 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal(), "");
        Vital brownVital1 = new Vital(connectors, BROWN_VITAL.ordinal(), "");

        Lifeform astronaut = new Lifeform(LifeformType.ASTRONAUT);

        ship.addComponent(cabin, 6, 7);
        ship.addComponent(purpleVital1, 6, 8);
        ship.addComponent(purpleVital2, 5, 7);
        ship.addComponent(brownVital1, 7, 7);

        ship.generateComponentSubLists();

        // Adding humans to cabins and checking if all humans are still there
        // even after removing vitals that are neighbouring their cabin
        ship.getCabinList().forEach(
                (Cabin c) -> {
                    if (c != ship.getCore()) {
                        c.addInhabitant(astronaut);
                        c.addInhabitant(astronaut);
                    }
                }
        );

        List<Lifeform> expectedInhabitants = new ArrayList<Lifeform>();
        expectedInhabitants.add(astronaut);
        expectedInhabitants.add(astronaut);

        ship.removeComponent(6, 8);
        for (Cabin c : ship.getCabinList()) {
            assertEquals(2, c.getInhabitants().size());
            for (int i = 0; i < 2; i++) {
                assertEquals(expectedInhabitants.get(i).getLifeformType(), c.getInhabitants().get(i).getLifeformType());
            }
        }

        ship.removeComponent(5, 7);
        for (Cabin c : ship.getCabinList()) {
            assertEquals(2, c.getInhabitants().size());
            for (int i = 0; i < 2; i++) {
                assertEquals(expectedInhabitants.get(i).getLifeformType(), c.getInhabitants().get(i).getLifeformType());
            }
        }

        ship.removeComponent(7, 7);
        for (Cabin c : ship.getCabinList()) {
            assertEquals(2, c.getInhabitants().size());
            for (int i = 0; i < 2; i++) {
                assertEquals(expectedInhabitants.get(i).getLifeformType(), c.getInhabitants().get(i).getLifeformType());
            }
        }
    }
}