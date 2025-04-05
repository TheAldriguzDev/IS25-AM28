package it.polimi.ingsw.is25am28;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;

public class PrintTest {

    @Test
    public void printTest() {
        List<String> screen = new ArrayList<>();
        // widht = 3*height - 2 | width =  height + i*4
        int scale = 3; // needs to be odd

        int height = scale;
        int width = 3*height - 2;
        String tmpString;

        // Upper border
        tmpString = "\u250C";
        for (int i = 1; i < width - 1; i++) {
            if (i == (width / 2)) {
                tmpString += THREE_PIPES.ordinal();
            } else {
                tmpString += "\u2500";
            }
        }
        tmpString += "\u2510";
        screen.add(tmpString);

        for (int i = 1; i < height - 1; i++) {
            tmpString = "";
            for (int j = 0; j < width; j++) {
                if (j == 0 || j == width - 1) {
                    if (i == height / 2) {
                        tmpString += THREE_PIPES.ordinal();
                    } else {
                        tmpString += "\u2502";
                    }
                } else {
                    tmpString += " ";
                }
            }
            screen.add(tmpString);
        }


        // Lower border
        tmpString = "\u2514";
        for (int i = 1; i < width - 1; i++) {
            if (i == (width / 2)) {
                tmpString += THREE_PIPES.ordinal();
            } else {
                tmpString += "\u2500";
            }
        }
        tmpString += "\u2518";
        screen.add(tmpString);

        for (String s : screen) {
            System.out.println(s);
        }

            //








    }
}
