package it.polimi.ingsw.is25am28.Model.Exceptions;

public class NoSupportVitalFoundException extends RuntimeException {
    /**
     * Creates a NoSupportVitalFoundException with an included message
     */
    public NoSupportVitalFoundException(String message) {
        super(message);
    }

    /**
     * Creates a NoSupportVitalFoundException without an included message
     */
    public NoSupportVitalFoundException() {
        super();
    }
}
