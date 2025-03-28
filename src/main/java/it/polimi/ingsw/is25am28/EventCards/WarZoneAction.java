package it.polimi.ingsw.is25am28.EventCards;

public enum WarZoneAction {
    FIREPOWER,
    ENGINEPOWER,
    HUMANS;

    @Override
    public String toString() {
        switch (this) {
            case FIREPOWER: return "Firepower";
            case ENGINEPOWER: return "Enginepower";
            case HUMANS: return "Humans";
            default: throw new IllegalArgumentException("Unknown action: " + this);
        }
    }
}
