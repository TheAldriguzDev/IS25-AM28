package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

public abstract class TUI {
    protected static final String UNKNOWN_COMMAND_ERROR = PrintUtils.addColor("ERROR: Selected command does not exist",ANSIColors.RED);
    protected static final String DEFAULT_INPUT_PREFIX = "Select an option: ";

    protected WidgetTUI tui;

    protected final ClientModel model;
    protected VirtualView client;
    protected final Object ioLock;
    protected CommandCTX currCommand;
    protected String playerNickname;

    // Constructor
    public TUI(ClientModel model) {
        this.model = model;
        this.ioLock = new Object();
    }

    /**
     * Prints the name of the game
     */
    protected static void printTitle() {
        System.out.println("""
         ██████╗  █████╗ ██╗      █████╗ ██╗  ██╗██╗   ██╗    ████████╗██████╗ ██╗   ██╗ ██████╗██╗  ██╗███████╗██████╗\s
        ██╔════╝ ██╔══██╗██║     ██╔══██╗╚██╗██╔╝╚██╗ ██╔╝    ╚══██╔══╝██╔══██╗██║   ██║██╔════╝██║ ██╔╝██╔════╝██╔══██╗
        ██║  ███╗███████║██║     ███████║ ╚███╔╝  ╚████╔╝        ██║   ██████╔╝██║   ██║██║     █████╔╝ █████╗  ██████╔╝
        ██║   ██║██╔══██║██║     ██╔══██║ ██╔██╗   ╚██╔╝         ██║   ██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝  ██╔══██╗
        ╚██████╔╝██║  ██║███████╗██║  ██║██╔╝ ██╗   ██║          ██║   ██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗██║  ██║
         ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝\s
        """);
    }

    /**
     * Clears the terminal from previous input
     */
    protected static void clearTerminal() {
        System.out.print("\033[H\033[2J");
    }

    /**
     * Shows the current TUI to terminal
     */
    public abstract void showTUI();

    /**
     * Prints to terminal the current error and also
     * runs the onError command
     */
    public void showError(ErrorAnswer error) {
        synchronized (this.ioLock) {
            TUI.clearTerminal();
            this.currCommand.handleError(error.getError());
        }
    }

    /**
     * @param client The virtual client to set this to
     */
    public void setVirtualClient(VirtualView client) {
        this.client = client;
    }
}
