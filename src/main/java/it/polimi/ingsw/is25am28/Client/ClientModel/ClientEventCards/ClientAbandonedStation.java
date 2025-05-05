package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.AbandonedStationJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;

public class ClientAbandonedStation extends ClientEventCard {
    private final int requiredCrew;
    private final int movementStep;
    private List<ItemColor> stationResources;
    private boolean isCardUsable;

    private AbandonedStationJSON abandonedStationJSON;

    public ClientAbandonedStation(CardStateJSON cardState) {
        super(cardState);
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementStep = cardState.getMovementSteps();
        this.hasBeenUsed = cardState.getIsCardUsable();
        this.stationResources = cardState.getStationResources();
        this.abandonedStationJSON = new AbandonedStationJSON();

        enabledCommands.add("setItemsToBeRemoved");
        enabledCommands.add("setItemsToBeTaken");
    }

    @Override
    public ActionJSON useCard() {
        this.abandonedStationJSON.setPlayerNickname(this.playerNickname);
        AbandonedStationJSON tmp = abandonedStationJSON;
        abandonedStationJSON = new AbandonedStationJSON();

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

    @Override
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        this.abandonedStationJSON.setItemsToBeRemoved(itemsToBeRemoved);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() throws UnsupportedOperationException {
        return this.abandonedStationJSON.getItemsToBeRemoved();
    }

    @Override
    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) throws UnsupportedOperationException {
        this.abandonedStationJSON.setItemsToBeTaken(itemsToBeTaken);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() throws UnsupportedOperationException {
        return this.abandonedStationJSON.getItemsToBeTaken();
    }
}
