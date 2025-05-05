package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.SmugglersJSON;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public class ClientSmugglers extends ClientEventCard {
    private static final List<String> enabledCommands;

    // Commands that this card will enable are added here
    static {
        enabledCommands = new ArrayList<>();
        enabledCommands.add("setDoubleCannonsToActivate");
        enabledCommands.add("setTakeReward");
        enabledCommands.add("setItemsToBeRemoved");
        enabledCommands.add("setItemsToBeTaken");
    }

    private final int requiredFirepower;
    private final int movementSteps;
    private final int redItems;
    private final int yellowItems;
    private final int blueItems;
    private final int greenItems;
    private final int takenItems;
    private boolean firstRound;
    private List<String> defeatedPlayers;

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
        this.firstRound = true;
        this.defeatedPlayers = new ArrayList<>();
        this.smugglersJSON = new SmugglersJSON();
    }

    @Override
    public List<String> getEnabledCommands() {
        return enabledCommands;
    }

    @Override
    public ActionJSON useCard() {
        this.smugglersJSON.setPlayerNickname(this.playerNickname);
        SmugglersJSON tmp = this.smugglersJSON;
        this.smugglersJSON = new SmugglersJSON();

        return tmp;
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

        cardWidget.appendString("~~~[" + this.cardName.toUpperCase() + " - LVL:" + this.cardLevel + "]~~~");

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
    public void setDoubleCannonsToActivate(List<ComponentHelper<Void>> doubleCannonsToActivate) throws UnsupportedOperationException {
        this.smugglersJSON.setDoubleCannonsToActivateCoordinates(doubleCannonsToActivate);
    }

    @Override
    public List<ComponentHelper<Void>> getDoubleCannonsToActivate() throws UnsupportedOperationException {
        return this.smugglersJSON.getDoubleCannonsToActivateCoordinates();
    }

    @Override
    public void setTakeReward(boolean takeReward) {
        this.smugglersJSON.setTakeLoot(takeReward);
    }

    @Override
    public boolean getTakeReward() {
        return this.smugglersJSON.getTakeLoot();
    }

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
}
