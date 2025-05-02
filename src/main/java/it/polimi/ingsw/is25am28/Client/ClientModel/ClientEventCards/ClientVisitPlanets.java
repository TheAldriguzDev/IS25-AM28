package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.Map;

public class ClientVisitPlanets extends ClientEventCard {
    Map<Integer, Map<ItemColor, Integer>> availablePlanets; // TODO: a list would serve this role better since the generateWidget needs order
    private int chosenPlanetIndex;

    public ClientVisitPlanets(CardStateJSON cardState) {
        super(cardState);
        this.availablePlanets = cardState.getAvailablePlanets();
    }

    @Override
    public void useCard() {

    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        // If a valid planet has been chosen by a player, the corresponding planed will be removed form the avaiable planets
        if (cardState.getChosenPlanetIndex() != -1) {
            availablePlanets.remove(cardState.getChosenPlanetIndex());
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        cardInfoWidget.appendString("Card Level: " + this.cardLevel);
        cardInfoWidget.appendString("Available Planets: ");

        for (Map.Entry<Integer, Map<ItemColor, Integer>> entry : availablePlanets.entrySet()) {
            cardInfoWidget.appendString(entry.getKey() + ": " + entry.getValue().toString());
        }

        if (this.playerNickname != null) {
            cardInfoWidget.appendString("Current Player: " + playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
