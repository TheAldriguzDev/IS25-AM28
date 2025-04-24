package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.Map;

public class ClientVisitPlanets extends ClientEventCard {
    Map<Integer, Map<ItemColor, Integer>> availablePlanets;

    public ClientVisitPlanets(CardStateJSON cardState) {
        super(cardState);
        this.availablePlanets = cardState.getAvailablePlanets();
    }

    @Override
    public void useCard() {

    }

    @Override
    public void updateCard(CardStateJSON cardState) {

    }

    @Override
    public WidgetTUI generateWidget() {
        return null;
    }
}
