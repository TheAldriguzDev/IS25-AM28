package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.Screen.COMPUTER_MSG_TAG;

/**
 * Represents the context for handling the network communication process.
 * Since client commands are sent without waiting for an immediate server response,
 * {@code CommandCTX} allows you to define {@code onSuccess} and {@code onError} callbacks.
 * These callbacks are invoked based on messages from the server that target a specific player,
 * enabling the application to determine whether the callback should be executed.
 */
public class CommandCTX {
    private String commandName;
    private Runnable onSuccess;
    private Runnable onError;
    private boolean hasBeenUsed; // Prevents multiples execution of the same context

    /**
     * Constructs a new CommandCTX instance, which represents a context for executing client commands
     * with defined success and error callbacks.
     *
     * @param commandName the name of the command being executed
     * @param onSuccess the callback to be executed if the command completes successfully
     * @param onError the callback to be executed if an error occurs during command execution
     */
    public CommandCTX(String commandName, Runnable onSuccess, Runnable onError) {
        this.commandName = commandName;
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.hasBeenUsed = false;
    }

    /**
     * Executes the success callback associated with this CommandCTX and marks the command as used.
     */
    public void handleSuccess() {
        if (!this.hasBeenUsed) {
            this.onSuccess.run();
            this.hasBeenUsed = true;
        }
    }

    /**
     * Executes the error callback associated with this CommandCTX and marks the command as used.
     */
    public void handleError(String error) {
        if (!this.hasBeenUsed) {
            System.out.println();
            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor(error, ANSIColors.RED))
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();

            this.onError.run();
            this.hasBeenUsed = true;
        }
    }

    /**
     * Determines whether the command context is usable.
     * A context is considered usable if it has not been marked as used.
     *
     * @return true if the command context has not been used, false otherwise
     */
    public boolean isUsable() {
        return !this.hasBeenUsed;
    }
}
