package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

public class ClientEpidemy extends ClientEventCard {

    public ClientEpidemy(ClientModel model, InputThread inputThread, CardStateJSON cardState) {
        super(model, inputThread, cardState);
    }

    @Override
    public ActionJSON useCard() {
        return null;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        cardInfoWidget.appendString(ANSIColors.MAGENTA + "                               " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "      ██    ███████    ██      " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "    ██        ███        ██    " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "  ██  ██   █████████   ██  ██  " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "        ██████   ██████        " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "██   ████   ███████   ████   ██" + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "██████████████   ██████████████" + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "██   ████   ███████   ████   ██" + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "        ██████   ██████        " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "  ██  ██   █████████   ██  ██  " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "    ██        ███        ██    " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.MAGENTA + "      ██    ███████    ██      " + ANSIColors.RESET);
        cardInfoWidget.wrapWidgetWithBorder();

        if (this.playerNickname != null) {
            cardInfoWidget.appendString("Current Player: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
