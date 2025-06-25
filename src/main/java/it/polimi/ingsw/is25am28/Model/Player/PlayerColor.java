package it.polimi.ingsw.is25am28.Model.Player;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;

public enum PlayerColor {
    GREEN(ANSIColors.GREEN),
    RED(ANSIColors.RED),
    BLUE(ANSIColors.BLUE),
    YELLOW(ANSIColors.YELLOW);

    // Each player color contains the corresponding ANSI color string
    private final String colorString;

    // Private Constructor
    PlayerColor(String colorString) {
        this.colorString = colorString;
    }

    /**
     * @param color The ordinal of the PlayerColor instance to retrieve.
     *
     * @return The requested PlayerColor instance.
     */
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

    /**
     * @param colorName The name of the PlayerColor instance to retrieve.
     *
     * @return The requested PlayerColor instance.
     */
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

    /**
     * @return The corresponding ANSI color string of this player color.
     */
    public String getColorString() {
        return this.colorString;
    }

    /**
     * @return This instance of PlayerColor's name.
     */
    public String getPlayerColorString() {
        return switch (this) {
            case RED -> "RED";
            case YELLOW -> "YELLOW";
            case GREEN -> "GREEN";
            case BLUE -> "BLUE";
        };
    }
}