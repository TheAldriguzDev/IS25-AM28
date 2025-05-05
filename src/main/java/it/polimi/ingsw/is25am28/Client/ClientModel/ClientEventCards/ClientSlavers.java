package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public class ClientSlavers extends ClientEventCard {
    // Commands that this card will enable are added here
    static {
        ClientEventCard.enabledCommands.add("setDoubleCannonsToActivate");
        ClientEventCard.enabledCommands.add("setTakeReward");
    }

    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;
    private boolean firstRound;
    private List<String> defeatedPlayers;

    private SlaversJSON slaversJSON;

    public ClientSlavers(CardStateJSON cardState) {
        super(cardState);
        this.requiredFirepower = cardState.getRequiredFirepower();
        this.movementSteps = cardState.getMovementSteps();
        this.givenCredits = cardState.getGivenCredits();
        this.takenCrew = cardState.getTakenCrew();
        this.defeatedPlayers = new ArrayList<>();
        this.firstRound = true;
        this.slaversJSON = new SlaversJSON();
    }

    @Override
    public ActionJSON useCard() {
        this.slaversJSON.setPlayerNickname(this.playerNickname);
        SlaversJSON tmp = this.slaversJSON;
        this.slaversJSON = new SlaversJSON();

        return tmp;
    }

    @Override
    public void updateCard(CardStateJSON slaversCardState) {
        this.playerNickname = slaversCardState.getPlayerNickname();
        this.firstRound = slaversCardState.getFirstRound();

        if (!this.firstRound) {
            this.defeatedPlayers = slaversCardState.getDefeatedPlayers();
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("~~~[" + this.cardName.toUpperCase() + " - LVL:" + this.cardLevel + "]~~~");

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

        if (this.firstRound) {
//            cardInfoWidget.appendString("─────────PREREQUISITES─────────");
            cardInfoWidget.appendString("Days: " + this.movementSteps + "      Firepower: " + this.requiredFirepower);
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Given credits: " + this.givenCredits);
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Taken Crew: " + this.takenCrew);
            if (this.playerNickname != null) {
                cardInfoWidget.appendString("───────────────────────────────");
                cardInfoWidget.appendString("Current Player: " + this.playerNickname);
            }
        } else {
            cardInfoWidget.appendString("Crew members to remove: " + this.takenCrew);
            cardInfoWidget.appendString("Target: " + this.playerNickname);
        }
        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public void setDoubleCannonsToActivate(List<ComponentHelper<Integer>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.slaversJSON.setDoubleCannonsToActivateCoordinates(doubleCannonsToActivate);
    }

    @Override
    public List<ComponentHelper<Integer>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.slaversJSON.getDoubleCannonsToActivateCoordinates();
    }

    @Override
    public void setTakeReward(boolean takeReward) {
        this.slaversJSON.setTakeCredits(takeReward);
    }

    @Override
    public boolean getTakeReward() {
        return this.slaversJSON.getTakeCredits();
    }
}
