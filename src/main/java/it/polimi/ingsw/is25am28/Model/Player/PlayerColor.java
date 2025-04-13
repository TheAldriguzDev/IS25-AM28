package it.polimi.ingsw.is25am28.Model.Player;

import it.polimi.ingsw.is25am28.Model.Items.ItemColor;

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

    public static PlayerColor fromString(String color) {
        return switch (color) {
            case "RED" -> PlayerColor.RED;
            case "YELLOW" -> PlayerColor.YELLOW;
            case "GREEN" -> PlayerColor.GREEN;
            case "BLUE" -> PlayerColor.BLUE;
            default -> null;
        };
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