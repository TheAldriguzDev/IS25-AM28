package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
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

    Ship customLevel2Ship() {
        Ship ship = new Ship(2);

        List<Integer> connectors = new ArrayList<Integer>();

        // Default connector is THREE_PIPES
        for (int i = 0; i < 4; i++) {
            connectors.add(THREE_PIPES.ordinal());
        }

        Battery tripleBattery1 = new Battery(connectors, 3);

        Cannon singleCannon1 = new Cannon(connectors, 1);
        Cannon singleCannon2 = new Cannon(connectors, 1);
        Cannon singleCannon3 = new Cannon(connectors, 1);
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
        ship.addComponent(doubleCannon1, 7, 3);
        ship.addComponent(singleCannon3, 7, 9);
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

        // Verifying that the ship is correctly built according to
        // ship building rules and each component's positioning rules
        assertTrue(ship.validateShip());

        return ship;
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
    void widgetScreen_composeScreenHorizontallyTest() {
        System.out.println("======================== composeScreenHorizontally() TEST ==========================");

        List<List<String>> composedInfoInput = new ArrayList<>();
        List<String> finalScreen;

        for (int i = 0; i < 10; i++) {
            List<String> screenToAdd = new ArrayList<String>();

            for (int j = 0; j < i; j++) {
                screenToAdd.add("[LINE " + j + "]");
            }

            composedInfoInput.add(screenToAdd);
        }

        composedInfoInput.add(null);

        finalScreen = WidgetTUI.composeScreensHorizontally(composedInfoInput);

        for (String s : finalScreen) {
            System.out.println(s);
        }

        finalScreen = WidgetTUI.composeScreensHorizontally(composedInfoInput.reversed());

        for (String s : finalScreen) {
            System.out.println(s);
        }

        WidgetTUI widget = new WidgetTUI(finalScreen);
        widget.wrapWidgetWithBorder();
        widget.printWidget();
    }

    @Test
    void widgetScreen_composeScreenVerticallyTest() {
        System.out.println("======================== composeScreenVertically() TEST ==========================");

        List<List<String>> composedInfoInput = new ArrayList<>();
        List<String> finalScreen;

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
        List<String> screen = new ArrayList<>();

        screen.add("The quick brown fox jumps over the lazy dog");
        screen.add("Lorem ipsum dolor si amet");
        screen.add("The quick brown fox jumps over the lazy dog");
        screen.add("The quick brown fox jumps over the lazy dog");
        screen.add("Lorem ipsum dolor si amet");

        composedInfoInput.add(screenShield_top_right);
        composedInfoInput.add(screenShield_bottom_right);
        composedInfoInput.add(screenShield_bottom_left);
        composedInfoInput.add(screenShield_top_left);
        composedInfoInput.add(screen);

        finalScreen = WidgetTUI.composeScreensVertically(composedInfoInput);

        for (String s : finalScreen) {
            System.out.println(s);
        }

        WidgetTUI widget = new WidgetTUI(finalScreen);
        widget.wrapWidgetWithBorder();
        widget.printWidget();

        widget.centerWidgetScreen();
        widget.printWidget();
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

        ship.generateComponentSubLists();

        WidgetTUI shipWidget = ship.generateWidget();
        shipWidget.wrapWidgetWithBorder();
        shipWidget.printWidget();
    }

    @Test
    void printShipTest2() {
        System.out.println("======================== PRINT SHIP TEST 2 ==========================");

        Ship ship = new Ship(2);
        customShip2(ship);

        WidgetTUI shipWidget = ship.generateWidget();
        shipWidget.wrapWidgetWithBorder();
        shipWidget.printWidget();
    }

    @Test
    void printShipTest3() {
        System.out.println("======================== PRINT SHIP TEST 3 ==========================");
        Ship ship = customLevel2Ship();
        WidgetTUI shipWidget;

        shipWidget = ship.generateWidget();
        shipWidget.wrapWidgetWithBorder();
        shipWidget.printWidget();
    }

    @Test
    void widget_actualWidthWithoutUnicodeCharacters() {
        String noUnicode = "HELLO";
        String yesUnicode = ANSIColors.GREEN + noUnicode + ANSIColors.RESET;

        assertEquals(noUnicode.length(), PrintUtils.removeUnicodeFromString(yesUnicode).length());
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
        widget.wrapWidgetWithBorder();
        widget.printWidget();
        layerCount++;
        assertEquals(layerCount, widget.getLayerCount());

        // First border unwrapping
        widget.unwrapWidgetFromBorder();
        widget.printWidget();
        layerCount--;
        assertEquals(layerCount, widget.getLayerCount());

        // (2) - Two border wrapping and two unwrapping
        // First border wrapping
        widget.wrapWidgetWithBorder();
        widget.printWidget();
        layerCount++;
        assertEquals(layerCount, widget.getLayerCount());

        // Second border wrapping
        widget.wrapWidgetWithBorder();
        widget.printWidget();
        layerCount++;
        assertEquals(layerCount, widget.getLayerCount());

        // Second border unwrapping
        widget.unwrapWidgetFromBorder();
        widget.printWidget();
        layerCount--;
        assertEquals(layerCount, widget.getLayerCount());

        // First border unwrapping
        widget.unwrapWidgetFromBorder();
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

        widgetTop.wrapWidgetWithBorder();
        widgetTop.wrapWidgetWithBorder();
        widgetBottom.wrapWidgetWithBorder();

        List<WidgetTUI> widgetList = new ArrayList<>();
        widgetList.add(widgetTop);
        widgetList.add(widgetBottom);

        composition = WidgetTUI.composeWidgetsVertically(widgetList);
        composition.centerWidgetScreen();
        composition.wrapWidgetWithBorder();
        composition.printWidget();

        widgetList.remove(widgetTop);
        widgetList.add(widgetTop);

        composition = WidgetTUI.composeWidgetsVertically(widgetList);
        composition.centerWidgetScreen();
        composition.wrapWidgetWithBorder();
        composition.printWidget();
    }

    @Test
    void widget_horizontalCompositionTest() {
        System.out.println("======================== HORIZONTAL COMPOSITION TEST ==========================");

        WidgetTUI widgetTop = new WidgetTUI();
        WidgetTUI widgetBottom = new WidgetTUI();
        WidgetTUI widget3 = new WidgetTUI();
        WidgetTUI widget4 = new WidgetTUI();
        WidgetTUI widget5 = new WidgetTUI();
        WidgetTUI composition;

        widgetTop.appendString("HELLO");
        widgetBottom.appendString("WORLD");
        widget3.appendString("W1");
        widget4.appendString("W2");
        widget5.appendString("W3");

        widget3.wrapWidgetWithBorder();
        widget3.wrapWidgetWithBorder();
        widget3.wrapWidgetWithBorder();

        widget4.wrapWidgetWithBorder();
        widget4.wrapWidgetWithBorder();
        widget4.wrapWidgetWithBorder();
        widget4.wrapWidgetWithBorder();

        widget5.wrapWidgetWithBorder();
        widget5.wrapWidgetWithBorder();
        widget5.wrapWidgetWithBorder();
        widget5.wrapWidgetWithBorder();
        widget5.wrapWidgetWithBorder();

        widgetTop.wrapWidgetWithBorder();
        widgetTop.wrapWidgetWithBorder();
        widgetBottom.wrapWidgetWithBorder();

        List<WidgetTUI> widgetList;

        widgetList = new ArrayList<>();
        widgetList.add(widgetTop);
        widgetList.add(widgetBottom);
        widgetList.add(widget3);
        widgetList.add(widget4);
        widgetList.add(widget5);

        composition = WidgetTUI.composeWidgetsHorizontally(widgetList);
        composition.wrapWidgetWithBorder();
        composition.printWidget();

        widgetList = new ArrayList<>();
        widgetList.add(widget5);
        widgetList.add(widget4);
        widgetList.add(widget3);
        widgetList.add(widgetBottom);
        widgetList.add(widgetTop);

        composition = WidgetTUI.composeWidgetsHorizontally(widgetList);
        composition.wrapWidgetWithBorder();
        composition.printWidget();
    }

    @Test
    void widget_verticalExtensionTest() {
        System.out.println("======================== WIDGET VERTICAL EXTENSION TEST ==========================");

        WidgetTUI widget = new WidgetTUI();

        widget.wrapWidgetWithBorder();
        widget.printWidget();
        widget.unwrapWidgetFromBorder();

        widget.appendString("HELLO WORLD");
        widget.appendString("COMPUTER");

        widget.setWidth(10);
        widget.setHeight(10);
        assertEquals(10, widget.getScreen().size());

        widget.wrapWidgetWithBorder();
        widget.printWidget();

        assertEquals(12, widget.getScreen().size());

        widget.centerWidgetScreen();
        widget.printWidget();
    }

    @Test
    void widget_horizontalExtensionTest() {
        System.out.println("======================== WIDGET HORIZONTAL EXTENSION TEST ==========================");

        WidgetTUI widget = new WidgetTUI();

        widget.wrapWidgetWithBorder();
        widget.printWidget();
        widget.unwrapWidgetFromBorder();

        widget.appendString("HELLO WORLD");
        widget.appendString("COMPUTER");

        widget.setWidth(50);
        widget.wrapWidgetWithBorder();
        widget.printWidget();

        assertEquals(4, widget.getScreen().size());
    }

    @Test
    void widget_boardWidgetTest() {
        System.out.println("======================== BOARD WIDGETS TEST ==========================");
        Board board = new BoardLevel2();
        List<Player> players = new ArrayList<Player>();

        board.buildBoard();

        players.add(new Player("TheD3stroy3r", PlayerColor.RED, 2));
        players.add(new Player("MasterChief1103", PlayerColor.GREEN, 2));
        players.add(new Player("C4taclism__", PlayerColor.BLUE, 2));
        players.add(new Player("ItzAlex_TTV", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            board.newPlayer(player);
            board.addPlayerToBoard(player);
        }

        WidgetTUI boardWidget;
        boardWidget = board.generateWidget();
        boardWidget.printWidget();
    }

    @Test
    void widget_boardWidgetTestWithEliminatedPlayers() {
        System.out.println("======================== BOARD WIDGETS TEST ==========================");
        Board board = new BoardLevel2();
        List<Player> players = new ArrayList<Player>();

        board.buildBoard();

        players.add(new Player("TheD3stroy3r", PlayerColor.RED, 2));
        players.add(new Player("MasterChief1103", PlayerColor.GREEN, 2));
        players.add(new Player("C4taclism__", PlayerColor.BLUE, 2));
        players.add(new Player("ItzAlex_TTV", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            board.newPlayer(player);
            board.addPlayerToBoard(player);
        }

        board.eliminatePlayer(players.get(0));
        board.eliminatePlayer(players.get(2));

        WidgetTUI boardWidget;
        boardWidget = board.generateWidget();
        boardWidget.printWidget();
    }

    @Test
    void widget_boardWidgetTestWithIncrementalPlayerCount() {
        System.out.println("======================== BOARD WIDGETS TEST ==========================");
        WidgetTUI boardWidget;
        Board board = new BoardLevel2();
        List<Player> players = new ArrayList<Player>();

        board.buildBoard();

        players.add(new Player("TheD3stroy3r", PlayerColor.RED, 2));
        players.add(new Player("MasterChief1103", PlayerColor.GREEN, 2));
        players.add(new Player("C4taclism__", PlayerColor.BLUE, 2));
        players.add(new Player("ItzAlex_TTV", PlayerColor.YELLOW, 2));

        boardWidget = board.generateWidget();
        boardWidget.printWidget();

        board.newPlayer(players.get(0));
        board.addPlayerToBoard(players.get(0));

        boardWidget = board.generateWidget();
        boardWidget.printWidget();

        board.newPlayer(players.get(1));
        board.addPlayerToBoard(players.get(1));

        boardWidget = board.generateWidget();
        boardWidget.printWidget();

        board.newPlayer(players.get(2));
        board.addPlayerToBoard(players.get(2));

        boardWidget = board.generateWidget();
        boardWidget.printWidget();

        board.newPlayer(players.get(3));
        board.addPlayerToBoard(players.get(3));

        boardWidget = board.generateWidget();
        boardWidget.printWidget();

        board.eliminatePlayer(board.getPlayers().get(0));
        board.eliminatePlayer(board.getPlayers().get(2));

        boardWidget = board.generateWidget();
        boardWidget.printWidget();
    }

    @Test
    void widget_boardWidgetTestFullRotation() {
        System.out.println("======================== BOARD WIDGETS TEST ==========================");

        Board board = new BoardLevel2();
        List<Player> players = new ArrayList<Player>();

        board.buildBoard();

        players.add(new Player("TheD3stroy3r", PlayerColor.RED, 2));
        players.add(new Player("MasterChief1103", PlayerColor.GREEN, 2));
        players.add(new Player("C4taclism__", PlayerColor.BLUE, 2));
        players.add(new Player("ItzAlex_TTV", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            board.newPlayer(player);
            board.addPlayerToBoard(player);
        }

        WidgetTUI boardWidget;

        for (int i = 0; i < board.getSize(); i++) {
            boardWidget = board.generateWidget();
            boardWidget.printWidget();
            board.validatePlayersPosition();
            board.movePlayerForward(board.getPlayers().getFirst(), 1);
            board.validatePlayersPosition();
        }
    }

    @Test
    void widget_boardAndShip() {
        System.out.println("======================== BOARD & SHIP WIDGETS TEST ==========================");

        Ship ship = new Ship(2);
        customShip2(ship);

        Board board = new BoardLevel2();
        List<Player> players = new ArrayList<Player>();

        board.buildBoard();

        players.add(new Player("TheD3stroy3r", PlayerColor.RED, 2));
        players.add(new Player("MasterChief1103", PlayerColor.GREEN, 2));
        players.add(new Player("C4taclism__", PlayerColor.BLUE, 2));
        players.add(new Player("ItzAlex_TTV", PlayerColor.YELLOW, 2));

        for (Player player : players) {
            board.newPlayer(player);
            board.addPlayerToBoard(player);
        }

        List<WidgetTUI> widgets = new ArrayList<>();
        WidgetTUI composition, spacer;
        spacer = new WidgetTUI();

        widgets.add(ship.getShipStatsWidget());
        spacer.setHeight(widgets.getFirst().getHeight());
        spacer.setWidth(widgets.getFirst().getWidth());
        widgets.add(spacer);

        composition = WidgetTUI.composeWidgetsHorizontally(widgets);

        widgets.clear();
        widgets.add(composition.centerWidgetScreen());
        widgets.add(board.generateWidget());

        composition = WidgetTUI.composeWidgetsVertically(widgets);
        composition.centerWidgetScreen().wrapWidgetWithBorder();

        widgets.clear();
        widgets.add(composition);
        widgets.add(ship.getShipGridWidget());

        composition = WidgetTUI.composeWidgetsHorizontally(widgets);
        composition.wrapWidgetWithBorder();
        composition.printWidget();
    }

    @Test
    void widget_testPaddingAddition() {
        WidgetTUI widget = new WidgetTUI();

        widget.appendString("HELLO WORLD");
        widget.wrapWidgetWithBorder().printWidget();
        widget.unwrapWidgetFromBorder();

        widget.addPadding(1, 0, 0, 0).wrapWidgetWithBorder().printWidget();
        widget.unwrapWidgetFromBorder();

        widget.addPadding(0, 1, 0, 0).wrapWidgetWithBorder().printWidget();
        widget.unwrapWidgetFromBorder();

        widget.addPadding(0, 0, 1, 0).wrapWidgetWithBorder().printWidget();
        widget.unwrapWidgetFromBorder();

        widget.addPadding(0, 0, 0, 1).wrapWidgetWithBorder().printWidget();
        widget.unwrapWidgetFromBorder();

        widget.addPadding(1, 1, 1, 1).wrapWidgetWithBorder().printWidget();
        widget.unwrapWidgetFromBorder(); 
    }

    @Test
    void unicode_width_test() {
        System.out.println("======================== UNICODE WIDTH TEST ==========================");
        System.out.println("|✦|✦|✦|✦|✦|✦|✦|✦|✦|");
        System.out.println("| | | | | | | | | |");
        System.out.println("|✧|✧|✧|✧|✧|✧|✧|✧|✧|");
        System.out.println("| | | | | | | | | |");
        System.out.println("|\u2321|\u2321|\u2321|\u2321|\u2321|\u2321|\u2321|\u2321|\u2321|");
        System.out.println("| | | | | | | | | |");
        System.out.println("| | | | | | | | | |");
        System.out.println("| | | | | | | | | |");
        System.out.println("| | | | | | | | | |");
        System.out.println("| | | | | | | | | |");
    }
}
