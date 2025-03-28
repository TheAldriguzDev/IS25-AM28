package it.polimi.ingsw.is25am28.EventCards;

public enum WarZoneConsequence {
    REQUIREDCREW,
    MOVEMENTSTEPS,
    SHOOTINGSEQUENCE;

    @Override
    public String toString() {
        switch (this) {
            case REQUIREDCREW: return "RequiredCrew";
            case MOVEMENTSTEPS: return "MovementSteps";
            case SHOOTINGSEQUENCE: return "ShootingSequence";
            default: throw new IllegalArgumentException("Unknown action consequence: " + this);
        }
    }
}
