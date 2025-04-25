package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;

public class ClientOpenSpace extends ClientEventCard {
//    private Map<String, Integer> updatedPositions;
//    private List<String> eliminatedPlayers;

    public ClientOpenSpace(CardStateJSON openSpaceState) {
        super(openSpaceState);
    }

    @Override
    public void useCard() {}

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        // if case to do this only when needed -> is an additional flag necessary?
//        this.updatedPositions = cardState.getUpdatedPositions();
//        this.eliminatedPlayers = cardState.getEliminatedPlayers();
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");


        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
