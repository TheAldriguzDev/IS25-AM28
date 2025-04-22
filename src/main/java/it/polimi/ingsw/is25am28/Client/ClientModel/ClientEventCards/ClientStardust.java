package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

public class ClientStardust extends ClientEventCard {

    public ClientStardust(CardStateJSON StardustCardState) {
        super(StardustCardState);
    }

    @Override
    public void useCard() {}

    @Override
    public void updateCard(CardStateJSON cardState) {}

    @Override
    public WidgetTUI generateWidget() {
        return null;
    }
}
