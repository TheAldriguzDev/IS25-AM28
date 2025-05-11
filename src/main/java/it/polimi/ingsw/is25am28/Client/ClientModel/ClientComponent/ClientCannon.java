package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Components.Cannon;
import it.polimi.ingsw.is25am28.Model.Connector;
import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public final class ClientCannon extends ClientComponent {
    private final int force;

    public ClientCannon(int id, List<Integer> sides, int force, String path) {
        super(id, sides, path);
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
    public boolean requireEnergy() {
        return force > 1;
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

        // Adding the component's name
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
