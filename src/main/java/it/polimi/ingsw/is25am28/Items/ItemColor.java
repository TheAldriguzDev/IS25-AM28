package it.polimi.ingsw.is25am28.Items;

public enum ItemColor {
    RED(4), YELLOW(3), GREEN(2), BLUE(1);

    private int value;

    ItemColor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
