package it.polimi.ingsw.is25am28.Model.EventCards;

public enum WarZoneAction {
    FIREPOWER,
    ENGINEPOWER,
    HUMANS;

    public static WarZoneAction fromInteger(Integer ordinal) throws IllegalArgumentException {
        return switch (ordinal) {
            case 0 -> FIREPOWER;
            case 1 -> ENGINEPOWER;
            case 2 -> HUMANS;
            default -> throw new IllegalArgumentException("Unknown action from ordinal: " + ordinal);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case FIREPOWER -> "Firepower";
            case ENGINEPOWER -> "Enginepower";
            case HUMANS -> "Humans";
            default -> throw new IllegalArgumentException("Unknown action: " + this);
        };
    }
}
