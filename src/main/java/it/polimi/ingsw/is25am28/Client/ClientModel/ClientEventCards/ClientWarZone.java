package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientWarZone extends ClientEventCard {
    private List<List<String>> actionAndConsequences;
    private final int requiredCrew;
    private final int movementSteps;
    private final int requiredResources;
    private String affectedPlayer;
    private int currActionIndex;
    private Map<String, Integer> currentPlasmaShot;
    private int diceThrowResult;


    public ClientWarZone(CardStateJSON cardState) {
        super(cardState);
        this.actionAndConsequences = cardState.getActionsAndConsequences();
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementSteps = cardState.getMovementSteps();
        this.requiredResources = cardState.getRequiredResources();
    }

    @Override
    public void useCard() {

    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname(); // If present, the current action (not the general card thumbnail) will be shown
        this.affectedPlayer = cardState.getAffectedPlayer();
        this.currActionIndex = cardState.getCurrActionIndex(); // Will be used in the generateWidget to determine what to display
        this.currentPlasmaShot = cardState.getCurrPlasmaShotDescriptor(); //NOT DIFFERENTIAL
        this.diceThrowResult = cardState.getDiceThrowResult(); // NOT DIFFERENTIAL
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");



        if (this.playerNickname != null) {
            cardInfoWidget.appendString("Current Player: " + playerNickname);
            cardWidget.appendString("currActionIndex: " + currActionIndex);
            cardWidget.appendString("currAction: " + actionAndConsequences.get(currActionIndex).getFirst());
            cardWidget.appendString("currConsequence: " + actionAndConsequences.get(currActionIndex).getLast());
            switch (actionAndConsequences.get(currActionIndex).getLast()) {
                case "RequiredCrew" -> cardInfoWidget.appendString("Required Crew: " + requiredCrew);
                case "ShootingSequence" -> {
                    // TODO: Control on Affected Player
                    cardInfoWidget.appendString("==== CURRENT PLASMASHOT INFO ====");
                    if (this.currentPlasmaShot != null) {
                        switch (this.currentPlasmaShot.get("shotDirection")) {
                            case 0 -> cardInfoWidget.appendString("Inbound Direction: ABOVE");
                            case 1 -> cardInfoWidget.appendString("Outbound Direction: RIGHT");
                            case 2 -> cardInfoWidget.appendString("Outbound Direction: BELOW");
                            case 3 -> cardInfoWidget.appendString("Inbound Direction: LEFT");
                        }
                        if (this.currentPlasmaShot.get("shotSize") == 1) {
                            cardInfoWidget.appendString("Size: SMALL PLASMASHOT");
                        } else {
                            cardInfoWidget.appendString("Size: BIG PLASMASHOT");
                        }
                        cardInfoWidget.appendString("Dice Throw Result: " + this.diceThrowResult);
                    } // TEMPORARY SOLUTION WHILE THE PLASMASHOT IS NOT DIFFERENTIAL
                }
                case "LossItems" -> {
                    cardInfoWidget.appendString("RequiredResorces: " + requiredResources);
                }
            }
        } else {
            cardInfoWidget.appendString("CurrActionsAndConsequences: " + actionAndConsequences);
            cardInfoWidget.appendString("Card Level: " + this.cardLevel);
        }

        if (this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
            cardInfoWidget.appendString("Affected Player: " + affectedPlayer);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
