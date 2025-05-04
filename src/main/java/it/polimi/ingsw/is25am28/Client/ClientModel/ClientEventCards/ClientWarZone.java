package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

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

    private WarZoneJSON warZoneJSON;

    public ClientWarZone(ClientModel model, InputThread inputThread, CardStateJSON cardState) {
        super(model, inputThread, cardState);
        this.actionAndConsequences = cardState.getActionsAndConsequences();
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementSteps = cardState.getMovementSteps();
        this.requiredResources = cardState.getRequiredResources();
        this.warZoneJSON = new WarZoneJSON();
    }

    @Override
    public ActionJSON useCard() {
        this.warZoneJSON = new WarZoneJSON();
        WarZoneJSON tmp = this.warZoneJSON;
        this.warZoneJSON = new WarZoneJSON();
        return tmp;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname(); // If present, the current action (not the general card thumbnail) will be shown
        this.affectedPlayer = cardState.getAffectedPlayer();
        this.currActionIndex = cardState.getCurrActionIndex(); // Will be used in the generateWidget to determine what to display
        this.currentPlasmaShot = cardState.getCurrPlasmaShotDescriptor();
        this.diceThrowResult = cardState.getDiceThrowResult();
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        if (this.playerNickname != null) {
            cardInfoWidget.appendString("Current Player: " + playerNickname);
            cardWidget.appendString("currAction: " + actionAndConsequences.get(currActionIndex).getFirst()); // SWITCH CASE TO WRITE IN A BETTER WAY
            cardWidget.appendString("currConsequence: " + actionAndConsequences.get(currActionIndex).getLast());
            switch (actionAndConsequences.get(currActionIndex).getLast()) {
                case "RequiredCrew" -> {
                    cardInfoWidget.appendString("==== *CHAINS IMAGE* ====");
                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("Required Crew: " + requiredCrew);
                    }
                }
                case "ShootingSequence" -> {
                    cardInfoWidget.appendString("==== *PLASMASHOT IMAGE* ===="); // ALSO INCLUDE SEQUENCE
                        if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                            cardInfoWidget.appendString("==== CURRENT PLASMASHOT INFO ====");
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
                        }
                }
                case "LossItems" -> {
                    cardInfoWidget.appendString("==== *ITEM IMAGE* ====");
                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("RequiredResorces: " + requiredResources);
                    }
                }
            }
        } else {
            cardInfoWidget.appendString("CurrActionsAndConsequences: " + actionAndConsequences);
            cardInfoWidget.appendString("Card Level: " + this.cardLevel);
        }

        if (this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) { // NOT NECESSARY IN PRINT, ONLY IN USECARD
            cardInfoWidget.appendString("Affected Player: " + affectedPlayer);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public void setDoubleCannonsToActivate(List<ComponentHelper<Integer>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.warZoneJSON.setCannonList(doubleCannonsToActivate);
    }

    @Override
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        this.warZoneJSON.setItemsToBeRemoved(itemsToBeRemoved);
    }

    @Override
    public void setUsedEnergy(int usedEnergy) {
        this.warZoneJSON.setUsedEnergy(usedEnergy);
    }

    @Override
    public void setShieldsToActivate(List<ComponentHelper<Integer>> shieldsToActivate) throws UnsupportedOperationException {
        this.warZoneJSON.setShieldList(shieldsToActivate);
    }
}
