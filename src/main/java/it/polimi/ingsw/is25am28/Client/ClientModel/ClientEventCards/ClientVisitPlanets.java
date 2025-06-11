package it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientVisitPlanets extends ClientEventCard {
    private final Map<Integer, Map<ItemColor, Integer>> availablePlanets;
    private int chosenPlanetIndex;
    private int movementSteps;

    private VisitPlanetsJSON visitPlanetsJSON;

    public ClientVisitPlanets(CardStateJSON cardState) {
        super(cardState);
        this.availablePlanets = cardState.getAvailablePlanets();
        this.visitPlanetsJSON = new VisitPlanetsJSON();
        this.movementSteps = cardState.getMovementSteps();

//        enabledCommands.add("setItemsToBeRemoved");
//        enabledCommands.add("setItemsToBeTaken");
        enabledCommands.add("setChosenPlanetIndex");
    }

    @Override
    public ActionJSON useCard() {
        this.visitPlanetsJSON.setPlayerNickname(this.playerNickname);
        return this.visitPlanetsJSON;
    }

    @Override
    public void updateCard(CardStateJSON cardState) {
        this.playerNickname = cardState.getPlayerNickname();
        // If a valid planet has been chosen by a player, the corresponding planed will be removed form the avaiable planets
        this.chosenPlanetIndex = cardState.getChosenPlanetIndex();
        if (chosenPlanetIndex != -1) {
            availablePlanets.remove(cardState.getChosenPlanetIndex());
        }
    }

    @Override
    public WidgetTUI generateWidget() {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        int redItems = 0;
        int yellowItems = 0;
        int blueItems = 0;
        int greenItems = 0;

        cardWidget.appendString("[" + this.cardName.toUpperCase() + " - LVL: " + this.cardLevel + "]");

        cardInfoWidget.appendString(ANSIColors.BLUE + "                               " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.GREEN + " ██████████" + ANSIColors.BLUE + "███████████████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.GREEN + " ████████" + ANSIColors.BLUE + "████████████" + ANSIColors.GREEN + "█████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.GREEN + " ██████" + ANSIColors.BLUE + "██████████" + ANSIColors.GREEN + "█████████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.GREEN + " ████" + ANSIColors.BLUE + "██████████" + ANSIColors.GREEN + "███████████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.BLUE + " ██████████████" + ANSIColors.GREEN + "███████████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.BLUE + " █████████████████" + ANSIColors.GREEN + "████████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.BLUE + " ███████████████████" + ANSIColors.GREEN + "██████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.BLUE + " █████████████████████" + ANSIColors.GREEN + "████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.BLUE + " ████" + ANSIColors.GREEN + "████████████" + ANSIColors.BLUE + "█████████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.BLUE + " ██" + ANSIColors.GREEN + "████████████████" + ANSIColors.BLUE + "███████████ " + ANSIColors.RESET);
        cardInfoWidget.appendString(ANSIColors.BLUE + "                               " + ANSIColors.RESET);

        cardInfoWidget.wrapWidgetWithBorder();

//        cardInfoWidget.appendString("Available Planets: ");
        cardInfoWidget.appendString("Days: " + this.movementSteps);

        for (Map.Entry<Integer, Map<ItemColor, Integer>> entry : availablePlanets.entrySet()) {
//            cardInfoWidget.appendString(entry.getKey() + ": " + entry.getValue().toString());
            redItems = (entry.getValue().getOrDefault(ItemColor.RED, 0));
            yellowItems = (entry.getValue().getOrDefault(ItemColor.YELLOW, 0));
            blueItems = (entry.getValue().getOrDefault(ItemColor.BLUE, 0));
            greenItems = (entry.getValue().getOrDefault(ItemColor.GREEN, 0));
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Planet ╿ Available ╿ " + ANSIColors.RED + "R: " + ANSIColors.RESET + redItems + "," + ANSIColors.YELLOW + " Y: " + ANSIColors.RESET + yellowItems);
            cardInfoWidget.appendString("Num: " + entry.getKey() + " ╽ Resources ╽ " + ANSIColors.BLUE + "B: " + ANSIColors.RESET + blueItems + "," + ANSIColors.GREEN + " G: " + ANSIColors.RESET + greenItems);
        }

        if (this.playerNickname != null) {
            cardInfoWidget.appendString("───────────────────────────────");
            cardInfoWidget.appendString("Current Player: " + playerNickname);
        }

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }

    @Override
    public String getAdditionalCardInfo() {
        if (this.visitPlanetsJSON.getChosenPlanetIndex() == -1) {
            return "No planet is currently selected";
        } else {
            Map<ItemColor, Integer> availableResources = availablePlanets.get(this.visitPlanetsJSON.getChosenPlanetIndex());
            return "[AVAILABLE RESOURCES]\n" + availableResources.get(ItemColor.RED) + "🟥 " + availableResources.get(ItemColor.YELLOW) + "🟨 " + availableResources.get(ItemColor.GREEN) + "🟩 " + availableResources.get(ItemColor.BLUE) + "🟦 ";
        }
    }

    @Override
    public void clearJSON() {
        this.visitPlanetsJSON = new VisitPlanetsJSON();
    }

    // Items
    @Override
    public void setItemsToBeRemoved(List<ComponentHelper<ItemColor>> itemsToBeRemoved) throws UnsupportedOperationException {
        this.visitPlanetsJSON.setItemsToDrop(itemsToBeRemoved);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeRemoved() throws UnsupportedOperationException {
        return this.visitPlanetsJSON.getItemsToDrop();
    }

    @Override
    public void setItemsToBeTaken(List<ComponentHelper<ItemColor>> itemsToBeTaken) throws UnsupportedOperationException {
        this.visitPlanetsJSON.setItemsToTake(itemsToBeTaken);
    }

    @Override
    public List<ComponentHelper<ItemColor>> getItemsToBeTaken() throws UnsupportedOperationException {
        return this.visitPlanetsJSON.getItemsToTake();
    }

    // Planet Index
    @Override
    public void setChosenPlanetIndex(int chosenPlanetIndex) throws UnsupportedOperationException {
        this.chosenPlanetIndex = chosenPlanetIndex;
        this.visitPlanetsJSON.setChosenPlanetIndex(this.chosenPlanetIndex);
    }

    @Override
    public Integer getChosenPlanetIndex() throws UnsupportedOperationException {
        return this.visitPlanetsJSON.getChosenPlanetIndex();
    }

    public Map<Integer, Map<ItemColor, Integer>> getAvailablePlanets() {
        return this.availablePlanets;
    }

    // Method necessary to the availableColors widget
    // Returns a list of the available colors
    /**
     * For this method to work correctly, it must be invoked only when a valid chosenPlanetIndex has been set
     * */
    @Override
    public List<ItemColor> getAvailableItemColors() {
        List<ItemColor> availableColors = new ArrayList<>();
        Map<ItemColor, Integer> availableResources = availablePlanets.get(chosenPlanetIndex);
        if (availableResources.get(ItemColor.RED) > 0) {
            availableColors.add(ItemColor.RED);
        }
        if (availableResources.get(ItemColor.YELLOW) > 0) {
            availableColors.add(ItemColor.YELLOW);
        }
        if (availableResources.get(ItemColor.BLUE) > 0) {
            availableColors.add(ItemColor.BLUE);
        }
        if (availableResources.get(ItemColor.GREEN) > 0) {
            availableColors.add(ItemColor.GREEN);
        }
        return availableColors;
    }

    /**
     * For this method to work correctly, it must be invoked only when a valid chosenPlanetIndex has been set
     * */
    @Override
    public void removeItem(ItemColor itemColor) {
        Map<ItemColor, Integer> availableResources = availablePlanets.get(chosenPlanetIndex);
        availableResources.replace(itemColor, availableResources.get(itemColor) - 1);
    }
}
