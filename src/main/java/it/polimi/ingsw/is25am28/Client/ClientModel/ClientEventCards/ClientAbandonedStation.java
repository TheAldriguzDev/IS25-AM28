package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;

public class ClientAbandonedStation extends ClientEventCard {
    private final int requiredCrew;
    private final int movementStep;
    private List<ItemColor> stationResources;
//    private List<ComponentHelper<ItemColor>> resourcesToDrop;
//    private List<ComponentHelper<ItemColor>> resourcesToTake;

    public ClientAbandonedStation(CardStateJSON cardState) {
        super(cardState);
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementStep = cardState.getMovementSteps();
        this.hasBeenUsed = cardState.getIsCardUsable();
        this.stationResources = cardState.getStationResources();
    }

    @Override
    public void useCard() {
        // Needs implementation
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        // Should only change the playerNickname during round execution
        this.playerNickname = cardState.getPlayerNickname();
//        this.hasBeenUsed = cardState.getIsCardUsable();
        // Need info about what the player took/dropped
//        this.resourcesToDrop = cardState.getResourcesToDrop();
//        this.resourcesToTake = cardState.getResourcesToTake();
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        cardInfoWidget.appendString("Level: " + this.cardLevel);
        cardInfoWidget.appendString("Required Crew: " + this.requiredCrew);
        cardInfoWidget.appendString("Movement Step: " + this.movementStep);
        cardInfoWidget.appendString("Station Resources: " + this.stationResources);

        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardInfoWidget, cardWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
