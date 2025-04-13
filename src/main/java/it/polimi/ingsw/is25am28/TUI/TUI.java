package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TUI {
    private ExecutorService threadPool;
    private WidgetTUI tui;
    private InputWidgetTUI inputWidget;
    private boolean recompose;

    // TODO: Setup virtual view and relative commands and transform them into CommandWidgetTUI instances
    // private VirtualView virtualView;

    // Constructor
    public TUI() {
        this.tui = new WidgetTUI();
        this.inputWidget = new InputWidgetTUI();

        // Setting the input widget's scanner to scan the stdin input stream
        this.inputWidget.setNewScanner(System.in);

        // TODO: Setup virtual view and relative commands and transform them into CommandWidgetTUI instances
        // this.inputWidget.addCommand(new CommandWidgetTUI(commandId, command));
    }

    /**
     * Sets up a new fixed thread pool with 2 threads (one each for I/O operations)
     */
    public void createIOThreadPool() {
        this.threadPool = Executors.newFixedThreadPool(2);
    }

    /**
     * Removes the currently stored thread pool (if present)
     */
    public void closeExecutorService() {
        if (this.threadPool != null) {
            this.threadPool.close();
        }
    }

    /**
     * Prints the TUI to terminal
     */
    public void printTUI() {
        // The TUI is updated and printed by a thread
        this.threadPool.submit(
            () -> {
                // Only recomposes all the stored widgets if
                // there was an update to the view, flagged by
                // the recompose flag upon receiving an update
                if (this.recompose) {
                    this.tui.composeStoredWidgets();
                    this.recompose = false;
                }

                this.tui.printWidget();
            }
        );
    }

    /**
     * Prints to terminal the command widget and opens
     * an input field to acquire a correct user input
     * (i.e.: the user input is correct iff a command with such input
     *        as a command identifier exists in the available commands)
     */
    public void getUserInput(String prefixMessage) {
        // The command widget and input field is printed by a thread
        this.threadPool.submit(
            () -> {
                while (! this.inputWidget.selectCommand(prefixMessage)) {
                    System.err.println("ERROR: Command not found");
                }
            }
        );
    }
}
