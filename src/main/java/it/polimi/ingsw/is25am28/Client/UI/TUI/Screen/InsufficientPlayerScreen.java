package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import static it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2.clearTerminal;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class InsufficientPlayerScreen extends Screen {
    public InsufficientPlayerScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {
        WidgetTUI waitingForPlayersWidget = new WidgetTUI();

        clearTerminal();

        waitingForPlayersWidget.appendString(PrintUtils.addColor("[COMPUTER]", ANSIColors.BRIGHT_CYAN) + SPACE + "Waiting for players...");
        waitingForPlayersWidget.appendString(PrintUtils.addColor("[COMPUTER]", ANSIColors.BRIGHT_CYAN) + SPACE + "Time until auto-win: " + (insufficientPlayer.getCountdown() / 1000) + "s");

        waitingForPlayersWidget.addPadding(1, 1, 1, 1);
        waitingForPlayersWidget.wrapWidgetWithBorder();
        waitingForPlayersWidget.printWidget();
    }
}
