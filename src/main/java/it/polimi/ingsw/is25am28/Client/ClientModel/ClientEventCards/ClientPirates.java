package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

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

    private PiratesJSON piratesJSON;

    public ClientPirates(CardStateJSON cardState) {
        super(cardState);
        this.firstRound = true;
        this.requiredFirepower = cardState.getRequiredFirepower();
        this.givenCredits = cardState.getGivenCredits();
        this.movementSteps = cardState.getMovementSteps();
        this.defeatedPlayers = new ArrayList<>();
        this.piratesJSON = new PiratesJSON();
    }

    @Override
    public ActionJSON useCard() {
        this.piratesJSON.setPlayerNickname(this.playerNickname);
        return this.piratesJSON;
    }

    @Override
    public void updateCard(CardStateJSON piratesState) {
        this.playerNickname = piratesState.getPlayerNickname();
        this.firstRound = piratesState.getFirstRound();

        enabledCommands.clear();
        enabledCommands.add("playCard");
        if (!this.firstRound) {
            this.diceThrowResult = piratesState.getDiceThrowResult();
            this.currentPlasmaShot = piratesState.getCurrPlasmaShotDescriptor();
            this.defeatedPlayers = piratesState.getDefeatedPlayers();

            enabledCommands.add("setShieldsToActivate");
        } else {
            enabledCommands.add("setDoubleCannonsToActivate");
        }
    }

    // TODO: Add colors to the plasma shot widget
    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("[" + this.cardName.toUpperCase() + " - LVL: " + this.cardLevel + "]");

        if (this.firstRound) {

            cardInfoWidget.appendString(ANSIColors.WHITE + "████                       ████" + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "█████                     █████" + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "  ████     " + ANSIColors.RESET + "█████████" + ANSIColors.WHITE +"     ███   " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "    ███ " + ANSIColors.RESET + "███████████████" + ANSIColors.WHITE +" ███    " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "     █ " + ANSIColors.RESET + "█████████████████" + ANSIColors.WHITE + " █     " + ANSIColors.RESET);
            cardInfoWidget.appendString("      ███      █      ███      " + ANSIColors.RESET);
            cardInfoWidget.appendString("      ███   " + ANSIColors.RED + "█" + ANSIColors.RESET + "  █  " + ANSIColors.RED +"█" + ANSIColors.RESET + "   ███      " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "     █ " + ANSIColors.RESET + "█████████████████" + ANSIColors.WHITE +" █    " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "    ███   " + ANSIColors.RESET + "███████████" + ANSIColors.WHITE +"   ███    " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "  ████   " + ANSIColors.RESET + "█ █ █ █ █ █ █" + ANSIColors.WHITE +"   ████  " + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "█████     " + ANSIColors.RESET + "█ █ █ █ █ █" + ANSIColors.WHITE +"     █████" + ANSIColors.RESET);
            cardInfoWidget.appendString(ANSIColors.WHITE + "████      " + ANSIColors.RESET + "███████████" + ANSIColors.WHITE +"      ████" + ANSIColors.RESET);
            cardInfoWidget.wrapWidgetWithBorder();

            cardInfoWidget.appendString("Given credits: " + this.givenCredits);
            cardInfoWidget.appendString("Days: " + this.movementSteps);
            // TODO : does the shootingSequence need to be shown to the clients as a whole?
            cardInfoWidget.appendString("Required Firepower: " + this.requiredFirepower);
            if (this.playerNickname != null) {
                cardInfoWidget.appendString("Current Player: " + this.playerNickname);
            }
        } else {
            cardInfoWidget.appendString("                 █                ");
            cardInfoWidget.appendString("                ███               ");
            cardInfoWidget.appendString("               █████              ");
            cardInfoWidget.appendString("               █████              ");
            cardInfoWidget.appendString("              ███████             ");
            cardInfoWidget.appendString("       █    ███████████    █      ");
            cardInfoWidget.appendString("       ██  █████████████  ██      ");
            cardInfoWidget.appendString("  ██    ███████████████████    ██ ");
            cardInfoWidget.appendString("  ███    █████████████████    ███ ");
            cardInfoWidget.appendString("   ████ ███████████████████ ████  ");
            cardInfoWidget.appendString("     █████████████████████████    ");
            cardInfoWidget.appendString("      ███████████████████████     ");
            cardInfoWidget.appendString("         █████████████████        ");
            cardInfoWidget.appendString("            ███████████           ");
            cardInfoWidget.wrapWidgetWithBorder();

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
            cardInfoWidget.appendString("Target: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public String getAdditionalCardInfo() {
        if (!this.firstRound) {
            return "[CURRENT PLASMASHOT INFO]\nComing from: "
                    + switch (this.currentPlasmaShot.get("shotDirection")) {
                case 0 -> "ABOVE";
                case 1 -> "RIGHT";
                case 2 -> ": BELOW";
                case 3 -> ": LEFT";
                default -> "";
            }
                    + "\nSize: "
                    + switch (this.currentPlasmaShot.get("shotSize")) {
                case 1 -> "SMALL";
                case 2 -> "BIG";
                default -> "";
            } + "\nDice Throw Result: " + this.diceThrowResult;
        } else {
            return "No plasmaShots headed\ntowards the ship";
        }
    }

    @Override
    public void clearJSON() {
        this.piratesJSON = new PiratesJSON();
    }

    // Shields
    @Override
    public void setShieldsToActivate(List<Pair<CoordinatePair, CoordinatePair>> shieldsToActivate) throws UnsupportedOperationException {
        this.piratesJSON.setShieldsActivatedCoordinates(shieldsToActivate);
    }

    @Override
    public List<Pair<CoordinatePair, CoordinatePair>> getShieldsToActivate() throws UnsupportedOperationException {
        return this.piratesJSON.getShieldsActivatedCoordinates();
    }

    // Cannons
    @Override
    public void setDoubleCannonsToActivate(List<Pair<CoordinatePair, CoordinatePair>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.piratesJSON.setDoubleCannonsToActivateCoordinates(doubleCannonsToActivate);
    }

    @Override
    public List<Pair<CoordinatePair, CoordinatePair>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.piratesJSON.getDoubleCannonsToActivateCoordinates();
    }

    // Reward
    @Override
    public void setTakeReward(boolean takeReward) {
        this.piratesJSON.setTakeCredits(takeReward);
    }

    @Override
    public Boolean getTakeReward() {
        return this.piratesJSON.getTakeCredits();
    }

    @Override
    public int getFirepower() {
        return this.requiredFirepower;
    }
}


