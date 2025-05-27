package it.polimi.ingsw.is25am28.Model.Lifeform;

import com.fasterxml.jackson.annotation.JsonCreator;

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

    @Override
    public String toString() {
        return switch (this) {
            case ASTRONAUT -> "ASTRONAUT";
            case PURPLE_ALIEN -> "PURPLE_ALIEN";
            case BROWN_ALIEN -> "BROWN_ALIEN";
        };
    }

    public String getImagePath() {
        return switch(this) {
            case ASTRONAUT -> "/imgs/icons/lifeforms/astronaut/astronaut.png";
            case PURPLE_ALIEN -> "/imgs/icons/lifeforms/purple_alien/PurpleAlien.png";
            case BROWN_ALIEN -> "/imgs/icons/lifeforms/brown_alien/BrownAlien.png";
        };
    }

    @JsonCreator
    public static LifeformType fromString(String value) {
        return LifeformType.valueOf(value.toUpperCase());
    }
}
