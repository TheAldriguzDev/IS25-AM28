package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Network.Messages.PlayCard;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.ConsoleWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class CardRoundScreen extends Screen {
    private static final int CONSOLE_WIDGET_MAX_HEIGHT = 6;
    private static final int CONSOLE_WIDGET_MAX_WIDTH = 40;

    private WidgetTUI boardWidget;
    private WidgetTUI currEventCardWidget;
    private WidgetTUI shipGridWidget;
    private WidgetTUI shipStatsWidget;
    private WidgetTUI playerNameWidget;

    private WidgetTUI availableLifeforms;
    private WidgetTUI availableItemColors;

    private WidgetTUI otherPlayerShipWidget;
    private WidgetTUI otherPlayerShipCommandsWidget;

    private WidgetTUI cardRoundCommandsWidget;
    private ConsoleWidgetTUI consoleWidget;

    private ClientEventCard currEventCard;

    private Map<String, Pair<Boolean, Callable<Object>>> indexedCardInputMethods;

    // Constructor
    public CardRoundScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Initializing the map of available input methods
        this.generateIndexedCardInputMethods();

        // Widgets initializations
        this.generateAvailableLifeformsWidget();
        this.generateAvailableItemColorsWidget();
        this.generatePlayerNameWidget();
        this.generateShipWidgets();
        this.generateCardRoundCommandsWidget();
        this.generateOtherPlayerShipCommandsWidget();

        this.boardWidget = this.model.getClientBoard().generateWidget();

        this.consoleWidget = new ConsoleWidgetTUI(
            CONSOLE_WIDGET_MAX_HEIGHT,
            CONSOLE_WIDGET_MAX_WIDTH
        );
    }

    /**
     * Initializes the map of available input methods to provide to the
     * client event card the player's interaction and relative data
     */
    private void generateIndexedCardInputMethods() {
        this.indexedCardInputMethods = new HashMap<>();

        this.indexedCardInputMethods.put("setCrewToRemove",             new Pair<>(false, this::getCrewToRemove));
        this.indexedCardInputMethods.put("setItemsToBeRemoved",         new Pair<>(false, this::getItemToBeTakenOrRemoved));
        this.indexedCardInputMethods.put("setItemsToBeTaken",           new Pair<>(false, this::getItemToBeTakenOrRemoved));
        this.indexedCardInputMethods.put("setTakeReward",               new Pair<>(false, this::getTakeReward));
        this.indexedCardInputMethods.put("setChosenPlanetIndex",        new Pair<>(false, this::getChosenPlanetIndex));
        this.indexedCardInputMethods.put("setWantsToVisit",             new Pair<>(false, this::getWantsToVisit));
        this.indexedCardInputMethods.put("setShieldsToActivate",         new Pair<>(false, this::getShieldToActivate));
        this.indexedCardInputMethods.put("setDoubleCannonsToActivate",   new Pair<>(false, this::getDoubleCannonToActivate));
        this.indexedCardInputMethods.put("setDoubleEnginesToActivate",   new Pair<>(false, this::getDoubleEnginesToActivate));

        ClientEventCard.setAvailableCommands(this.indexedCardInputMethods);
    }

    /**
     * @return A component helper containing the lifeform the player
     *         wants to remove and the relative cabin coordinates
     *         of where it's located.
     */
    private ComponentHelper<LifeformType> getCrewToRemove() {
        ComponentHelper<LifeformType> lifeformPosition;
        Map.Entry<Integer, Integer> componentCoordinates;
        AtomicReference<ClientShip> ship;
        ClientComponent component;
        boolean correctInput;
        LifeformType lfType;
        String line;
        int lfIndex;

        // Getting the player's ship
        ship = new AtomicReference<>();
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(ship::set);

        // Getting the lifeform type to remove
        do {
            System.out.print("Available lifeforms to remove:");
            availableLifeforms.printWidget();
            System.out.print(DEFAULT_COMMAND_PREFIX);

            correctInput = false;
            lfType = null;

            try {
                line = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (line == null) return null;

                lfIndex = Integer.parseInt(line);
                correctInput = true;

                try {
                    lfType = LifeformType.values()[lfIndex];
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please select a valid lifeform.", ANSIColors.RED));
                    correctInput = false;
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return null;
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
        }
        while (!correctInput);

        correctInput = false;

        // Getting the component coordinates
        do {
            try {
                componentCoordinates = this.getComponentCoordinates();
                Map.Entry<Integer, Integer> finalComponentCoordinates = componentCoordinates;

                component = ship.get().getComponent(
                    finalComponentCoordinates.getKey(),
                    finalComponentCoordinates.getValue()
                );

                switch (component) {
                    case ClientCabin cabin -> { correctInput = true; }
                    case null, default -> {
                        System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Component at (" + componentCoordinates.getKey() + ", " + componentCoordinates.getValue() + ") is not a cabin.", ANSIColors.RED));
                    }
                }
            }
            catch (InterruptedException e) {
                return null;
            }
        }
        while (!correctInput);

        // Assembling all together
        lifeformPosition = new ComponentHelper<LifeformType>(
            componentCoordinates.getKey(),
            componentCoordinates.getValue()
        ).addItem(lfType);

        return lifeformPosition;
    }

    /**
     * @return A component helper containing the item color the player
     *         wants to remove or take (depends on where the method it's used)
     *         and the relative storage coordinates of where it's located.
     */
    private ComponentHelper<ItemColor> getItemToBeTakenOrRemoved() {
        ComponentHelper<ItemColor> itemPosition;
        Map.Entry<Integer, Integer> componentCoordinates;
        AtomicReference<ClientShip> ship;
        ClientComponent component;
        boolean correctInput;
        ItemColor itemColor;
        String line;
        int itemIndex;

        // Getting the player's ship
        ship = new AtomicReference<>();
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(ship::set);

        // Getting the items to remove or take
        do {
            System.out.print("Available item colors:");
            availableItemColors.printWidget();
            System.out.print(DEFAULT_COMMAND_PREFIX);

            correctInput = false;
            itemColor = null;

            try {
                line = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (line == null) return null;

                itemIndex = Integer.parseInt(line);
                correctInput = true;

                try {
                    itemColor = ItemColor.values()[itemIndex];
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please select a valid item color.", ANSIColors.RED));
                    correctInput = false;
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return null;
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
        }
        while (!correctInput);

        correctInput = false;

        // Getting the component coordinates
        do {
            try {
                componentCoordinates = this.getComponentCoordinates();
                Map.Entry<Integer, Integer> finalComponentCoordinates = componentCoordinates;

                component = ship.get().getComponent(
                    finalComponentCoordinates.getKey(),
                    finalComponentCoordinates.getValue()
                );

                switch (component) {
                    case ClientStorage storage -> { correctInput = true; }
                    case null, default -> {
                        System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Component at (" + componentCoordinates.getKey() + ", " + componentCoordinates.getValue() + ") is not a storage.", ANSIColors.RED));
                    }
                }
            }
            catch (InterruptedException e) {
                return null;
            }
        }
        while (!correctInput);

        // Assembling all together
        itemPosition = new ComponentHelper<ItemColor>(
            componentCoordinates.getKey(),
            componentCoordinates.getValue()
        ).addItem(itemColor);

        return itemPosition;
    }

    /**
     * @return TRUE if the current player wants to take the resources of a
     *         card that poses this question, FALSE otherwise
     */
    public boolean getTakeReward() {
        return this.getBooleanAnswerToQuestion("Do you want to take the reward?");
    }

    /**
     * @return The player's chosen planet index to land on
     */
    public int getChosenPlanetIndex() {
        WidgetTUI availablePlanetsWidget = new WidgetTUI();
        boolean correctInput = false;
        int chosenIndex = 0;
        String line;

        List<Integer> availablePlanetIndexes =
                this.model.getState()
                    .getCardRoundDTO()
                    .getCardInfo()
                    .getAvailablePlanets()
                    .keySet().stream().toList();

        for (Integer planetIdx : availablePlanetIndexes) {
            availablePlanetsWidget.appendString("(" + planetIdx + ") Planet #" + planetIdx);
        }

        availablePlanetsWidget
                .addPadding(0, 1 , 0, 1)
                .wrapWidgetWithBorder();

        do {
            System.out.print("Available planets to choose:");
            availablePlanetsWidget.printWidget();
            System.out.print(DEFAULT_COMMAND_PREFIX);

            try {
                line = this.inputThread.waitForInput();
                if (line == null) return 0;

                chosenIndex = Integer.parseInt(line);

                if (availablePlanetIndexes.contains(chosenIndex)) {
                    correctInput = true;
                }
                else {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Planet with index " + chosenIndex + " does not exist.", ANSIColors.RED));
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
        }
        while (!correctInput);

        return chosenIndex;
    }

    /**
     * @return TRUE if the player wants to visit the POI (Point of Interest)
     *         offered by the current card, FALSE otherwise
     */
    public boolean getWantsToVisit() {
        return this.getBooleanAnswerToQuestion("Do you want to visit it?");
    }

    /**
     * @return A component helper containing the coordinates of
     *         the current player's chosen shield to activate
     */
    public ComponentHelper<Void> getShieldToActivate() {
        ComponentHelper<Void> componentHelper;
        AtomicReference<ClientShip> shipRef;
        ClientComponent component;
        boolean correctInput;

        // Getting the ship
        shipRef = new AtomicReference<ClientShip>(null);
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(shipRef::set);

        correctInput = false;

        // Verify that the selected component is a shield
        do {
            componentHelper = this.getComponentHelperOfComponent();
            component = shipRef.get().getComponent(
                componentHelper.getI(),
                componentHelper.getJ()
            );

            switch (component) {
                case ClientShield shield -> {
                    correctInput = true;
                }
                case null, default -> {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Component at (" + componentHelper.getI() + ", " + componentHelper.getJ() + ") is not a shield.", ANSIColors.RED));
                }
            }
        }
        while (!correctInput);

        return new ComponentHelper<Void>(
            componentHelper.getI(),
            componentHelper.getJ()
        );
    }

    /**
     * @return A component helper containing the coordinates of
     *         the current player's chosen double cannon to activate
     */
    public ComponentHelper<Void> getDoubleCannonToActivate() {
        ComponentHelper<Void> componentHelper;
        AtomicReference<ClientShip> shipRef;
        ClientComponent component;
        boolean correctInput;

        // Getting the ship
        shipRef = new AtomicReference<ClientShip>(null);
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(shipRef::set);

        correctInput = false;

        // Verify that the selected component is a cannon
        do {
            componentHelper = this.getComponentHelperOfComponent();

            component = shipRef.get().getComponent(
                    componentHelper.getI(),
                    componentHelper.getJ()
            );

            switch (component) {
                case ClientCannon cannon -> {
                    // Verify that it's also a double cannon and
                    // not just a single cannon
                    if (cannon.requireEnergy()) {
                        correctInput = true;
                    }
                }
                case null, default -> {
                }
            }

            if (!correctInput) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Component at (" + componentHelper.getI() + ", " + componentHelper.getJ() + ") is not a double cannon.", ANSIColors.RED));
            }
        }
        while (!correctInput);

        return new ComponentHelper<Void>(
            componentHelper.getI(),
            componentHelper.getJ()
        );
    }

    /**
     * @return The amount of double engines the player wants to activate
     * <br>
     * NOTE: If the player's chosen amount exceeds the actual amount of double engines, then
     *       server-side this is equivalent to activating all double engines (input saturation)
     */
    public int getDoubleEnginesToActivate() {
        AtomicReference<ClientShip> shipRef;
        int doubleEnginesToActivate;
        boolean correctInput;
        String line;

        // Getting the ship
        shipRef = new AtomicReference<ClientShip>(null);
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(shipRef::set);

        doubleEnginesToActivate = 0;
        correctInput = false;

        // Verify that the selected component is an engine
        do {
            try {
                System.out.print("Insert amount of double engines to activate: ");
                line = this.inputThread.waitForInput();

                if (line == null) return 0;

                doubleEnginesToActivate = Integer.parseInt(line);

                if (doubleEnginesToActivate > 0) {
                    correctInput = true;
                }
                else {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Amount must be greater than 0.", ANSIColors.RED));
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return 0;
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
        }
        while (!correctInput);

        return doubleEnginesToActivate;
    }

    /**
     * Generates the available lifeforms widget with the relative
     * value the player needs to insert to select it
     */
    private void generateAvailableLifeformsWidget() {
        this.availableLifeforms = new WidgetTUI();
        int len = LifeformType.values().length;

        for (int i = 0; i < len; i++) {
            this.availableLifeforms.appendString(LifeformType.values()[i].toString());
        }

        this.availableLifeforms
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();
    }

    /**
     * Generates the available item colors widget with the relative
     * value the player needs to insert to select it
     */
    private void generateAvailableItemColorsWidget() {
        this.availableItemColors = new WidgetTUI();
        int len = ItemColor.values().length;

        for (int i = 0; i < len; i++) {
            this.availableItemColors.appendString(ItemColor.values()[i].toString());
        }

        this.availableItemColors
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();
    }

    /**
     * Generates a widget with the player's name and some
     * information about the fact that he's viewing his ship
     */
    private void generatePlayerNameWidget() {
        String playerNickname = this.model.getNickname();
        String playerColorString = this.model.getAllClientPlayers().get(this.model.getNickname()).getColor().getColorString();

        this.playerNameWidget = new WidgetTUI()
                .appendString(PrintUtils.addColor(COMPUTER_MSG_TAG, ANSIColors.BRIGHT_CYAN) + "Viewing your ship")
                .appendString("Player: " + PrintUtils.addColor(playerNickname, playerColorString))
                .centerWidgetScreen()
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();
    }

    /**
     * Generates the ship's widget (component grid and stats)
     */
    private void generateShipWidgets() {
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> {
                this.shipStatsWidget = ship.getShipStatsWidget();
                this.shipGridWidget = ship.getShipGridWidget();
            }
        );
    }

    /**
     * Generates a widget containing all available commands
     * that each player can choose from
     */
    private void generateCardRoundCommandsWidget() {
        this.cardRoundCommandsWidget = new WidgetTUI();

        this.cardRoundCommandsWidget.appendString("(1) Use current card");
        this.cardRoundCommandsWidget.appendString("(2) Visualize subdecks");
        this.cardRoundCommandsWidget.appendString("(3) Visualize ships");

        this.cardRoundCommandsWidget
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();
    }

    /**
     * Generates the widget of the current event card
     */
    private void generateCurrEventCardWidget() {
        this.getCurrEventCard();
        this.currEventCardWidget = this.currEventCard.generateWidget();
    }

    /**
     * Initializes the widget containing all the
     * available ships that a player can look at
     */
    private void generateOtherPlayerShipCommandsWidget() {
        int i, len;

        this.otherPlayerShipCommandsWidget = new WidgetTUI();
        List<String> allNicknames = this.model.getAllPlayersNicknames();
        len = allNicknames.size();

        for (i = 0; i < len; i++) {
            String s = "(" + i + ") Show ship of \"" + allNicknames.get(i) + "\"";

            if (allNicknames.get(i).equals(this.model.getNickname())) {
                this.otherPlayerShipCommandsWidget.appendString(
                        s + SPACE + PrintUtils.addColor(
                                "(YOU)",
                                this.model.getAllClientPlayers().get(allNicknames.get(i))
                                        .getColor()
                                        .getColorString()
                        )
                );
            }
            else {
                this.otherPlayerShipCommandsWidget.appendString(s);
            }
        }

        this.otherPlayerShipCommandsWidget.appendString("(-1) Go back");

        this.otherPlayerShipCommandsWidget.addPadding(0, 1, 0, 1);
        this.otherPlayerShipCommandsWidget.wrapWidgetWithBorder();
    }

    /**
     * Sets the currEventCard parameter to the one communicated
     * by the server through the CardRoundDTO
     */
    private void getCurrEventCard() {
        int cardId;

        cardId = this.model.getState().getCardRoundDTO().getCardInfo().getId();

        for (ClientEventCard card : this.model.getClientEventCards()) {
            if (card.getId() == cardId) {
                this.currEventCard = card;
                return;
            }
        }
    }

    /**
     * Prints the widget that contains the result of the composition
     * of all widgets belonging to the card round phase
     */
    private void printCardRoundWidgets() {
        // Updating all widgets before using them
        this.generateCurrEventCardWidget();
        this.generateShipWidgets();
        this.boardWidget = this.model.getClientBoard().generateWidget();

        this.playerNameWidget.printWidget();

        WidgetTUI.composeTwoWidgetsHorizontally(
            WidgetTUI.fillScreenWithSpaces(
                WidgetTUI.composeTwoWidgetsVertically(
                    this.boardWidget.addPadding(0, 0, 1, 0),
                    this.shipStatsWidget
                )
                .centerWidgetScreen()
                .addPadding(0, 1, 0, 0)
            ),
            WidgetTUI.composeTwoWidgetsHorizontally(
                this.shipGridWidget.addPadding(0, 1, 0, 0),
                this.currEventCardWidget
            )
        ).printWidget();
    }

    /**
     * Asks the user to choose one of
     * the available commands
     */
    private void getCardRoundCommand() {
        String line;
        int choice;

        System.out.println();
        clearTerminal();

        // Printing the entire card round TUI
        this.printCardRoundWidgets();

        System.out.println();
        System.out.println("Available commands:");
        this.cardRoundCommandsWidget.printWidget();

        try {
            System.out.print(DEFAULT_COMMAND_PREFIX);
            line = this.inputThread.waitForInput();

            // A forced interrupt arrived
            if (line == null) return;
        }
        catch (InterruptedException e) {
            // A forced interrupt arrived
            return;
        }

        try {
            choice = Integer.parseInt(line);
        }
        catch (NumberFormatException e) {
            System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            this.getCardRoundCommand();
            return;
        }

        switch (choice) {
            case 1 -> {
                // (1) - Play card (submits the player's input)
                try {
                    this.playCard();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] playCard::sendMessage threw \"" + e.getClass().getSimpleName() + "\"", ANSIColors.RED));
                }
            }
            case 2 -> {
                // (2) - Visualize ships
                this.getOtherShipCommand();
                this.getCardRoundCommand();
            }
            default -> {
                // Loopback and ask for a valid command
                System.out.println(UNKNOWN_COMMAND_ERROR);
                this.getCardRoundCommand();
            }
        }
    }

    /**
     * Asks the player which other player's ship he wants to view
     * and prints it to terminal
     */
    private void getOtherShipCommand() {
        int availableShips;
        int chosenShip;
        String line;

        availableShips = this.model.getAllClientPlayers().size();
        chosenShip = -1;

        do {
            System.out.println();
            System.out.println("Available ships to view:");
            this.otherPlayerShipCommandsWidget.printWidget();

            try {
                System.out.print(DEFAULT_COMMAND_PREFIX);
                line = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (line == null) return;

                chosenShip = Integer.parseInt(line);
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
            catch (InterruptedException e) {
                // A force interrupt arrived
                return;
            }

            if (chosenShip == -1) {
                // Go back was selected
                return;
            }

            if (chosenShip < 0 || chosenShip >= availableShips) {
                System.out.println(UNKNOWN_COMMAND_ERROR);
            }
        }
        while (chosenShip < 0 || chosenShip >= availableShips);

        // Obtaining the chosen ship (if present)
        this.model.getShipOfPlayer(this.model.getAllPlayersNicknames().get(chosenShip))
                .ifPresent(
                        (ClientShip ship) -> {
                            this.otherPlayerShipWidget = ship.getShipGridWidget();
                        }
                );

        // Printing the player's chosen ship (if present)
        if (this.otherPlayerShipWidget != null) {
            clearTerminal();
            this.otherPlayerShipWidget.printWidget();

            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + "You're now viewing \"" + this.model.getAllPlayersNicknames().get(chosenShip) + "\"'s ship")
                    .addPadding(1, 1, 1, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();

            try {
                System.out.print("Press any key and then press [ENTER] to go back...");
                line = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (line == null) return;
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }

            this.otherPlayerShipWidget = null;
        }
    }

    /**
     * @return A pair of integers that represents the (row, col) indexes
     *         where the player wants to place the selected component.
     */
    private Map.Entry<Integer, Integer> getComponentCoordinates() throws InterruptedException {
        Map<Integer, Integer> coordinates = new HashMap<>();
        boolean validCoordinate;
        String line;

        int i = 0;
        int j = 0;

        int minRowValue = ClientShip.shipOffsets.get(this.model.getDifficultyLevel()).getKey();
        int maxRowValue = ClientShip.shipDimensions.get(this.model.getDifficultyLevel()).getKey() + minRowValue + 1;

        int minColValue = ClientShip.shipOffsets.get(this.model.getDifficultyLevel()).getValue();
        int maxColValue = ClientShip.shipDimensions.get(this.model.getDifficultyLevel()).getValue() + minColValue + 1;

        // Getting the row --> i
        do {
            System.out.print("Insert component row: ");
            try {
                line = this.inputThread.waitForInput();

                if (line == null) {
                    // A force interrupt arrived
                    throw new InterruptedException();
                }

                i = Integer.parseInt(line);
                validCoordinate = (i > minRowValue && i < maxRowValue);

                if (!validCoordinate) {
                    System.out.println(PrintUtils.addColor("ERROR: Given row is out of the ship boundaries (range is [" + (minRowValue + 1) + ", " + (maxRowValue - 1) + "])", ANSIColors.RED));
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
                validCoordinate = false;
            }
        }
        while (!validCoordinate);

        // Getting the col --> j
        do {
            System.out.print("Insert component column: ");
            try {
                line = this.inputThread.waitForInput();

                if (line == null) {
                    // A force interrupt arrived
                    throw new InterruptedException();
                }

                j = Integer.parseInt(line);
                validCoordinate = (j > minColValue && j < maxColValue);

                if (!validCoordinate) {
                    System.out.println(PrintUtils.addColor("ERROR: Given column is out of the ship boundaries (range is [" + (minRowValue + 1) + ", " + (maxColValue - 1) + "])", ANSIColors.RED));
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
                validCoordinate = false;
            }
        }
        while (!validCoordinate);

        // Reducing both by 1 since they will then be used as
        // indexes inside the client ship component matrix
        coordinates.put(--i, --j);
        return coordinates.entrySet().stream().toList().getFirst();
    }

    /**
     * @return TRUE if the current player answers YES to the
     *         given YES/NO question, FALSE otherwise
     */
    public boolean getBooleanAnswerToQuestion(String question) {
        boolean playerChoice = false;
        boolean choiceMade = false;
        String input;

        String yesMessage = "Y";
        String noMessage = "N";

        do {
            System.out.println();
            System.out.print(question + " [" + yesMessage + "/" + noMessage + "] ");

            try {
                input = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (input == null) return false;

                if (input.equalsIgnoreCase(yesMessage)) {
                    playerChoice = true;
                    choiceMade = true;
                }
                else if (input.equalsIgnoreCase(noMessage)) {
                    choiceMade = true;
                }
                else {
                    System.out.println(PrintUtils.addColor(UNKNOWN_COMMAND_ERROR, ANSIColors.RED));
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return false;
            }
        }
        while (!choiceMade);

        return playerChoice;
    }

    /**
     * @return A component helper containing the coordinates of a component
     *         chosen by the player
     */
    public ComponentHelper<Void> getComponentHelperOfComponent() {
        Map.Entry<Integer, Integer> componentCoordinates;
        ComponentHelper<Void> componentHelper;

        // Getting the component coordinates
        try {
            componentCoordinates = this.getComponentCoordinates();
        }
        catch (InterruptedException e) {
            return null;
        }

        // Assembling all together
        componentHelper = new ComponentHelper<Void>(
            componentCoordinates.getKey(),
            componentCoordinates.getValue()
        );

        return componentHelper;
    }

    /**
     * Interacts with the current card when it's this player's turn
     * by sending to the server the ActionJSON, generated by the useCard
     * method of the current card, which contains all the data and actions
     * the user wants to perform.
     */
    private void playCard() throws Exception {
        ActionJSON response = this.currEventCard.useCard();

        this.ctx = new CommandCTX(
            "playCard",
            () -> {
                  // TODO: Implement onSuccess (if it needs to do something)
            },
            () -> {
                System.out.println(PrintUtils.addColor("[ERROR] There was an error while playing the card. Please try again.", ANSIColors.RED));
                this.getCardRoundCommand();
            }
        );

        this.client.sendMessage(
            new PlayCard(
                this.model.getNickname(),
                response
            )
        );
    }

    /**
     * TUI screen entry point for the card round game phase
     */
    @Override
    public void showCardRound(CardRoundDTO cardRound) throws Exception {
        // Prints the card round TUI and asks the user for a command
        this.getCardRoundCommand();
    }
}
