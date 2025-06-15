package it.polimi.ingsw.is25am28.Model.Items;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;

public enum ItemColor {
    RED(4),
    YELLOW(3),
    GREEN(2),
    BLUE(1);

    private final int value;

    // Private Constructor
    ItemColor(int value) {
        this.value = value;
    }

    /**
     * @return This item color's respective value.
     */
    public int getValue() {
        return value;
    }

    /**
     * @return This item color's name
     */
    @Override
    public String toString() {
        return switch (this) {
            case RED -> "RED";
            case YELLOW -> "YELLOW";
            case GREEN -> "GREEN";
            case BLUE -> "BLUE";
        };
    }

    /**
     * @return The corresponding ANSI color
     */
    public String getANSIColor() {
        return switch (this) {
            case RED -> ANSIColors.RED;
            case YELLOW -> ANSIColors.YELLOW;
            case GREEN -> ANSIColors.GREEN;
            case BLUE -> ANSIColors.BLUE;
        };
    }

    /**
     * @return This item color's image to display in the GUI
     */
    public String getImagePath() {
        return switch (this) {
            case RED -> "/imgs/icons/items/item_red.png";
            case YELLOW ->  "/imgs/icons/items/item_yellow.png";
            case GREEN -> "/imgs/icons/items/item_green.png";
            case BLUE -> "/imgs/icons/items/item_blue.png";
        };
    }
}
