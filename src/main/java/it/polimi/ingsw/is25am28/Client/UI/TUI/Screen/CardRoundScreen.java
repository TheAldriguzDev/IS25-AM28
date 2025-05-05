package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Network.Messages.PlayCard;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.ConsoleWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class CardRoundScreen extends Screen {
    private static final int CONSOLE_WIDGET_MAX_HEIGHT = 6;
    private static final int CONSOLE_WIDGET_MAX_WIDTH = 40;
    private static final int COMMAND_GROUPING_FACTOR = 2;

    private WidgetTUI boardWidget;
    private WidgetTUI currEventCardWidget;
    private WidgetTUI shipGridWidget;
    private WidgetTUI shipStatsWidget;
    private WidgetTUI playerNameWidget;

    private WidgetTUI availableLifeforms;
    private WidgetTUI availableItemColors;

    private WidgetTUI otherPlayerShipWidget;
    private WidgetTUI otherPlayerShipCommandsWidget;

    private InputWidgetTUI cardRoundCommandsWidget;
    private ConsoleWidgetTUI consoleWidget;

    private ClientEventCard currEventCard;
    private CardStateJSON currEventCardState;
    private Map<String, Pair<Boolean, CommandWidgetTUI>> indexedCardInputMethods;

    // Constructor
    public CardRoundScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Widgets initializations
        this.generateAvailableLifeformsWidget();
        this.generateAvailableItemColorsWidget();
        this.generatePlayerNameWidget();
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
    private void generateIndexedCardInputMethodsMap() {
        CommandWidgetTUI command;

        this.indexedCardInputMethods = new HashMap<>();

        // (2) - Add crew to remove
        command = new CommandWidgetTUI(
            "2",
            () -> {
                this.getCrewToRemove();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Add crew to remove");
        this.indexedCardInputMethods.put("setCrewToRemove", new Pair<>(false, command));

        // (3) - Add items to remove
        command = new CommandWidgetTUI(
            "3",
            () -> {
                this.getItemToBeRemoved();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Add item to remove");
        this.indexedCardInputMethods.put("setItemsToBeRemoved", new Pair<>(false, command));

        // (4) - Add item to take
        command = new CommandWidgetTUI(
            "4",
            () -> {
                this.getItemToBeTaken();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Add item to take");
        this.indexedCardInputMethods.put("setItemsToBeTaken", new Pair<>(false, command));

        // (5) - Take reward?
        command = new CommandWidgetTUI(
            "5",
            () -> {
                this.getTakeReward();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Take reward?");
        this.indexedCardInputMethods.put("setTakeReward", new Pair<>(false, command));

        // (6) - Choose planet
        command = new CommandWidgetTUI(
            "6",
            () -> {
                this.getChosenPlanetIndex();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Choose planet");
        this.indexedCardInputMethods.put("setChosenPlanetIndex", new Pair<>(false, command));

        // (7) - Visit the POI?
        command = new CommandWidgetTUI(
            "7",
            () -> {
                this.getWantsToVisit();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Visit the POI?");
        this.indexedCardInputMethods.put("setWantsToVisit", new Pair<>(false, command));

        // (8) - Add shield to activate
        command = new CommandWidgetTUI(
            "8",
            () -> {
                this.getShieldToActivate();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Add shield to activate");
        this.indexedCardInputMethods.put("setShieldsToActivate", new Pair<>(false, command));

        // (9) - Add double cannon to activate
        command = new CommandWidgetTUI(
            "9",
            () -> {
                this.getDoubleCannonToActivate();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Add double cannon to activate");
        this.indexedCardInputMethods.put("setDoubleCannonsToActivate", new Pair<>(false, command));

        // (10) - Set double engines to activate
        command = new CommandWidgetTUI(
            "10",
            () -> {
                this.getDoubleEnginesToActivate();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Set double engines to activate");
        this.indexedCardInputMethods.put("setDoubleEnginesToActivate", new Pair<>(false, command));

        // (11) - Acknowledge and continue
        command = new CommandWidgetTUI(
            "11",
            () -> {
                this.getPlayerAck();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Acknowledge and continue");
        this.indexedCardInputMethods.put("getPlayerAck", new Pair<>(false, command));

        ClientEventCard.setAvailableCommands(this.currEventCard.getEnabledCommands(), this.indexedCardInputMethods);
    }

    /**
     * Adds component helper containing the lifeform the player
     * wants to remove and the relative cabin coordinates
     * of where it's located to the corresponding ActionJSON
     */
    private void getCrewToRemove() {
        List<ComponentHelper<LifeformType>> crewToRemove;
        ComponentHelper<LifeformType> lifeformPosition;
        Map.Entry<Integer, Integer> componentCoordinates;
        ClientComponent component;
        boolean correctInput;
        LifeformType lfType;
        ClientShip ship;
        String line;
        int lfIndex;

        crewToRemove = this.currEventCard.getCrewToRemove();
        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) return;

        // Getting the lifeform type to remove
        do {
            System.out.println("Available lifeforms to remove:");
            availableLifeforms.printWidget();
            System.out.print(DEFAULT_COMMAND_PREFIX);

            correctInput = false;
            lfType = null;

            try {
                line = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (line == null) return;

                lfIndex = Integer.parseInt(line);

                try {
                    lfType = LifeformType.values()[lfIndex];

                    if (lfType == LifeformType.PURPLE_ALIEN && ship.getPurpleAlienPosition() == null) {
                        System.out.println(PrintUtils.addColor("[ERROR] You don't have a purple alien onboard! Select another lifeform.", ANSIColors.RED));
                    }
                    else if (lfType == LifeformType.BROWN_ALIEN && ship.getBrownAlienPosition() == null) {
                        System.out.println(PrintUtils.addColor("[ERROR] You don't have a brown alien onboard! Select another lifeform.", ANSIColors.RED));
                    }
                    else {
                        correctInput = true;
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please select a valid lifeform.", ANSIColors.RED));
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
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

                component = ship.getComponent(
                    finalComponentCoordinates.getKey(),
                    finalComponentCoordinates.getValue()
                );

                switch (component) {
                    case ClientCabin cabin -> {
                        if (cabin.getAvailableSpace() <= 1) {
                            correctInput = true;
                        }
                        else {
                            System.out.println(PrintUtils.addColor("[ERROR] Selected cabin is empty.", ANSIColors.RED));
                        }
                    }
                    case null, default -> {
                        System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Component at (" + componentCoordinates.getKey() + ", " + componentCoordinates.getValue() + ") is not a cabin.", ANSIColors.RED));
                    }
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!correctInput);

        // Assembling all together
        lifeformPosition = new ComponentHelper<LifeformType>(
            componentCoordinates.getKey(),
            componentCoordinates.getValue()
        ).addItem(lfType);

        crewToRemove.add(lifeformPosition);
        this.currEventCard.setCrewToRemove(crewToRemove);
    }

    /**
     * Adds a component helper containing the item color the player
     * wants to remove (depends on where the method it's used)
     * and the relative storage coordinates of where it's located.
     */
    private void getItemToBeRemoved() {
        List<ComponentHelper<ItemColor>> itemsToBeRemoved;
        ComponentHelper<ItemColor> itemPosition;
        Map.Entry<Integer, Integer> componentCoordinates;
        ClientComponent component;
        boolean correctInput;
        ItemColor itemColor;
        ClientShip ship;
        int itemIndex;
        String line;

        itemsToBeRemoved = this.currEventCard.getItemsToBeTaken();
        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) return;

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
                if (line == null) return;

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
                return;
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

                component = ship.getComponent(
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
                return;
            }
        }
        while (!correctInput);

        // Assembling all together
        itemPosition = new ComponentHelper<ItemColor>(
                componentCoordinates.getKey(),
                componentCoordinates.getValue()
        ).addItem(itemColor);

        itemsToBeRemoved.add(itemPosition);
        this.currEventCard.setItemsToBeRemoved(itemsToBeRemoved);
    }

    /**
     * Adds a component helper containing the item color the player
     * wants to take (depends on where the method it's used)
     * and the relative storage coordinates of where to put them
     */
    private void getItemToBeTaken() {
        List<ComponentHelper<ItemColor>> itemsToBeTaken;
        ComponentHelper<ItemColor> itemPosition;
        Map.Entry<Integer, Integer> componentCoordinates;
        ClientComponent component;
        boolean correctInput;
        ItemColor itemColor;
        ClientShip ship;
        int itemIndex;
        String line;

        itemsToBeTaken = this.currEventCard.getItemsToBeTaken();
        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) return;

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
                if (line == null) return;

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
                return;
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

                component = ship.getComponent(
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
                return;
            }
        }
        while (!correctInput);

        // Assembling all together
        itemPosition = new ComponentHelper<ItemColor>(
            componentCoordinates.getKey(),
            componentCoordinates.getValue()
        ).addItem(itemColor);

        itemsToBeTaken.add(itemPosition);
        this.currEventCard.setItemsToBeTaken(itemsToBeTaken);
    }

    /**
     * Sets the relative JSON flag to TRUE if the current player wants to take
     * the resources of a card that poses this question, FALSE otherwise.
     */
    public void getTakeReward() {
        this.currEventCard.setTakeReward(this.getBooleanAnswerToQuestion("Do you want to take the reward?"));
    }

    /**
     * Sets the relative JSON attribute to the player's
     * chosen planet index to land on.
     */
    public void getChosenPlanetIndex() {
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
                if (line == null) return;

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
                return;
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
        }
        while (!correctInput);

        this.currEventCard.setChosenPlanetIndex(chosenIndex);
    }

    /**
     * Sets the relative JSON flag to TRUE if the current player wants to
     * visit the POI (Point of Interest) offered by the card, FALSE otherwise.
     */
    public void getWantsToVisit() {
         this.currEventCard.setWantsToVisit(this.getBooleanAnswerToQuestion("Do you want to visit it?"));
    }

    /**
     * Adds a component helper with coordinates pointing to
     * a shield that the player wants to activate.
     */
    public void getShieldToActivate() {
        List<ComponentHelper<Void>> componentHelperList;
        ComponentHelper<Void> componentHelper;
        ClientComponent component;
        boolean correctInput;
        ClientShip ship;

        componentHelperList = this.currEventCard.getShieldsToActivate();
        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);
        correctInput = false;

        if (ship == null) return;

        // Verify that the selected component is a shield
        do {
            componentHelper = this.getComponentHelperOfComponent();
            component = ship.getComponent(
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

        componentHelper = new ComponentHelper<Void>(
            componentHelper.getI(),
            componentHelper.getJ()
        );

        componentHelperList.add(componentHelper);
        this.currEventCard.setShieldsToActivate(componentHelperList);
    }

    /**
     * Adds a component helper with coordinates pointing to
     * a double cannon that the player wants to activate.
     */
    public void getDoubleCannonToActivate() {
        List<ComponentHelper<Void>> componentHelperList;
        ComponentHelper<Void> componentHelper;
        ClientComponent component;
        boolean correctInput;
        ClientShip ship;

        componentHelperList = this.currEventCard.getDoubleCannonsToActivate();
        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);
        correctInput = false;

        if (ship == null) return;

        // Verify that the selected component is a cannon
        do {
            componentHelper = this.getComponentHelperOfComponent();

            component = ship.getComponent(
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

        componentHelper = new ComponentHelper<Void>(
            componentHelper.getI(),
            componentHelper.getJ()
        );

        componentHelperList.add(componentHelper);
        this.currEventCard.setDoubleCannonsToActivate(componentHelperList);
    }

    /**
     * Sets the amount of double engines the player wants to activate.
     * <br>
     * NOTE: If the player's chosen amount exceeds the actual amount of double engines, then
     *       server-side this is equivalent to activating all double engines (input saturation)
     */
    public void getDoubleEnginesToActivate() {
        int doubleEnginesToActivate;
        boolean correctInput;
        String line;

        doubleEnginesToActivate = 0;
        correctInput = false;

        // Verify that the selected component is an engine
        do {
            try {
                System.out.print("Insert amount of double engines to activate: ");
                line = this.inputThread.waitForInput();

                if (line == null) return;

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
                return;
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
        }
        while (!correctInput);

        this.currEventCard.setDoubleEnginesToActivate(doubleEnginesToActivate);
    }

    /**
     * Method used to make the player aware that the current operation
     * is automatic, and he just has to acknowledge it.
     */
    public void getPlayerAck() {
        System.out.print("Press any key and then press [ENTER] to continue...");

        try {
            this.inputThread.waitForInput();
        }
        catch (InterruptedException e) {
            // A forced interrupt arrived
        }
    }

    /**
     * Generates the available lifeforms widget with the relative
     * value the player needs to insert to select it
     */
    private void generateAvailableLifeformsWidget() {
        this.availableLifeforms = new WidgetTUI();
        int len = LifeformType.values().length;

        for (int i = 0; i < len; i++) {
            this.availableLifeforms.appendString("(" + i + ")" + SPACE + LifeformType.values()[i].toString());
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
            this.availableItemColors.appendString("(" + i + ")" + SPACE + ItemColor.values()[i].toString());
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
        CommandWidgetTUI command;

        this.cardRoundCommandsWidget = new InputWidgetTUI(this.inputThread);
        this.cardRoundCommandsWidget.setColumnGroupingAmount(COMMAND_GROUPING_FACTOR);

        // (0) - Play card
        command = new CommandWidgetTUI(
            "0",
            () -> {
                try {
                    this.playCard();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" thrown by method 'playCard'.", ANSIColors.RED));
                }

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Play card");
        this.cardRoundCommandsWidget.addCommand(command);

        // (1) - Visualize ship
        command = new CommandWidgetTUI(
            "1",
            () -> {
                this.getOtherShipCommand();

                // Go back to the card round available commands
                this.getCardRoundCommand();
            }
        );
        command.appendString("Visualize ship");
        this.cardRoundCommandsWidget.addCommand(command);

        // Adding all other commands whose flag is set to TRUE by
        // the currently active event card
        this.cardRoundCommandsWidget.setCommands(
            this.indexedCardInputMethods.values().stream()
                .filter(pair -> (pair.getKey() == true))
                .map(Pair::getValue)
                .toList()
        );
    }

    /**
     * Generates the widget of the current event card
     */
    private void generateCurrEventCardWidget() {
        this.getCurrEventCard(this.currEventCardState);
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
    private void getCurrEventCard(CardStateJSON cardState) {
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
        boolean commandSelected;

        System.out.println();
        clearTerminal();

        do {
            // Printing the entire card round TUI
            this.printCardRoundWidgets();

            // Showing currently available commands
            System.out.println();
            System.out.println("Available commands:");

            try {
                commandSelected = this.cardRoundCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);
            }
            catch (InterruptedException e) {
                return;
            }

            if (!commandSelected) {
                System.out.println(UNKNOWN_COMMAND_ERROR);
            }
        }
        while (!commandSelected);
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
                System.out.println("onSuccess");

                // TODO: Implement onSuccess (if it needs to do something)
                // TODO: view a screen saying that your turn is over
            },
            () -> {
                System.out.println("onError");

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
        this.currEventCardState = cardRound.getCardInfo();

        // Updating this player's ship widget and
        // getting the current event card
        this.getCurrEventCard(this.currEventCardState);
        this.generateShipWidgets();

        // Updating the current event card
        this.currEventCard.updateCard(cardRound.getCardInfo());

        // Filtering only the commands that the
        // current event card is enabling
        this.generateIndexedCardInputMethodsMap();
        this.generateCardRoundCommandsWidget();

        System.out.println();
        System.out.println(ANSIColors.BRIGHT_CYAN + "[!] CardRoundDTO has arrived [!]" + ANSIColors.RESET);
        System.out.println();

        // Prints the card round TUI and asks the user for a command
        this.getCardRoundCommand();
    }
}
