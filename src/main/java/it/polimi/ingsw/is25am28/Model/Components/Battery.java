package it.polimi.ingsw.is25am28.Model.Components;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

public final class Battery extends Component {
    public static final String alias = "BATTERY";
    private final int maxAvailability;
    private int available;

    // Constructor
    public Battery(List<Integer> connectors, int maxAvailability, String path) {
        super(connectors, path);

        this.maxAvailability = maxAvailability;
        available = maxAvailability;
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
     * @param energyLevel The energy level that this battery will be set to
     * @throws IllegalArgumentException If anyone tries to overcharge the battery or set its charge level to a negative value
     */
    public void setAvailability(int energyLevel) throws IllegalArgumentException {
        if (energyLevel < 0 || energyLevel > maxAvailability) {
            throw new IllegalArgumentException("ERROR: Battery energy level must be between 0 and maxAvailability");
        }
        else {
            available = energyLevel;
        }
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
    public Map<String,Object> toMap() {
        Map<String,Object> map = super.toMap();

        map.put("capacity", maxAvailability );
        map.put("available", available );


        return map;
    }

    @Override
    public List<String> getComponentScreen() {
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