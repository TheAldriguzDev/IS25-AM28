package it.polimi.ingsw.is25am28.Lifeform;

public enum LifeformType {
    ASTRONAUT(1, 0, 0),
    PURPLE_ALIEN(2, 2, 0),
    BROWN_ALIEN(2, 0, 2);

    private final int requiredSpace;
    private final int attackBoost;
    private final int powerBoost;

    LifeformType(int requiredSpace, int attackBoost, int powerBoost) {
        this.requiredSpace = requiredSpace;
        this.attackBoost = attackBoost;
        this.powerBoost = powerBoost;
    }

    public int getRequiredSpace() {
        return requiredSpace;
    }

    public int getAttackBoost() {
        return attackBoost;
    }

    public int getPowerBoost() {
        return powerBoost;
    }
}
