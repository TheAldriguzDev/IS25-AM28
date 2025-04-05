package it.polimi.ingsw.is25am28.PrintUtils;

import it.polimi.ingsw.is25am28.Components.Battery;
import it.polimi.ingsw.is25am28.Components.Structural;
import it.polimi.ingsw.is25am28.Components.Vital;
import it.polimi.ingsw.is25am28.Components.VitalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;

public class PrintTest {
    List<Integer> connectors;

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
    void printTest_structural() {
        Structural structure = new Structural(connectors);

        List<String> screen = structure.print();

        for (String s : screen) {
            System.out.println(s);
        }
    }

    @Test
    void printTest_battery() {
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

        List<String> screenBattery1 = battery1.print();
        List<String> screenBattery2 = battery2.print();
        List<String> screenBattery3 = battery3.print();
        List<String> screenBattery4 = battery4.print();
        List<String> screenBattery5 = battery5.print();

        List<String> screenEmptyBattery2 = emptyBattery2.print();
        List<String> screenEmptyBattery3 = emptyBattery3.print();

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
    void printTest_vital() {
        Vital vital_purple = new Vital(connectors, VitalType.PURPLE_VITAL.ordinal());
        Vital vital_brown = new Vital(connectors, VitalType.BROWN_VITAL.ordinal());

        List<String> screenVital1 = vital_purple.print();
        List<String> screenVital2 = vital_brown.print();

        for (String s : screenVital1) {
            System.out.println(s);
        }

        for (String s : screenVital2) {
            System.out.println(s);
        }
    }
}
