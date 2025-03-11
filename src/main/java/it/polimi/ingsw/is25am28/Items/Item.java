package it.polimi.ingsw.is25am28.Items;

public class Item {
    private final ItemColor color;

    Item(ItemColor color) {
        this.color = color;
    }

    public ItemColor getColor() {
        return color;
    }

    public int getValue() {
        return color.getValue();
    }
}
