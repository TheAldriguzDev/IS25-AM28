package it.polimi.ingsw.is25am28.exceptions;

public class ExistingComponentException extends RuntimeException {
    // Creates a NullComponentException with an included message
    public ExistingComponentException(String message) {
        super(message);
    }

    // Creates a NullComponentException without an included message
    public ExistingComponentException() {
        super();
    }
}