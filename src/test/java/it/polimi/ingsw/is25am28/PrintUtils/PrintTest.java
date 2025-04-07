package it.polimi.ingsw.is25am28.PrintUtils;

import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Ship.Ship;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    void customShip2(Ship ship) {
        /*
               ==== Ship Configuration (LEVEL 2) ====
            \       4       5       6       7       8
            4               a
            5               b       c       d
            6       e       f       g       h       i
            7       j       k       l       m       n
            8       o       p               q       r

            Total components = 18 (17 + 1 core)

            a = doubleCannon1 at (4, 5)
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

        Battery tripleBattery1 = new Battery(connectors, 3);

        Cannon singleCannon1 = new Cannon(connectors, 1);
        Cannon singleCannon2 = new Cannon(connectors, 1);
        Cannon doubleCannon1 = new Cannon(connectors, 2);
        Cannon doubleCannon2 = new Cannon(connectors, 2);

        Engine singleEngine1 = new Engine(connectors, 1);
        Engine singleEngine2 = new Engine(connectors, 1);
        Engine singleEngine3 = new Engine(connectors, 1);
        Engine doubleEngine1 = new Engine(connectors, 2);

        Cabin cabin1 = new Cabin(connectors, false);

        Shield shield1 = new Shield(connectors);

        Storage normalDoubleStorage1 = new Storage(connectors, 2, false);
        Storage normalTripleStorage1 = new Storage(connectors, 3, false);
        Storage specialSingleStorage1 = new Storage(connectors, 1, true);
        Storage specialDoubleStorage1 = new Storage(connectors, 2, true);

        Structural structural1 = new Structural(connectors);

        Vital purpleVital1 = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
        Vital brownVital1 = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

        // Adding the components created above
        ship.addComponent(doubleCannon1, 4, 5);
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

        // Generating the component sub-lists right after the ship is created
        ship.generateComponentSubLists();
    }

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

        WidgetTUI widgetBattery1 = battery1.generateWidget();
        WidgetTUI widgetBattery2 = battery2.generateWidget();
        WidgetTUI widgetBattery3 = battery3.generateWidget();
        WidgetTUI widgetBattery4 = battery4.generateWidget();
        WidgetTUI widgetBattery5 = battery5.generateWidget();
        WidgetTUI widgetEmptyBattery2 = emptyBattery2.generateWidget();
        WidgetTUI widgetEmptyBattery3 = emptyBattery3.generateWidget();

        widgetBattery1.printWidget();
        widgetBattery2.printWidget();
        widgetBattery3.printWidget();
        widgetBattery4.printWidget();
        widgetBattery5.printWidget();
        widgetEmptyBattery2.printWidget();
        widgetEmptyBattery3.printWidget();
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

        WidgetTUI widgetCoreCabin = coreCabin.generateWidget();
        WidgetTUI widgetEmptyCabin = emptyCabin.generateWidget();
        WidgetTUI widgetOneAstronautCabin = oneAstronautCabin.generateWidget();
        WidgetTUI widgetTwoAstronautCabin = twoAstronautCabin.generateWidget();
        WidgetTUI widgetPurpleAlienCabin = purpleAlienCabin.generateWidget();
        WidgetTUI widgetBrownAlienCabin = brownAlienCabin.generateWidget();

        widgetCoreCabin.printWidget();
        widgetEmptyCabin.printWidget();
        widgetOneAstronautCabin.printWidget();
        widgetTwoAstronautCabin.printWidget();
        widgetPurpleAlienCabin.printWidget();
        widgetBrownAlienCabin.printWidget();
    }

    @Test
    void printTest_cannon() {
        System.out.println("======================== CANNON PRINT TEST ==========================");

        Cannon singleCannon = new Cannon(connectors, 1);
        Cannon doubleCannon = new Cannon(connectors, 2);

        WidgetTUI widgetSingleCannon;
        WidgetTUI widgetDoubleCannon;

        for (int i = 0; i < 4; i++) {
            widgetSingleCannon = singleCannon.generateWidget();
            widgetDoubleCannon = doubleCannon.generateWidget();

            widgetSingleCannon.printWidget();
            widgetDoubleCannon.printWidget();

            singleCannon.rotateRight();
            doubleCannon.rotateRight();
        }
    }

    @Test
    void printTest_engine() {
        System.out.println("======================== ENGINE PRINT TEST ==========================");

        Engine singleEngine = new Engine(connectors, 1);
        Engine doubleEngine = new Engine(connectors, 2);

        WidgetTUI widgetSingleEngine;
        WidgetTUI widgetDoubleEngine;

        for (int i = 0; i < 4; i++) {
            widgetSingleEngine = singleEngine.generateWidget();
            widgetDoubleEngine = doubleEngine.generateWidget();

            widgetSingleEngine.printWidget();
            widgetDoubleEngine.printWidget();

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

        WidgetTUI widgetShield_top_right = shield_top_right.generateWidget();
        WidgetTUI widgetShield_bottom_right = shield_bottom_right.generateWidget();
        WidgetTUI widgetShield_bottom_left = shield_bottom_left.generateWidget();
        WidgetTUI widgetShield_top_left = shield_top_left.generateWidget();

        widgetShield_top_right.printWidget();
        widgetShield_bottom_right.printWidget();
        widgetShield_bottom_left.printWidget();
        widgetShield_top_left.printWidget();
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

        WidgetTUI widgetSpecialSingleStorage = specialSingleStorage.generateWidget();
        WidgetTUI widgetSpecialDoubleStorage = specialDoubleStorage.generateWidget();
        WidgetTUI widgetNormalDoubleStorage = normalDoubleStorage.generateWidget();
        WidgetTUI widgetNormalTripleStorage  = normalTripleStorage.generateWidget();
        WidgetTUI widgetEmptySpecialSingleStorage  = emptySpecialSingleStorage.generateWidget();
        WidgetTUI widgetEmptyNormalTripleStorage  = emptyNormalTripleStorage.generateWidget();

        widgetSpecialSingleStorage.printWidget();
        widgetSpecialDoubleStorage.printWidget();
        widgetNormalDoubleStorage.printWidget();
        widgetNormalTripleStorage.printWidget();
        widgetEmptySpecialSingleStorage.printWidget();
        widgetEmptyNormalTripleStorage.printWidget();
    }

    @Test
    void printTest_structural() {
        System.out.println("======================== STRUCTURAL PRINT TEST ==========================");
        Structural structure = new Structural(connectors);

        WidgetTUI widget = structure.generateWidget();
        widget.printWidget();
    }

    @Test
    void printTest_vital() {
        System.out.println("======================== VITAL PRINT TEST ==========================");
        Vital vital_purple = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
        Vital vital_brown = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

        WidgetTUI purpleVitalWidget = vital_purple.generateWidget();
        WidgetTUI brownVitalWidget = vital_brown.generateWidget();
        purpleVitalWidget.printWidget();
        brownVitalWidget.printWidget();
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

        List<String> screenShield_top_right = shield_top_right.generateWidget().getScreen();
        List<String> screenShield_bottom_right = shield_bottom_right.generateWidget().getScreen();
        List<String> screenShield_bottom_left = shield_bottom_left.generateWidget().getScreen();
        List<String> screenShield_top_left = shield_top_left.generateWidget().getScreen();

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

        WidgetTUI shipWidget = ship.generateWidget();
        shipWidget.wrapScreenWithBorder();
        shipWidget.printWidget();
    }

    @Test
    void printShipTest2() {
        System.out.println("======================== PRINT SHIP TEST 2 ==========================");

        Ship ship = new Ship(2);
        customShip2(ship);

        WidgetTUI shipWidget = ship.generateWidget();
        shipWidget.wrapScreenWithBorder();
        shipWidget.printWidget();
    }

    @Test
    void widget_wrapUnwrapBorderTest() {
        System.out.println("======================== BORDER MULTIPLE LAYERING TEST ==========================");

        WidgetTUI widget = new WidgetTUI();
        int layerCount = 0;

        widget.appendString("==HELLO WORLD==");
        widget.printWidget();
        assertEquals(layerCount, widget.getLayerCount());

        // (1) - One border wrapping and One unwrapping
        // First border wrapping
        widget.wrapScreenWithBorder();
        widget.printWidget();
        layerCount++;
        assertEquals(layerCount, widget.getLayerCount());

        // First border unwrapping
        widget.unwrapScreenFromBorder();
        widget.printWidget();
        layerCount--;
        assertEquals(layerCount, widget.getLayerCount());

        // (2) - Two border wrapping and two unwrapping
        // First border wrapping
        widget.wrapScreenWithBorder();
        widget.printWidget();
        layerCount++;
        assertEquals(layerCount, widget.getLayerCount());

        // Second border wrapping
        widget.wrapScreenWithBorder();
        widget.printWidget();
        layerCount++;
        assertEquals(layerCount, widget.getLayerCount());

        // Second border unwrapping
        widget.unwrapScreenFromBorder();
        widget.printWidget();
        layerCount--;
        assertEquals(layerCount, widget.getLayerCount());

        // First border unwrapping
        widget.unwrapScreenFromBorder();
        widget.printWidget();
        layerCount--;
        assertEquals(layerCount, widget.getLayerCount());
    }

    @Test
    void widget_verticalCompositionTest() {
        System.out.println("======================== VERTICAL COMPOSITION TEST ==========================");

        WidgetTUI widgetTop = new WidgetTUI();
        WidgetTUI widgetBottom = new WidgetTUI();
        WidgetTUI composition;

        widgetTop.appendString("HELLO");
        widgetBottom.appendString("WORLD");

        widgetTop.wrapScreenWithBorder();
        widgetTop.wrapScreenWithBorder();
        widgetBottom.wrapScreenWithBorder();

        List<WidgetTUI> widgetList = new ArrayList<>();
        widgetList.add(widgetTop);
        widgetList.add(widgetBottom);

        composition = WidgetTUI.composeWidgetsVertically(widgetList);
        composition.centerWidgetScreen();
        composition.wrapScreenWithBorder();
        composition.printWidget();

        widgetList.remove(widgetTop);
        widgetList.add(widgetTop);

        composition = WidgetTUI.composeWidgetsVertically(widgetList);
        composition.centerWidgetScreen();
        composition.wrapScreenWithBorder();
        composition.printWidget();
    }

    @Test
    void widget_horizontalCompositionTest() {
        System.out.println("======================== HORIZONTAL COMPOSITION TEST ==========================");

        WidgetTUI widgetTop = new WidgetTUI();
        WidgetTUI widgetBottom = new WidgetTUI();
        WidgetTUI composition;

        widgetTop.appendString("HELLO");
        widgetBottom.appendString("WORLD");

        widgetTop.wrapScreenWithBorder();
        widgetTop.wrapScreenWithBorder();
        widgetBottom.wrapScreenWithBorder();

        List<WidgetTUI> widgetList = new ArrayList<>();
        widgetList.add(widgetTop);
        widgetList.add(widgetBottom);

        composition = WidgetTUI.composeWidgetsHorizontally(widgetList);
        composition.wrapScreenWithBorder();
        composition.printWidget();

        widgetList.remove(widgetTop);
        widgetList.add(widgetTop);

        composition = WidgetTUI.composeWidgetsHorizontally(widgetList);
        composition.wrapScreenWithBorder();
        composition.printWidget();
    }
}
