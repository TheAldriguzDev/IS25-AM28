package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
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
    private List<String> defeatedPlayers;

//    List<String> defeatedPlayers;


    public ClientSmugglers(ClientModel model, InputThread inputThread, CardStateJSON cardState) {
        super(model, inputThread, cardState);
        this.requiredFirepower = cardState.getRequiredFirepower();
        this.movementSteps = cardState.getMovementSteps();
        this.redItems = cardState.getRedItems();
        this.yellowItems = cardState.getYellowItems();
        this.blueItems = cardState.getBlueItems();
        this.greenItems = cardState.getGreenItems();
        this.takenItems = cardState.getTakenItems();
        this.firstRound = true;
        this.defeatedPlayers = new ArrayList<>();
    }

    @Override
    public ActionJSON useCard() {
        return null;
    }

    @Override
    public void updateCard(CardStateJSON smugglersState) {
        this.playerNickname = smugglersState.getPlayerNickname();
        this.firstRound = smugglersState.getFirstRound();
        if (this.firstRound) {
            this.defeatedPlayers = smugglersState.getDefeatedPlayers();
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + this.cardName.toUpperCase() + "====");

        cardInfoWidget.appendString(ANSIColors.RED + "████                       ████" + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED + "  ████                   ████  " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED + "    ████               ████    " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED +"      █████         █████      " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED +"       █ " + ANSIColors.WHITE + "█████████████" + ANSIColors.RED + " █       " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "         █      ███  █         " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "         █    ███    █         " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "         █  ███      █         " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED +"       █ " + ANSIColors.WHITE + "█████████████" + ANSIColors.RED + " █       " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED +"      █████         █████      " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED +"    ████               ████    " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED +"  ████                   ████  " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.RED +"████                       ████" + ANSIColors.RESET);
        cardInfoWidget.wrapWidgetWithBorder();

        if (firstRound) {
            cardInfoWidget.appendString("Level: " + this.cardLevel);
            cardInfoWidget.appendString("Days: " + this.movementSteps);
            cardInfoWidget.appendString("Required Firepower: " + this.requiredFirepower);
            cardInfoWidget.appendString("available items:");
            cardInfoWidget.appendString(ANSIColors.RED + " Red: " + ANSIColors.RESET + this.redItems + " |" + ANSIColors.YELLOW + " Yellow: " + ANSIColors.RESET + this.yellowItems);
            cardInfoWidget.appendString(ANSIColors.BLUE + "Blue: " + ANSIColors.RESET + this.blueItems + " |" + ANSIColors.GREEN + "  Green: " + ANSIColors.RESET + this.greenItems);
            cardInfoWidget.appendString("Taken items: " + this.takenItems);
            if (this.playerNickname != null) {
                cardInfoWidget.appendString("Current Player: " + this.playerNickname);
            }
        } else {
            cardInfoWidget.appendString("Number of items remove: " + this.takenItems);
            cardInfoWidget.appendString("Target: " + this.playerNickname);
        }


        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

}
