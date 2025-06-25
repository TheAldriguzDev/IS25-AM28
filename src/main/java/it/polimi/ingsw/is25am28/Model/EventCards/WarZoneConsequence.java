package it.polimi.ingsw.is25am28.Model.EventCards;

public enum WarZoneConsequence {
    REQUIREDCREW,
    MOVEMENTSTEPS,
    SHOOTINGSEQUENCE,
    LOSSITEMS;

    /**
     * @param ordinal The integer value associated with a specific WarZone consequence
     * @return The WarZone consequence corresponding to the given ordinal value.
     * @throws IllegalArgumentException If the given ordinal is invalid (i.e.: doesn't have a corresponding consequence)
     */
    public static WarZoneConsequence fromInteger(Integer ordinal) throws IllegalArgumentException {
        return switch (ordinal) {
            case 0 -> REQUIREDCREW;
            case 1 -> MOVEMENTSTEPS;
            case 2 -> SHOOTINGSEQUENCE;
            case 3 -> LOSSITEMS;
            default -> throw new IllegalArgumentException("Unknown action from ordinal: " + ordinal);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case REQUIREDCREW -> "RequiredCrew";
            case MOVEMENTSTEPS -> "MovementSteps";
            case SHOOTINGSEQUENCE -> "ShootingSequence";
            case LOSSITEMS -> "LossItems";
        };
    }
}
