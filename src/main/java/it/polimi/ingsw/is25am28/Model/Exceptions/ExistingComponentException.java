package it.polimi.ingsw.is25am28.Model.Exceptions;

public class ExistingComponentException extends RuntimeException {
    /**
     * Creates a ExistingComponentException with an included message
     */
    public ExistingComponentException(String message) {
        super(message);
    }

    /**
     * Creates a ExistingComponentException without an included message
     */
    public ExistingComponentException() {
        super();
    }
}