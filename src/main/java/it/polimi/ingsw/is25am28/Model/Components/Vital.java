package it.polimi.ingsw.is25am28.Model.Components;

import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.*;

public final class Vital extends Component {
    public static final String alias = "VITAL";
    private final VitalType vitalType;

    public Vital(List<Integer> connectors, int type) {
        super(connectors);

        if (type == VitalType.BROWN_VITAL.ordinal()) {
            this.vitalType = VitalType.BROWN_VITAL;
        }
        else if (type == VitalType.PURPLE_VITAL.ordinal()) {
            this.vitalType = VitalType.PURPLE_VITAL;
        }
        else {
            throw new IllegalArgumentException("ERROR: Given vital type is not recognized");
        }
    }

    public VitalType getVitalType() {
        return vitalType;
    }

    @Override
    public Map<String,Object> toMap() {
        Map<String,Object> map = super.toMap();

        map.put("type", vitalType.ordinal());

        return map;
    }

    @Override
    public List<String> getComponentScreen() {
        // TODO: Understand better these indexes
        int scale = 3;
        int height = scale;
        int width = 3 * height + 2;
        int padding;

        List<String> screen = new ArrayList<String>();
        StringBuilder paddedString = new StringBuilder();

        // Creating the custom border character list that will be
        // used by the wrapper to create the border
        List<String> customBorderScheme = generateComponentCustomBorder();
        padding = width - Vital.alias.length() - 1;

        // Setting the name with the same color as the vital unit's color
        if (this.getVitalType() == VitalType.PURPLE_VITAL) {
            paddedString.append(addColor(SPACE + Vital.alias, ANSIColors.MAGENTA));
        }
        else {
            paddedString.append(addColor(SPACE + Vital.alias, ANSIColors.BRIGHT_YELLOW));
        }

        // Adding the colored name string to the screen
        paddedString.append(SPACE.repeat(padding));
        screen.add(paddedString.toString());

        // Filling the rest of the space with padding
        for (int i = 1; i < height; i++) {
            paddedString = new StringBuilder();
            paddedString.append(SPACE.repeat(width));
            screen.add(paddedString.toString());
        }

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}