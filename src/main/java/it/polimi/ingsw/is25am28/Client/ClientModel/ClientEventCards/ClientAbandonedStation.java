package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public class ClientAbandonedStation extends ClientEventCard {
    private final int requiredCrew;
    private final int movementStep;
//    private List<ItemColor> stationResources;
    private boolean isCardUsable;
    int redItems = 0;
    int yellowItems = 0;
    int blueItems = 0;
    int greenItems = 0;


    private AbandonedStationJSON abandonedStationJSON;

    public ClientAbandonedStation(CardStateJSON cardState) {
        super(cardState);
        this.requiredCrew = cardState.getRequiredCrewMembers();
        this.movementStep = cardState.getMovementSteps();
        this.isCardUsable = cardState.getIsCardUsable();
//        this.stationResources = cardState.getStationResources();
        this.abandonedStationJSON = new AbandonedStationJSON();

        this.redItems = 0;
        this.yellowItems = 0;
        this.blueItems = 0;
        this.greenItems = 0;

        for (ItemColor color : cardState.getStationResources()) {
            switch (color) {
                case ItemColor.RED -> redItems++;
                case ItemColor.YELLOW -> yellowItems++;
                case ItemColor.BLUE -> blueItems++;
                case ItemColor.GREEN -> greenItems++;
            }
        }


    }

    @Override
    public ActionJSON useCard() {
        this.abandonedStationJSON.setPlayerNickname(this.playerNickname);
        return this.abandonedStationJSON;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        this.isCardUsable = cardState.getIsCardUsable();
        enabledCommands.clear();
        enabledCommands.add("playCard");
        if (this.isCardUsable) {
            enabledCommands.add("setWantsToVisit");
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("[" + this.cardName.toUpperCase() + " - LVL: " + this.cardLevel + "]");

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
        cardInfoWidget.appendString("Available ╿ " + ANSIColors.RED + "R: " + ANSIColors.RESET + redItems + "," + ANSIColors.YELLOW + " Y: " + ANSIColors.RESET + yellowItems);
        cardInfoWidget.appendString("Resources ╽ " + ANSIColors.BLUE + "B: " + ANSIColors.RESET + blueItems + "," + ANSIColors.GREEN + " G: " + ANSIColors.RESET + greenItems);
        if (this.playerNickname != null) {
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Current Player: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public String getAdditionalCardInfo() {
        return "[CURRENT STATION RESOURCES]\n" + this.redItems + "🟥 " + this.yellowItems + "🟨 " + this.greenItems + "🟩 " + this.blueItems + "🟦 ";
    }

    @Override
    public void clearJSON() {
        this.abandonedStationJSON = new AbandonedStationJSON();
    }

    // Items
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

    // WantsToVisit
    @Override
    public void setWantsToVisit(boolean wantsToVisit) {
        this.abandonedStationJSON.setWantToVisitStation(wantsToVisit);
    }

    @Override
    public Boolean getWantsToVisit() {
        return this.abandonedStationJSON.getWantToVisitStation();
    }

    // Method necessary to the availableColors widget
    // Returns a list of the available colors
    @Override
    public List<ItemColor> getAvailableItemColors() {
//        List<ItemColor> availableColors = new ArrayList<>();
//        for(ItemColor color : ItemColor.values()) {
//            if (this.stationResources.contains(color)) {
//                availableColors.add(color);
//            }
//        }
//        return availableColors;
        List<ItemColor> availableColors = new ArrayList<>();
        if (redItems > 0) {
            availableColors.add(ItemColor.RED);
        }
        if (yellowItems > 0) {
            availableColors.add(ItemColor.YELLOW);
        }
        if (blueItems > 0) {
            availableColors.add(ItemColor.BLUE);
        }
        if (greenItems > 0) {
            availableColors.add(ItemColor.GREEN);
        }
        return availableColors;
    }

    @Override
    public void removeItem(ItemColor itemColor) {
//        this.stationResources.remove(itemColor);
        switch (itemColor) {
            case RED -> this.redItems--;
            case YELLOW -> this.yellowItems--;
            case BLUE -> this.blueItems--;
            case GREEN -> this.greenItems--;
        }
    }


}
