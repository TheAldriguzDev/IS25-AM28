package it.polimi.ingsw.is25am28.Model.Exceptions;

public class TooManyAliensException extends RuntimeException {
    /**
     * Creates a TooManyAliensException with an included message
     */
    public TooManyAliensException(String message) {
        super(message);
    }

    /**
     * Creates a TooManyAliensException without an included message
     */
    public TooManyAliensException() {
        super();
    }
}
