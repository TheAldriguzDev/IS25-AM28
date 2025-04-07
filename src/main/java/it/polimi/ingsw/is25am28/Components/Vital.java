package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.ComponentAlias;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.TUI.PrintUtils.*;

public final class Vital extends Component {
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
    protected void setComponentScreen(WidgetTUI componentWidget) throws NullWidgetException {
        if (componentWidget == null) {
            throw new NullWidgetException("ERROR: Given widget is null (Cannot add screen)");
        }

        int height = componentWidget.getHeight();
        int width = componentWidget.getWidth();
        int padding;

        List<String> screen = new ArrayList<String>();
        StringBuilder paddedString = new StringBuilder();

        padding = width - ComponentAlias.VITAL.getAlias().length() - 1;

        // Setting the name with the same color as the vital unit's color
        if (this.getVitalType() == VitalType.PURPLE_VITAL) {
            paddedString.append(addColor(getSpace() + ComponentAlias.VITAL.getAlias(), ANSIColors.MAGENTA));
        }
        else {
            paddedString.append(addColor(getSpace() + ComponentAlias.VITAL.getAlias(), ANSIColors.BRIGHT_YELLOW));
        }

        // Adding the colored name string to the screen
        paddedString.append(getSpace().repeat(padding));
        screen.add(paddedString.toString());

        // Filling the rest of the space with padding
        for (int i = 1; i < height; i++) {
            paddedString = new StringBuilder();
            paddedString.append(getSpace().repeat(width));
            screen.add(paddedString.toString());
        }

        componentWidget.setScreen(screen);
    }
}