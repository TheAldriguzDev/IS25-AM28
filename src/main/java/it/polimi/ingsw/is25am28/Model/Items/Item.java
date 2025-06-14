package it.polimi.ingsw.is25am28.Model.Items;

public class Item {
    private final ItemColor color;

    // Constructor
    public Item(ItemColor color) {
        this.color = color;
    }

    /**
     * @return This item's color
     */
    public ItemColor getColor() {
        return color;
    }

    /**
     * @return This item's value in credits
     */
    public int getValue() {
        return color.getValue();
    }
    
    @Override
    public String toString() {
        return color.toString();
    }
}
