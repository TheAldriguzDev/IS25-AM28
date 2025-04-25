package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;

public class ClientSmugglers extends ClientEventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int redItems;
    private final int yellowItems;
    private final int blueItems;
    private final int greenItems;
    private final int takenItems;
    private boolean firstRound;

//    List<String> defeatedPlayers;


    public ClientSmugglers(CardStateJSON smugglersState) {
        super(smugglersState);
        this.requiredFirepower = smugglersState.getRequiredFirepower();
        this.movementSteps = smugglersState.getMovementSteps();
        this.redItems = smugglersState.getRedItems();
        this.yellowItems = smugglersState.getYellowItems();
        this.blueItems = smugglersState.getBlueItems();
        this.greenItems = smugglersState.getGreenItems();
        this.takenItems = smugglersState.getTakenItems();
        this.firstRound = smugglersState.getFirstRound();
    }

    @Override
    public void useCard() {}

    @Override
    public void updateCard(CardStateJSON smugglersState) {
        this.playerNickname = smugglersState.getPlayerNickname();
        this.firstRound = smugglersState.getFirstRound();
//        if (!smugglersState.getFirstRound()) {
//            defeatedPlayers = smugglersState.getDefeatedPlayers();
//            // TODO : Resources/Batteries previously taken (not shown the first time)
//        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        // TODO: Modify to print the symbol (located in printTest)

        if (firstRound) {
            cardInfoWidget.appendString("Level: " + this.cardLevel);
            cardInfoWidget.appendString("Days: " + this.movementSteps);
            cardInfoWidget.appendString("Require Firepower: " + this.requiredFirepower);
            cardInfoWidget.appendString("Red items: " + this.redItems);
            cardInfoWidget.appendString("Yellow items: " + this.yellowItems);
            cardInfoWidget.appendString("Blue items: " + this.blueItems);
            cardInfoWidget.appendString("Green items: " + this.greenItems);
            cardInfoWidget.appendString("Taken items: " + this.takenItems);
        } else {
            cardInfoWidget.appendString("Player: " + this.playerNickname + " has to drop " + this.takenItems + " items");
        }
        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

}
