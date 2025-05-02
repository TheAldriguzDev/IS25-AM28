package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUIGenerator;

public abstract class ClientEventCard implements WidgetTUIGenerator {

    protected final int id;
    protected String playerNickname;
    protected String cardName;
    protected int cardLevel;
    protected boolean hasBeenUsed;
    protected boolean hasBeenActivated; // this flag allows the card to send its full static information (like when only visualized at the start of the game) only when ita has not been used a single time wit useCard()

    public ClientEventCard(CardStateJSON cardState) {
        this.id = cardState.getId();
        this.cardName = cardState.getCardName();
        this.cardLevel = cardState.getCardLevel();
    }

    public abstract void useCard();

    /*
    * This method is in charge of updating the card's data as the round goes on*/
    public abstract void updateCard(CardStateJSON cardState);

    /*This method generated a widget with the relevant card's info*/
    public abstract WidgetTUI generateWidget();
    // TODO: Place the current/target player in a separated bordered widget
}
