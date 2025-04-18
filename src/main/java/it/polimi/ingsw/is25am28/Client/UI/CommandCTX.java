package it.polimi.ingsw.is25am28.Client.UI;

import java.util.function.Consumer;

public class CommandCTX {
    private String commandName;
    private Runnable onSuccess;
    private Runnable onError;

    public CommandCTX(String commandName, Runnable onSuccess, Runnable onError) {
        this.commandName = commandName;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    /**
     * Method used to run the onSuccess Runnable
     * */
    public void handleSuccess() {
        this.onSuccess.run();
    }

    /**
     * Method used to run the onError Runnable and print the given Error
     * */
    public void handleError(String error) {
        System.err.println(error);
        this.onError.run();
    }


}
