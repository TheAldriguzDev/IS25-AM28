package it.polimi.ingsw.is25am28.Model.Lifeform;

public class Lifeform {
    private final LifeformType lifeformType;

    public Lifeform(LifeformType lifeformType) {
        this.lifeformType = lifeformType;
    }

    public LifeformType getLifeformType() {
        return lifeformType;
    }

    public int getRequiredSpace() {
        return lifeformType.getRequiredSpace();
    }

    public int getAttackBoost() {
        return lifeformType.getAttackBoost();
    }

    public int getPowerBoost() {
        return lifeformType.getPowerBoost();
    }
}
