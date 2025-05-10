package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.AbandonedShipJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;

public class ClientAbandonedShip extends ClientEventCard {
    private final int requiredCrew;
    private final int movementStep;
    private final int givenCredits;
    private boolean isCardUsable;

    private AbandonedShipJSON abandonedShipJSON;

    public ClientAbandonedShip(CardStateJSON cardState) {
        super(cardState);
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementStep = cardState.getMovementSteps();
        this.givenCredits = cardState.getGivenCredits();
        this.abandonedShipJSON = new AbandonedShipJSON();
        this.isCardUsable = cardState.getIsCardUsable();


    }

    @Override
    public ActionJSON useCard() {
        this.abandonedShipJSON.setPlayerNickname(this.playerNickname);
        AbandonedShipJSON tmp = this.abandonedShipJSON;
        this.abandonedShipJSON = new AbandonedShipJSON();

        return tmp;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        this.isCardUsable = cardState.getIsCardUsable();
        enabledCommands.clear();
        enabledCommands.add("playCard");
        if (this.isCardUsable) {
            enabledCommands.add("setCrewToRemove");
            enabledCommands.add("setWantsToVisit");
        }
    }

    // TODO: Remake the widget into something that makes sense
    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("~~~[" + this.cardName.toUpperCase() + " - LVL:" + this.cardLevel + "]~~~");

        cardInfoWidget.appendString(ANSIColors.WHITE + "                               " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "                               " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "                               " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "  █████████                    " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "███████████████████      ██████" + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "█████████████████████████████  " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + " ██████████████████████████    " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "          █████████████████    " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "            █████████████████  " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "                         ██████" + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "                               " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "                               " + ANSIColors.RESET);

        cardInfoWidget.wrapWidgetWithBorder();

        cardInfoWidget.appendString("Days: " + this.movementStep + "      Crew: " + this.requiredCrew);
        cardInfoWidget.appendString("───────────────────────────────");
        cardInfoWidget.appendString("Given Credits: " + this.givenCredits);
        if (this.playerNickname != null) {
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Current Player: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    // CrewToRemove
    @Override
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) {
        this.abandonedShipJSON.setWantToVisitShip(true);
        this.abandonedShipJSON.setLifeformsToBeRemoved(crewToRemove);
    }

    @Override
    public List<ComponentHelper<LifeformType>> getCrewToRemove() {
        return this.abandonedShipJSON.getLifeformsToBeRemoved();
    }

    // WantsToVisit
    @Override
    public void setWantsToVisit(boolean wantsToVisit) throws UnsupportedOperationException {
        this.abandonedShipJSON.setWantToVisitShip(wantsToVisit);
    }

    @Override
    public Boolean getWantsToVisit() {
        return this.abandonedShipJSON.getWantToVisitShip();
    }
}
