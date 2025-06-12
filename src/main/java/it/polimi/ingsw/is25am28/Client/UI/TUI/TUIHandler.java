package it.polimi.ingsw.is25am28.Client.UI.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Screen.*;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

public class TUIHandler implements ClientUI {
    private final ClientModel model;
    private Screen screen;

    private final InputThread inputThread;
    private final Object ioLock;
    private VirtualView virtualClient;

    public TUIHandler(ClientModel model, InputThread inputThread) {
        this.model = model;
        this.inputThread = inputThread;
        this.ioLock = new Object();
    }

    /**
     * Runs the "clear" command on Unix-like systems or the
     * "cls" command on Windows systems to clear the current
     * terminal screen as well as the scrollback buffer.
     */
    public static void clearTerminal() {
        try {
            String operatingSystem = System.getProperty("os.name").toLowerCase();

            if (operatingSystem.contains("win")) {
                // For Windows
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                pb.inheritIO();
                pb.environment().put("TERM", "xterm");
                pb.start().waitFor();
            }
            else if (operatingSystem.contains("mac") || operatingSystem.contains("nix") || operatingSystem.contains("nux")) {
                // For macOS and Linux
                ProcessBuilder pb = new ProcessBuilder("clear");
                pb.inheritIO();
                pb.environment().put("TERM", "xterm");
                pb.start().waitFor();
            }
            else {
                // For other platforms, you can print a message or handle it as needed
                new WidgetTUI()
                        .appendString(PrintUtils.addColor("Unsupported operating system. Unable to clear the terminal.", ANSIColors.RED))
                        .addPadding(0, 1, 0, 1)
                        .wrapWidgetWithBorder();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the current screen to the given one.
     */
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
            if (!(this.screen instanceof LobbyScreen)) {
                this.setScreen(new LobbyScreen(this.model, this.inputThread));
            }

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
    public void showShipFixing(FixShipDTO fixShip) throws Exception {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        synchronized (this.ioLock) {
            this.setScreen(new FixShipScreen(this.model, this.inputThread));
        }

        this.screen.showShipFixing(fixShip);
    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        synchronized (this.ioLock) {
            this.setScreen(new PopulateShipScreen(this.model, this.inputThread));
        }

        this.screen.showShipPopulate(populateShip);
    }

    @Override
    public void showCardRound(CardRoundDTO cardRound) throws Exception {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        synchronized (this.ioLock) {
            this.setScreen(new CardRoundScreen(this.model, this.inputThread));
        }

        this.screen.showCardRound(cardRound);
    }

    @Override
    public void showEndGame(EndGameDTO endGame) {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        synchronized (this.ioLock) {
            this.setScreen(new EndGameScreen(this.model, this.inputThread));
        }

        this.screen.showEndGame(endGame);
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        // Interrupt the inputThread to prevent actions from the player
        this.inputThread.interruptInputReader();

        // Force and show the new screen
        this.setScreen(new InsufficientPlayerScreen(this.model, this.inputThread));
        this.screen.showInsufficientPlayer(insufficientPlayer);
    }

    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {
        this.screen.receiveTimerDTO(timerDTO);
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

    @Override
    public void updateShipConstructionComponent(ConstructionComponentDTO component) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    @Override
    public void updateShipPlacedComponent(PlacedComponentDTO data) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    @Override
    public void placePlayerInTheBoard(String playerNickname) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    @Override
    public void updateShipRemovedComponent(FixedComponentDTO data) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    @Override
    public void updateShipPlacedLifeForm(PopulateShipComponentDTO data) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    @Override
    public void updateVisuals(CardRoundDTO data) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    @Override
    public void handlePlayerFastShip(String playerNickname) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    /**
     * Forces an interrupt of this input thread
     */
    public void interruptCurrScreen() {
        this.inputThread.interruptInputReader();
    }
}
