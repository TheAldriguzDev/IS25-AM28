package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
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

    public ClientAbandonedShip(ClientModel model, InputThread inputThread, CardStateJSON cardState) {
        super(model, inputThread, cardState);
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementStep = cardState.getMovementSteps();
        this.givenCredits = cardState.getGivenCredits();
        this.abandonedShipJSON = new AbandonedShipJSON();
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
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

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

        cardInfoWidget.appendString("Level: " + this.cardLevel);
        cardInfoWidget.appendString("Required Crew: " + this.requiredCrew);
        cardInfoWidget.appendString("Given Credits: " + this.givenCredits);
        cardInfoWidget.appendString("Movement Step: " + this.movementStep);
        if (this.playerNickname != null) {
            cardInfoWidget.appendString("Current Player: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public void setCrewToRemove(List<ComponentHelper<LifeformType>> crewToRemove) {
        this.abandonedShipJSON.setLifeformsToBeRemoved(crewToRemove);
    }
}
