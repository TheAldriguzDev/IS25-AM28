package it.polimi.ingsw.is25am28.Model.Exceptions;

public class OutOfShipException extends RuntimeException {
    /**
     * Creates a OutOfShipException with an included message
     */
    public OutOfShipException(String message) {
        super(message);
    }

    /**
     * Creates a OutOfShipException without an included message
     */
    public OutOfShipException() {
        super();
    }
}
