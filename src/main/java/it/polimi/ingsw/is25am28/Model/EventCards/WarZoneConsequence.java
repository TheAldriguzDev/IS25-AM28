package it.polimi.ingsw.is25am28.Model.EventCards;

public enum WarZoneConsequence {
    REQUIREDCREW,
    MOVEMENTSTEPS,
    SHOOTINGSEQUENCE,
    LOSSITEMS;


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
            default -> throw new IllegalArgumentException("Unknown action consequence: " + this);
        };
    }
}
