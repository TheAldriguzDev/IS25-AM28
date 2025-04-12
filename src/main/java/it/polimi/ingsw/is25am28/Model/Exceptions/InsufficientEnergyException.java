package it.polimi.ingsw.is25am28.Model.Exceptions;

public class InsufficientEnergyException extends RuntimeException {
    // Creates an InsufficientEnergyException with an included message
    public InsufficientEnergyException(String message) {
        super(message);
    }

    // Creates a InsufficientEnergyException without an included message
    public InsufficientEnergyException() { super(); }
}
