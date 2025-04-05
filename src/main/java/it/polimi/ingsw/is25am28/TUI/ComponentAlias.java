package it.polimi.ingsw.is25am28.TUI;

public enum ComponentAlias {
    BATTERY("BATTERY"),
    CABIN("CABIN"),
    CANNON("CANNON"),
    ENGINE("ENGINE"),
    SHIELD("SHIELD"),
    STORAGE("STORAGE"),
    STRUCTURAL("STRUCT"),
    VITAL("VITAL");

    private final String alias;

    ComponentAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
