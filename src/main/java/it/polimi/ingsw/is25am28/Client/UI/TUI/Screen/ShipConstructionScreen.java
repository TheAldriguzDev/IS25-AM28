package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;
import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

public class ShipConstructionScreen extends Screen {
    // Default component selection matrix (row, col) dimensions
    public static final int DEFAULT_COMPONENT_ROWS = 8;
    public static final int DEFAULT_COMPONENT_COLS = 19;

    public static final int COMPONENT_SELECTION_COMMAND_GROUPING_FACTOR = 2;
    public static final int SHIP_CONSTRUCTION_COMMAND_GROUPING_FACTOR = 2;

    private WidgetTUI componentSelectionWidget;
    private WidgetTUI reservedComponentsWidget;
    private WidgetTUI selectedComponentWidget;
    private WidgetTUI currPlayerShipWidget;
    private WidgetTUI cardSubdeckWidget;
    private WidgetTUI otherPlayerShipWidget;
    private WidgetTUI playersFinishedShipWidget;
    private WidgetTUI coveredComponentWidget;
    private WidgetTUI emptyComponentWidget;

    private InputWidgetTUI componentSelectionCommandsWidget;
    private InputWidgetTUI shipConstructionCommandsWidget;
    private InputWidgetTUI cardSubdeckCommandsWidget;
    private InputWidgetTUI otherPlayerShipCommandsWidget;

    private ClientComponent selectedComponent;
    private ClientShip currPlayerShip;

    private final AtomicInteger otherShipId;
    private final AtomicInteger selectedSubdeckId;
    private boolean isSelectedTileReserved;

    // Constructor
    public ShipConstructionScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Retrieving this player's ship
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> { this.currPlayerShip = ship; }
        );

        // Other initializations
        this.selectedComponent = null;
        this.selectedComponentWidget = null;
        this.isSelectedTileReserved = false;
        this.otherShipId = new AtomicInteger(0);
        this.selectedSubdeckId = new AtomicInteger(0);

        // Creating the covered and empty component widgets
        this.generateCoveredComponentWidget();
        this.generateEmptyComponentWidget();

        // Generate all the widgets with the commands to
        // display to the current player
        this.generateComponentSelectionCommands();
        this.generateShipConstructionCommands();
        this.generateOtherPlayerShipCommandsWidget(this.otherShipId);
        this.generateCardSubdeckCommandsWidget(this.selectedSubdeckId);
    }

    /**
     * Initializes the widget containing all the commands
     * available during the component selection menu
     */
    private void generateComponentSelectionCommands() {
        CommandWidgetTUI command;

        this.componentSelectionCommandsWidget = new InputWidgetTUI(this.inputThread);

        if (!this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
            // (1) - Select tile
            command = new CommandWidgetTUI(
                "1",
                () -> {
                    try {
                        this.selectTile();
                    }
                    catch (Exception e) {
                        clearTerminal();

                        new WidgetTUI()
                            .appendString(
                                PrintUtils.addColor(
                                    "ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.",
                                    ANSIColors.RED
                                )
                            )
                            .addPadding(0, 1, 0, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();

                        this.getComponentSelectionCommand();
                    }
                }
            );
            command.appendString("Select tile");
            this.componentSelectionCommandsWidget.addCommand(command);

            // (2) - Select reserved tile
            command = new CommandWidgetTUI(
                "2",
                () -> {
                    try {
                        this.selectReservedTile();
                    }
                    catch (IllegalArgumentException e) {
                        clearTerminal();

                        new WidgetTUI()
                            .appendString(COMPUTER_MSG_TAG + e.getMessage())
                            .addPadding(0, 1, 0, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();

                        this.getComponentSelectionCommand();
                    }
                }
            );
            command.appendString("Select reserved tile");
            this.componentSelectionCommandsWidget.addCommand(command);

            // (3) - Finish ship
            command = new CommandWidgetTUI(
                "3",
                () -> {
                    boolean shipSendConfirmation;

                    try {
                        shipSendConfirmation = this.getShipFinishedConfirmation();
                    }
                    catch (InterruptedException e) {
                        // A forced interrupt arrived
                        return;
                    }

                    if (shipSendConfirmation) {
                        try {
                            // Sending the ship to the server
                            this.sendFinishedShip();
                        }
                        catch (Exception e) {
                            System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                        }
                    }
                    else {
                        // Otherwise, go back to the component selection screen
                        clearTerminal();
                        this.getComponentSelectionCommand();
                    }
                }
            );
            command.appendString("Finish ship");
            this.componentSelectionCommandsWidget.addCommand(command);

            // (4) - Fast build
            command = new CommandWidgetTUI(
                    "4",
                    () -> {
                        // TODO: Implement new feature


                        clearTerminal();

                        new WidgetTUI()
                                .appendString(
                                    PrintUtils.addColor(
                                        "WIP",
                                        ANSIColors.BRIGHT_CYAN
                                    )
                                )
                                .addPadding(1, 8, 1, 8)
                                .wrapWidgetWithBorder()
                                .printWidget();

                        this.getComponentSelectionCommand();
                    }
            );
            command.appendString("Fast build");
            this.componentSelectionCommandsWidget.addCommand(command);
        }

        // (5) - Flip timer
        command = new CommandWidgetTUI(
            "5",
            () -> {
                try {
                    this.flipTimer();
                }
                catch (Exception e) {
                    clearTerminal();

                    new WidgetTUI()
                        .appendString(
                            PrintUtils.addColor(
                                "ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.",
                                ANSIColors.RED
                            )
                        )
                        .addPadding(0, 1, 0, 1)
                        .wrapWidgetWithBorder()
                        .printWidget();

                    this.getComponentSelectionCommand();
                }
            }
        );
        command.appendString("Flip timer");
        this.componentSelectionCommandsWidget.addCommand(command);

        // (6) - Visualize subdeck
        command = new CommandWidgetTUI(
            "6",
            () -> {
                try {
                    // Getting the player's chosen subdeck and
                    // prints it directly to terminal
                    this.getCardSubdeckCommand();
                }
                catch (Exception e) {
                    clearTerminal();

                    new WidgetTUI()
                        .appendString(
                            PrintUtils.addColor(
                                "ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.",
                                ANSIColors.RED
                            )
                        )
                        .addPadding(0, 1, 0, 1)
                        .wrapWidgetWithBorder()
                        .printWidget();

                    this.getComponentSelectionCommand();
                }
            }
        );
        command.appendString("Visualize subdeck");
        this.componentSelectionCommandsWidget.addCommand(command);

        // (7) - Visualize ships
        command = new CommandWidgetTUI(
            "7",
            () -> {
                this.getOtherShipCommand();
                clearTerminal();
                this.getComponentSelectionCommand();
            }
        );
        command.appendString("Visualize ships");
        this.componentSelectionCommandsWidget.addCommand(command);

        this.componentSelectionCommandsWidget.setColumnGroupingAmount(
            COMPONENT_SELECTION_COMMAND_GROUPING_FACTOR
        );
    }

    /**
     * Initializes the widget containing all the commands
     * available during the ship construction menu
     */
    private void generateShipConstructionCommands() {
        CommandWidgetTUI command;

        this.shipConstructionCommandsWidget = new InputWidgetTUI(this.inputThread);

        if (!this.isSelectedTileReserved) {
            // (1) - Deselect tile
            command = new CommandWidgetTUI(
                "1",
                () -> {
                    try {
                        // Deselects the component that is currently taken by this user
                        this.deselectTile();
                    }
                    catch (Exception e) {
                        clearTerminal();

                        new WidgetTUI()
                            .appendString(
                                PrintUtils.addColor(
                                    "ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.",
                                    ANSIColors.RED
                                )
                            )
                            .addPadding(0, 1, 0, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();

                        this.getShipConstructionCommand();
                    }
                }
            );
            command.appendString("Deselect tile");
            this.shipConstructionCommandsWidget.addCommand(command);
        }

        // (2) - Reserve tile
        command = new CommandWidgetTUI(
            "2",
            () -> {
                List<ClientComponent> reservedComponents = this.model.getState().getReservedComponents();

                // Puts the selected component in the reserved tiles
                // If it can't, then it'll ask the user to do something else
                if (reservedComponents.size() < 2) {
                    try {
                        // Reserve the component that is currently taken by this user
                        this.reserveTile();
                    }
                    catch (Exception e) {
                        clearTerminal();

                        new WidgetTUI()
                            .appendString(
                                PrintUtils.addColor(
                                    "ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.",
                                    ANSIColors.RED
                                )
                            )
                            .addPadding(0, 1, 0, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();

                        this.getShipConstructionCommand();
                    }
                }
                else {
                    // Otherwise, it means that the user already has 2 reserved tiles,
                    // therefore he cannot store any additional ones
                    clearTerminal();

                    new WidgetTUI()
                        .appendString(
                            COMPUTER_MSG_TAG
                            + PrintUtils.addColor(
                                "ERROR: You already have 2 (max) reserved tiles! Use them before storing others!",
                                ANSIColors.RED
                            )
                        )
                        .wrapWidgetWithBorder()
                        .printWidget();

                    this.getShipConstructionCommand();
                }
            }
        );
        command.appendString("Reserve tile");
        this.shipConstructionCommandsWidget.addCommand(command);

        // (3) - Place selected tile
        command = new CommandWidgetTUI(
            "3",
            () -> {
                try {
                    // Puts the currently selected tile in the ship and in the
                    // ClientShipConstructionState when the ship will be sent to
                    // the server for validation
                    this.placeSelectedTile();
                }
                catch (Exception e) {
                    clearTerminal();

                    new WidgetTUI()
                        .appendString(
                            PrintUtils.addColor(
                                "ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.",
                                ANSIColors.RED
                            )
                        )
                        .addPadding(0, 1, 0, 1)
                        .wrapWidgetWithBorder()
                        .printWidget();

                    this.getShipConstructionCommand();
                }
            }
        );
        command.appendString("Place selected tile");
        this.shipConstructionCommandsWidget.addCommand(command);

        // (4) - Rotate right
        command = new CommandWidgetTUI(
            "4",
            () -> {
                this.selectedComponent.rotateRight();

                // NOTE: Default behavior after rotation is to go
                //       back to the ship construction screen
                clearTerminal();
                this.getShipConstructionCommand();
            }
        );
        command.appendString("Rotate right");
        this.shipConstructionCommandsWidget.addCommand(command);

        // (5) - Rotate left
        command = new CommandWidgetTUI(
            "5",
            () -> {
                // (5) - Rotate left
                this.selectedComponent.rotateLeft();

                // NOTE: Default behavior after rotation is to go
                //       back to the ship construction screen
                clearTerminal();
                this.getShipConstructionCommand();
            }
        );
        command.appendString("Rotate left");
        this.shipConstructionCommandsWidget.addCommand(command);

        this.shipConstructionCommandsWidget.setColumnGroupingAmount(
            SHIP_CONSTRUCTION_COMMAND_GROUPING_FACTOR
        );
    }

    /**
     * Initializes the widget containing all the available
     * subdecks that a player can choose from
     */
    private void generateCardSubdeckCommandsWidget(AtomicInteger selectedSubdeck) {
        CommandWidgetTUI command;

        if (selectedSubdeck == null) {
            throw new RuntimeException("ERROR: Given atomic reference \"selectedSubdeck\" cannot be null (cannot store the player's choice)");
        }

        this.cardSubdeckCommandsWidget = new InputWidgetTUI(this.inputThread);

        // (1) - Select deck #1
        command = new CommandWidgetTUI(
            "1",
            () -> {
                selectedSubdeck.set(1);
            }
        );
        command.appendString("Select deck #1");
        this.cardSubdeckCommandsWidget.addCommand(command);

        // (2) - Select deck #2
        command = new CommandWidgetTUI(
            "2",
            () -> {
                selectedSubdeck.set(2);
            }
        );
        command.appendString("Select deck #2");
        this.cardSubdeckCommandsWidget.addCommand(command);

        // (3) - Select deck #3
        command = new CommandWidgetTUI(
            "3",
            () -> {
                selectedSubdeck.set(3);
            }
        );
        command.appendString("Select deck #3");
        this.cardSubdeckCommandsWidget.addCommand(command);

        // (-1) - Go back
        command = new CommandWidgetTUI(
            "-1",
            () -> {
                // Nothing
            }
        );
        command.appendString("Go back");
        this.cardSubdeckCommandsWidget.addCommand(command);

        this.cardSubdeckCommandsWidget.setColumnGroupingAmount(
            this.cardSubdeckCommandsWidget.getCommandMap().size()
        );
    }

    /**
     * Initializes the widget containing all the
     * available ships that a player can look at
     */
    private void generateOtherPlayerShipCommandsWidget(AtomicInteger otherShipId) {
        CommandWidgetTUI command;
        int i, len;

        if (otherShipId == null) {
            throw new RuntimeException("ERROR: Given atomic reference \"otherShipId\" cannot be null (cannot store the player's choice)");
        }

        this.otherPlayerShipCommandsWidget = new InputWidgetTUI(this.inputThread);

        List<String> allNicknames = this.model.getAllPlayersNicknames();
        len = allNicknames.size();

        for (i = 0; i < len; i++) {
            StringBuilder s = new StringBuilder("Show ship of \"" + allNicknames.get(i) + "\"");

            if (allNicknames.get(i).equals(this.model.getNickname())) {
                s.append(SPACE);
                s.append(
                    PrintUtils.addColor(
                        "(YOU)",
                        this.model.getAllClientPlayers().get(allNicknames.get(i))
                                .getColor()
                                .getColorString()
                    )
                );
            }

            int finalI = i;

            // (n) - Show ship of n-th player
            command = new CommandWidgetTUI(
                Integer.toString(i),
                () -> {
                    otherShipId.set(finalI);
                }
            );
            command.appendString(s.toString());
            this.otherPlayerShipCommandsWidget.addCommand(command);
        }

        // (-1) - Go back
        command = new CommandWidgetTUI(
            "-1",
            () -> {
                // Nothing
            }
        );
        command.appendString("Go back");
        this.otherPlayerShipCommandsWidget.addCommand(command);

        this.otherPlayerShipCommandsWidget.setColumnGroupingAmount(
            this.otherPlayerShipCommandsWidget.getCommandMap().size()
        );
    }

    /**
     * Generates a widget that will act as the back side of each component when covered
     */
    private void generateCoveredComponentWidget() {
        this.coveredComponentWidget = new WidgetTUI();

        this.coveredComponentWidget.setHeight(3);
        this.coveredComponentWidget.setWidth(11);
        this.coveredComponentWidget.wrapWidgetWithBorder();
    }

    /**
     * Generates a widget that will act as a blank space when the
     * corresponding widget is flagged as non-visible
     */
    private void generateEmptyComponentWidget() {
        this.emptyComponentWidget = new WidgetTUI();
        this.emptyComponentWidget.setHeight(5);
        this.emptyComponentWidget.setWidth(13);
    }

    /**
     * Generates the matrix where a player can choose a component
     * to either instantly place or reserve for later
     */
    private void generateComponentSelectionWidget() {
        this.componentSelectionWidget = new WidgetTUI();

        List<String> allRows = new ArrayList<>();
        List<List<String>> row = new ArrayList<>();
        ClientComponent currComponent;
        List<ClientComponent> clientComponents = this.model.getState().getConstructionShipComponents();

        int availableComponents = clientComponents.size();
        int iteratedComponents = 0;

        for (int i = 0; i < DEFAULT_COMPONENT_ROWS; i++) {
            for (int j = 0; j < DEFAULT_COMPONENT_COLS; j++) {
                int index = (i * DEFAULT_COMPONENT_COLS) + j;
                currComponent = clientComponents.get(index);

                // Only adding something to print if the component is visible
                if (currComponent.isVisible()) {
                    List<String> screen = new ArrayList<>();

                    // Adding the current component ID at the top of the screen
                    screen.add("(" + index + ")");

                    if (currComponent.isFlipped()) {
                        screen.addAll(currComponent.generateWidget().getScreen());
                    }
                    else {
                        screen.addAll(this.coveredComponentWidget.getScreen());
                    }

                    row.add(WidgetTUI.fillScreenWithSpaces(screen));
                }
                else {
                    row.add(this.emptyComponentWidget.getScreen());
                }

                iteratedComponents++;

                if (iteratedComponents == availableComponents) {
                    break;
                }
            }

            if (!row.isEmpty()) {
                // Composing the current component widget row
                allRows.addAll(WidgetTUI.composeScreensHorizontally(row));
                row = new ArrayList<>();
            }

            if (iteratedComponents == availableComponents) {
                break;
            }
        }

        // Composing all rows into the final single matrix
        this.componentSelectionWidget.setScreen(allRows);
        this.componentSelectionWidget.wrapWidgetWithBorder();
    }

    /**
     * Generates the reserved component widget and the frame around it
     */
    private void generateReservedComponentsWidget() {
        this.reservedComponentsWidget = new WidgetTUI();
        int i, emptySlots;

        // Adding a title
        this.reservedComponentsWidget.appendString("[RESERVED COMPONENTS]");

        // Adding each reserved component in the list
        int reservedComponentAmount = this.model.getState().getReservedComponents().size();
        for (i = 0; i < reservedComponentAmount; i++) {
            // Adding a single space just for padding
            this.reservedComponentsWidget.appendString(SPACE);
            this.reservedComponentsWidget.appendString("[SLOT " + (i + 1) + "]");
            this.reservedComponentsWidget.appendScreen(this.model.getState().getReservedComponents().get(i).generateWidget().getScreen());
        }

        // Substituting empty slots with placeholder widgets
        emptySlots = 2 - reservedComponentAmount;
        while (emptySlots > 0) {
            // Adding a single space just for padding
            this.reservedComponentsWidget.appendString(SPACE);
            this.reservedComponentsWidget.appendString("[SLOT " + (i + 1) + "]");
            this.reservedComponentsWidget.appendScreen(this.coveredComponentWidget.getScreen());
            emptySlots--;
            i++;
        }

        // Finally, add some more padding, then center the content and finally wrap it all
        this.reservedComponentsWidget.addPadding(0, 2, 0, 2);
        this.reservedComponentsWidget.centerWidgetScreen();
        this.reservedComponentsWidget.wrapWidgetWithBorder();
    }

    /**
     * Generates the selected component's widget and the frame around it
     */
    private void generateSelectedComponentWidget() {
        if (this.selectedComponent != null) {
            this.selectedComponentWidget = new WidgetTUI();

            // Adding a title and some padding at the bottom
            this.selectedComponentWidget.appendString("[SELECTED COMPONENT]");
            this.selectedComponentWidget.addPadding(0, 0, 1, 0);

            // Adding the selected component's generated widget screen
            this.selectedComponentWidget.appendScreen(this.selectedComponent.generateWidget().getScreen());

            // Centering the screen, adding some padding and then wrapping the final widget
            this.selectedComponentWidget.centerWidgetScreen();
            this.selectedComponentWidget.addPadding(0, 2, 1, 3);
            this.selectedComponentWidget.wrapWidgetWithBorder();
        }
    }

    /**
     * Generates the widget that contains all the given subdeck cards' widgets
     */
    private void generateCardSubdeckWidget(List<ClientEventCard> selectedSubdeck) {
        // Only generate the widget if the subdeck was chosen
        if (selectedSubdeck != null && !selectedSubdeck.isEmpty()) {
            WidgetTUI tmpCardWidget;

            // Initializations
            this.cardSubdeckWidget = new WidgetTUI();
            tmpCardWidget = new WidgetTUI();

            for (ClientEventCard card : selectedSubdeck) {
                tmpCardWidget = WidgetTUI.fillScreenWithSpaces(
                    WidgetTUI.composeTwoWidgetsHorizontally(
                        tmpCardWidget,
                        card.generateWidget().addPadding(0, 1, 0, 0)
                    )
                );
            }

            // Adding a centered title
            this.cardSubdeckWidget.appendString("[SELECTED SUBDECK]");
            this.cardSubdeckWidget.setWidth(tmpCardWidget.getWidth());
            this.cardSubdeckWidget.centerWidgetScreen();

            // Adding the widget screen of all the cards
            // that belong to the selected subdeck
            this.cardSubdeckWidget.appendScreen(tmpCardWidget.getScreen());
            this.cardSubdeckWidget.addPadding(0, 0, 0, 1);
            this.cardSubdeckWidget.wrapWidgetWithBorder();
        }
    }

    /**
     * Generates the widget containing all the players that
     * have finished building and already sent their ships.
     */
    private void generatePlayersFinishedShipWidget() {
        List<ClientPlayer> playersThatFinished = new ArrayList<>();

        for (ClientPlayer player : this.model.getAllClientPlayers().values()) {
            if (this.model.getState().getPlayerFinishedBuildingShip(player.getNickname())) {
                playersThatFinished.add(player);
            }
        }

        if (!playersThatFinished.isEmpty()) {
            this.playersFinishedShipWidget =
                    new WidgetTUI()
                            .appendString("[PLAYERS THAT FINISHED]")
                            .addPadding(0, 0, 1, 0);

            for (ClientPlayer player : playersThatFinished) {
                    this.playersFinishedShipWidget
                            .appendString(
                                    PrintUtils.addColor(
                                            player.getNickname(),
                                            player.getColor().getColorString()
                                    )
                            );
            }

            this.playersFinishedShipWidget
                    .centerWidgetScreen()
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder();
        }
    }

    /**
     * @return A widget that is the result of the composition of the
     *         widgets that make up the component selection screen
     */
    private WidgetTUI composeComponentSelectionWidgets() {
        // Ensuring each widget is updated
        this.generateReservedComponentsWidget();
        this.generateComponentSelectionWidget();
        this.generatePlayersFinishedShipWidget();

        return WidgetTUI.composeTwoWidgetsHorizontally(
                WidgetTUI.composeTwoWidgetsVertically(
                        this.reservedComponentsWidget,
                        this.playersFinishedShipWidget
                )
                .addPadding(0, 1, 0, 0),
                this.componentSelectionWidget
        );
    }

    /**
     * @return A widget that is the result of the composition of
     *         all widgets that comprise the ship construction screen
     */
    private WidgetTUI composeShipConstructionWidgets() {
        // Ensuring each widget is updated
        this.generateSelectedComponentWidget();
        this.generateReservedComponentsWidget();
        this.generatePlayersFinishedShipWidget();
        this.currPlayerShipWidget = this.currPlayerShip.getShipGridWidget();

        return WidgetTUI.composeTwoWidgetsHorizontally(
            WidgetTUI.composeTwoWidgetsVertically(
                    this.selectedComponentWidget,
                    this.reservedComponentsWidget
            ).addPadding(0, 1, 0, 0).centerWidgetScreen(),
            WidgetTUI.composeTwoWidgetsHorizontally(
                    this.currPlayerShipWidget,
                    this.playersFinishedShipWidget
            )
        );
    }

    /**
     * Asks the player to choose from one of the available
     * commands in the component selection screen
     */
    private void getComponentSelectionCommand() {
        boolean commandExecuted;

        do {
            if (this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
                this.generateComponentSelectionCommands();

                new WidgetTUI()
                        .appendString(COMPUTER_MSG_TAG + "Your ship was sent!")
                        .appendString(COMPUTER_MSG_TAG + "Wait until either all other players have finished or the timer runs out!")
                        .addPadding(0, 1, 0, 1)
                        .wrapWidgetWithBorder()
                        .printWidget();
            }
            else {
                // Show all the available commands
                this.composeComponentSelectionWidgets().printWidget();
            }

            // If it's not null, it means that it's available to be flipped
            if (this.model.getTimerDTO() != null) {
                if (this.model.getTimerDTO().getIsServerAction()) {
                    new WidgetTUI()
                            .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor("Hourglass can now be flipped!", ANSIColors.BRIGHT_MAGENTA))
                            .addPadding(1, 1, 1, 1)
                            .wrapWidgetWithBorder()
                            .printWidget();
                }
            }

            System.out.println();
            System.out.println("Available commands:");

            try {
                commandExecuted = this.componentSelectionCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);
            }
            catch (InterruptedException e) {
                return;
            }

            if (!commandExecuted) {
                clearTerminal();

                new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + UNKNOWN_COMMAND_ERROR)
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
            }
        }
        while (!commandExecuted);
    }

    /**
     * Stays in the menu until a valid command was given
     */
    private void getShipConstructionCommand() {
        boolean commandExecuted;

        do {
            // Show the ship construction screen
            this.composeShipConstructionWidgets().printWidget();

            System.out.println();
            System.out.println("Available ship construction commands:");

            try {
                commandExecuted = this.shipConstructionCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);
            }
            catch (InterruptedException e) {
                return;
            }

            if (!commandExecuted) {
                clearTerminal();

                new WidgetTUI()
                    .appendString(COMPUTER_MSG_TAG + UNKNOWN_COMMAND_ERROR)
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
            }
        }
        while (!commandExecuted);
    }

    /**
     * Books a subdeck to observe and prints it
     * to the user that requests it
     */
    private void getCardSubdeckCommand() throws Exception {
        boolean commandExecuted;
        int subdeckSize;

        this.selectedSubdeckId.set(-1);
        subdeckSize = this.model.getClientEventCards().size() / 4;

        do {
            System.out.println();
            System.out.println("Choose a subdeck to view:");

            try {
                commandExecuted = this.cardSubdeckCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

                if (!commandExecuted) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                    continue;
                }

                if (this.selectedSubdeckId.get() == -1) {
                    // Go back to the component selection screen
                    clearTerminal();
                    this.getComponentSelectionCommand();
                    return;
                }

                if (this.model.getState().isSubdeckSelected(this.selectedSubdeckId.get() - 1)) {
                    System.out.println(PrintUtils.addColor("[ERROR] Given subdeck is currently being viewed by another player.", ANSIColors.RED));
                }
            }
            catch (InterruptedException e) {
                return;
            }
        }
        while (!commandExecuted);

        int subdeckIndex = this.selectedSubdeckId.get() - 1;
        int start = (subdeckIndex * subdeckSize);
        int end = (start + subdeckSize);

        this.ctx = new CommandCTX(
            "selectDeselectSubdeck",
            () -> {
                // When the server gives the OK to lock the subdeck, then
                // proceed to generate and show the corresponding widget
                this.generateCardSubdeckWidget(
                    this.model.getClientEventCards().subList(start, end)
                );

                clearTerminal();
                this.cardSubdeckWidget.printWidget();

                try {
                    this.deselectSubdeck(subdeckIndex);
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] Couldn't deselect subdeck #" + (subdeckIndex + 1), ANSIColors.RED));
                }
            },
            () -> {
                clearTerminal();

                // Show an error if the selected subdeck is
                // currently in the hands of another player
                new WidgetTUI()
                    .appendString(
                        COMPUTER_MSG_TAG
                        + PrintUtils.addColor(
                            "[ERROR] Selected deck #" + (subdeckIndex + 1) + " is currently observed by another player. You must wait.",
                            ANSIColors.RED
                        )
                    )
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();

                // Go back to the component selection screen
                this.getComponentSelectionCommand();
            }
        );

        this.client.selectDeselectSubdeck(
            this.model.getNickname(),
            subdeckIndex,
            true
        );
    }

    /**
     * Asks the player which other player's ship he wants to view
     * and prints it to terminal
     */
    private void getOtherShipCommand() {
        boolean commandExecuted;

        this.otherShipId.set(-1);

        do {
            System.out.println();
            System.out.println("Available ships to view:");

            try {
                commandExecuted = this.otherPlayerShipCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }

            if (!commandExecuted) {
                System.out.println(UNKNOWN_COMMAND_ERROR);
                continue;
            }

            if (this.otherShipId.get() == -1) {
                // Go back was selected
                return;
            }
        }
        while (!commandExecuted);

        // Obtaining the chosen ship (if present)
        this.model.getShipOfPlayer(this.model.getAllPlayersNicknames().get(this.otherShipId.get()))
            .ifPresent(
                (ClientShip ship) -> {
                    this.otherPlayerShipWidget = ship.getShipGridWidget();
                }
            );

        // Printing the player's chosen ship (if present)
        if (this.otherPlayerShipWidget != null) {
            clearTerminal();

            this.otherPlayerShipWidget.printWidget();
            this.otherPlayerShipWidget = null;

            new WidgetTUI()
                .appendString(
                    COMPUTER_MSG_TAG
                    + "You're now viewing \"" + this.model.getAllPlayersNicknames().get(this.otherShipId.get()) + "\"'s ship"
                )
                .addPadding(1, 1, 1, 1)
                .wrapWidgetWithBorder()
                .printWidget();

            try {
                System.out.print("Press any key and then press [ENTER] to go back...");
                this.inputThread.waitForInput();
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
            }
        }
    }

    /**
     * @return TRUE if the current player has confirmed that he wants to send the ship,
     *         FALSE otherwise
     */
    private boolean getShipFinishedConfirmation() throws InterruptedException {
        boolean sendShip = false;
        boolean choiceMade = false;
        String input;

        String yesMessage = "Y";
        String noMessage = "N";

        do {
            System.out.print(PrintUtils.addColor("[!] [WARNING: This action is IRREVERSIBLE] [!]", ANSIColors.RED));
            System.out.println();
            System.out.print("Do you want to send your ship? [" + yesMessage + "/" + noMessage + "] ");

            input = this.inputThread.waitForInput();

            if (input == null) {
                // A force interrupt arrived
                throw new InterruptedException();
            }

            if (input.equalsIgnoreCase(yesMessage)) {
                sendShip = true;
                choiceMade = true;
            }
            else if (input.equalsIgnoreCase(noMessage)) {
                choiceMade = true;
            }
            else {
                System.out.println(PrintUtils.addColor(UNKNOWN_COMMAND_ERROR, ANSIColors.RED));
            }
        }
        while (!choiceMade);

        return sendShip;
    }

    /**
     * @return The current player's chosen tile index
     */
    private int getTileIndex() throws InterruptedException {
        int selectableComponentsAmount = this.model.getState().getConstructionShipComponents().size();
        String input;
        int idx = -1;

        do {
            System.out.print("Enter tile index (between 0 and " + (selectableComponentsAmount - 1) + "): ");
            input = this.inputThread.waitForInput();

            if (input == null) {
                // A force interrupt arrived
                throw new InterruptedException();
            }

            try {
                int tmpIndex = Integer.parseInt(input);

                if (tmpIndex < 0 || tmpIndex >= selectableComponentsAmount) {
                    System.out.println(PrintUtils.addColor("ERROR: Given index must be between 0 and " + (selectableComponentsAmount - 1) + ".", ANSIColors.RED));
                }
                else {
                    idx = tmpIndex;
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Must insert a number between 0 and " + (selectableComponentsAmount - 1) + " or 'r' to select it at random.", ANSIColors.RED));
            }

            if (idx >= 0 && !this.model.getState().getConstructionShipComponents().get(idx).isVisible()) {
                System.out.println(PrintUtils.addColor("ERROR: This component is already selected by someone else.", ANSIColors.RED));
                idx = -1;   // Reset to retry
            }
        }
        while (idx < 0);

        return idx;
    }

    /**
     * Selects a tile among the available ones and "binds" it to the user that
     * selected it until he either places, deselects or reserves that tile.
     */
    private void selectTile() throws Exception {
        // Getting the player's chosen tile by its index
        int idx = this.getTileIndex();

        // If the selection was successful, then we get the user's selected
        // component and store it in the aptly named attribute
        this.selectedComponent = this.model.getState().getConstructionShipComponents().get(idx);

        // After getting the player's chosen tile index, it gets sent to the server who
        // will then validate whether the tile can be selectable or not and, from here, the client
        // will then execute either the onSuccess or onError lambda based on the server's response
        this.ctx = new CommandCTX(
            "selectTile",
            () -> {
                // Go to the ship construction screen
                clearTerminal();
                this.getShipConstructionCommand();
            },
            () -> {
                // If an error occurred we re-execute the command and reset
                // the currently selected component attribute and widget
                try {
                    this.selectedComponent = null;
                    this.selectedComponentWidget = null;

                    this.composeComponentSelectionWidgets().printWidget();
                    this.selectTile();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" was thrown by 'selectTile' method", ANSIColors.RED));
                }
            }
        );

        this.client.selectTile(this.model.getNickname(), this.selectedComponent.getID());
    }

    /**
     * Deselects the currently selected tile, leaving it available for other players
     */
    private void deselectTile() throws Exception {
        // If an error occurred we go back to the
        // ship construction menu
        this.ctx = new CommandCTX(
            "deselectTile",
            () -> {
                // Once we have deselected the tile we can return to
                // the component selection menu
                this.selectedComponent = null;
                this.selectedComponentWidget = null;
                // this.isSelectedTileReserved = false;

                // At the end, it goes back to asking again a new
                // component selection command
                clearTerminal();
                this.getComponentSelectionCommand();
            },
            this::getShipConstructionCommand
        );

        this.client.deselectTile(this.model.getNickname(), this.selectedComponent.getID());
    }

    /**
     * Asks the current player to insert any key and then press [ENTER]
     * to go back to the component selection screen, thus ending the
     * selected subdeck visualization
     */
    private void deselectSubdeck(int subdeckIndex) throws Exception {
        // The user can observe his chosen subdeck for as much as
        // he wants (unless a forced interrupt arrives)
        try {
            System.out.print("Press any key and then press [ENTER] to go back...");
            String line = this.inputThread.waitForInput();

            // A forced interrupt arrived
            if (line == null) return;
        }
        catch (InterruptedException e) {
            // A forced interrupt arrived
            return;
        }

        // Dereferencing the subdeck widget
        this.cardSubdeckWidget = null;

        // Deselect the deck by sending a message to the server
        this.ctx = new CommandCTX(
                "selectDeselectSubdeck",
                () -> {
                    clearTerminal();
                    this.getComponentSelectionCommand();
                },
                () -> {
                    // Make the user choose another subdeck command
                    try {
                        this.getCardSubdeckCommand();
                    }
                    catch (Exception e) {
                        clearTerminal();

                        new WidgetTUI()
                                .appendString(
                                        PrintUtils.addColor(
                                                "ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.",
                                                ANSIColors.RED
                                        )
                                )
                                .addPadding(0, 1, 0, 1)
                                .wrapWidgetWithBorder()
                                .printWidget();
                    }
                }
        );

        this.client.selectDeselectSubdeck(
                this.model.getNickname(),
                subdeckIndex,
                false
        );
    }

    /**
     * Reserves the currently selected tile
     */
    private void reserveTile() throws Exception {
        Runnable task = () -> {
            // Removing the selected tile from the selected component slot
            // since the user decided to reserve it for the future
            this.selectedComponent = null;
            this.selectedComponentWidget = null;

            // Adding back the "(1) - Deselect tile" command
            // if the previously selected tile was reserved
            if (this.isSelectedTileReserved) {
                this.isSelectedTileReserved = false;
                this.generateShipConstructionCommands();
            }

            // At the end, it goes back to asking again a new
            // component selection command
            clearTerminal();
            this.getComponentSelectionCommand();
        };

        if (this.model.getState().getReservedComponents().contains(this.selectedComponent)) {
            task.run();
            return;
        }

        // If an error occurred we go back to the
        // ship construction menu
        this.ctx = new CommandCTX(
            "reserveTile",
            task,
            () -> {
                try {
                    this.getShipConstructionCommand();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );

        this.client.reserveTile(this.model.getNickname(), this.selectedComponent.getID());
    }

    /**
     * Selects the chosen reserved component to use it
     */
    private void selectReservedTile() throws IllegalArgumentException {
        boolean componentRetrieved;
        String line;
        int index;

        index = -1;
        componentRetrieved = false;

        if (this.model.getState().getReservedComponents().isEmpty()) {
            throw new IllegalArgumentException(PrintUtils.addColor("[ERROR] You don't have any reserved components!", ANSIColors.RED));
        }

        do {
            System.out.print("Enter reserved tile index to select (0 = Slot1, 1 = Slot2): ");

            try {
                line = this.inputThread.waitForInput();

                // A forced interrupt arrived
                if (line == null) return;
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }

            try {
                index = Integer.parseInt(line);

                // Selecting the chosen reserved component
                this.selectedComponent = this.model.getState().getReservedComponents().get(index);

                this.isSelectedTileReserved = true;
                componentRetrieved = true;

                // Regenerate the ship construction widget which removes the commands
                // that should not be seen when using a reserved tile as the selected tile
                this.generateShipConstructionCommands();
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println(PrintUtils.addColor("ERROR: The reserved tile index you inserted is either wrong or no tile is reserved in slot " + index + ".", ANSIColors.RED));
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
            }
        }
        while (!componentRetrieved);

        // Go to the ship construction screen
        clearTerminal();
        this.getShipConstructionCommand();
    }

    /**
     * Handles the placement of the selected component to the current player's client ship
     */
    private void placeSelectedTile() throws Exception {
        CoordinatePair componentPosition;

        // Adding the client component both to the client ship and
        // to the ClientShipConstructionState to send it later when the
        // ship is completed and needs to be sent to the server for validation
        if (this.selectedComponent != null) {
            // Get the component's position and send it to the server
            // to evaluate if the placement is legal
            componentPosition = this.getComponentCoordinates();

            this.ctx = new CommandCTX(
                "placeTile",
                () -> {
                    // If the currently selected tile is from the reserve, then it needs
                    // to be removed from the reserved component list since it was just placed
                    if (this.isSelectedTileReserved) {
                        this.model.getState().getReservedComponents().remove(this.selectedComponent);
                        this.isSelectedTileReserved = false;

                        // Adding back the "(1) - Deselect tile" command
                        // if the previously selected tile was reserved
                        this.generateShipConstructionCommands();
                    }

                    // Removing the component from the selected slot, since it was placed on the ship
                    this.selectedComponent = null;
                    this.selectedComponentWidget = null;

                    // Updating the ship widget
                    Optional<ClientShip> optionalShip = this.model.getShipOfPlayer(this.model.getNickname());
                    optionalShip.ifPresent(clientShip -> this.currPlayerShipWidget = clientShip.getShipGridWidget());

                    // Then go back to the component selection screen
                    clearTerminal();
                    this.getComponentSelectionCommand();
                },
                () -> {
                    clearTerminal();
                    new WidgetTUI()
                        .appendString(
                            PrintUtils.addColor(
                                "ERROR: Couldn't place tile at (" + (componentPosition.getI() + 1) + ", " + (componentPosition.getJ() + 1) + ")",
                                ANSIColors.RED
                            )
                        )
                        .addPadding(0, 1, 0, 1)
                        .wrapWidgetWithBorder()
                        .printWidget();

                    // On failure, go back to the ship construction screen
                    this.getShipConstructionCommand();
                }
            );

            // Broadcasting to all players that the current player placed
            // his currently selected tile on his ship
            this.client.placeTile(
                    this.model.getNickname(),
                    this.selectedComponent.getID(),
                    componentPosition.getI(),
                    componentPosition.getJ(),
                    this.selectedComponent.getDirection()
            );
        }
    }

    /**
     * @return A pair of integers that represents the (row, col) indexes
     *         where the player wants to place the selected component.
     */
    private CoordinatePair getComponentCoordinates() throws InterruptedException {
        CoordinatePair coordinates = new CoordinatePair();
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
            System.out.print("Insert row where to put the selected component: ");
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
            System.out.print("Insert column where to put the selected component: ");
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
        coordinates.setI(--i);
        coordinates.setJ(--j);

        return coordinates;
    }

    /**
     * Sends the ship to the server for evaluation
     */
    private void sendFinishedShip() throws Exception {
        this.ctx = new CommandCTX(
            "sendShipConfirmation",
            () -> {
                // Update the component selection commands
                // with the only commands available for his state
                clearTerminal();
                this.generateComponentSelectionCommands();

                // Go back to the component selection command menu where the only available
                // commands will be: 1) Show other ship, 2) Flip timer, 3) Show card deck
                try {
                    this.getComponentSelectionCommand();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" was thrown when calling 'getComponentSelectionCommand' method", ANSIColors.RED));
                }
            },
            () -> {
                // Otherwise, if the command fails, then the player goes back to the
                // component selection commands menu

                // NOTE: Error is communicated to the player through the showError() method
                try {
                    this.getComponentSelectionCommand();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" was thrown when calling 'getComponentSelectionCommand' method", ANSIColors.RED));
                }
            }
        );

        // Sends the current player's ship when he
        // decides to finish building it
        this.client.sendShipConfirmation(
            this.model.getNickname(),
            this.model.getState().getReservedComponents().size()
        );
    }

    /**
     * Flips the timer and sends the relative message to broadcast it to other players
     */
    private void flipTimer() throws Exception {
        this.ctx = new CommandCTX(
            "flipTimer",
            () -> {
                clearTerminal();
                new WidgetTUI()
                        .appendString(COMPUTER_MSG_TAG + PrintUtils.addColor("Timer flipped successfully!", ANSIColors.BRIGHT_MAGENTA))
                        .addPadding(1, 1, 1, 1)
                        .wrapWidgetWithBorder()
                        .printWidget();

                this.getComponentSelectionCommand();
            },
            this::getComponentSelectionCommand
        );

        this.client.flipTimer(this.model.getNickname());
    }

    /**
     * Forcing this player (as well as all the others) to
     * send his ship (if they didn't do it already) when the
     * server-side timer ends (i.e.: no more flips available)
     */
    private void sendShipOnTimerEnd() throws Exception {
        // Force-send the ship iff the current player hasn't done it already
        if (!this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
            this.ctx = new CommandCTX(
                    "sendShipOnTimerEnd",
                    () -> {
                        // Nothing
                    },
                    () -> {
                        // Retrying to send the ship if it fails
                        // TODO: Implement, if needed, a "max retry"
                        //       functionality to avoid infinite loop
                        try {
                            this.sendShipOnTimerEnd();
                        }
                        catch (Exception e) {
                            System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                        }
                    }
            );

            // Sends the current player's ship when
            // the timer completely runs out
            this.client.sendShipConfirmation(this.model.getNickname(), this.model.getState().getReservedComponents().size());
        }
    }

    /**
     * @param timerDTO The updated timerDTO generated when
     *                 the server-side timer ends
     */
    @Override
    public void receiveTimerDTO(TimerDTO timerDTO) {
        this.model.setTimerDTO(timerDTO);

        if (timerDTO != null && timerDTO.getHasEnded()) {
            try {
                new WidgetTUI()
                        .appendString(COMPUTER_MSG_TAG + "Time's up! All ships will now be sent.")
                        .addPadding(1, 1, 1, 1)
                        .wrapWidgetWithBorder();

                this.sendShipOnTimerEnd();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * TUI screen entry point for the ship construction phase
     */
    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {
        clearTerminal();

        // Show all selectable components grid as well as the reserved
        // components to display any components that the player might
        // choose to reserve during this phase
        this.getComponentSelectionCommand();
    }
}
