package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.EpidemyJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public class ClientEpidemy extends ClientEventCard {
    private static final List<String> enabledCommands;

    // Commands that this card will enable are added here
    static {
        enabledCommands = new ArrayList<>();
        enabledCommands.add("getPlayerAck");
    }

    private EpidemyJSON epidemyJSON;

    public ClientEpidemy(CardStateJSON cardState) {
        super(cardState);
        this.epidemyJSON = new EpidemyJSON();
    }

    @Override
    public List<String> getEnabledCommands() {
        return enabledCommands;
    }

    @Override
    public ActionJSON useCard() {
        this.epidemyJSON.setPlayerNickname(this.playerNickname);
        EpidemyJSON tmp = this.epidemyJSON;
        this.epidemyJSON = new EpidemyJSON();
        return tmp;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("~~~[" + this.cardName.toUpperCase() + " - LVL:" + this.cardLevel + "]~~~");

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
