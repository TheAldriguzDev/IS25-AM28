package it.polimi.ingsw.is25am28.Model.EventCards;

public enum WarZoneAction {
    FIREPOWER,
    ENGINEPOWER,
    HUMANS;

    /**
     * @param ordinal The integer value associated with a specific WarZone action
     * @return The WarZone action corresponding to the given ordinal value.
     * @throws IllegalArgumentException If the given ordinal is invalid (i.e.: doesn't have a corresponding action)
     */
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
        };
    }
}
