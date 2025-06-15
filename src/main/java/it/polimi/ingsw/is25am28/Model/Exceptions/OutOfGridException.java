package it.polimi.ingsw.is25am28.Model.Exceptions;

public class OutOfGridException extends RuntimeException {
    /**
     * Creates a OutOfGridException with an included message
     */
    public OutOfGridException(String message) {
        super(message);
    }

    /**
     * Creates a OutOfGridException without an included message
     */
    public OutOfGridException() {
        super();
    }
}
