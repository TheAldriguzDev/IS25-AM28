package it.polimi.ingsw.is25am28.Player;

public enum PlayerColor {
    GREEN,
    RED,
    BLUE,
    YELLOW;

    public static PlayerColor fromInteger( int color ){
        
        if( color == GREEN.ordinal() ){
            return GREEN;
        } else if( color == RED.ordinal() ){
            return RED;
        } else if( color == BLUE.ordinal() ){
            return BLUE;
        } 

        return YELLOW;
    }

    @Override
    public String toString() {
        return switch (this) {
            case GREEN -> "GREEN";
            case RED -> "RED";
            case BLUE -> "BLUE";
            case YELLOW -> "YELLOW";
        };
    }
}