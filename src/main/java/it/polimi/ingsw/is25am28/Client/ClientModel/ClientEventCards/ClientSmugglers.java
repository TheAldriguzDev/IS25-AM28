package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public class ClientSmugglers extends ClientEventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private int redItems;
    private int yellowItems;
    private int blueItems;
    private int greenItems;
    private final int takenItems;
    //private boolean firstRound;
    //private List<String> defeatedPlayers;
    private boolean isPlayerDefeated;


    private SmugglersJSON smugglersJSON;

    public ClientSmugglers(CardStateJSON cardState) {
        super(cardState);
        this.requiredFirepower = cardState.getRequiredFirepower();
        this.movementSteps = cardState.getMovementSteps();
        this.redItems = cardState.getRedItems();
        this.yellowItems = cardState.getYellowItems();
        this.blueItems = cardState.getBlueItems();
        this.greenItems = cardState.getGreenItems();
        this.takenItems = cardState.getTakenItems();
        //this.firstRound = true;
        this.isPlayerDefeated = false;
        //this.defeatedPlayers = new ArrayList<>();
        this.smugglersJSON = new SmugglersJSON();

        enabledCommands.add("setDoubleCannonsToActivate");
        enabledCommands.add("setTakeReward");
        enabledCommands.add("setItemsToBeRemoved");
        enabledCommands.add("setItemsToBeTaken");
    }

    @Override
    public ActionJSON useCard() {
        this.smugglersJSON.setPlayerNickname(this.playerNickname);
        return this.smugglersJSON;
    }

    @Override
    public void updateCard(CardStateJSON smugglersState) {
        this.playerNickname = smugglersState.getPlayerNickname();
        this.isPlayerDefeated = smugglersState.getIsPlayerDefeated();
        enabledCommands.clear();
        enabledCommands.add("playCard");
        if (this.isPlayerDefeated) {
            enabledCommands.add("setItemsToBeRemoved");
        } else {
            enabledCommands.add("setDoubleCannonsToActivate");
            enabledCommands.add("setTakeReward");
            enabledCommands.add("setItemsToBeRemoved");
            enabledCommands.add("setItemsToBeTaken");
        }

    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("[" + this.cardName.toUpperCase() + " - LVL: " + this.cardLevel + "]");

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

        if (!this.isPlayerDefeated) {
            cardInfoWidget.appendString("Days: " + this.movementSteps + "      Firepower: " + this.requiredFirepower);
//            cardInfoWidget.appendString("Days: " + this.movementSteps);
//            cardInfoWidget.appendString("Required Firepower: " + this.requiredFirepower);
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Available ╿ " + ANSIColors.RED + "R: " + ANSIColors.RESET + redItems + "," + ANSIColors.YELLOW + " Y: " + ANSIColors.RESET + yellowItems);
            cardInfoWidget.appendString("Resources ╽ " + ANSIColors.BLUE + "B: " + ANSIColors.RESET + blueItems + "," + ANSIColors.GREEN + " G: " + ANSIColors.RESET + greenItems);
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Taken items: " + this.takenItems);

            if (this.playerNickname != null) {
                cardInfoWidget.appendString("───────────────────────────────");
                cardInfoWidget.appendString("Current Player: " + this.playerNickname);
            }
        }
        else {
            cardInfoWidget.appendString("Number of items remove: " + this.takenItems);
            cardInfoWidget.appendString("Target: " + this.playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public void clearJSON() {
        this.smugglersJSON = new SmugglersJSON();
    }

    // Cannons
    @Override
    public void setDoubleCannonsToActivate(List<ComponentHelper<Void>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.smugglersJSON.setDoubleCannonsToActivateCoordinates(doubleCannonsToActivate);
    }

    @Override
    public List<ComponentHelper<Void>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.smugglersJSON.getDoubleCannonsToActivateCoordinates();
    }

    // Reward
    @Override
    public void setTakeReward(boolean takeReward) {
        this.smugglersJSON.setTakeLoot(takeReward);
    }

    @Override
    public Boolean getTakeReward() {
        return this.smugglersJSON.getTakeLoot();
    }

    // Items
    @Override
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        this.smugglersJSON.setItemsToBeRemoved(itemsToBeRemoved);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() throws UnsupportedOperationException {
        return this.smugglersJSON.getItemsToBeRemoved();
    }

    @Override
    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) throws UnsupportedOperationException {
        this.smugglersJSON.setItemsToBeTaken(itemsToBeTaken);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() throws UnsupportedOperationException {
        return this.smugglersJSON.getItemsToBeTaken();
    }

    // Method necessary to the availableColors widget
    // Returns a list of the available colors
    @Override
    public List<ItemColor> getAvailableItemColors() {
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
        switch (itemColor) {
            case RED -> this.redItems--;
            case YELLOW -> this.yellowItems--;
            case BLUE -> this.blueItems--;
            case GREEN -> this.greenItems--;
        }
    }
}
