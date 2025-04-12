package it.polimi.ingsw.is25am28.Model.Items;

public class Item {
    private final ItemColor color;

    public Item(ItemColor color) {
        this.color = color;
    }

    public ItemColor getColor() {
        return color;
    }

    public int getValue() {
        return color.getValue();
    }

    @Override
    public String toString() {
        return color.toString();
    }
}
