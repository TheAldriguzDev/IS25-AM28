package it.polimi.ingsw.is25am28.Model.EventCards.HazardEntities;

public class PlasmaShot {
    private final int size;
    private final int orientation;

    // Constructor
    public PlasmaShot(int size, int orientation) {
        this.size = size;
        this.orientation = orientation;
    }

    /**
     * @return This plasma shot's size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @return This plasma shot's orientation
     */
    public int getOrientation() {
        return this.orientation;
    }
}