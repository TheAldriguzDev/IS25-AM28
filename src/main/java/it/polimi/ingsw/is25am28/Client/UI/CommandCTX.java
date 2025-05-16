package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.Screen.COMPUTER_MSG_TAG;

public class CommandCTX {
    private String commandName;
    private Runnable onSuccess;
    private Runnable onError;
    private boolean hasBeenUsed;

    public CommandCTX(String commandName, Runnable onSuccess, Runnable onError) {
        this.commandName = commandName;
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.hasBeenUsed = false;
    }

    /**
     * Method used to run the onSuccess Runnable
     * */
    public void handleSuccess() {
        this.onSuccess.run();
        this.hasBeenUsed = true;
    }

    /**
     * Method used to run the onError Runnable and print the given Error
     * */
    public void handleError(String error) {
        System.out.println();
        new WidgetTUI()
                .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor(error, ANSIColors.RED))
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder()
                .printWidget();

        this.onError.run();
        this.hasBeenUsed = true;
    }

    public boolean isUsable() {
        return !this.hasBeenUsed;
    }
}
