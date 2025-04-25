package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;

import static it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2.DEFAULT_WRONG_METHOD_INVOCATION_ERROR;

public abstract sealed class TUIPage implements ClientUI permits GameMenuTUIPage, ShipConstructionTUIPage {
    public static final String UNKNOWN_COMMAND_ERROR = PrintUtils.addColor("ERROR: Selected command does not exist",ANSIColors.RED);
    public static final String DEFAULT_INPUT_PREFIX = "Select an option: ";

    protected ClientTUI_v2 clientTUI;

    // Constructor
    public TUIPage(ClientTUI_v2 clientTUI) {
        this.clientTUI = clientTUI;
    }

    public void setVirtualClient(VirtualView client) {
        this.clientTUI.setVirtualClient(client);
    }

    public void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws RuntimeException {
        throw new RuntimeException(DEFAULT_WRONG_METHOD_INVOCATION_ERROR);
    }

    public void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers) {
        throw new RuntimeException(DEFAULT_WRONG_METHOD_INVOCATION_ERROR);
    }

    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws RuntimeException {
        throw new RuntimeException(DEFAULT_WRONG_METHOD_INVOCATION_ERROR);
    }

    public void showError(ErrorAnswer error) {
        this.clientTUI.showError(error);
    }

    public void commitCommand(String playerNickname) {
        this.clientTUI.commitCommand(playerNickname);
    }
}
