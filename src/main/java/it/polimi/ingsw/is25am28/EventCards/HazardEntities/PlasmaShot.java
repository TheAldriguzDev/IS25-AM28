package it.polimi.ingsw.is25am28.EventCards.HazardEntities;

public class PlasmaShot {
    private final int size;
    private final int orientation;

    public PlasmaShot(int size, int orientation) {
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