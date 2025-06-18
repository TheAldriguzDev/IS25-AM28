package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;
import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

public class Screen implements ClientUI {
    public static final String UNKNOWN_COMMAND_ERROR = PrintUtils.addColor("ERROR: Selected command does not exist", ANSIColors.RED);
    public static final String DEFAULT_COMMAND_PREFIX = "Select an option: ";
    public static final String COMPUTER_MSG_TAG = PrintUtils.addColor("[COMPUTER]", ANSIColors.BRIGHT_CYAN) + SPACE;

    protected final ClientModel model;
    protected InputThread inputThread;
    protected VirtualView client;
    protected CommandCTX ctx;

    public Screen(ClientModel model, InputThread inputThread) {
        this.model = model;
        this.inputThread = inputThread;
    }

    @Override
    public void setVirtualClient(VirtualView client) {
        this.client = client;
    }

    @Override
    public void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws Exception {
        throw new UnsupportedOperationException("The 'showLobbies' is not supported in the " + this + "screen");
    }

    @Override
    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        throw new UnsupportedOperationException("The 'showWaitingForPlayers' is not supported in the " + this + "screen");
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {
        throw new UnsupportedOperationException("The 'showShipConstruction' is not supported in the " + this + "screen");
    }

    @Override
    public void showShipFixing(FixShipDTO fixShip) throws Exception {
        throw new UnsupportedOperationException("The 'showShipFixing' is not supported in the " + this + "screen");
    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {
        throw new UnsupportedOperationException("The 'showShipPopulate' is not supported in the " + this + "screen");
    }

    @Override
    public void showCardRound(CardRoundDTO cardRound) throws Exception {
        throw new UnsupportedOperationException("The 'showCardRound' is not supported in the " + this + "screen");
    }

    @Override
    public void showEndGame(EndGameDTO endGame) {
        throw new UnsupportedOperationException("The 'showEndGame' is not supported in the " + this + "screen");
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        throw new UnsupportedOperationException("The 'showInsufficientPlayer' is not supported in the " + this + "screen");
    }

    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {
        throw new UnsupportedOperationException("The 'receiveTimerDTO' is not supported in the " + this + "screen");
    }

    @Override
    public void commitCommand(String playerNickname) {
        if (this.ctx != null && playerNickname.equals(this.model.getNickname())) {
            this.ctx.handleSuccess();
        }
    }

    @Override
    public void showError(ErrorAnswer error) {
        System.out.println();
        clearTerminal();

        if (this.ctx != null) {
            this.ctx.handleError(error.getError());
        }
        else {
            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor(error.getError(), ANSIColors.RED))
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }
    }

    @Override
    public boolean isCTXAvailable() {
        return ctx != null && ctx.isUsable();
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

    @Override
    public void handleRemovePlayerFromBoard(String playerNickname) {
        // Empty because in the TUI will not be used, only the model will be updated
    }

    @Override
    public void interruptCurrScreen() {
        // Empty since it's not needed to invoke the method here, but instead it invoke the input thread class
    }

    /**
     * This method will be used to stop the input thread
     */
    public void forceStopScreen() {
        this.inputThread.interruptInputReader();
    }
}
