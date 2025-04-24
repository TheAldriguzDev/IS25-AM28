package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

public class ClientAbandonedShip extends ClientEventCard {
    private final int requiredCrew;
    private final int movementStep;
    private final int givenCredits;
    private boolean is;

    public ClientAbandonedShip(CardStateJSON cardState) {
        super(cardState);
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementStep = cardState.getMovementSteps();
        this.givenCredits = cardState.getGivenCredits();
        this.hasBeenUsed = cardState.getIsCardUsable();
        //this.hasBeenUsed // Purpose of isCardUsable in this card?

    }

    @Override
    public void useCard() {
        // Needs implementation
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        // TODO: generate state need to send info about the removed crew
        // Should only change the playerNickname during round execution
        this.playerNickname = cardState.getPlayerNickname();
        this.hasBeenUsed = cardState.getIsCardUsable();
        // Need info about what the removed crew

    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        cardInfoWidget.appendString("Level: " + this.cardLevel);
        cardInfoWidget.appendString("Required Crew: " + this.requiredCrew);
        cardInfoWidget.appendString("Given Credits: " + this.givenCredits);
        cardInfoWidget.appendString("Movement Step: " + this.movementStep);

        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardInfoWidget, cardWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
