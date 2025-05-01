package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientPirates extends ClientEventCard {
    private int diceThrowResult;
    private boolean firstRound;
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    Map<String, Integer> currentPlasmaShot;
    private List<String> defeatedPlayers;

    public ClientPirates(CardStateJSON piratesState) {
        super(piratesState);
        this.firstRound = piratesState.getFirstRound();
        this.requiredFirepower = piratesState.getRequiredFirepower();
        this.givenCredits = piratesState.getGivenCredits();
        this.movementSteps = piratesState.getMovementSteps();
        this.defeatedPlayers = new ArrayList<>();
    }

    @Override
    public void useCard() {

    }

    @Override
    public void updateCard(CardStateJSON piratesState) {
        this.playerNickname = piratesState.getPlayerNickname();
        this.firstRound = piratesState.getFirstRound();
        if (!this.firstRound) {
            this.diceThrowResult = piratesState.getDiceThrowResult();
            this.currentPlasmaShot = piratesState.getCurrPlasmaShotDescriptor();
            this.defeatedPlayers = piratesState.getDefeatedPlayers();
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        if (this.firstRound) {
            cardInfoWidget.appendString("Level: " + this.cardLevel);
            cardInfoWidget.appendString("Given credits: " + this.givenCredits);
            cardInfoWidget.appendString("Days: " + this.movementSteps);
            // TODO : does the shootingSequence need to be shown to the clients as a whole?
            cardInfoWidget.appendString("Required Firepower: " + this.requiredFirepower);
        } else {
            cardInfoWidget.appendString("Dice result: " + this.diceThrowResult);
        }
        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
