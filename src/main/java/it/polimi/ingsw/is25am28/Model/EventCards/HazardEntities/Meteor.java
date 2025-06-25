package it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities;

public class Meteor {
    private final int size;
    private final int orientation;

    // Constructor
    public Meteor(int size, int orientation) {
        this.size = size;
        this.orientation = orientation;
    }

    /**
     * @return This meteor's size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @return This meteor's orientation
     */
    public int getOrientation() {
        return this.orientation;
    }
}