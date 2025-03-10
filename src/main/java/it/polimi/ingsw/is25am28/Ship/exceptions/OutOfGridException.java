package it.polimi.ingsw.is25am28.Ship.exceptions;

public class OutOfGridException extends RuntimeException {
    // Creates an OutOfGridException with an included message
    public OutOfGridException(String message) { super(message); }

    // Creates an OutOfGridException without an included message
    public OutOfGridException() { super(); }
}
