package it.polimi.ingsw.is25am28;

public enum Connector {

    ZERO_PIPES,
    ONE_PIPE,
    TWO_PIPES,
    THREE_PIPES;


    /**
     * ######### IMPORTANT NOTE ############
     * must be overwritten if the declaration changes. 
     * generally, DON'T change declaration order.
     * @param ordinal
     * @return
     */
    public static Connector fromOrdinal( int ordinal ){
        switch(ordinal%4){
            case 0: return ZERO_PIPES;
            case 1: return ONE_PIPE;
            case 2: return TWO_PIPES;
            case 3: return THREE_PIPES;
        }
        throw new Error("[Connector.fromOrdinal] invalid ordinal value: " + ordinal );
    }

}