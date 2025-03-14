package it.polimi.ingsw.is25am28.Exceptions;

public class NullComponentException extends RuntimeException {
    // Creates a NullComponentException with an included message
    public NullComponentException(String message) {
        super(message);
    }

    // Creates a NullComponentException without an included message
    public NullComponentException() { super(); }
}
