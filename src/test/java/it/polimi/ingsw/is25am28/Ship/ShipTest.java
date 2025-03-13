package it.polimi.ingsw.is25am28.Ship;

import it.polimi.ingsw.is25am28.components.Engine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.is25am28.Connector.*;
import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    void getEnginePower() {
        Ship ship = new Ship();
        int[] sides = new int[4];
        sides[0] = ZERO_PIPES.ordinal();
        sides[1] = ZERO_PIPES.ordinal();
        sides[2] = THREE_PIPES.ordinal();
        sides[3] = TWO_PIPES.ordinal();

        Engine engine = new Engine(0, 0, 0, sides);

        ship.addComponent(engine, 2, 2);

        Assertions.assertEquals(engine.hashCode(), ship.getComponent(2, 2).hashCode());
    }

    @Test
    void getFirePower() {
    }

    @Test
    void getAllItems() {
    }

    @Test
    void getAllItemsValue() {
    }

    @Test
    void setProtectedSides() {
    }

    @Test
    void setEnergy() {
    }

    @Test
    void getAllLifeforms() {
    }

    @Test
    void traverse() {
    }

    @Test
    void getNearestComponents() {
    }

    @Test
    void addComponent() {
    }

    @Test
    void removeComponent() {
    }

    @Test
    void getComponent() {
    }
}