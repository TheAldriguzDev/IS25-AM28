package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;

public class ClientStardust extends ClientEventCard {
    private Map<String, Integer> updatedPositions;

    public ClientStardust(CardStateJSON StardustCardState) {
        super(StardustCardState);
    }

    @Override
    public void useCard() {}

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        if (cardState.getNeedsBoardUpdate()) {
            this.updatedPositions = cardState.getUpdatedPositions();
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
