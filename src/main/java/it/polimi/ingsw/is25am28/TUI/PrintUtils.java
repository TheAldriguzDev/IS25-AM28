package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Components.*;

import java.util.ArrayList;
import java.util.List;



public class PrintUtils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static List<String> getComponentInfo(Component component, int width, int height) {
        List<String> componentInfo = new ArrayList<>();
        int padding;
        String paddedString;

        // Writes the component's name
        padding = width - 2 - 1 - ComponentAlias.STRUCTURAL.getAlias().length();
        paddedString = " " + ComponentAlias.STRUCTURAL.getAlias();

        for (int i = 0; i < padding; i++) {
            paddedString += " ";
        }
        componentInfo.add(paddedString);


        switch (component) {
            case Battery battery -> {
                if (battery.getMaxAvailability() == 2) {

                }
            }
            case Cabin cabin -> {

            }
            case Cannon cannon -> {

            }
            case Engine engine -> {

            }
            case Shield shield -> {

            }
            case Storage storage -> {

            }
            case Structural structure -> {
                for (int i = 0; i < height - 2; i++) {
                    paddedString = "";
                    for (int j = 0; j < width - 2; j++) {
                        paddedString += " ";
                    }
                    componentInfo.add(paddedString);
                }
            }
            case Vital vital -> {

            }
        }

        return componentInfo;
    }


}