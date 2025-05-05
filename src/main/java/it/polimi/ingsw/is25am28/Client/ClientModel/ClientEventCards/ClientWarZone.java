package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.WarZoneJSON;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.Map;

public class ClientWarZone extends ClientEventCard {
    // Commands that this card will enable are added here
    static {
        ClientEventCard.enabledCommands.add("setItemsToBeRemoved");
        ClientEventCard.enabledCommands.add("setDoubleCannonsToActivate");
        ClientEventCard.enabledCommands.add("setDoubleEnginesToActivate");
        ClientEventCard.enabledCommands.add("setShieldsToActivate");
    }

    private List<List<String>> actionAndConsequences;
    private final int requiredCrew;
    private final int movementSteps;
    private final int requiredResources;
    private String affectedPlayer;
    private int currActionIndex;
    private Map<String, Integer> currentPlasmaShot;
    private int diceThrowResult;

    private WarZoneJSON warZoneJSON;

    public ClientWarZone(CardStateJSON cardState) {
        super(cardState);
        this.actionAndConsequences = cardState.getActionsAndConsequences();
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementSteps = cardState.getMovementSteps();
        this.requiredResources = cardState.getRequiredResources();
        this.warZoneJSON = new WarZoneJSON();
    }

    @Override
    public ActionJSON useCard() {
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

        String tmpAction = null;
        String tmpConsequence = null;
        switch (this.actionAndConsequences.get(currActionIndex).getFirst()) {
            case "Humans" -> tmpAction = "Crew";
            case "Enginepower" -> tmpAction = "EnginePower";
            case "Firepower" -> tmpAction = "FirePower";
        }
        switch (this.actionAndConsequences.get(currActionIndex).getLast()) {
            case "RequiredCrew" -> tmpConsequence = "Taken Crew";
            case "MovementSteps" ->tmpConsequence = "Days";
            case "ShootingSequence" -> tmpConsequence = "PlasmaShots";
            case "LossItems" -> tmpConsequence = "Taken Items";
        }

        cardWidget.appendString("~~~[" + this.cardName.toUpperCase() + " - LVL:" + this.cardLevel + "]~~~");

        if (this.playerNickname != null) {
            cardInfoWidget.appendString("Current Player: " + playerNickname);
//            cardWidget.appendString("currAction: " + actionAndConsequences.get(currActionIndex).getFirst()); // SWITCH CASE TO WRITE IN A BETTER WAY
//            cardWidget.appendString("currConsequence: " + actionAndConsequences.get(currActionIndex).getLast());

            switch (this.actionAndConsequences.get(currActionIndex).getLast()) {
                case "RequiredCrew" -> {
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██        ██        " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██      ██████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "      ██████      ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "        ██        ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "      ██████      ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██       ████       " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██       ████       " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██      ██████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "      ██████      ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "        ██        ██    ██     " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██      ██████      " + ANSIColors.RESET);
                    cardInfoWidget.appendString(ANSIColors.WHITE + "     ██    ██        ██        " + ANSIColors.RESET);
                    cardInfoWidget.wrapWidgetWithBorder();

                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("Required Crew: " + requiredCrew);
                    } else {
                        cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
                    }

//                    cardInfoWidget.appendString(this.actionAndConsequences.get(currActionIndex).getFirst() + " --> " + this.actionAndConsequences.get(currActionIndex).getLast());

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
                            }
                            else {
                                cardInfoWidget.appendString("Size: BIG PLASMASHOT");
                            }

                            cardInfoWidget.appendString("Dice Throw Result: " + this.diceThrowResult);
                        }
                }
                case "LossItems" -> {
                    cardInfoWidget.appendString("==== *ITEM IMAGE* ====");
                    if(this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) {
                        cardInfoWidget.appendString("RequiredResources: " + requiredResources);
                    } else {
                        cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
                    }
                }
            }


        }
        else {
            cardInfoWidget.appendString(ANSIColors.WHITE + "      ██  ██  ███  ██  ██      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "            ███████            " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "         █████████████         " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "        ███████████████        " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "       █████████████████       " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.GREEN + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.RED   + "      ███████████████████      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.RED   + "        ███████████████        " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.RED   + "          ███████████          " + ANSIColors.RESET);
            cardInfoWidget.wrapWidgetWithBorder();

            for (List<String> pair : actionAndConsequences) {
//                cardInfoWidget.appendString(pair.get(0) + " --> " + pair.get(1));
                switch (pair.getFirst()) {
                    case "Humans" -> tmpAction = "Crew";
                    case "Enginepower" -> tmpAction = "EnginePower";
                    case "Firepower" -> tmpAction = "FirePower";
                }
                switch (pair.getLast()) {
                    case "RequiredCrew" -> tmpConsequence = "Taken Crew";
                    case "MovementSteps" ->tmpConsequence = "Days";
                    case "ShootingSequence" -> tmpConsequence = "PlasmaShots";
                    case "LossItems" -> tmpConsequence = "Taken Items";
                }
                cardInfoWidget.appendString(tmpAction + " --> " + tmpConsequence);
            }
//            cardInfoWidget.appendString("CurrActionsAndConsequences: " + actionAndConsequences);
        }

        if (this.affectedPlayer != null && !this.affectedPlayer.isEmpty()) { // NOT NECESSARY IN PRINT, ONLY IN USECARD
            cardInfoWidget.appendString("Affected Player: " + affectedPlayer);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget)
                .centerWidgetScreen()
                .wrapWidgetWithBorder();
    }

    @Override
    public void setDoubleCannonsToActivate(List<ComponentHelper<Integer>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.warZoneJSON.setCannonList(doubleCannonsToActivate);
    }

    @Override
    public List<ComponentHelper<Integer>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.warZoneJSON.getCannonList();
    }

    @Override
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        this.warZoneJSON.setItemsToBeRemoved(itemsToBeRemoved);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() throws UnsupportedOperationException {
        return this.warZoneJSON.getItemsToBeRemoved();
    }

    @Override
    public void setDoubleEnginesToActivate(int doubleEnginesToActivate) {
        this.warZoneJSON.setUsedEnergy(doubleEnginesToActivate);
    }

    @Override
    public int getDoubleEnginesToActivate() {
        return this.warZoneJSON.getUsedEnergy();
    }

    @Override
    public void setShieldsToActivate(List<ComponentHelper<Integer>> shieldsToActivate) throws UnsupportedOperationException {
        this.warZoneJSON.setShieldList(shieldsToActivate);
    }

    @Override
    public List<ComponentHelper<Integer>> getShieldsToActivate() throws UnsupportedOperationException {
        return this.warZoneJSON.getShieldList();
    }
}
