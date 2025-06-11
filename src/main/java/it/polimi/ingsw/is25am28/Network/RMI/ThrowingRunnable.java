package it.polimi.ingsw.is25am28.Network.RMI;

/**
 * Represents a functional interface that defines a runnable task which can throw exceptions during its execution.
 * It is similar to {@link Runnable} but allows exceptions.
 */

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
