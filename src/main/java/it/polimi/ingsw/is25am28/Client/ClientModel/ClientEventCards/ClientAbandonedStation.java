package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;

public class ClientAbandonedStation extends ClientEventCard {
    private final int requiredCrew;
    private final int movementStep;
    private List<ItemColor> stationResources;
    private boolean isCardUsable;

    public ClientAbandonedStation(CardStateJSON cardState, InputThread inputThread, ClientModel model) {
        super(cardState, inputThread, model);
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementStep = cardState.getMovementSteps();
        this.hasBeenUsed = cardState.getIsCardUsable();
        this.stationResources = cardState.getStationResources();
    }

    @Override
    public ActionJSON useCard() {
        return null;
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

        int redItems = 0;
        int yellowItems = 0;
        int blueItems = 0;
        int greenItems = 0;

        for (ItemColor color : this.stationResources) {
            switch (color) {
                case ItemColor.RED -> redItems++;
                case ItemColor.YELLOW -> yellowItems++;
                case ItemColor.BLUE -> blueItems++;
                case ItemColor.GREEN -> greenItems++;
                }
            }


        cardWidget.appendString("~~~[" + this.cardName.toUpperCase() + " - LVL:" + this.cardLevel + "]~~~");

        cardInfoWidget.appendString(                   "            ██ █     █         "                   );
        cardInfoWidget.appendString(                   "              █     █          "                   );
        cardInfoWidget.appendString(ANSIColors.WHITE + "        ███████████████        " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "     █████████████████████     " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "   ██████ ██ ██ ██ ██ ██████   " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "       █████████████████       " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "            ███████            " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "  █████    █████████    █████  " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "      █████ ███████ █████      " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.WHITE + "              ███              " + ANSIColors.RESET);
        cardInfoWidget.appendString(                   "            █ " + ANSIColors.WHITE + "███" + ANSIColors.RESET + " █            "                   );
        cardInfoWidget.appendString(                   "             █ █ █             "                   );
        cardInfoWidget.wrapWidgetWithBorder();

        cardInfoWidget.appendString("Days: " + this.movementStep + "      Crew: " + this.requiredCrew);
        cardInfoWidget.appendString("───────────────────────────────");
//        cardInfoWidget.appendString("Required Crew: " + this.requiredCrew);
//        cardInfoWidget.appendString("Days: " + this.movementStep);
//        cardInfoWidget.appendString("Available Resources:");
        cardInfoWidget.appendString("Available ╿ " + ANSIColors.RED + "R: " + ANSIColors.RESET + redItems + "," + ANSIColors.YELLOW + " Y: " + ANSIColors.RESET + yellowItems);
        cardInfoWidget.appendString("Resources ╽ " + ANSIColors.BLUE + "B: " + ANSIColors.RESET + blueItems + "," + ANSIColors.GREEN + " G: " + ANSIColors.RESET + greenItems);
//        cardInfoWidget.appendString(ANSIColors.RED + " Red: " + ANSIColors.RESET + redItems + " │" + ANSIColors.YELLOW + " Yellow: " + ANSIColors.RESET + yellowItems);
//        cardInfoWidget.appendString(ANSIColors.BLUE + "Blue: " + ANSIColors.RESET + blueItems + " │" + ANSIColors.GREEN + "  Green: " + ANSIColors.RESET + greenItems);
        if (this.playerNickname != null) {
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Current Player: " + this.playerNickname);
        }


        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}
