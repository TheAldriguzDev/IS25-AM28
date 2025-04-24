package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public class ClientSlavers extends ClientEventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;
    private boolean firstRound;

    private List<String> defeatedPlayers;

    public ClientSlavers(CardStateJSON slaversCardState) {
        super(slaversCardState);
        this.requiredFirepower = slaversCardState.getRequiredFirepower();
        this.movementSteps = slaversCardState.getMovementSteps();
        this.givenCredits = slaversCardState.getGivenCredits();
        this.takenCrew = slaversCardState.getTakenCrew();
        this.firstRound = slaversCardState.getFirstRound();
        this.defeatedPlayers = null;
    }

    @Override
    public void useCard() {

    }

    @Override
    public void updateCard(CardStateJSON slaversCardState) {
        this.playerNickname = slaversCardState.getPlayerNickname();
        this.firstRound = slaversCardState.getFirstRound();
        if (slaversCardState.getFirstRound()) {
            defeatedPlayers = slaversCardState.getDefeatedPlayers();
            // TODO : previous removed lifeforms (not shown the first time)
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        // TODO: Modify to print the symbol (located in printTest)

        if (this.firstRound) {
            cardInfoWidget.appendString("Level: " + this.cardLevel);
            cardInfoWidget.appendString("Given Credits: " + this.givenCredits);
            cardInfoWidget.appendString("Days: " + this.movementSteps);
            cardInfoWidget.appendString("Required Firepower: " + this.requiredFirepower);
            cardInfoWidget.appendString("Taken Crew: " + this.takenCrew);
        } else {
            cardInfoWidget.appendString("Player: " + this.playerNickname + " has to give up " + this.takenCrew + " crew members");
        }
        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
