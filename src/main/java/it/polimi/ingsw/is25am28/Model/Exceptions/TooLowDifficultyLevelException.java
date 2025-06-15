package it.polimi.ingsw.is25am28.Model.Exceptions;

public class TooLowDifficultyLevelException extends RuntimeException {
    /**
     * Creates a TooLowDifficultyLevelException with an included message
     */
    public TooLowDifficultyLevelException(String message) {
        super(message);
    }

    /**
     * Creates a TooLowDifficultyLevelException without an included message
     */
    public TooLowDifficultyLevelException() {
        super();
    }
}
