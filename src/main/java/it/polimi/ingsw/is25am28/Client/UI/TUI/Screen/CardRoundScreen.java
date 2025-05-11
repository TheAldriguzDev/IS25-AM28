package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.*;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Network.Messages.PlayCard;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.ConsoleWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.*;

public class CardRoundScreen extends Screen {
    private static final int CONSOLE_WIDGET_MAX_HEIGHT = 6;
    private static final int CONSOLE_WIDGET_MAX_WIDTH = 40;
    private static final int COMMAND_GROUPING_FACTOR = 2;

    private WidgetTUI boardWidget;
    private WidgetTUI currEventCardWidget;
    private WidgetTUI shipGridWidget;
    private WidgetTUI shipStatsWidget;
    private WidgetTUI playerNameWidget;
    private WidgetTUI playerTurnWidget;

    private InputWidgetTUI availableLifeforms;
    private InputWidgetTUI availableItemColors;

    private WidgetTUI otherPlayerShipWidget;
    private InputWidgetTUI otherPlayerShipCommandsWidget;

    private WidgetTUI playerActionsRecapWidget;

    private InputWidgetTUI cardRoundCommandsWidget;
    private ConsoleWidgetTUI consoleWidget;

    private ClientEventCard currEventCard;
    private CardStateJSON currEventCardState;
    private String currPlayerNickname;
    private Map<String, Pair<Boolean, CommandWidgetTUI>> indexedCardInputMethods;

    // Constructor
    public CardRoundScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Widgets initializations
        this.generatePlayerNameWidget();
        this.generateOtherPlayerShipCommandsWidget();

        this.boardWidget = this.model.getClientBoard().generateWidget();

        this.consoleWidget = new ConsoleWidgetTUI(
                CONSOLE_WIDGET_MAX_HEIGHT,
                CONSOLE_WIDGET_MAX_WIDTH
        );
    }

    /**
     * Prints the current ActionJSON giving the current player
     * an overview of the changes he's staging for submission when
     * he'll play the current event card.
     */
    private void generatePlayerActionsRecapWidget() {
        this.playerActionsRecapWidget = new WidgetTUI();

        // (1) - Crew to remove
        try {
            List<ComponentHelper<LifeformType>> crewToRemove = this.currEventCard.getCrewToRemove();

            if (crewToRemove != null && !crewToRemove.isEmpty()) {
                this.playerActionsRecapWidget.appendString("Crew to remove:");

                for (ComponentHelper<LifeformType> lfToRemove : crewToRemove) {
                    lfToRemove.getItem().ifPresent(
                            (LifeformType lfType) -> {
                                this.playerActionsRecapWidget
                                        .appendString(TAB + lfType + " @ (row=" + (lfToRemove.getI() + 1) + ", col=" + (lfToRemove.getJ() + 1) + ")");
                            }
                    );
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (2) - Items to remove
        try {
            List<ComponentHelper<ItemColor>> itemsToRemove = this.currEventCard.getItemsToBeRemoved();

            if (itemsToRemove != null && !itemsToRemove.isEmpty()) {
                this.playerActionsRecapWidget.appendString("Items to remove:");

                for (ComponentHelper<ItemColor> itemToRemove : itemsToRemove) {
                    itemToRemove.getItem().ifPresent(
                            (ItemColor itemColor) -> {
                                this.playerActionsRecapWidget
                                        .appendString(TAB + itemColor + " @ (row=" + (itemToRemove.getI() + 1) + ", col=" + (itemToRemove.getJ() + 1) + ")");
                            }
                    );
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (3) - Items to take
        try {
            List<ComponentHelper<ItemColor>> itemsToTake = this.currEventCard.getItemsToBeTaken();

            if (itemsToTake != null && !itemsToTake.isEmpty()) {
                this.playerActionsRecapWidget.appendString("Items to take:");

                for (ComponentHelper<ItemColor> itemToTake : itemsToTake) {
                    itemToTake.getItem().ifPresent(
                            (ItemColor itemColor) -> {
                                this.playerActionsRecapWidget
                                        .appendString(TAB + itemColor + " @ (row=" + (itemToTake.getI() + 1) + ", col=" + (itemToTake.getJ() + 1) + ")");
                            }
                    );
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (4) - Take reward?
        try {
            Boolean takeReward = this.currEventCard.getTakeReward();

            if (takeReward != null) {
                this.playerActionsRecapWidget
                        .appendString("Take reward?: " + (takeReward ? "Yes" : "No"));
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (5) - Chosen planet index
        try {
            Integer chosenPlanetIndex = this.currEventCard.getChosenPlanetIndex();

            if (chosenPlanetIndex != null) {
                this.playerActionsRecapWidget
                        .appendString("Chosen planet index: " + chosenPlanetIndex);
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (6) - Visit the POI?
        try {
            Boolean wantsToVisit = this.currEventCard.getWantsToVisit();

            if (wantsToVisit != null) {
                this.playerActionsRecapWidget
                        .appendString("Visit the POI?: " + (wantsToVisit ? "Yes" : "No"));
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (7) - Shields to activate
        try {
            List<ComponentHelper<Void>> shieldsToActivate = this.currEventCard.getShieldsToActivate();

            if (shieldsToActivate != null && !shieldsToActivate.isEmpty()) {
                this.playerActionsRecapWidget.appendString("Shields to activate:");

                for (ComponentHelper<Void> shieldToActivate : shieldsToActivate) {
                    this.playerActionsRecapWidget
                            .appendString(TAB + "Shield @ (row=" + (shieldToActivate.getI() + 1) + ", col=" + (shieldToActivate.getJ() + 1) + ")");
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (8) - Double cannons to activate
        try {
            List<ComponentHelper<Void>> doubleCannonsToActivate = this.currEventCard.getDoubleCannonsToActivate();

            if (doubleCannonsToActivate != null && !doubleCannonsToActivate.isEmpty()) {
                this.playerActionsRecapWidget.appendString("Double cannons to activate:");

                for (ComponentHelper<Void> doubleCannonToActivate : doubleCannonsToActivate) {
                    this.playerActionsRecapWidget
                            .appendString(TAB + "DoubleCannon @ (row=" + (doubleCannonToActivate.getI() + 1) + ", col=" + (doubleCannonToActivate.getJ() + 1) + ")");
                }
            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        // (9) - Double engines to activate
        try {
            Integer doubleEnginesToActivate = this.currEventCard.getDoubleEnginesToActivate();

            if (doubleEnginesToActivate != null) {
                this.playerActionsRecapWidget
                        .appendString("Double engines to activate: " + doubleEnginesToActivate);

            }
        } catch (UnsupportedOperationException e) {
            // Nothing is added
        }

        WidgetTUI actionsScreen = this.playerActionsRecapWidget;
        this.playerActionsRecapWidget = new WidgetTUI();

        if (actionsScreen.getScreen().isEmpty()) {
            actionsScreen.appendString("No actions selected");
        }

        this.playerActionsRecapWidget
                .setWidth(actionsScreen.getWidth())
                .appendString("[YOUR ACTIONS]")
                .centerWidgetScreen()
                .addPadding(0, 0, 1, 0)
                .appendScreen(actionsScreen.getScreen());

        this.playerActionsRecapWidget
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();
    }

    /**
     * Initializes the map of available input methods to provide to the
     * client event card the player's interaction and relative data
     */
    private void generateIndexedCardInputMethodsMap() {
        CommandWidgetTUI command;

        this.indexedCardInputMethods = new HashMap<>();

        // (1) - Play card
        command = new CommandWidgetTUI(
                "1",
                () -> {
                    try {
                        this.playCard();
                    } catch (Exception e) {
                        System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" thrown by method 'playCard'.", ANSIColors.RED));
                    }
                }
        );
        command.appendString("Play card");
        this.indexedCardInputMethods.put("playCard", new Pair<>(false, command));

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
    }

    /**
     * Generates the available lifeforms widget with the relative
     * value the player needs to insert to select it
     */
    private void generateAvailableLifeformsWidget(List<LifeformType> availableLifeforms, AtomicReference<LifeformType> selectedLifeform) {
        CommandWidgetTUI command;
        int index;

        this.availableLifeforms = new InputWidgetTUI(this.inputThread);
        index = 0;

        for (LifeformType lfType : availableLifeforms) {
            command = new CommandWidgetTUI(
                    "" + index,
                    () -> {
                        selectedLifeform.set(lfType);
                    }
            );
            command.appendString(lfType.toString());
            this.availableLifeforms.addCommand(command);
            index++;
        }

        // (-1) - Go back
        command = new CommandWidgetTUI(
                "-1",
                () -> {
                    selectedLifeform.set(null);
                }
        );
        command.appendString("Go back to menu");
        this.availableLifeforms.addCommand(command);

        this.availableItemColors.setColumnGroupingAmount(
                this.availableItemColors.getCommandMap().size()
        );
    }

    /**
     * Generates the available item colors widget with the relative
     * value the player needs to insert to select it
     */
    private void generateAvailableItemColorsWidget(List<ItemColor> availableItemColors, AtomicReference<ItemColor> selectedColor) {
        CommandWidgetTUI command;
        int index;

        this.availableItemColors = new InputWidgetTUI(this.inputThread);
        index = 0;

        for (ItemColor color : availableItemColors) {
            command = new CommandWidgetTUI(
                    "" + index,
                    () -> {
                        selectedColor.set(color);
                    }
            );
            command.appendString(color.toString());
            this.availableItemColors.addCommand(command);
            index++;
        }

        // (-1) - Go back
        command = new CommandWidgetTUI(
                "-1",
                () -> {
                    selectedColor.set(null);
                }
        );
        command.appendString("Go back to menu");
        this.availableItemColors.addCommand(command);

        this.availableItemColors.setColumnGroupingAmount(
                this.availableItemColors.getCommandMap().size()
        );
    }

    /**
     * Adds component helper containing the lifeform the player
     * wants to remove and the relative cabin coordinates
     * of where it's located to the corresponding ActionJSON
     */
    private void getCrewToRemove() {
        List<ComponentHelper<LifeformType>> crewToRemove;
        ComponentHelper<LifeformType> lifeformPosition;
        AtomicReference<LifeformType> selectedLifeform;
        List<LifeformType> availableLifeformsOnboard;
        ClientShip ship;
        boolean commandSelected;

        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [getCrewToRemove()] ClientShip is null", ANSIColors.RED));
            return;
        }

        selectedLifeform = new AtomicReference<>(null);
        crewToRemove = this.currEventCard.getCrewToRemove();

        availableLifeformsOnboard = ship.getAllLifeforms().stream()
                .map(Lifeform::getLifeformType)
                .distinct()
                .toList();

        this.generateAvailableLifeformsWidget(
                availableLifeformsOnboard,
                selectedLifeform
        );

        // Getting the lifeform type to remove
        do {
            try {
                System.out.println();
                System.out.println("Available lifeforms to remove:");
                commandSelected = this.availableLifeforms.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedLifeform.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        // TODO: need parallel capacity list or local changes to the ship -> requires removing the ship from the updates and having a local resourceBank
        // TODO: add prevPlayerNickname field to carsState so that the locally modified ship does not accept the update (only on some specific things)

        AtomicReference<ClientCabin> selectedCabin;
        InputWidgetTUI availableCabins;
        CommandWidgetTUI command;
        int index;

        selectedCabin = new AtomicReference<>(null);
        availableCabins = new InputWidgetTUI(this.inputThread);
        availableCabins.setColumnGroupingAmount(4);
        index = 0;

        if (selectedLifeform.get().equals(LifeformType.PURPLE_ALIEN)) {
            // PURPLE_ALIEN selected
            ClientCabin cabin = ship.getPurpleAlienPosition();

            command = new CommandWidgetTUI(
                    "" + index,
                    () -> {
                        selectedCabin.set(cabin);
                    }
            );
            command.appendString("Cabin @ (row=" + (cabin.getI() + 1) + ", col=" + (cabin.getJ() + 1) + ")");
            availableCabins.addCommand(command);
        }
        else if (selectedLifeform.get().equals(LifeformType.BROWN_ALIEN)) {
            // BROWN_ALIEN selected
            ClientCabin cabin = ship.getBrownAlienPosition();

            command = new CommandWidgetTUI(
                    "" + index,
                    () -> {
                        selectedCabin.set(cabin);
                    }
            );
            command.appendString("Cabin @ (row=" + (cabin.getI() + 1) + ", col=" + (cabin.getJ() + 1) + ")");
            availableCabins.addCommand(command);
        }
        else {
            // ASTRONAUT selected
            for (ClientCabin cabin : ship.getCabinList()) {
                if (
                        (!cabin.getInhabitants().isEmpty())
                                && ship.getPurpleAlienPosition() != cabin
                                && ship.getBrownAlienPosition() != cabin
                ) {
                    command = new CommandWidgetTUI(
                            "" + index,
                            () -> {
                                selectedCabin.set(cabin);
                            }
                    );
                    command.appendString("Cabin @ (row=" + (cabin.getI() + 1) + ", col=" + (cabin.getJ() + 1) + ")");
                    availableCabins.addCommand(command);
                    index++;
                }
            }
        }

        command = new CommandWidgetTUI(
                "-1",
                () -> {}
        );
        command.appendString("Go back to menu");
        availableCabins.addCommand(command);

        availableCabins.setColumnGroupingAmount(
                availableCabins.getCommandMap().size()
        );

        // Selecting the cabins from where the chosen lifeform will be taken
        do {
            try {
                System.out.println();
                System.out.println("Select a cabin from where to remove \"" + selectedLifeform.get().toString() + "\":");
                commandSelected = availableCabins.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedLifeform.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        // Assembling all together
        lifeformPosition = new ComponentHelper<LifeformType>(
                selectedCabin.get().getI(),
                selectedCabin.get().getJ()
        ).addItem(selectedLifeform.get());

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
        AtomicReference<ItemColor> selectedItem;
        List<ItemColor> availableItemsOnboard;
        ClientShip ship;
        boolean commandSelected;

        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [getItemToBeRemoved()] ClientShip is null", ANSIColors.RED));
            return;
        }

        availableItemsOnboard = ship.getAllItems().stream()
                .map(Item::getColor)
                .distinct()
                .toList();

        if (availableItemsOnboard.isEmpty()) {
            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + "Your ship's cargo is " + PrintUtils.addColor("EMPTY", ANSIColors.RED) + "!")
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }

        selectedItem = new AtomicReference<>(null);
        itemsToBeRemoved = this.currEventCard.getItemsToBeRemoved();

        this.generateAvailableItemColorsWidget(
                availableItemsOnboard,
                selectedItem
        );

        // Getting the lifeform type to remove
        do {
            try {
                System.out.println();
                System.out.println("Available items to remove:");
                commandSelected = this.availableItemColors.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedItem.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        // TODO: need parallel capacity list or local changes to the ship -> requires removing the ship from the updates and having a local resourceBank
        // TODO: add prevPlayerNickname field to carsState so that the locally modified ship does not accept the update (only on some specific things)

        AtomicReference<ClientStorage> selectedStorage;
        InputWidgetTUI availableStorages;
        CommandWidgetTUI command;
        int index;

        availableStorages = new InputWidgetTUI(this.inputThread);
        selectedStorage = new AtomicReference<>(null);
        index = 0;

        for (ClientStorage storage : ship.getStorageList()) {
            // If the storage is not empty and has the item that the user
            // wants to discard, then add it to the selectable ones
            if (storage.availableSpace() != storage.getCapacity()) {
                List<ItemColor> currStorageItemColors = storage.getStoredItems().stream().map(Item::getColor).toList();

                if (currStorageItemColors.contains(selectedItem.get())) {
                    command = new CommandWidgetTUI(
                            "" + index,
                            () -> {
                                selectedStorage.set(storage);
                            }
                    );
                    command.appendString("Storage @ (row=" + (storage.getI() + 1) + ", col=" + (storage.getJ() + 1) + ")");
                    availableStorages.addCommand(command);
                    index++;
                }
            }
        }

        // (-1) Go back to menu
        command = new CommandWidgetTUI(
                "-1",
                () -> {}
        );
        command.appendString("Go back to menu");
        availableStorages.addCommand(command);

        availableStorages.setColumnGroupingAmount(
                availableStorages.getCommandMap().size()
        );

        // Getting the storage from where the selected item will be removed
        do {
            try {
                System.out.println();
                System.out.println("Available storages:");
                commandSelected = availableStorages.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedStorage.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            } catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        // Assembling all together
        itemPosition = new ComponentHelper<ItemColor>(
                selectedStorage.get().getI(),
                selectedStorage.get().getJ()
        ).addItem(selectedItem.get());

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
        AtomicReference<ItemColor> selectedItem;
        List<ItemColor> availableItemsToTake;
        ClientShip ship;
        boolean commandSelected;

        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [getItemToBeRemoved()] ClientShip is null", ANSIColors.RED));
            return;
        }

        selectedItem = new AtomicReference<>(null);
        itemsToBeTaken = this.currEventCard.getItemsToBeTaken();

        availableItemsToTake = this.currEventCard.getAvailableItemColors();

        this.generateAvailableItemColorsWidget(
                availableItemsToTake,
                selectedItem
        );

        // Getting the lifeform type to remove
        do {
            try {
                System.out.println("Available items to take:");
                commandSelected = this.availableItemColors.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedItem.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        // TODO: need parallel capacity list or local changes to the ship -> requires removing the ship from the updates and having a local resourceBank
        // TODO: add prevPlayerNickname field to carsState so that the locally modified ship does not accept the update (only on some specific things)

        AtomicReference<ClientStorage> selectedStorage;
        InputWidgetTUI availableStorages;
        CommandWidgetTUI command;
        int index;

        availableStorages = new InputWidgetTUI(this.inputThread);
        selectedStorage = new AtomicReference<>(null);
        index = 0;

        for (ClientStorage storage : ship.getStorageList()) {
            if (storage.availableSpace() > 0) {
                command = new CommandWidgetTUI(
                    "" + index,
                    () -> {
                        selectedStorage.set(storage);
                    }
                );
                command.appendString("Storage @ (row=" + (storage.getI() + 1) + ", col=" + (storage.getJ() + 1) + ")");
                availableStorages.addCommand(command);
                index++;
            }
        }

        if (availableStorages.getCommandMap().isEmpty()) {
            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + "Your ship's storage is " + PrintUtils.addColor("FULL", ANSIColors.RED) + "! You must free up some space before retrieving other items!")
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }

        // (-1) Go back to menu
        command = new CommandWidgetTUI(
                "-1",
                () -> {}
        );
        command.appendString("Go back to menu");
        availableStorages.addCommand(command);

        availableStorages.setColumnGroupingAmount(
                availableStorages.getCommandMap().size()
        );

        // Getting the storage from where the selected item will be removed
        do {
            try {
                System.out.println("Available storages:");
                commandSelected = availableStorages.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedStorage.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        // Assembling all together
        itemPosition = new ComponentHelper<ItemColor>(
                selectedStorage.get().getI(),
                selectedStorage.get().getJ()
        ).addItem(selectedItem.get());

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
        int chosenIndex = -1;
        String line;

        List<Integer> availablePlanetIndexes =
                ((ClientVisitPlanets) this.currEventCard)
                        .getAvailablePlanets()
                        .keySet().stream().toList();

        for (Integer planetIdx : availablePlanetIndexes) {
            availablePlanetsWidget.appendString("(" + planetIdx + ") Planet #" + planetIdx);
        }

        availablePlanetsWidget
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();

        do {
            System.out.println("Available planets to choose:");
            availablePlanetsWidget.printWidget();
            System.out.print(DEFAULT_COMMAND_PREFIX);

            try {
                line = this.inputThread.waitForInput();
                if (line == null) return;

                chosenIndex = Integer.parseInt(line);

                if (availablePlanetIndexes.contains(chosenIndex)) {
                    correctInput = true;
                    CommandWidgetTUI command;

                    // Disables the "setChosenPlanetIndex" command
                    command = this.indexedCardInputMethods.get("setChosenPlanetIndex").getValue();
                    this.indexedCardInputMethods.replace("setChosenPlanetIndex", new Pair<>(false, command));

                    // Enables the "setItemsToBeTaken" command
                    command = this.indexedCardInputMethods.get("setItemsToBeTaken").getValue();
                    this.indexedCardInputMethods.replace("setItemsToBeTaken", new Pair<>(true, command));

                    // Enables the "setItemsToBeRemoved" command
                    command = this.indexedCardInputMethods.get("setItemsToBeRemoved").getValue();
                    this.indexedCardInputMethods.replace("setItemsToBeRemoved", new Pair<>(true, command));

                    // Generates the updated command widget
                    this.generateCardRoundCommandsWidget();
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
        AtomicReference<ClientShield> selectedShield;
        InputWidgetTUI availableShields;
        CommandWidgetTUI command;
        ClientShip ship;
        boolean commandSelected;
        int i, len;

        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [getShieldToActivate()] ClientShip is null", ANSIColors.RED));
            return;
        }

        List<ClientShield> shieldsList = ship.getShieldList();
        len = shieldsList.size();

        if (len == 0) {
            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + "You don't have any shields to activate!")
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }

        // TODO: need parallel capacity list or local changes to the ship -> requires removing the ship from the updates and having a local resourceBank
        // TODO: add prevPlayerNickname field to carsState so that the locally modified ship does not accept the update (only on some specific things)

        componentHelperList = this.currEventCard.getShieldsToActivate();
        selectedShield = new AtomicReference<>(null);
        availableShields = new InputWidgetTUI(this.inputThread);
        availableShields.setColumnGroupingAmount(4);

        for (i = 0; i < len; i++) {
            ClientShield shield = shieldsList.get(i);

            command = new CommandWidgetTUI(
                    "" + i,
                    () -> {
                        selectedShield.set(shield);
                    }
            );
            command.appendString("Shield @ (row=" + (shield.getI() + 1) + ", col=" + (shield.getJ() + 1) + ")");
            availableShields.addCommand(command);
        }

        // (-1) Go back to menu
        command = new CommandWidgetTUI(
                "-1",
                () -> {}
        );
        command.appendString("Go back to menu");
        availableShields.addCommand(command);

        availableShields.setColumnGroupingAmount(
                availableShields.getCommandMap().size()
        );

        do {
            try {
                System.out.println("Available shields to activate:");
                commandSelected = availableShields.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedShield.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        componentHelper = new ComponentHelper<>(
                selectedShield.get().getI(),
                selectedShield.get().getJ()
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
        AtomicReference<ClientCannon> selectedDoubleCannon;
        InputWidgetTUI availableDoubleCannons;
        CommandWidgetTUI command;
        ClientShip ship;
        boolean commandSelected;
        int i, len;

        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [getDoubleCannonToActivate()] ClientShip is null", ANSIColors.RED));
            return;
        }

        // TODO: need parallel capacity list or local changes to the ship -> requires removing the ship from the updates and having a local resourceBank
        // TODO: add prevPlayerNickname field to carsState so that the locally modified ship does not accept the update (only on some specific things)

        List<ClientCannon> doubleCannonsList = ship.getDoubleCannons();
        len = doubleCannonsList.size();

        if (len == 0) {
            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + "You don't have any double cannons to activate!")
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }

        componentHelperList = this.currEventCard.getDoubleCannonsToActivate();
        selectedDoubleCannon = new AtomicReference<>(null);
        availableDoubleCannons = new InputWidgetTUI(this.inputThread);
        availableDoubleCannons.setColumnGroupingAmount(4);

        for (i = 0; i < len; i++) {
            ClientCannon doubleCannon = doubleCannonsList.get(i);

            command = new CommandWidgetTUI(
                    "" + i,
                    () -> {
                        selectedDoubleCannon.set(doubleCannon);
                    }
            );
            command.appendString("Double Cannon @ (row=" + (doubleCannon.getI() + 1) + ", col=" + (doubleCannon.getJ() + 1) + ")");
            availableDoubleCannons.addCommand(command);
        }

        // (-1) Go back to menu
        command = new CommandWidgetTUI(
                "-1",
                () -> {}
        );
        command.appendString("Go back to menu");
        availableDoubleCannons.addCommand(command);

        availableDoubleCannons.setColumnGroupingAmount(
                availableDoubleCannons.getCommandMap().size()
        );

        do {
            try {
                System.out.println("Available double cannons to activate:");
                commandSelected = availableDoubleCannons.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (commandSelected) {
                    // If the user selected "(-1) Go back to menu", then return
                    if (selectedDoubleCannon.get() == null) return;
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);

        componentHelper = new ComponentHelper<>(
                selectedDoubleCannon.get().getI(),
                selectedDoubleCannon.get().getJ()
        );

        componentHelperList.add(componentHelper);
        this.currEventCard.setDoubleCannonsToActivate(componentHelperList);
    }

    /**
     * Sets the amount of double engines the player wants to activate.
     * <br>
     * NOTE: If the player's chosen amount exceeds the actual amount of double engines, then
     * server-side this is equivalent to activating all double engines (input saturation)
     */
    public void getDoubleEnginesToActivate() {
        int doubleEnginesToActivate;
        boolean correctInput;
        ClientShip ship;
        String line;

        ship = this.model.getShipOfPlayer(this.model.getNickname()).orElse(null);

        if (ship == null) {
            System.out.println(PrintUtils.addColor("[ERROR] [getDoubleEnginesToActivate()] ClientShip is null", ANSIColors.RED));
            return;
        }

        if (ship.getDoubleEngines().isEmpty()) {
            new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + "You don't have any double engines to activate!")
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }

        doubleEnginesToActivate = 0;
        correctInput = false;

        // Verify that the selected component is an engine
        do {
            try {
                System.out.print("Insert amount of double engines to activate (-1 to go back): ");
                line = this.inputThread.waitForInput();

                if (line == null) return;

                doubleEnginesToActivate = Integer.parseInt(line);

                if (doubleEnginesToActivate >= 0) {
                    correctInput = true;
                }
                else if (doubleEnginesToActivate == -1) {
                    // User wanted to quit
                    return;
                }
                else {
                    System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Amount must be greater than or equal to 0.", ANSIColors.RED));
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
                    // Ensuring all components are present
                    ship.generateComponentSubLists();

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

        // (0) - Visualize ship
        command = new CommandWidgetTUI(
                "0",
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
        this.getCurrEventCard();
        this.currEventCardWidget = this.currEventCard.generateWidget();
    }

    /**
     * Initializes the widget containing all the
     * available ships that a player can look at
     */
    private void generateOtherPlayerShipCommandsWidget() {
        CommandWidgetTUI command;
        int i, len;

        this.otherPlayerShipCommandsWidget = new InputWidgetTUI(this.inputThread);

        List<String> allNicknames = this.model.getAllPlayersNicknames();
        len = allNicknames.size();

        for (i = 0; i < len; i++) {
            int finalI = i;

            command = new CommandWidgetTUI(
                    "" + i,
                    () -> {
                        String line;

                        // Obtaining the chosen ship (if present)
                        this.model.getShipOfPlayer(this.model.getAllPlayersNicknames().get(finalI))
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
                                    .appendString(COMPUTER_MSG_TAG + "You're now viewing \"" + this.model.getAllPlayersNicknames().get(finalI) + "\"'s ship")
                                    .addPadding(1, 1, 1, 1)
                                    .wrapWidgetWithBorder()
                                    .printWidget();

                            try {
                                System.out.print("Press any key and then press [ENTER] to go back...");
                                line = this.inputThread.waitForInput();

                                // A forced interrupt arrived
                                if (line == null) return;
                            } catch (InterruptedException e) {
                                // A forced interrupt arrived
                                return;
                            }

                            this.otherPlayerShipWidget = null;
                        }
                    }
            );

            String option = "Show ship of \"" + allNicknames.get(i) + "\"";

            if (allNicknames.get(i).equals(this.model.getNickname())) {
                option += SPACE;
                option += PrintUtils.addColor(
                        "(YOU)",
                        this.model.getAllClientPlayers().get(allNicknames.get(i))
                                .getColor()
                                .getColorString()
                );
            }

            command.appendString(option);
            this.otherPlayerShipCommandsWidget.addCommand(command);
        }

        // (-1) - Go back
        command = new CommandWidgetTUI(
                "-1",
                this::getCardRoundCommand
        );
        command.appendString("Go back");
        this.otherPlayerShipCommandsWidget.addCommand(command);

        this.otherPlayerShipCommandsWidget.setColumnGroupingAmount(
                this.otherPlayerShipCommandsWidget.getCommandMap().size()
        );
    }

    /**
     * Generates a widget that shows the current player if it's
     * his turn or, in the other case, whose turn it is to play.
     */
    private void generatePlayerTurnWidget() {
        boolean isEliminated;

        this.playerTurnWidget = new WidgetTUI();

        isEliminated = this.model.getClientBoard().getEliminatedPlayers().stream()
                .map(ClientPlayer::getNickname)
                .toList()
                .contains(this.model.getNickname());

        if (isEliminated) {
            this.playerTurnWidget
                    .appendString(COMPUTER_MSG_TAG + "You've been " + PrintUtils.addColor("ELIMINATED", ANSIColors.BRIGHT_RED));
        }
        else {
            if (this.currPlayerNickname.equals(this.model.getNickname())) {
                this.playerTurnWidget
                        .appendString(COMPUTER_MSG_TAG + "It's " + PrintUtils.addColor("YOUR TURN", ANSIColors.BRIGHT_GREEN) + " to play");
            }
            else {
                this.playerTurnWidget
                        .appendString(COMPUTER_MSG_TAG + "It's " + PrintUtils.addColor("NOT YOUR TURN", ANSIColors.BRIGHT_RED) + " to play")
                        .appendString("Current player is: " + PrintUtils.addColor(this.currPlayerNickname, this.model.getAllClientPlayers().get(this.currPlayerNickname).getColor().getColorString()));
            }
        }

        this.playerTurnWidget
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder();
    }

    /**
     * Sets the currEventCard parameter to the one communicated
     * by the server through the CardRoundDTO
     */
    private void getCurrEventCard() {
        int cardId;

        cardId = this.currEventCardState.getCardID();

        for (ClientEventCard card : this.model.getClientEventCards()) {
            if (card.getCardID() == cardId) {
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
        WidgetTUI currCardAndPlayerActions;

        // Updating all widgets before using them
        this.generateCurrEventCardWidget();
        this.generateShipWidgets();
        this.boardWidget = this.model.getClientBoard().generateWidget();

        this.playerNameWidget.printWidget();

        // Show the current actions only to the
        // player that is currently playing
        if (this.currPlayerNickname.equals(this.model.getNickname())) {
            this.generatePlayerActionsRecapWidget();

            currCardAndPlayerActions = WidgetTUI.composeTwoWidgetsHorizontally(
                    this.currEventCardWidget,
                    this.playerActionsRecapWidget
            );
        }
        else {
            // Otherwise, a player that is not playing will only
            // see the current event card (since he's not playing)
            currCardAndPlayerActions = this.currEventCardWidget;
        }

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
                        currCardAndPlayerActions
                )
        ).printWidget();

        this.playerTurnWidget.printWidget();
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

                if (!commandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            } catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);
    }

    /**
     * Asks the player which other player's ship he wants to view
     * and prints it to terminal
     */
    private void getOtherShipCommand() {
        boolean commandSelected;

        do {
            try {
                System.out.println();
                System.out.println("Select a ship to view:");
                commandSelected = this.otherPlayerShipCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (!commandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            } catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }
        }
        while (!commandSelected);
    }

    /**
     * @return TRUE if the current player answers YES to the
     * given YES/NO question, FALSE otherwise
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
                    // System.out.println("onSuccess");
                    this.ctx = null;

                    // TODO: Implement onSuccess (if it needs to do something)
                },
                () -> {
                    // System.out.println("onError");

                    System.out.println(PrintUtils.addColor("[ERROR] There was an error while playing the card. Please try again.", ANSIColors.RED));
                    this.ctx = null;
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
        this.forceStopScreen();

        // Storing the current event card's card state and the currently playing player
        this.currEventCardState = cardRound.getCardInfo();
        this.currPlayerNickname = cardRound.getCardInfo().getPlayerNickname();

        // Updating this player's ship widget and
        // getting the current event card
        this.getCurrEventCard();
        this.generateShipWidgets();
        this.generatePlayerTurnWidget();

        // Updating the current event card
        this.currEventCard.updateCard(cardRound.getCardInfo());

        this.generateIndexedCardInputMethodsMap();

        // Available commands will be shown only to the
        // player that the card expects to see
        if (cardRound.getCardInfo().getPlayerNickname().equals(this.model.getNickname())) {
            // Filtering only the commands that the current event card is enabling
            this.currEventCard.setAvailableCommands(this.indexedCardInputMethods);
        }

        this.generateCardRoundCommandsWidget();

        // Prints the card round TUI and asks the user for a command
        this.getCardRoundCommand();
    }
}