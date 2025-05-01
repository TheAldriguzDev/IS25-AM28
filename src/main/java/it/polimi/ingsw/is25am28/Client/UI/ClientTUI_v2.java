package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.TUI.GameMenuTUIPage;
import it.polimi.ingsw.is25am28.TUI.ShipConstructionTUIPage;
import it.polimi.ingsw.is25am28.TUI.TUIPage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class ClientTUI_v2 implements ClientUI {
    public static final String DEFAULT_WRONG_METHOD_INVOCATION_ERROR = "ERROR: This command wasn't meant to be invoked in this current state";

    protected final ClientModel model;
    protected VirtualView client;

    protected final Object ioLock;
    protected CommandCTX currCommand;
    protected BufferedReader bufferedReader;
    protected Random random;
    protected String playerNickname;

    // Current TUI page
    private TUIPage currPage;

    public ClientTUI_v2(ClientModel model) {
        this.model = model;
        this.client = null;
        this.ioLock = new Object();
        this.currCommand = null;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.random = new Random();
        this.playerNickname = null;
        this.currPage = new GameMenuTUIPage(this);
    }

    /**
     * Prints the name of the game
     */
    public static void printTitle() {
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
    public static void clearTerminal() {
        System.out.print("\033[H\033[2J");
    }

    /**
     * @return This client model instance
     */
    public ClientModel getModel() {
        return this.model;
    }

    /**
     * @return This virtual client instance
     */
    public VirtualView getVirtualView() {
        return this.client;
    }

    /**
     * @return This IO lock instance
     */
    public Object getIoLock() {
        return this.ioLock;
    }

    /**
     * @return This command context instance
     */
    public CommandCTX getCurrCommand() {
        return this.currCommand;
    }

    /**
     * @param currCommand The current command context to set
     */
    public void setCurrCommand(CommandCTX currCommand) {
        this.currCommand = currCommand;
    }

    /**
     * @return This buffered reader instance
     */
    public BufferedReader getBufferedReader() {
        return this.bufferedReader;
    }

    /**
     * @return This random generator instance
     */
    public Random getRandom() {
        return this.random;
    }

    /**
     * @return This player nickname
     */
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    /**
     * @param playerNickname The current player nickname to set it to
     */
    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    /**
     * @param client The virtual client to set this TUIPage to
     */
    public void setVirtualClient(VirtualView client) {
        this.client = client;
    }

    /**
     * @return The current TUI page
     */
    public TUIPage getCurrPage() {
        return this.currPage;
    }

    /**
     * Sets the current TUI page to display
     */
    public void setCurrPage(TUIPage page) {
        this.currPage = page;
    }

    /**
     * ViewUpdater triggers the GameMenuTUIPage page to show the game menu
     * and let the user see the available games he can join or reconnect to
     * or directly create a new game from scratch
     *
     * @param availableGames Information about all currently open games that are waiting
     *                       to reach the specified capacity before starting
     * @param isFirstAccess Boolean value used to print the title
     *                      (not used in this implementation, since it's handled automatically already)
     */
    @Override
    public void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws RuntimeException {
        this.currPage.showLobbies(availableGames, isFirstAccess);
    }

    /**
     * ViewUpdater triggers the GameMenuTUIPage page to show the user the
     * players that are currently connected to the game and how many are left
     * before the game can start
     *
     * @param waitingForPlayers The current amount of players waiting in the current game
     */
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        this.currPage.showWaitingForPlayers(waitingForPlayers);
    }

    /**
     * ViewUpdater triggers the ShipConstructionTUIPage to spawn for each player the
     * component selection panel and the ship builder panel, thus giving each player
     * the possibility to create their own ship with the available components
     *
     * @param shipConstruction The components that a player can use to build his ship
     */
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws RuntimeException {
        this.currPage = new ShipConstructionTUIPage(this);
        this.currPage.showShipConstruction(shipConstruction);
    }

    @Override
    public void showShipFixing(FixShipDTO fixShip) {

    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) {

    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {

    }

    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {

    }

    // TODO: Add the other methods in the game

    /**
     * Prints to terminal the current error and also
     * runs the onError command
     */
    public void showError(ErrorAnswer error) {
        synchronized (this.ioLock) {
            ClientTUI_v2.clearTerminal();

            if (this.currCommand != null) {
                this.currCommand.handleError(error.getError());
            }
            else {
                System.out.println(error.getError());
            }
        }
    }

    @Override
    public boolean isCTXAvailable() {
        return this.currCommand != null;
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
