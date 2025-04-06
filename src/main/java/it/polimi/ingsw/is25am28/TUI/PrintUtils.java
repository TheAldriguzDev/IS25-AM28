package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Components.*;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;

import java.util.ArrayList;
import java.util.List;

public class PrintUtils {
    private static final String SPACE = " ";

    /**
     * @param string The string to color
     * @param unicodeColorString The color to add to the string
     * @return The colored string
     */
    public static String addColor(String string, String unicodeColorString) {
        return unicodeColorString + string + ANSIColors.RESET;
    }

    /**
     * Prints to terminal the given component's information and TUI representation
     *
     * @param component The component to represent via TUI
     * @param width The width of the box that holds the component (minimum is 5)
     * @param height The height of the box that holds the component (minimum is 5)
     * @return A list of strings to be printed out to terminal, in order, that represent
     *         the current state of the given component
     */
    public static List<String> getComponentInfo(Component component, int width, int height) {
        List<String> componentInfo = new ArrayList<>();
        int padding;
        StringBuilder paddedString;

        switch (component) {
            case Battery battery -> {
                // Writes the component's name
                padding = width - 2 - 1 - ComponentAlias.BATTERY.getAlias().length();
                paddedString = new StringBuilder(SPACE + ComponentAlias.BATTERY.getAlias());

                paddedString.append(SPACE.repeat(Math.max(0, padding)));
                componentInfo.add(paddedString.toString());

                int maxCapacity = battery.getMaxAvailability();
                int batteryLevel = battery.getAvailability();
                int batteryStringLength = 2 * maxCapacity - 1;

                for (int i = 1; i < height - 2; i++) {
                    paddedString = new StringBuilder();

                    for (int j = 1; j < width - 1; j++) {
                        if (i == height / 2 && j == width / 2) {
                            padding = (width - 2 - batteryStringLength) / 2;
                            paddedString = new StringBuilder();

                            // Padding before the energy indicator
                            paddedString.append(SPACE.repeat(padding));

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
                                    paddedString.append(SPACE);
                                }
                            }

                            // Padding after the energy indicator
                            paddedString.append(SPACE.repeat(padding));
                            break;
                        }
                        else {
                            paddedString.append(SPACE);
                        }
                    }
                    componentInfo.add(paddedString.toString());
                }
            }
            case Cabin cabin -> {
                LifeformType storedLifeformType;
                StringBuilder housingString = new StringBuilder();
                int cabinCapacity = 2;
                int occupiedSpace = cabinCapacity - cabin.getAvailableSpace();

                // Writes the cabin's name, colored with which lifeform it's housing and
                // also defines the string that shows which lifeforms are housed inside it
                if (cabin.getInhabitants().isEmpty()) {
                    // Case 0 - Empty cabin
                    paddedString = new StringBuilder(SPACE + ComponentAlias.CABIN.getAlias());
                    padding = (width - 2 - 3) / 2;

                    housingString.append(SPACE.repeat(padding));
                    housingString.append(addColor(SPACE, ANSIColors.BRIGHT_BACKGROUND_WHITE));
                    housingString.append(SPACE);
                    housingString.append(addColor(SPACE, ANSIColors.BRIGHT_BACKGROUND_WHITE));
                    housingString.append(SPACE.repeat(padding));
                }
                else {
                    storedLifeformType = cabin.getInhabitants().getFirst().getLifeformType();

                    if (storedLifeformType == LifeformType.ASTRONAUT) {
                        paddedString = new StringBuilder(SPACE + ComponentAlias.CABIN.getAlias());

                        padding = (width - 2 - 3) / 2;
                        housingString.append(SPACE.repeat(padding));

                        if (occupiedSpace > 1) {
                            housingString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE))
                                    .append(SPACE)
                                    .append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE));
                        }
                        else {
                            housingString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BRIGHT_BACKGROUND_WHITE))
                                    .append(SPACE)
                                    .append(addColor(SPACE, ANSIColors.BRIGHT_BACKGROUND_WHITE));
                        }

                        housingString.append(SPACE.repeat(padding));
                    }
                    else if (storedLifeformType == LifeformType.PURPLE_ALIEN) {
                        paddedString = new StringBuilder(
                            addColor(
                                SPACE + ComponentAlias.CABIN.getAlias(),
                                ANSIColors.MAGENTA
                            )
                        );

                        padding = (width - 2 - 1) / 2;
                        housingString.append(SPACE.repeat(padding));
                        housingString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BACKGROUND_MAGENTA));
                        housingString.append(SPACE.repeat(padding));
                    }
                    else if (storedLifeformType == LifeformType.BROWN_ALIEN) {
                        paddedString = new StringBuilder(
                            addColor(
                                SPACE + ComponentAlias.CABIN.getAlias(),
                                ANSIColors.YELLOW
                            )
                        );

                        padding = (width - 2 - 1) / 2;
                        housingString.append(SPACE.repeat(padding));
                        housingString.append(addColor(addColor("A", ANSIColors.BLACK), ANSIColors.BACKGROUND_YELLOW));
                        housingString.append(SPACE.repeat(padding));
                    }
                    else {
                        throw new IllegalArgumentException("ERROR: Unrecognized lifeform type");
                    }
                }

                // Padding to use with the component's name
                padding = width - 2 - 1 - ComponentAlias.CABIN.getAlias().length();

                // Adds a GOLDEN STAR as a tag to specify that the cabin is the core
                if (cabin.isCore()) {
                    String coreLabel = "(*)";
                    paddedString.append(SPACE);
                    paddedString.append(addColor(coreLabel, ANSIColors.BRIGHT_YELLOW));
                    paddedString.append(SPACE.repeat(padding - coreLabel.length() - 1));
                }
                else {
                    paddedString.append(SPACE.repeat(padding));
                }

                componentInfo.add(paddedString.toString());

                for (int i = 1; i < height - 2; i++) {
                    paddedString = new StringBuilder();

                    if (i == (height / 2)) {
                        paddedString.append(housingString);
                    }
                    else {
                        paddedString.append(SPACE.repeat(width - 2));
                    }
                    componentInfo.add(paddedString.toString());
                }
            }
            case Cannon cannon -> {

            }
            case Engine engine -> {

            }
            case Shield shield -> {

                int direction = shield.getDirection();



                switch (direction) {
                    // 0 --> Shield is covering top and right sides
                    case 0 -> {

                        paddedString = new StringBuilder();
                        paddedString.append(" ");
                        paddedString.append(addColor("\u2500", ANSIColors.GREEN).repeat(width - 5));
                        paddedString.append(addColor("\u2510", ANSIColors.GREEN));
                        paddedString.append(" ");
                        componentInfo.add(paddedString.toString());

                        for (int i = 1; i < height - 2; i++) {
                            if(i == (height - 2) / 2){
                                padding = (width - 6 -2 - 2) / 2;
                                paddedString = new StringBuilder();
                                paddedString.append(" ".repeat(padding + 1));
                                paddedString.append(addColor(ComponentAlias.SHIELD.getAlias(), ANSIColors.GREEN));
                                paddedString.append(" ".repeat(padding));
                                paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                                paddedString.append(" ");
                                componentInfo.add(paddedString.toString());
                            } else {
                                paddedString = new StringBuilder();
                                paddedString.append(" ".repeat(width - 4));
                                paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                                paddedString.append(" ");
                                componentInfo.add(paddedString.toString());
                            }
                        }
                    }
                    // 1 --> Shield is covering right and bottom sides
                    case 1 -> {

                        for (int i = 0; i < height - 3; i++) {
                            if(i == (height - 3) / 2){
                                padding = (width - 6 -2 - 2) / 2;
                                paddedString = new StringBuilder();
                                paddedString.append(" ".repeat(padding + 1));
                                paddedString.append(addColor(ComponentAlias.SHIELD.getAlias(), ANSIColors.GREEN));
                                paddedString.append(" ".repeat(padding));
                                paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                                paddedString.append(" ");
                                componentInfo.add(paddedString.toString());
                            } else {
                            paddedString = new StringBuilder();
                            paddedString.append(" ".repeat(width - 4));
                            paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                            paddedString.append(" ");
                            componentInfo.add(paddedString.toString());
                            }
                        }
                        paddedString = new StringBuilder();
                        paddedString.append(" ");
                        paddedString.append(addColor("\u2500", ANSIColors.GREEN).repeat(width - 5));
                        paddedString.append(addColor("\u2518", ANSIColors.GREEN));
                        paddedString.append(" ");
                        componentInfo.add(paddedString.toString());


                    }
                    // 2 --> Shield is covering bottom and left sides
                    case 2 -> {

                        for (int i = 0; i < height - 3; i++) {
                            if(i == (height - 3) / 2){
                                padding = (width - 6 -2 - 2) / 2;
                                paddedString = new StringBuilder();
                                paddedString.append(" ");
                                paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                                paddedString.append(" ".repeat(padding - 1));
                                paddedString.append(addColor(ComponentAlias.SHIELD.getAlias(), ANSIColors.GREEN));
                                paddedString.append(" ".repeat(padding + 2));
                                componentInfo.add(paddedString.toString());
                            } else {
                                paddedString = new StringBuilder();
                                paddedString.append(" ");
                                paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                                paddedString.append(" ".repeat(width - 4));
                                componentInfo.add(paddedString.toString());
                            }
                        }

                        paddedString = new StringBuilder();
                        paddedString.append(" ");
                        paddedString.append(addColor("\u2514", ANSIColors.GREEN));
                        paddedString.append(addColor("\u2500", ANSIColors.GREEN).repeat(width - 5));
                        paddedString.append(" ");
                        componentInfo.add(paddedString.toString());
                    }
                    // 3 --> Shield is covering left and top sides
                    case 3 -> {

                        paddedString = new StringBuilder();
                        paddedString.append(" ");
                        paddedString.append(addColor("\u250C", ANSIColors.GREEN));
                        paddedString.append(addColor("\u2500", ANSIColors.GREEN).repeat(width - 5));
                        paddedString.append(" ");
                        componentInfo.add(paddedString.toString());
                        for (int i = 1; i < height - 2; i++) {
                            if(i == (height - 2) / 2){
                                padding = (width - 6 -2 - 2) / 2;
                                paddedString = new StringBuilder();
                                paddedString.append(" ");
                                paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                                paddedString.append(" ".repeat(padding - 1));
                                paddedString.append(addColor(ComponentAlias.SHIELD.getAlias(), ANSIColors.GREEN));
                                paddedString.append(" ".repeat(padding + 2));
                                componentInfo.add(paddedString.toString());
                            } else {
                                paddedString = new StringBuilder();
                                paddedString.append(" ");
                                paddedString.append(addColor("\u2502", ANSIColors.GREEN));
                                paddedString.append(" ".repeat(width - 4));
                                componentInfo.add(paddedString.toString());
                            }
                        }


                    }
                }






//                for (int i = 0; i < height - 2; i++) {
//                    paddedString = new StringBuilder();
//                    paddedString.append(" ".repeat(width - 2));
//                    componentInfo.add(paddedString.toString());
//                }




            }
            case Storage storage -> {
                // Writes the component's name
                padding = width - 2 - 1 - ComponentAlias.STORAGE.getAlias().length();
                paddedString = new StringBuilder(SPACE + ComponentAlias.STORAGE.getAlias());

                paddedString.append(SPACE.repeat(padding));
                componentInfo.add(paddedString.toString());

                int maxCapacity = storage.getCapacity();
                int occupiedSlots = storage.getStoredItems().size();
                int storageStringLength = 2 * maxCapacity - 1;
                int currItemIndex = 0;

                for (int i = 1; i < height - 2; i++) {
                    paddedString = new StringBuilder();

                    for (int j = 1; j < width - 1; j++) {
                        if (i == height / 2 && j == width / 2) {
                            padding = (width - 2 - storageStringLength) / 2;
                            paddedString = new StringBuilder();

                            // Padding before the storage indicator
                            paddedString.append(SPACE.repeat(padding));

                            // Adding alternated storage indicators
                            for (int k = 0; k < storageStringLength; k++) {
                                if (k % 2 == 0) {
                                    if (occupiedSlots > 0) {
                                        switch (storage.getStoredItems().get(currItemIndex).getColor()) {
                                            case RED -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_RED));
                                            case YELLOW -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_YELLOW));
                                            case GREEN -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_GREEN));
                                            case BLUE -> paddedString.append(PrintUtils.addColor("\u2588", ANSIColors.BRIGHT_BLUE));
                                        }
                                        occupiedSlots--;
                                        currItemIndex++;
                                    }
                                    else {
                                        paddedString.append("\u2588");
                                    }
                                }
                                else {
                                    paddedString.append(SPACE);
                                }
                            }

                            // Padding after the storage indicator
                            paddedString.append(SPACE.repeat(padding));
                            break;
                        }
                        else {
                            paddedString.append(SPACE);
                        }
                    }
                    componentInfo.add(paddedString.toString());
                }
            }
            case Structural structure -> {
                // Writes the component's name
                padding = width - 2 - 1 - ComponentAlias.STRUCTURAL.getAlias().length();
                paddedString = new StringBuilder(SPACE + ComponentAlias.STRUCTURAL.getAlias());

                paddedString.append(SPACE.repeat(padding));
                componentInfo.add(paddedString.toString());

                for (int i = 0; i < height - 2; i++) {
                    paddedString = new StringBuilder();
                    paddedString.append(SPACE.repeat(width - 2));
                    componentInfo.add(paddedString.toString());
                }
            }
            case Vital vital -> {
                padding = width - 2 - 1 - ComponentAlias.VITAL.getAlias().length();
                paddedString = new StringBuilder();
                if(vital.getVitalType() == VitalType.PURPLE_VITAL) {
                    paddedString.append(addColor(SPACE + ComponentAlias.VITAL.getAlias(), ANSIColors.MAGENTA));
                } else {
                    paddedString.append(addColor(SPACE + ComponentAlias.VITAL.getAlias(), ANSIColors.BRIGHT_YELLOW));
                }

                paddedString.append(SPACE.repeat(padding));
                componentInfo.add(paddedString.toString());

                for (int i = 0; i < height - 2; i++) {
                    paddedString = new StringBuilder();
                    paddedString.append(SPACE.repeat(width - 2));
                    componentInfo.add(paddedString.toString());
                }
            }
        }

        return componentInfo;
    }

    // Test version, will be tested with 2 parameters
    /**
     * ATTENTION: The components given in input will be composed on a single line!
     *
     * @param componentsInfo A list which contains multiple componentInfo
     * @param width The width of the box that holds the component (minimum is 5)
     * @param height The height of the box that holds the component (minimum is 5)
     * @return A single List<String> that contains the composition of all the component info give in input
     * */
    public static List<String> composeComponents(List<List<String>> componentsInfo, int width, int height) {
        List<String> composedInfo = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            StringBuilder composedLine = new StringBuilder();
            for (int j = 0; j < componentsInfo.size(); j++) {
                composedLine.append(componentsInfo.get(j).get(i));
                composedLine.append(SPACE);
            }
//            for (List<String> componentInfo : componentsInfo) {
//                composedLine.append(componentInfo);
//                composedLine.append(SPACE);
//            }
            composedInfo.add(composedLine.toString());
        }
        return composedInfo;
    }


}