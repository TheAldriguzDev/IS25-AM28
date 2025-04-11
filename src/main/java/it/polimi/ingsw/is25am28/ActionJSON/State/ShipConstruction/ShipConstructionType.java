package it.polimi.ingsw.is25am28.ActionJSON.State.ShipConstruction;

public enum ShipConstructionType {
    TILE_EVENT,
    SHIP_EVENT,
    TIMER_EVENT;

    @Override
    public String toString() {
        return switch (this){
            case TILE_EVENT -> "TILE_EVENT";
            case SHIP_EVENT -> "SHIP_EVENT";
            case TIMER_EVENT -> "TIMER_EVENT";
        };
    }
}
