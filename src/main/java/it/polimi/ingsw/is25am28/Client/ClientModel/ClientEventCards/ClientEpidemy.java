package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

public class ClientEpidemy extends ClientEventCard {
    public ClientEpidemy(CardStateJSON cardState) {
        super(cardState);
    }

    @Override
    public void useCard() {}

    @Override
    public void updateCard(CardStateJSON cardState) {
        if(cardState.getIsCardUsable()) {
            this.playerNickname = cardState.getPlayerNickname();
        } else {

        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");


        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
