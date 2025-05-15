package it.polimi.ingsw.is25am28.Model.Player;

import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;

public enum PlayerColor {
    GREEN(ANSIColors.GREEN),
    RED(ANSIColors.RED),
    BLUE(ANSIColors.BLUE),
    YELLOW(ANSIColors.YELLOW);

    // Each player color contains the corresponding ANSI color string
    private String colorString;

    PlayerColor(String colorString) {
        this.colorString = colorString;
    }

    public static PlayerColor fromInteger(int color) {
        if (color == GREEN.ordinal()) {
            return GREEN;
        }
        else if (color == RED.ordinal()) {
            return RED;
        }
        else if (color == BLUE.ordinal()) {
            return BLUE;
        }

        return YELLOW;
    }

    public static PlayerColor fromString(String colorName) {
        if (colorName != null && !colorName.isEmpty()) {
            colorName = colorName.toUpperCase();

            if (colorName.equals(RED.name()))       return RED;
            if (colorName.equals(YELLOW.name()))    return YELLOW;
            if (colorName.equals(GREEN.name()))     return GREEN;
            if (colorName.equals(BLUE.name()))      return BLUE;
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public String formatColor(String text) {
        return switch (this) {
            case RED -> PrintUtils.addColor(text, ANSIColors.RED);
            case GREEN -> PrintUtils.addColor(text, ANSIColors.GREEN);
            case BLUE -> PrintUtils.addColor(text, ANSIColors.BLUE);
            case YELLOW -> PrintUtils.addColor(text, ANSIColors.YELLOW);
        };
    }

    /**
     * @return The corresponding ANSI color string of this player color
     */
    public String getColorString() {
        return this.colorString;
    }
}