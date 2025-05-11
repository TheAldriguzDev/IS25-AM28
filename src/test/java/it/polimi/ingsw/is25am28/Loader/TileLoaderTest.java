package it.polimi.ingsw.is25am28.Loader;

import it.polimi.ingsw.is25am28.Model.Components.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TileLoaderTest {

    @Test
    void test_tiles_loading() throws IOException {
        TileLoader tileLoader = new TileLoader();

        List<Component> comp = tileLoader.getTiles();

        List<Structural> structural = new ArrayList<>();
        List<Cannon> cannon = new ArrayList<>();
        List<Cannon> doubleCannon = new ArrayList<>();
        List<Engine> engine = new ArrayList<>();
        List<Engine> doubleEngine = new ArrayList<>();
        List<Cabin> cabins = new ArrayList<>();
        List<Storage> normalStorage = new ArrayList<>();
        List<Storage> specialStorage = new ArrayList<>();
        List<Vital> purpleVitals = new ArrayList<>();
        List<Vital> brownVitals = new ArrayList<>();
        List<Battery> batteries  = new ArrayList<>();
        List<Shield> shields = new ArrayList<>();

        for (Component component : comp) {

            switch (component) {
                case Structural data -> {
                    structural.add(data);
                }
                case Cannon data -> {
                    if (data.getFirePower() == 1) {
                        cannon.add(data);
                    } else {
                        doubleCannon.add(data);
                    }
                }
                case Engine data -> {
                    if (data.getSpeed() == 1) {
                        engine.add(data);
                    } else {
                        doubleEngine.add(data);
                    }
                }
                case Cabin data -> {
                    cabins.add(data);
                }
                case Storage data -> {
                    if (data.isSpecialStorage()) {
                        specialStorage.add(data);
                    } else {
                        normalStorage.add(data);
                    }
                }
                case Vital data -> {
                    if (data.getVitalType().equals(VitalType.PURPLE_VITAL)) {
                        purpleVitals.add(data);
                    } else {
                        brownVitals.add(data);
                    }
                }
                case Battery data -> {
                    batteries.add(data);
                }
                case Shield data -> {
                    shields.add(data);
                }
                default -> throw new IllegalStateException("Unexpected value: " + component);
            }

        }

        assertEquals(152, comp.size());
        assertEquals(8, structural.size());
        assertEquals(25, cannon.size());
        assertEquals(11, doubleCannon.size());
        assertEquals(21, engine.size());
        assertEquals(9, doubleEngine.size());
        assertEquals(17, cabins.size());
        assertEquals(6 + 9, normalStorage.size());
        assertEquals(3 + 6, specialStorage.size());
        assertEquals(6, purpleVitals.size());
        assertEquals(6, brownVitals.size());
        assertEquals(6 + 11, batteries.size());
        assertEquals(8, shields.size());
    }
}