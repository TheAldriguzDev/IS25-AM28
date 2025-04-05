package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Components.*;

import java.util.ArrayList;
import java.util.List;

public class PrintUtils {

    /**
     * @param string The string to color
     * @param unicodeColorString The color to add to the string
     * @return The colored string
     */
    public static String addColor(String string, String unicodeColorString) {
        return unicodeColorString + string + ANSIColors.RESET;
    }

    public static List<String> getComponentInfo(Component component, int width, int height) {
        List<String> componentInfo = new ArrayList<>();
        int padding;
        StringBuilder paddedString;

        switch (component) {
            // Battery - Adding the energy indicator
            case Battery battery -> {
                // Writes the component's name
                padding = width - 2 - 1 - ComponentAlias.BATTERY.getAlias().length();
                paddedString = new StringBuilder(" " + ComponentAlias.BATTERY.getAlias());

                for (int i = 0; i < padding; i++) {
                    paddedString.append(" ");
                }
                componentInfo.add(paddedString.toString());

                int maxCapacity = battery.getMaxAvailability();
                int batteryLevel = battery.getAvailability();
                int batteryStringLength = 2 * maxCapacity - 1;

                for (int i = 1; i < height - 2; i++) {
                    paddedString = new StringBuilder();

                    for (int j = 0; j < width - 2; j++) {
                        if (i == height / 2 && j == width / 2) {
                            padding = (width - 2 - batteryStringLength) / 2;
                            paddedString = new StringBuilder();

                            // Padding before the energy indicator
                            paddedString.append(" ".repeat(padding));

                            // Adding alternated battery indicators
                            for (int k = 0; k < batteryStringLength; k++) {
                                if (k % 2 == 0) {
                                    if (batteryLevel > 0) {
                                        paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.GREEN));
                                        batteryLevel--;
                                    }
                                    else {
                                        paddedString.append("\u2588");
                                    }
                                }
                                else {
                                    paddedString.append(" ");
                                }
                            }

                            // Padding after the energy indicator
                            paddedString.append(" ".repeat(padding));
                            break;
                        }
                        else {
                            paddedString.append(" ");
                        }
                    }
                    componentInfo.add(paddedString.toString());
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
                // Writes the component's name
                padding = width - 2 - 1 - ComponentAlias.STRUCTURAL.getAlias().length();
                paddedString = new StringBuilder(" " + ComponentAlias.STRUCTURAL.getAlias());

                for (int i = 0; i < padding; i++) {
                    paddedString.append(" ");
                }
                componentInfo.add(paddedString.toString());

                for (int i = 0; i < height - 2; i++) {
                    paddedString = new StringBuilder();
                    paddedString.append(" ".repeat(width - 2));
                    componentInfo.add(paddedString.toString());
                }
            }
            case Vital vital -> {
                padding = width - 2 - 1 - ComponentAlias.VITAL.getAlias().length();
                paddedString = new StringBuilder();
                if(vital.getVitalType() == VitalType.PURPLE_VITAL) {
                    paddedString.append(addColor(" " + ComponentAlias.VITAL.getAlias(), ANSIColors.MAGENTA));
                } else {
                    paddedString.append(addColor(" " + ComponentAlias.VITAL.getAlias(), ANSIColors.BRIGHT_YELLOW));
                }

                for (int i = 0; i < padding; i++) {
                    paddedString.append(" ");
                }
                componentInfo.add(paddedString.toString());

                for (int i = 0; i < height - 2; i++) {
                    paddedString = new StringBuilder();
                    paddedString.append(" ".repeat(width - 2));
                    componentInfo.add(paddedString.toString());
                }
            }
        }

        return componentInfo;
    }


}