package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import javafx.util.Pair;

import java.util.List;

public class ClientPirates extends ClientEventCard {
    private int diceThrowResult;
    private boolean firstRound;
    private final int requiredFirepower;
    private final int givenCredits;
    private final int movementSteps;
    Pair<Integer, Integer> currentPlasmaShot;
    List<Player> playersToHit;

    public ClientPirates(CardStateJSON piratesState) {
        super(piratesState);
        this.firstRound = piratesState.getFirstRound();
        this.requiredFirepower = piratesState.getRequiredFirepower();
        this.givenCredits = piratesState.getGivenCredits();
        this.movementSteps = piratesState.getMovementSteps();
    }

    @Override
    public void useCard() {

    }

    @Override
    public void updateCard(CardStateJSON piratesState) {
        if (piratesState.getFirstRound()) {
            this.playerNickname = piratesState.getPlayerNickname();
        } else {
            firstRound = piratesState.getFirstRound();
            diceThrowResult = piratesState.getDiceThrowResult();
            currentPlasmaShot = piratesState.getCurrPlasmaShotDescriptor();
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
    }
}
