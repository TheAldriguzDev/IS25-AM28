package it.polimi.ingsw.is25am28.Player;

import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;

public enum PlayerColor {
    GREEN(ANSIColors.GREEN),
    RED(ANSIColors.RED),
    BLUE(ANSIColors.BLUE),
    YELLOW(ANSIColors.YELLOW);

    // Each player color contains the corresponding ANSI color string
    private String colorString;

    PlayerColor(String colorString) {
        this.colorString = colorString;
    }

    public static PlayerColor fromInteger( int color ){
        
        if( color == GREEN.ordinal() ){
            return GREEN;
        }else if( color == RED.ordinal() ){
            return RED;
        }else if( color == BLUE.ordinal() ){
            return BLUE;
        } 

        return YELLOW;
    }

    /**
     * @return The corresponding ANSI color string of this player color
     */
    public String getColorString() {
        return this.colorString;
    }
}