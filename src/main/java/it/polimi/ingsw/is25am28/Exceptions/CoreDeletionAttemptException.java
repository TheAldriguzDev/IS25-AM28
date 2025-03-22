package it.polimi.ingsw.is25am28.Exceptions;

public class CoreDeletionAttemptException extends RuntimeException {
    // Creates a CoreDeletionAttemptException with an included message
    public CoreDeletionAttemptException(String message) {
        super(message);
    }

    // Creates a CoreDeletionAttemptException without an included message
    public CoreDeletionAttemptException() {
        super();
    }}
