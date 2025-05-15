package it.polimi.ingsw.is25am28.Model.Components;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

public final class Engine extends Component {
    public static final String alias = "ENGINE";
    private final int speed;

    public Engine(List<Integer> connectors, int speed, String path) {
        super(connectors, path);
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean requireEnergy() {
        return speed > 1;
    }

    @Override
    public boolean check(Component[] nearest) {
        // Engine is wrongly placed if the thruster is not facing backwards
        // (i.e.: the engine's direction is not pointing towards the thrust vector)
        if (getDirection() != 0) {
            return false;
        }

        // Engine is wrongly placed if the thruster is blocked by a component
        if (nearest[2] != null) {
            return false;
        }

        return super.check(nearest);
    }

    @Override
    public Map<String,Object> toMap(){
        Map<String,Object> map = super.toMap();

        map.put("speed", this.speed);

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

        // Adding the component's name
        paddedString = new StringBuilder(PrintUtils.SPACE + Engine.alias);
        screen.add(paddedString + PrintUtils.SPACE.repeat(width - paddedString.length()));

        for (int i = 1; i < height; i++) {
            paddedString = new StringBuilder();

            if (i == (height / 2) + 1) {
                switch (direction) {
                    // 0 --> Engine is pointing to the bottom of the ship
                    case 0 -> {
                        if (this.getSpeed() == 2) {
                            padding = (width - 3) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_BOTTOM_ARROW);
                            paddedString.append(PrintUtils.SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_BOTTOM_ARROW);
                        paddedString.append(PrintUtils.SPACE.repeat(padding));
                    }
                    // 1 --> Engine is pointing to the left of the ship
                    case 1 -> {
                        if (this.getSpeed() == 2) {
                            padding = (width - 3) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_LEFT_ARROW);
                            paddedString.append(PrintUtils.SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_LEFT_ARROW);
                        paddedString.append(PrintUtils.SPACE.repeat(padding));
                    }
                    // 2 --> Engine is pointing to the top of the ship
                    case 2 -> {
                        if (this.getSpeed() == 2) {
                            padding = (width - 3) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_TOP_ARROW);
                            paddedString.append(PrintUtils.SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_TOP_ARROW);
                        paddedString.append(PrintUtils.SPACE.repeat(padding));
                    }
                    // 3 --> Engine is pointing to the right of the ship
                    case 3 -> {
                        if (this.getSpeed() == 2) {
                            padding = (width - 3) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                            paddedString.append(UnicodeCharacters.SINGLE_RIGHT_ARROW);
                            paddedString.append(PrintUtils.SPACE);
                        } else {
                            padding = (width - 1) / 2;
                            paddedString.append(PrintUtils.SPACE.repeat(padding));
                        }
                        paddedString.append(UnicodeCharacters.SINGLE_RIGHT_ARROW);
                        paddedString.append(PrintUtils.SPACE.repeat(padding));
                    }
                }
            }
            else {
                paddedString.append(PrintUtils.SPACE.repeat(width));
            }

            screen.add(paddedString.toString());
        }

        return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
    }
}