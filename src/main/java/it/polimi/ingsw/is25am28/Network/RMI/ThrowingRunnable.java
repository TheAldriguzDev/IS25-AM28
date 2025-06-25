package it.polimi.ingsw.is25am28.Network.RMI;

/**
 * A functional interface representing a runnable task that can throw checked exceptions.
 * Unlike {@link Runnable}, this interface allows exceptions to be declared and propagated,
 * enabling cleaner lambda expressions without internal try-catch blocks.
 */
@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
