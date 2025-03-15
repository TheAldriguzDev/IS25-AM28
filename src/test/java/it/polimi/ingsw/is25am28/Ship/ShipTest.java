package it.polimi.ingsw.is25am28.Ship;

import it.polimi.ingsw.is25am28.Components.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    void generateComponentSubLists() {
        Ship ship = new Ship(5, 5);

        Battery battery = new Battery(0, 0, 0, null, 3);
        Cannon cannon = new Cannon(0, 0, 0, null, 1);
        Vital vital = new Vital(0, 0, 0, null, null);
        Cabin core = new Cabin(0, 0, 0, null, true);

        ship.addComponent(battery, 0, 0);
        ship.addComponent(cannon, 0, 1);
        ship.addComponent(vital, 0, 2);
        ship.addComponent(core, 0, 3);

        assertEquals(, );
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
    }

    @Test
    void getNearestComponents() {
    }

    @Test
    void addComponent() {
        Ship ship = new Ship(5, 5);
        Cabin core = new Cabin(true);

        ship.addComponent(core, 5/2, 5/2);

        assertEquals(core, ship.getComponent(5/2, 5/2));
    }

    @Test
    void removeComponent() {
    }

    @Test
    void getComponent() {

    }
}