package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.SlaversJSON;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public class ClientSlavers extends ClientEventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int givenCredits;
    private final int takenCrew;
//    private boolean firstRound;
    private boolean isPlayerDefeated;
    //private List<String> defeatedPlayers;

    private SlaversJSON slaversJSON;

    public ClientSlavers(CardStateJSON cardState) {
        super(cardState);
        this.requiredFirepower = cardState.getRequiredFirepower();
        this.movementSteps = cardState.getMovementSteps();
        this.givenCredits = cardState.getGivenCredits();
        this.takenCrew = cardState.getTakenCrew();
        //this.defeatedPlayers = new ArrayList<>();
//        this.firstRound = true;
        this.isPlayerDefeated = false;
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
//        this.firstRound = slaversCardState.getFirstRound();
        this.isPlayerDefeated = slaversCardState.getIsPlayerDefeated();
        enabledCommands.clear();
        enabledCommands.add("playCard");
        if (this.isPlayerDefeated) {
            enabledCommands.add("setCrewToRemove");
        } else {
            enabledCommands.add("setDoubleCannonsToActivate");
            enabledCommands.add("setTakeReward");
        }



//        if (!this.firstRound) {
//            this.defeatedPlayers = slaversCardState.getDefeatedPlayers();
//        }
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

        if (!this.isPlayerDefeated) {
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

    // Cannons
    @Override
    public void setDoubleCannonsToActivate(List<ComponentHelper<Void>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.slaversJSON.setDoubleCannonsToActivateCoordinates(doubleCannonsToActivate);
    }

    @Override
    public List<ComponentHelper<Void>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.slaversJSON.getDoubleCannonsToActivateCoordinates();
    }

    // Reward
    @Override
    public void setTakeReward(boolean takeReward) {
        this.slaversJSON.setTakeCredits(takeReward);
    }

    @Override
    public Boolean getTakeReward() {
        return this.slaversJSON.getTakeCredits();
    }

    // Crew
    @Override
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) throws UnsupportedOperationException {
        this.slaversJSON.setCrewToRemove(crewToRemove);
    }

    @Override
    public List<ComponentHelper<LifeformType>> getCrewToRemove() throws UnsupportedOperationException {
        return this.slaversJSON.getCrewToRemove();
    }
}
