package it.polimi.ingsw.is25am28.Client.UI.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
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

    private void setScreen(Screen screen) {
        this.screen = screen;
        this.screen.setVirtualClient(this.virtualClient);
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
                this.setScreen(new LobbyScreen(this.model, this.inputThread));
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
                this.setScreen(new ShipConstructionScreen(this.model, this.inputThread));
            }

            this.screen.showShipConstruction(shipConstruction);
        }
    }

    @Override
    public void showShipFixing(FixShipDTO fixShip) {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        synchronized (this.ioLock) {
            this.setScreen(new FixShipScreen(this.model, this.inputThread));
        }

        this.screen.showShipFixing(fixShip);
    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        synchronized (this.ioLock) {
            this.setScreen(new PopulateShipScreen(this.model, this.inputThread));
        }

        this.screen.showShipPopulate(populateShip);
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        // Save the previous screen
        this.prevScreen = this.screen;

        // Force and show the new screen
        this.setScreen(new InsufficientPlayerScreen(this.model, this.inputThread));
        this.screen.showInsufficientPlayer(insufficientPlayer);
    }

    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {
        this.model.setTimerDTO(timerDTO);
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

    /**
     * Forces an interrupt of this input thread
     */
    public void interruptCurrScreen() {
        this.inputThread.interrupt();
    }
}
