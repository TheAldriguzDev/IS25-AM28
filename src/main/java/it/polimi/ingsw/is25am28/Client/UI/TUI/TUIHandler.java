package it.polimi.ingsw.is25am28.Client.UI.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.InsufficientPlayerScreen;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.LobbyScreen;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.Screen;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.ShipConstructionScreen;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

public class TUIHandler implements ClientUI {
    private ClientModel model;
    private Screen screen;

    // If the screen needs to be force quit, we can save it to resume it --> TODO: Probably not useful
    private Screen prevScreen;
    private final InputThread inputThread;
    private final Object ioLock;

    private VirtualView virtualClient;
    private CommandCTX commandCTX;


    public TUIHandler(ClientModel model) {
        this.model = model;
        this.inputThread = new InputThread();
        this.inputThread.setDaemon(true); // set the thread as daemon = true to avoid his termination
        this.inputThread.start();
        this.ioLock = new Object();
    }

    @Override
    public void setVirtualClient(VirtualView client) {
        this.virtualClient = client;
    }

    @Override
    public void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws Exception {
        synchronized (this.ioLock) {
            // Check if the screen has been loaded, otherwise create it
            if (!(this.screen instanceof LobbyScreen)) {
                this.screen = new LobbyScreen(this.model, this.inputThread);
                this.screen.setVirtualClient(virtualClient);
            }

            this.screen.showLobbies(availableGames, isFirstAccess);
        }
    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        synchronized (this.ioLock) {
            this.screen.showWaitingForPlayers(waitingForPlayers);
        }
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {
        synchronized (this.ioLock) {
            if (!(screen instanceof ShipConstructionScreen)) {
                // TODO: Handle this better to init the data in the state
                this.screen = new ShipConstructionScreen(this.model, this.inputThread);
            }

            this.screen.showShipConstruction(shipConstruction);
        }
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        this.prevScreen = this.screen;

        this.screen = new InsufficientPlayerScreen(this.model, this.inputThread);
        this.inputThread.interruptInputReader();
        this.screen.showInsufficientPlayer(insufficientPlayer);
    }

    @Override
    public void commitCommand(String playerNickname) {
        this.screen.commitCommand(playerNickname);
    }

    @Override
    public void showError(ErrorAnswer error) {
        this.screen.showError(error);
    }

    @Override
    public boolean isCTXAvailable() {
        return this.screen.isCTXAvailable();
    }
}
