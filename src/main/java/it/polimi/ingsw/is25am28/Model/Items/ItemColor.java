package it.polimi.ingsw.is25am28.Model.Items;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;

public enum ItemColor {
    RED(4), YELLOW(3), GREEN(2), BLUE(1);

    private int value;

    ItemColor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

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

    public String getImagePath() {
        return switch (this) {
            case RED -> "/imgs/icons/items/red_item/red_item.png";
            case YELLOW ->  "/imgs/icons/items/yellow_item/yellow_item.png";
            case GREEN -> "/imgs/icons/items/green_item/green_item.png";
            case BLUE -> "/imgs/icons/items/blue_item/blue_item.png";
        };
    }
}
