package it.polimi.ingsw.is25am28.Model.Lifeform;

public class Lifeform {
    private final LifeformType lifeformType;

    // Constructor
    public Lifeform(LifeformType lifeformType) {
        this.lifeformType = lifeformType;
    }

    /**
     * @return The instance of this lifeform's type
     */
    public LifeformType getLifeformType() {
        return lifeformType;
    }

    /**
     * @return The cabin space occupied by this lifeform.
     */
    public int getRequiredSpace() {
        return lifeformType.getRequiredSpace();
    }

    /**
     * @return The firepower boost granted when this lifeform is onboard a ship.
     */
    public int getAttackBoost() {
        return lifeformType.getAttackBoost();
    }

    /**
     * @return The engine power boost granted when this lifeform is onboard a ship.
     */
    public int getPowerBoost() {
        return lifeformType.getPowerBoost();
    }
}
