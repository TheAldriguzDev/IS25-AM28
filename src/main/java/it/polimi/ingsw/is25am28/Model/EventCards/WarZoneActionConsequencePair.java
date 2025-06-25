package it.polimi.ingsw.is25am28.Model.EventCards;

public class WarZoneActionConsequencePair {
    private final WarZoneAction action;
    private final WarZoneConsequence consequence;

    // Constructor
    public WarZoneActionConsequencePair( WarZoneAction action, WarZoneConsequence consequence ) {
        this.action = action;
        this.consequence = consequence;
    }

    /**
     * @return This pair's action
     */
    public WarZoneAction getAction() {
        return this.action;
    }

    /**
     * @return This pair's consequence
     */
    public WarZoneConsequence getConsequence() {
        return this.consequence;
    }

    @Override
    public String toString() {
        return this.action + " --> " + this.consequence;
    }
}
