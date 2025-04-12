package it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities;

public class Meteor {
    private final int size;
    private final int orientation;

    public Meteor(int size, int orientation) {
        this.size = size;
        this.orientation = orientation;
    }

    public int getSize() {
        return size;
    }

    public int getOrientation() {
        return orientation;
    }
}