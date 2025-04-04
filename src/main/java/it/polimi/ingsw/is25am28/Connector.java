package it.polimi.ingsw.is25am28;

public enum Connector {
    ZERO_PIPES,
    ONE_PIPE,
    TWO_PIPES,
    THREE_PIPES;

    /**
     * @param ordinal The value of the Connector instance that you want to get
     * @return The corresponding Connector instance to the given ordinal value
     */
    public static Connector fromOrdinal( int ordinal ){
        if (ordinal == ZERO_PIPES.ordinal()) {
            return ZERO_PIPES;
        }
        else if (ordinal == ONE_PIPE.ordinal()) {
            return ONE_PIPE;
        }
        else if (ordinal == TWO_PIPES.ordinal()) {
            return TWO_PIPES;
        }
        else if (ordinal == THREE_PIPES.ordinal()) {
            return THREE_PIPES;
        }
        else {
            throw new IllegalArgumentException("[Connector::fromOrdinal] ERROR: Given ordinal value \"" + ordinal + "\" is invalid");
        }
    }
}