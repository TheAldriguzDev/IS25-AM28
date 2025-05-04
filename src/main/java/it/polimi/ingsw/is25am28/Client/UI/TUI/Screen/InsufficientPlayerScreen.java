package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import static it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2.clearTerminal;

public class InsufficientPlayerScreen extends Screen {

    // Constructor
    public InsufficientPlayerScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    /**
     * TUI screen entry point for the insufficient players condition
     */
    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        WidgetTUI waitingForPlayersWidget = new WidgetTUI();

        System.out.println();
        clearTerminal();

        waitingForPlayersWidget.appendString(COMPUTER_MSG_TAG + "Current game is too empty! (Insufficient Players)");
        waitingForPlayersWidget.appendString(COMPUTER_MSG_TAG + "Waiting for players...");
        waitingForPlayersWidget.appendString(COMPUTER_MSG_TAG + "Time until auto-win: " + (insufficientPlayer.getCountdown() / 1000) + "s");

        waitingForPlayersWidget.addPadding(0, 1, 0, 1);
        waitingForPlayersWidget.wrapWidgetWithBorder();
        waitingForPlayersWidget.printWidget();
    }
}
