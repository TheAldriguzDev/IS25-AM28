package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Components.Battery;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

public final class ClientBattery extends ClientComponent {
    private final int maxAvailability;
    private int available;

    public ClientBattery(int id, List<Integer> sides, int maxAvailability, String path) {
        super(id, sides, path);
        this.maxAvailability = maxAvailability;
        this.available = maxAvailability;
    }

    /**
     * @return The currently available energy amount stored inside the battery
     */
    public int getAvailability() {
        return available;
    }

    /**
     * @return The maximum charge this battery can hold when full
     */
    public int getMaxAvailability() {
        return maxAvailability;
    }

    /**
     * @return Sets the battery's availability
     */
    public void setAvailability(int energyLevel) throws IllegalArgumentException {
            available = energyLevel;
    }

    /**
     * @param energyToConsume The units of charge that will be consumed from this battery
     * @throws IllegalArgumentException If anyone attempts to discharge the battery for more than its currently storing
     */
    public void useBattery(int energyToConsume) throws IllegalArgumentException {
        if (available >= energyToConsume) {
            available -= energyToConsume;
        }
        else {
            throw new IllegalArgumentException("ERROR: Cannot consume more charge than available");
        }
    }

    @Override
    public List<String> getComponentScreen() {
        // TODO: Understand better these indexes
        int scale = 3;
        int height = scale;
        int width = 3 * height + 2;

        List<String> screen = new ArrayList<String>();
        String nameAlias = SPACE + Battery.alias;
        StringBuilder paddedString;
        int padding;

        int maxCapacity = this.getMaxAvailability();
        int batteryLevel = this.getAvailability();
        int batteryStringLength = 2 * maxCapacity - 1;

        // Creating the custom border character list that will be
        // used by the wrapper to create the border
        List<String> customBorderScheme = generateComponentCustomBorder();

        // Adding the name
        screen.add(nameAlias + SPACE.repeat(width - nameAlias.length()));

        // Adding the battery indicator and all the padding spaces
        for (int i = 1; i < height; i++) {
            if (i == (height / 2) + 1) {
                paddedString = new StringBuilder();
                padding = (width - batteryStringLength) / 2;

                // Padding before the energy indicator
                paddedString.append(SPACE.repeat(padding));

                // Adding alternated battery indicators
                for (int k = 0; k < batteryStringLength; k++) {
                    if (k % 2 == 0) {
                        if (batteryLevel > 0) {
                            paddedString.append(PrintUtils.addColor(UnicodeCharacters.FULL_BLOCK, ANSIColors.GREEN));
                            batteryLevel--;
                        }
                        else {
                            paddedString.append(UnicodeCharacters.FULL_BLOCK);
                        }
                    }
                    else {
                        paddedString.append(SPACE);
                    }
                }

                // Padding after the energy indicator
                paddedString.append(SPACE.repeat(padding));
                screen.add(paddedString.toString());
            }
            else {
                screen.add(SPACE.repeat(width));
            }
        }

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}

