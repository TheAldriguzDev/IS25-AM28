package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;

import java.util.Random;
import java.util.Scanner;

public abstract class TUI implements ClientUI {
    protected static final String UNKNOWN_COMMAND_ERROR = PrintUtils.addColor("ERROR: Selected command does not exist",ANSIColors.RED);
    protected static final String DEFAULT_INPUT_PREFIX = "Select an option: ";
    protected static final String DEFAULT_WRONG_METHOD_INVOCATION_ERROR = "ERROR: This command wasn't meant to be invoked in this current state";

    protected final ClientModel model;
    protected VirtualView client;

    protected final Object ioLock;
    protected CommandCTX currCommand;
    protected Scanner scanner;
    protected Random random;
    protected String playerNickname;

    // Constructor
    public TUI(ClientModel model) {
        this.model = model;
        this.ioLock = new Object();
        this.currCommand = null;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
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
     * Clears the terminal from previous input.
     * <p>
     *     <b>NOTE on its functionality:</b>
     *     <ul>
     *         <li>This <b>will</b> work on terminals that support ANSI escape codes</li>
     *         <li>It <b>will not</b> work on Windows' CMD</li>
     *         <li>It <b>will not</b> work in the IDE's terminal</li>
     *     </ul>
     * </p>
     */
    protected static void clearTerminal() {
        System.out.print("\033[H\033[2J");
    }

    /**
     * @param client The virtual client to set this TUI to
     */
    public void setVirtualClient(VirtualView client) {
        this.client = client;
    }

    /**
     * ViewUpdater triggers the GameMenuTUI page to show the game menu
     * and let the user see the available games he can join or reconnect to
     * or directly create a new game from scratch
     *
     * @param availableGames Information about all currently open games that are waiting
     *                       to reach the specified capacity before starting
     * @param isFirstAccess Boolean value used to print the title
     *                      (not used in this implementation, since it's handled automatically already)
     */
    public void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws RuntimeException {
        throw new RuntimeException(DEFAULT_WRONG_METHOD_INVOCATION_ERROR);
    }

    /**
     * ViewUpdater triggers the GameMenuTUI page to show the user the
     * players that are currently connected to the game and how many are left
     * before the game can start
     *
     * @param waitingForPlayers The current amount of players waiting in the current game
     */
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        throw new RuntimeException(DEFAULT_WRONG_METHOD_INVOCATION_ERROR);
    }

    /**
     * ViewUpdater triggers the ShipConstructionTUI to spawn for each player the
     * component selection panel and the ship builder panel, thus giving each player
     * the possibility to create their own ship with the available components
     *
     * @param shipConstruction The components that a player can use to build his ship
     */
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws RuntimeException {
        throw new RuntimeException(DEFAULT_WRONG_METHOD_INVOCATION_ERROR);
    }

    /**
     * Prints to terminal the current error and also
     * runs the onError command
     */
    public void showError(ErrorAnswer error) {
        synchronized (this.ioLock) {
            clearTerminal();

            if (this.currCommand != null) {
                this.currCommand.handleError(error.getError());
            }
            else {
                System.out.println(error.getError());
            }
        }
    }

    /**
     * Executes the command if the previous action (that created
     * the CommandCTX instance) was determined to be successful
     */
    public void commitCommand(String playerNickname) {
        if (playerNickname.equals(this.playerNickname) && this.currCommand != null) {
            this.currCommand.handleSuccess();
        }
    }
}
