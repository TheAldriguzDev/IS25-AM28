package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.*;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Battery extends Component {
    public static final String alias = "BATTERY";
    private final int maxAvailability;
    private int available;

    public Battery(List<Integer> connectors, int maxAvailability) {
        super(connectors);
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
        // TODO: Understand better these indexes
        int scale = 3;
        int height = scale;
        int width = 3 * height + 2;

        List<String> screen = new ArrayList<String>();
        String nameAlias = PrintUtils.getSpace() + Battery.alias;
        StringBuilder paddedString;
        int padding;

        int maxCapacity = this.getMaxAvailability();
        int batteryLevel = this.getAvailability();
        int batteryStringLength = 2 * maxCapacity - 1;

        // Creating the custom border character list that will be
        // used by the wrapper to create the border
        List<String> customBorderScheme = new ArrayList<String>(WidgetTUI.defaultBorderCharacters);

        // Adding this component's connectors to the border scheme
        customBorderScheme.set(8, "" + this.getTopSide().ordinal());
        customBorderScheme.set(9, "" + this.getRightSide().ordinal());
        customBorderScheme.set(10, "" + this.getBottomSide().ordinal());
        customBorderScheme.set(11, "" + this.getLeftSide().ordinal());

        // Adding the name
        screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

        // Adding the battery indicator and all the padding spaces
        for (int i = 1; i < height; i++) {
            if (i == (height / 2) + 1) {
                paddedString = new StringBuilder();
                padding = (width - batteryStringLength) / 2;

                // Padding before the energy indicator
                paddedString.append(PrintUtils.getSpace().repeat(padding));

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
                        paddedString.append(PrintUtils.getSpace());
                    }
                }

                // Padding after the energy indicator
                paddedString.append(PrintUtils.getSpace().repeat(padding));
                screen.add(paddedString.toString());
            }
            else {
                screen.add(PrintUtils.getSpace().repeat(width));
            }
        }

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}