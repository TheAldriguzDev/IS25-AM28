package it.polimi.ingsw.is25am28.Model.Exceptions;

public class TooLowDifficultyLevelException extends RuntimeException {

    public TooLowDifficultyLevelException() {}

    public TooLowDifficultyLevelException(String message) {
        super(message);
    }
}
