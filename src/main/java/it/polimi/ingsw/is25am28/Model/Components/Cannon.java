package it.polimi.ingsw.is25am28.Model.Components;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

public final class Cannon extends Component {
    public static final String alias = "CANNON";
    private final int force;

    public Cannon(List<Integer> connectors, int force, String path) {
        super(connectors, path);
        this.force = force;
    }

    /**
     * @return This cannon's firepower by also taking into account the
     * current rotation of the cannon
     */
    public float getFirePower() {
        return (getDirection() != 0) ? (((float) force) / 2) : force;
    }

    /**
     * @return If the current cannon is a double cannon and thus
     * requires energy to be activated
     */
    @Override
    public boolean requiresEnergy() {
        return (force > 1);
    }

    /**
     * @param nearest This cannon's neighbours in the following order:
     *                top[0], right[1], bottom[2], left[3]
     * @return TRUE if the cannon is correctly placed, in terms of component connectors, and if
     * the cannon has an empty cell directly in front of the barrel, FALSE otherwise
     */
    @Override
    public boolean check(Component[] nearest) {
        if (nearest[getDirection()] != null) {
            return false;
        }

        return super.check(nearest);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();

        map.put("force", force);

        return map;
    }

    @Override
    public List<String> getComponentScreen() {
        // TODO: Understand better these indexes
        int scale = 3;
        int height = scale;
        int width = 3 * height + 2;

        int direction = this.getDirection();
        int padding;

        List<String> screen = new ArrayList<String>();
        StringBuilder paddedString;

        // Creating the custom border character list that will be
        // used by the wrapper to create the border
        List<String> customBorderScheme = generateComponentCustomBorder();

        // Adding the component's cardName
        paddedString = new StringBuilder(SPACE + Cannon.alias);
        screen.add(paddedString + SPACE.repeat(width - paddedString.length()));

        for (int i = 1; i < height; i++) {
            paddedString = new StringBuilder();

            if (i == (height / 2) + 1) {
                switch (direction) {
                    // 0 --> Cannon is pointing in front of the ship
                    case 0 -> {
                        if (this.getFirePower() == 2) {
                            padding = (width - 3) / 2;
                            paddedString.append(SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_TOP_ARROW);
                            paddedString.append(SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_TOP_ARROW);
                        paddedString.append(SPACE.repeat(padding));
                    }
                    // 1 --> Cannon is pointing to the right of the ship
                    case 1 -> {
                        if (this.getFirePower() == 1) {
                            padding = (width - 3) / 2;
                            paddedString.append(SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_RIGHT_ARROW);
                            paddedString.append(SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_RIGHT_ARROW);
                        paddedString.append(SPACE.repeat(padding));
                    }
                    // 2 --> Cannon is pointing to the back of the ship
                    case 2 -> {
                        if (this.getFirePower() == 1) {
                            padding = (width - 3) / 2;
                            paddedString.append(SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_BOTTOM_ARROW);
                            paddedString.append(SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_BOTTOM_ARROW);
                        paddedString.append(SPACE.repeat(padding));
                    }
                    // 3 --> Cannon is pointing to the left of the ship
                    case 3 -> {
                        if (this.getFirePower() == 1) {
                            padding = (width - 3) / 2;
                            paddedString.append(SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_LEFT_ARROW);
                            paddedString.append(SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_LEFT_ARROW);
                        paddedString.append(SPACE.repeat(padding));
                    }
                }
            }
            else {
                paddedString.append(SPACE.repeat(width));
            }

            screen.add(paddedString.toString());
        }

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}