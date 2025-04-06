package it.polimi.ingsw.is25am28.PrintUtils;

import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Ship.Ship;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PrintTest {
    List<Integer> connectors;
    int scale = 5;
    int height = scale;
    int width = 3 * height - 2;

    @BeforeEach
    void init() {
        connectors = new ArrayList<>();
        connectors.add(0);
        connectors.add(1);
        connectors.add(2);
        connectors.add(3);
    }

//    @Test
//    void printTest() {
//        List<String> screen = new ArrayList<>();
//        // widht = 3*height - 2 | width =  height + i*4
//        int scale = 3; // needs to be odd
//
//        int height = scale;
//        int width = 3*height - 2;
//        String tmpString;
//
//        // Upper border
//        tmpString = "\u250C";
//        for (int i = 1; i < width - 1; i++) {
//            if (i == (width / 2)) {
//                tmpString += THREE_PIPES.ordinal();
//            } else {
//                tmpString += "\u2500";
//            }
//        }
//        tmpString += "\u2510";
//        screen.add(tmpString);
//
//        // Middle
//        for (int i = 1; i < height - 1; i++) {
//            tmpString = "";
//            for (int j = 0; j < width; j++) {
//                if (j == 0 || j == width - 1) {
//                    if (i == height / 2) {
//                        tmpString += THREE_PIPES.ordinal();
//                    } else {
//                        tmpString += "\u2502";
//                    }
//                } else if (j == width / 2 && i == height / 2) {
//                    tmpString += "X";
//                } else {
//                    tmpString += " ";
//                }
//            }
//            screen.add(tmpString);
//        }
//
//        // Lower border
//        tmpString = "\u2514";
//        for (int i = 1; i < width - 1; i++) {
//            if (i == (width / 2)) {
//                tmpString += THREE_PIPES.ordinal();
//            } else {
//                tmpString += "\u2500";
//            }
//        }
//        tmpString += "\u2518";
//        screen.add(tmpString);
//
//        for (String s : screen) {
//            System.out.println(s);
//        }
//    }

    @Test
    void printTest_battery() {
        System.out.println("======================== BATTERY PRINT TEST ==========================");

        Battery battery1 = new Battery(connectors, 2);
        Battery battery2 = new Battery(connectors, 2);
        Battery battery3 = new Battery(connectors, 3);
        Battery battery4 = new Battery(connectors, 3);
        Battery battery5 = new Battery(connectors, 3);

        Battery emptyBattery2 = new Battery(connectors, 2);
        Battery emptyBattery3 = new Battery(connectors, 3);

        battery2.setAvailability(1);
        battery4.setAvailability(2);
        battery5.setAvailability(1);

        emptyBattery2.setAvailability(0);
        emptyBattery3.setAvailability(0);

        List<String> screenBattery1 = battery1.print(scale);
        List<String> screenBattery2 = battery2.print(scale);
        List<String> screenBattery3 = battery3.print(scale);
        List<String> screenBattery4 = battery4.print(scale);
        List<String> screenBattery5 = battery5.print(scale);
        List<String> screenEmptyBattery2 = emptyBattery2.print(scale);
        List<String> screenEmptyBattery3 = emptyBattery3.print(scale);

        for (String s : screenBattery1) {
            System.out.println(s);
        }

        for (String s : screenBattery2) {
            System.out.println(s);
        }

        for (String s : screenBattery3) {
            System.out.println(s);
        }

        for (String s : screenBattery4) {
            System.out.println(s);
        }

        for (String s : screenBattery5) {
            System.out.println(s);
        }

        for (String s : screenEmptyBattery2) {
            System.out.println(s);
        }

        for (String s : screenEmptyBattery3) {
            System.out.println(s);
        }
    }

    @Test
    void printTest_cabin() {
        System.out.println("======================== CABIN PRINT TEST ==========================");

        Cabin coreCabin = new Cabin(connectors, true);
        Cabin emptyCabin = new Cabin(connectors, false);
        Cabin oneAstronautCabin = new Cabin(connectors, false);
        Cabin twoAstronautCabin = new Cabin(connectors, false);
        Cabin purpleAlienCabin = new Cabin(connectors, false);
        Cabin brownAlienCabin = new Cabin(connectors, false);

        Lifeform astronaut = new Lifeform(LifeformType.ASTRONAUT);
        Lifeform purpleAlien = new Lifeform(LifeformType.PURPLE_ALIEN);
        Lifeform brownAlien = new Lifeform(LifeformType.BROWN_ALIEN);

        oneAstronautCabin.addInhabitant(astronaut);
        twoAstronautCabin.addInhabitant(astronaut);
        twoAstronautCabin.addInhabitant(astronaut);

        purpleAlienCabin.addInhabitant(purpleAlien);
        brownAlienCabin.addInhabitant(brownAlien);

        List<String> screenCoreCabin = coreCabin.print(scale);
        List<String> screenEmptyCabin = emptyCabin.print(scale);
        List<String> screenOneAstronautCabin = oneAstronautCabin.print(scale);
        List<String> screenTwoAstronautCabin = twoAstronautCabin.print(scale);
        List<String> screenPurpleAlienCabin = purpleAlienCabin.print(scale);
        List<String> screenBrownAlienCabin = brownAlienCabin.print(scale);

        for (String s : screenCoreCabin) {
            System.out.println(s);
        }

        for (String s : screenEmptyCabin) {
            System.out.println(s);
        }

        for (String s : screenOneAstronautCabin) {
            System.out.println(s);
        }

        for (String s : screenTwoAstronautCabin) {
            System.out.println(s);
        }

        for (String s : screenPurpleAlienCabin) {
            System.out.println(s);
        }

        for (String s : screenBrownAlienCabin) {
            System.out.println(s);
        }

    }

    @Test
    void printTest_cannon() {
        System.out.println("======================== CANNON PRINT TEST ==========================");

        Cannon singleCannon = new Cannon(connectors, 1);
        Cannon doubleCannon = new Cannon(connectors, 2);

        List<String> screenSingleCannon;
        List<String> screenDoubleCannon;

        for (int i = 0; i < 4; i++) {
            screenSingleCannon = singleCannon.print(scale);
            screenDoubleCannon = doubleCannon.print(scale);

            for (String s : screenSingleCannon) {
                System.out.println(s);
            }

            for (String s : screenDoubleCannon) {
                System.out.println(s);
            }

            singleCannon.rotateRight();
            doubleCannon.rotateRight();
        }
    }

    @Test
    void printTest_engine() {
        System.out.println("======================== ENGINE PRINT TEST ==========================");

        Engine singleEngine = new Engine(connectors, 1);
        Engine doubleEngine = new Engine(connectors, 2);

        List<String> screenSingleEngine;
        List<String> screenDoubleEngine;

        for (int i = 0; i < 4; i++) {
            screenSingleEngine = singleEngine.print(scale);
            screenDoubleEngine = doubleEngine.print(scale);

            for (String s : screenSingleEngine) {
                System.out.println(s);
            }

            for (String s : screenDoubleEngine) {
                System.out.println(s);
            }

            singleEngine.rotateRight();
            doubleEngine.rotateRight();
        }
    }

    @Test
    void printTest_shield() {
        System.out.println("======================== SHIELD PRINT TEST ==========================");

        Shield shield_top_right = new Shield(connectors);

        Shield shield_bottom_right = new Shield(connectors);
        shield_bottom_right.rotateRight();

        Shield shield_bottom_left = new Shield(connectors);
        shield_bottom_left.rotateRight();
        shield_bottom_left.rotateRight();

        Shield shield_top_left = new Shield(connectors);
        shield_top_left.rotateLeft();

        List<String> screenShield_top_right = shield_top_right.print(scale);
        List<String> screenShield_bottom_right = shield_bottom_right.print(scale);
        List<String> screenShield_bottom_left = shield_bottom_left.print(scale);
        List<String> screenShield_top_left = shield_top_left.print(scale);

        for (String s : screenShield_top_right) {
            System.out.println(s);
        }

        for (String s : screenShield_bottom_right) {
            System.out.println(s);
        }

        for (String s : screenShield_bottom_left) {
            System.out.println(s);
        }

        for (String s : screenShield_top_left) {
            System.out.println(s);
        }

    }

    @Test
    void printTest_storage() {
        System.out.println("======================== STORAGE PRINT TEST ==========================");

        Storage specialSingleStorage = new Storage(connectors, 1, true);
        Storage specialDoubleStorage = new Storage(connectors, 2, true);
        Storage normalDoubleStorage = new Storage(connectors, 2, false);
        Storage normalTripleStorage = new Storage(connectors, 3, false);
        Storage emptySpecialSingleStorage = new Storage(connectors, 1, true);
        Storage emptyNormalTripleStorage = new Storage(connectors, 3, false);

        Item red = new Item(ItemColor.RED);
        Item yellow = new Item(ItemColor.YELLOW);
        Item green = new Item(ItemColor.GREEN);
        Item blue = new Item(ItemColor.BLUE);

        specialSingleStorage.storeItem(yellow);
        specialDoubleStorage.storeItem(red);
        specialDoubleStorage.storeItem(red);
        normalDoubleStorage.storeItem(yellow);
        normalDoubleStorage.storeItem(blue);
        normalTripleStorage.storeItem(green);
        normalTripleStorage.storeItem(green);
        normalTripleStorage.storeItem(blue);

        List<String> screenSpecialSingleStorage = specialSingleStorage.print(scale);
        List<String> screenSpecialDoubleStorage = specialDoubleStorage.print(scale);
        List<String> screenNormalDoubleStorage = normalDoubleStorage.print(scale);
        List<String> screenNormalTripleStorage  = normalTripleStorage.print(scale);
        List<String> screenEmptySpecialSingleStorage  = emptySpecialSingleStorage.print(scale);
        List<String> screenEmptyNormalTripleStorage  = emptyNormalTripleStorage.print(scale);

        for (String s : screenSpecialSingleStorage) {
            System.out.println(s);
        }

        for (String s : screenSpecialDoubleStorage) {
            System.out.println(s);
        }

        for (String s : screenNormalDoubleStorage) {
            System.out.println(s);
        }

        for (String s : screenNormalTripleStorage) {
            System.out.println(s);
        }

        for (String s : screenEmptySpecialSingleStorage) {
            System.out.println(s);
        }

        for (String s : screenEmptyNormalTripleStorage) {
            System.out.println(s);
        }

    }

    @Test
    void printTest_structural() {
        System.out.println("======================== STRUCTURAL PRINT TEST ==========================");
        Structural structure = new Structural(connectors);

        List<String> screen = structure.print(scale);

        for (String s : screen) {
            System.out.println(s);
        }

    }

    @Test
    void printTest_vital() {
        System.out.println("======================== VITAL PRINT TEST ==========================");
        Vital vital_purple = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
        Vital vital_brown = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

        List<String> screenVital1 = vital_purple.print(scale);
        List<String> screenVital2 = vital_brown.print(scale);

        for (String s : screenVital1) {
            System.out.println(s);
        }

        for (String s : screenVital2) {
            System.out.println(s);
        }
    }

    @Test
    void composeTest() {
        System.out.println("======================== COMPOSE TEST ==========================");

        List<List<String>> composedInfoInput = new ArrayList<>();
        List<String> composedInfo;

        // In this test we'll be composing shields along a line
        Shield shield_top_right = new Shield(connectors);

        Shield shield_bottom_right = new Shield(connectors);
        shield_bottom_right.rotateRight();

        Shield shield_bottom_left = new Shield(connectors);
        shield_bottom_left.rotateRight();
        shield_bottom_left.rotateRight();

        Shield shield_top_left = new Shield(connectors);
        shield_top_left.rotateLeft();

        List<String> screenShield_top_right = shield_top_right.print(scale);
        List<String> screenShield_bottom_right = shield_bottom_right.print(scale);
        List<String> screenShield_bottom_left = shield_bottom_left.print(scale);
        List<String> screenShield_top_left = shield_top_left.print(scale);

        composedInfoInput.add(screenShield_top_right);
        composedInfoInput.add(screenShield_bottom_right);
        composedInfoInput.add(screenShield_bottom_left);
        composedInfoInput.add(screenShield_top_left);




         composedInfo =  PrintUtils.composeComponents(composedInfoInput, width, height);

         for (String s : composedInfo) {
             System.out.println(s);
         }



    }

    @Test
    void printShipTest1() {
        System.out.println("======================== PRINT SHIP TEST 1 ==========================");

        Ship ship = new Ship(2);

        Structural structural = new Structural(connectors);
        ship.addComponent(structural, 5, 6);

        Battery battery_1 = new Battery(connectors, 3);
        ship.addComponent(battery_1, 6, 7);

        Storage normal_storage_1 = new Storage(connectors, 3, false);
        normal_storage_1.storeItem(new Item(ItemColor.YELLOW));
        normal_storage_1.storeItem(new Item(ItemColor.BLUE));
        normal_storage_1.storeItem(new Item(ItemColor.GREEN));
        ship.addComponent(normal_storage_1, 6, 5);

        Shield shield_1 = new Shield(connectors);
        ship.addComponent(shield_1, 7, 6);

        Shield shield_2 = new Shield(connectors);
        shield_2.rotateRight();
        ship.addComponent(shield_2, 6, 4);

        Vital brown_vital = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());
        ship.addComponent(brown_vital, 7, 7);

        Cabin cabin_1 = new Cabin(connectors, false);
        cabin_1.addInhabitant(new Lifeform(LifeformType.BROWN_ALIEN));
        ship.addComponent(cabin_1, 7, 8);

        Cabin cabin_2 = new Cabin(connectors, false);
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        cabin_2.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.addComponent(cabin_2, 7, 5);

        Battery battery_2 = new Battery(connectors, 2);
        ship.addComponent(battery_2, 8, 5);

        Storage special_storage_1 = new Storage(connectors, 2, true);
        special_storage_1.storeItem(new Item(ItemColor.RED));
        ship.addComponent(special_storage_1, 6, 8);

        Cabin cabin_3 = new Cabin(connectors, false);
        cabin_3.addInhabitant(new Lifeform(LifeformType.ASTRONAUT));
        ship.addComponent(cabin_3, 5, 5);



        List<String> shipScreen = ship.print(scale);

        for (String s : shipScreen) {
            System.out.println(s);
        }



    }
}
