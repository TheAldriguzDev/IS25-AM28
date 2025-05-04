package it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction;

public enum ShipConstructionType {
    TILE_EVENT,
    PLACE_EVENT,
    SHIP_EVENT,
    DECK_EVENT,
    TIMER_EVENT,
    POPULATE_EVENT;

    @Override
    public String toString() {
        return switch (this){
            case TILE_EVENT -> "TILE_EVENT";
            case PLACE_EVENT -> "PLACE_EVENT";
            case SHIP_EVENT -> "SHIP_EVENT";
            case DECK_EVENT -> "DECK_EVENT";
            case TIMER_EVENT -> "TIMER_EVENT";
            case POPULATE_EVENT -> "POPULATE_EVENT";
        };
    }
}
