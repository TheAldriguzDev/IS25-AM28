package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUIGenerator;

public abstract class ClientEventCard implements WidgetTUIGenerator {
    protected String playerNickname;
    protected String cardName;
    protected int cardLevel;
    protected boolean hasBeenUsed;
    protected boolean needsUpdate;

    public ClientEventCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        this.cardName = cardState.getCardName();
        this.cardLevel = cardState.getCardLevel();
        this.needsUpdate = false;
    }

    public abstract void useCard();

    /*
    * This method is in charge of updating the card's data as the round goes on*/
    public abstract void updateCard(CardStateJSON cardState);

    /*This method generated a widget with the relevant card's info*/
    public abstract WidgetTUI generateWidget();
}
