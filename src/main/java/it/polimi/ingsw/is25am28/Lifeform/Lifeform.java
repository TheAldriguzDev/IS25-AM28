package it.polimi.ingsw.is25am28.Lifeform;

public class Lifeform {
    private final LifeformType lifeformType;

    Lifeform(LifeformType lifeformType) {
        this.lifeformType = lifeformType;
    }

    public LifeformType getLifeformType() {
        return lifeformType;
    }

    public int getRequiredSpace() {
        return lifeformType.getRequiredSpace();
    }

    public int getAttackBoos() {
        return lifeformType.getAttackBoost();
    }

    public int getPowerBoost() {
        return lifeformType.getPowerBoost();
    }
}
