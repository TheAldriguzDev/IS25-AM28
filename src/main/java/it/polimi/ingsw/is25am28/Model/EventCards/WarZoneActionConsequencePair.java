package it.polimi.ingsw.is25am28.Model.EventCards;

public class WarZoneActionConsequencePair {
    private final WarZoneAction action;
    private final WarZoneConsequence consequence;

    public WarZoneActionConsequencePair( WarZoneAction action, WarZoneConsequence consequence ) {
        this.action = action;
        this.consequence = consequence;
    }

    public WarZoneAction getAction() {
        return this.action;
    }

    public WarZoneConsequence getConsequence() {
        return this.consequence;
    }

    @Override
    public String toString() {
        return this.action + " --> " + this.consequence;
    }
}
