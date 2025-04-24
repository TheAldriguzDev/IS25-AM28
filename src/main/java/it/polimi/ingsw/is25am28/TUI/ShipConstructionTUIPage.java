package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Network.Messages.DeselectTile;
import it.polimi.ingsw.is25am28.Network.Messages.SelectTile;
import it.polimi.ingsw.is25am28.Network.Messages.SendShipConfirmation;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2.*;

public final class ShipConstructionTUIPage extends TUIPage {
    // Default component matrix (row, col) dimensions
    public static final int DEFAULT_COMPONENT_ROWS = 8;
    public static final int DEFAULT_COMPONENT_COLS = 19;
    public static final int COMMANDS_PER_COLUMN = 2;

    // All widgets that compose the three distinct TUIs, which are:
    //  1) Component selection
    //  2) Ship construction
    //  3) Deck visualization
    private WidgetTUI selectableComponentsWidget;
    private WidgetTUI reservedComponentsWidget;
    private WidgetTUI selectedComponentWidget;
    private WidgetTUI coveredComponentWidget;
    private WidgetTUI emptyComponentWidget;
    private WidgetTUI cardDeckVisualizationWidget;
    private WidgetTUI shipWidget;

    // All input widgets
    private final InputWidgetTUI componentSelectionCommandsWidget;
    private final InputWidgetTUI shipConstructionCommandsWidget;

    // Placeholder for the currently selected component
    private ClientComponent selectedComponent;
    private boolean isSelectedTileReserved;

    // Constructor
    public ShipConstructionTUIPage(ClientTUI_v2 clientTUI) {
        super(clientTUI);

        // Initializing the selected component
        this.selectedComponent = null;
        this.selectedComponentWidget = null;
        this.isSelectedTileReserved = false;

        // Initializing this player's ClientShip and getting the player nickname for this client
        this.clientTUI.getModel().setShipToPlayer(
            this.clientTUI.getPlayerNickname(),
            new ClientShip(this.clientTUI.getModel().getDifficultyLevel())
        );

        // Generating the covered component widget and the empty component widget
        this.generateCoveredComponentWidget();
        this.generateEmptyComponentWidget();

        // Initializing all selectable components
        this.selectableComponentsWidget = null;

        // Initializing the staging area with placeholders
        this.reservedComponentsWidget = null;

        // Initializing the component selection command widget
        this.componentSelectionCommandsWidget = new InputWidgetTUI();
        this.componentSelectionCommandsWidget.setNewScanner(System.in);
        this.componentSelectionCommandsWidget.setColumnGroupingAmount(COMMANDS_PER_COLUMN);
        this.initComponentSelectionCommands();

        // Initializing the ship construction command widget
        this.shipConstructionCommandsWidget = new InputWidgetTUI();
        this.shipConstructionCommandsWidget.setNewScanner(System.in);
        this.shipConstructionCommandsWidget.setColumnGroupingAmount(COMMANDS_PER_COLUMN);
        this.initShipConstructionCommands();
    }

    /**
     * Initializes the commands in the component selection input widget
     */
    private void initComponentSelectionCommands() {
        // Initializes (only once!) the commands inside the component selection input widget
        if (this.componentSelectionCommandsWidget.getCommandMap() == null || this.componentSelectionCommandsWidget.getCommandMap().isEmpty()) {
            CommandWidgetTUI componentSelectionCommand;

            // (1) - Select Tile
            componentSelectionCommand = new CommandWidgetTUI(
                "1",
                () -> {
                    // Asks for the index and then selects the tile
                    try {
                        this.selectTile();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // Then goes to the ship construction TUIPage and
                    // waits for a command to handle the selected tile
                    clearTerminal();
                    this.composeShipConstructionWidget().printWidget();
                    this.getShipConstructionCommand();
                }
            );
            componentSelectionCommand.appendString("Select Tile");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

            // (2) - Select Reserved Tile
            componentSelectionCommand = new CommandWidgetTUI(
                "2",
                () -> {
                    try {
                        this.selectReservedTile();

                        // If a reserved tile was present AND correctly selected, then
                        // go to the ship construction TUIPage and wait for a command
                        // to handle the selected tile
                        clearTerminal();
                        this.composeShipConstructionWidget().printWidget();
                        this.getShipConstructionCommand();
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());

                        // Otherwise, go back to the component selection command widget
                        this.getComponentSelectionCommand();
                    }
                }
            );
            componentSelectionCommand.appendString("Select Reserved Tile");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

            // (3) - Flip Timer
            componentSelectionCommand = new CommandWidgetTUI(
                "3",
                () -> {
                    // Flips the timer (if possible), otherwise throws an error saying
                    // that time it is currently flowing (i.e.: cannot be flipped until it finishes)

                    // Printing the selectable components widget before the timer flip result
                    clearTerminal();
                    this.selectableComponentsWidget.printWidget();

                    // if hourglass.flip() == true, then notify the user and reprint

                    // TODO: Figure out where to put the hourglass
                    // TODO: Substitute the "true" here with "timer.flip()"
                    if (true) {
                        System.out.println(PrintUtils.addColor("Timer flipped successfully!", ANSIColors.BRIGHT_MAGENTA));
                    }
                    else {
                        System.out.println(PrintUtils.addColor("Someone else already flipped the timer, you must wait that it ends before flipping it again!", ANSIColors.BRIGHT_MAGENTA));
                    }

                    // Asking the user for the next command in
                    // the component selection input widget
                    this.getComponentSelectionCommand();
                }
            );
            componentSelectionCommand.appendString("Flip Timer");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

            // (4) - Visualize Deck
            componentSelectionCommand = new CommandWidgetTUI(
                "4",
                () -> {
                    // Visualizes the selected deck from the board (if someone is not already observing it)
                    boolean existingCommandSelected;

                    // TODO: Synchronize on the ClientDeck when a player is observing a particular deck

                    // Printing the selected deck to terminal and staying there until the
                    // user is satisfied with his observation and wants to go back
                    clearTerminal();
                    this.cardDeckVisualizationWidget.printWidget();
                    System.out.println("Enter any key to go back...");

                    try {
                        this.clientTUI.getBufferedReader().readLine();
                    }
                    catch (IOException e) {
                        System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                    }

                    // Asking the user for the next command in
                    // the component selection input widget
                    clearTerminal();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            );
            componentSelectionCommand.appendString("Visualize Deck");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

            // (5) - Finish Ship
            componentSelectionCommand = new CommandWidgetTUI(
                "5",
                () -> {
                    if (this.getShipFinishedConfirmation()) {
                        // Sending the ship to the server
                        try {
                            this.sendShipConfirmation();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        // If player refuses to send the confirmation to conclude the ship
                        // building phase, then go back to the component selection menu
                        clearTerminal();
                        this.selectableComponentsWidget.printWidget();
                        this.getComponentSelectionCommand();
                    }
                }
            );
            componentSelectionCommand.appendString("Finish Ship");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);
        }
    }

    /**
     * Initializes the commands in the ship construction input widget
     */
    private void initShipConstructionCommands() {
        // Initializes (only once!) the commands inside the ship construction input widget
        if (this.shipConstructionCommandsWidget.getCommandMap() == null || this.shipConstructionCommandsWidget.getCommandMap().isEmpty()) {
            CommandWidgetTUI shipConstructionCommand;

            // (1) - Deselect Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "1",
                () -> {
                    // Deselects the component that is currently taken by this user
                    try {
                        this.deselectTile();
                    }
                    catch (Exception e) {
                        System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                    }

                    // At the end, it goes back to asking again a new
                    // component selection command
                    clearTerminal();
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            );
            shipConstructionCommand.appendString("Deselect Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (2) - Reserve Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "2",
                () -> {
                    List<ClientComponent> reservedComponents = this.clientTUI.getModel().getState().getReservedComponents();

                    // If the currently selected tile is from the reserved components, then
                    // just don't do anything and leave it in the reserved components list
                    if (!this.isSelectedTileReserved) {
                        // Puts the selected component in the reserved tiles
                        // If it can't, then it'll ask the user to do something else
                        if (reservedComponents.size() < 2) {
                            // Adding the currently selected component to the reserved component list
                            this.clientTUI.getModel().getState().reserveTile(this.selectedComponent);

                            // Setting the currently selected tile as non-visible, since the player decided
                            // to reserve it and thus place it in the near future
                            this.selectedComponent = null;

                            // At the end, it goes back to asking again a new
                            // component selection command
                            clearTerminal();
                            this.generateSelectableComponentsWidget();
                            this.selectableComponentsWidget.printWidget();
                            this.getComponentSelectionCommand();
                        }
                        else {
                            // Otherwise, it means that the user already has 2 reserved tiles,
                            // therefore he cannot store any additional ones
                            System.out.println(PrintUtils.addColor("ERROR: You already have 2 (max) reserved tiles! Use them before storing others!", ANSIColors.RED));

                            // Go back to asking the user what to do with the selected component
                            clearTerminal();
                            this.composeShipConstructionWidget().printWidget();
                            this.getShipConstructionCommand();
                        }
                    }
                    else {
                        // Since the current component is from the reserved component list, then
                        // just go ahead and remove it from the selected component attribute
                        this.selectedComponent = null;

                        // And go back to the component selection screen
                        clearTerminal();
                        this.generateSelectableComponentsWidget();
                        this.selectableComponentsWidget.printWidget();
                        this.getComponentSelectionCommand();
                    }
                }
            );
            shipConstructionCommand.appendString("Reserve Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (3) - Place Selected Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "3",
                () -> {
                    // Puts the currently selected tile in the ship and in the
                    // ClientShipConstructionState when the ship will be sent to
                    // the server for validation
                    this.handleClientComponentAddition();

                    // At the end, it goes back to asking again a new
                    // component selection command
                    clearTerminal();
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            );
            shipConstructionCommand.appendString("Place Selected Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (4) - Right Rotate Selected Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "4",
                () -> {
                    // Rotates the current component
                    this.selectedComponent.rotateRight();

                    // And then it goes back to the ship construction menu
                    clearTerminal();
                    this.composeShipConstructionWidget().printWidget();
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Right Rotate Selected Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (5) - Left Rotate Selected Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "5",
                () -> {
                    // Rotates the current component
                    this.selectedComponent.rotateLeft();

                    // And then it goes back to the ship construction menu
                    clearTerminal();
                    this.composeShipConstructionWidget().printWidget();
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Left Rotate Selected Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);
        }
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
        this.selectedComponent = this.clientTUI.getModel().getState().getConstructionShipComponents().get(idx);

        // Then generate the selected component widget
        this.generateSelectedComponentWidget();

        // After getting the player's chosen tile index, it gets sent to the server who
        // will then validate whether the tile can be selectable or not and, from here, the client
        // will then execute either the onSuccess or onError lambda based on the server's response
        this.clientTUI.setCurrCommand(
            new CommandCTX(
                "selectTile",
                () -> {
                    // Move to the ship construction TUIPage by recomposing it and
                    // ask the user what to do with the selected component
                    clearTerminal();
                    this.composeShipConstructionWidget().printWidget();
                    this.getShipConstructionCommand();
                },
                () -> {
                    // If an error occurred we re-execute the command and reset
                    // the currently selected component attribute and widget
                    try {
                        this.selectedComponent = null;
                        this.selectedComponentWidget = null;
                        this.selectTile();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            )
        );

        int construction_i = idx / DEFAULT_COMPONENT_COLS;
        int construction_j = idx % DEFAULT_COMPONENT_COLS;

        this.clientTUI.getVirtualView().sendMessage(new SelectTile(this.clientTUI.getPlayerNickname(), construction_i, construction_j));
    }

    /**
     * Deselects the currently selected tile, leaving it available for other players
     */
    private void deselectTile() throws Exception {
        this.clientTUI.setCurrCommand(
            new CommandCTX(
                "deselectTile",
                () -> {
                    // Once we have deselected the tile we can return to
                    // the component selection menu
                    this.selectedComponent = null;
                    this.isSelectedTileReserved = false;

                    clearTerminal();
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                },
                () -> {
                    // If an error occurred we go back to the
                    // ship construction menu
                    clearTerminal();
                    this.composeShipConstructionWidget().printWidget();
                    this.getShipConstructionCommand();
                }
            )
        );

        int id = this.selectedComponent.getID();
        int construction_i = id / 19;
        int construction_j = id % 19;

        this.clientTUI.getVirtualView().sendMessage(new DeselectTile(this.clientTUI.getPlayerNickname(), construction_i, construction_j));
    }

    /**
     * Handles the addition of the selected component to the current player's client ship
     */
    private void handleClientComponentAddition() {
        Map.Entry<Integer, Integer> componentPosition;
        boolean correctCoordinates;

        // Adding the client component both to the client ship and
        // to the ClientShipConstructionState to send it later when the
        // ship is completed and needs to be sent to the server for validation
        if (this.selectedComponent != null) {
            correctCoordinates = false;

            do {
                // Getting the component's coordinates
                componentPosition = this.getComponentCoordinates();

                // Adding the currently selected component at those coordinates
                // in the current player's ship
                try {
                    this.clientTUI.getModel()
                        .getShipOfPlayer(this.clientTUI.getPlayerNickname())
                        .addComponent(
                            this.selectedComponent,
                            componentPosition.getKey(),
                            componentPosition.getValue()
                        );

                    // Exit the loop only if the component can actually
                    // be placed at the given coordinates
                    correctCoordinates = true;
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                }
            }
            while (!correctCoordinates);

            // And then adding the client component to the ship descriptor inside ClientShipConstructionState
            this.clientTUI.getModel().getState().placeTile(
                this.selectedComponent,
                componentPosition.getKey(),
                componentPosition.getValue()
            );

            // Since the component was added, it is now unusable for other
            // players thus they must not be able to see it
            this.selectedComponent.setIsVisible(false);

            // If the currently selected tile is from the reserve, then it needs
            // to be removed from the reserved component list since it was just placed
            if (this.isSelectedTileReserved) {
                this.clientTUI.getModel().getState().getReservedComponents().remove(this.selectedComponent);
            }

            // Updating the ship widget
            this.shipWidget = this.clientTUI.getModel().getShipOfPlayer(this.clientTUI.getPlayerNickname()).generateWidget();
        }
    }

    /**
     * @return The linearized index of the (row, col) coordinate pair where
     *         the user wants to put the selected component
     */
    private Map.Entry<Integer, Integer> getComponentCoordinates() {
        Map<Integer, Integer> coordinates = new HashMap<>();
        boolean validCoordinate;
        int i = 0;
        int j = 0;

        int minRowValue = ClientShip.shipOffsets.get(this.clientTUI.getModel().getDifficultyLevel()).getKey();
        int maxRowValue = ClientShip.shipDimensions.get(this.clientTUI.getModel().getDifficultyLevel()).getKey() + minRowValue + 1;

        int minColValue = ClientShip.shipOffsets.get(this.clientTUI.getModel().getDifficultyLevel()).getValue();
        int maxColValue = ClientShip.shipDimensions.get(this.clientTUI.getModel().getDifficultyLevel()).getValue() + minColValue + 1;

        // Getting the row --> i
        do {
            System.out.print("Insert row where to put the selected component: ");

            try {
                i = Integer.parseInt(this.clientTUI.getBufferedReader().readLine());
                validCoordinate = (i > minRowValue && i < maxRowValue);

                if (!validCoordinate) {
                    System.out.println(PrintUtils.addColor("ERROR: Given row is out of the ship boundaries (range is [" + (minRowValue + 1) + ", " + (maxRowValue - 1) + "])", ANSIColors.RED));
                }
            }
            catch (IOException | NumberFormatException e) {
                System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                validCoordinate = false;
            }
        }
        while (!validCoordinate);

        // Getting the col --> j
        do {
            System.out.print("Insert column where to put the selected component: ");

            try {
                j = Integer.parseInt(this.clientTUI.getBufferedReader().readLine());
                validCoordinate = (j > minColValue && j < maxColValue);

                if (!validCoordinate) {
                    System.out.println(PrintUtils.addColor("ERROR: Given column is out of the ship boundaries (range is [" + (minColValue + 1) + ", " + (maxColValue - 1) + "])", ANSIColors.RED));
                }
            }
            catch (IOException | NumberFormatException e) {
                System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                validCoordinate = false;
            }
        }
        while (!validCoordinate);

        // Reducing both by 1 since they will then be used as
        // indexes inside the client ship component matrix
        coordinates.put(i, j);
        return coordinates.entrySet().stream().toList().getFirst();
    }

    /**
     * Sends the ship to the server for evaluation
     */
    private void sendShipConfirmation() throws Exception {
        this.clientTUI.setCurrCommand(
            new CommandCTX(
                "sendShipConfirmation",
                () -> {
                    // TODO: onSuccess --> Go to PopulateShip phase
                    System.out.println(PrintUtils.addColor("SHIP IS VALID --> Next: PopulateShip", ANSIColors.BRIGHT_GREEN));
                },
                () -> {
                    // TODO: onError --> Go to FixShip phase
                    System.out.println(PrintUtils.addColor("SHIP IS INVALID --> Next: FixShip", ANSIColors.BRIGHT_RED));
                }
            )
        );

        this.clientTUI.getVirtualView().sendMessage(
            new SendShipConfirmation(
                this.clientTUI.getModel().getNickname(),
                this.clientTUI.getModel().getState().getCreatedShip()
            )
        );
    }

    /**
     * Generates the selected component's widget and the frame around it
     */
    public void generateSelectedComponentWidget() {
        if (this.selectedComponent != null) {
            this.selectedComponentWidget = new WidgetTUI();

            // Adding a title and some padding at the bottom
            this.selectedComponentWidget.appendString("[SELECTED COMPONENT]");
            this.selectedComponentWidget.addPadding(0, 0, 1, 0);

            // Adding the selected component's generated widget screen
            this.selectedComponentWidget.appendScreen(this.selectedComponent.generateWidget().getScreen());

            // Centering the screen, adding some padding and then wrapping the final widget
            this.selectedComponentWidget.centerWidgetScreen();
            this.selectedComponentWidget.addPadding(0, 2, 1, 2);
            this.selectedComponentWidget.wrapWidgetWithBorder();
        }
    }

    /**
     * Generates the reserved component widget and the frame around it
     */
    public void generateReservedComponentWidget() {
        this.reservedComponentsWidget = new WidgetTUI();
        int i, emptySlots;

        // Adding a title
        this.reservedComponentsWidget.appendString("[RESERVED COMPONENTS]");

        // Adding each reserved component in the list
        int reservedComponentAmount = this.clientTUI.getModel().getState().getReservedComponents().size();
        for (i = 0; i < reservedComponentAmount; i++) {
            // Adding a single space just for padding
            this.reservedComponentsWidget.appendString(PrintUtils.SPACE);
            this.reservedComponentsWidget.appendString("[SLOT " + (i + 1) + "]");
            this.reservedComponentsWidget.appendScreen(this.clientTUI.getModel().getState().getReservedComponents().get(i).generateWidget().getScreen());
        }

        // Substituting empty slots with placeholder widgets
        emptySlots = 2 - reservedComponentAmount;
        while (emptySlots > 0) {
            // Adding a single space just for padding
            this.reservedComponentsWidget.appendString(PrintUtils.SPACE);
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
     * Generates a widget that will act as the back side of each component when covered
     */
    private void generateCoveredComponentWidget() {
        if (this.coveredComponentWidget == null) {
            this.coveredComponentWidget = new WidgetTUI();

            this.coveredComponentWidget.setHeight(3);
            this.coveredComponentWidget.setWidth(11);
            this.coveredComponentWidget.wrapWidgetWithBorder();
        }
    }

    /**
     * Generates a widget that will act as a blank space when the
     * corresponding widget is flagged as non-visible
     */
    private void generateEmptyComponentWidget() {
        if (this.emptyComponentWidget == null) {
            this.emptyComponentWidget = new WidgetTUI();

            this.emptyComponentWidget.setHeight(5);
            this.emptyComponentWidget.setWidth(13);
        }
    }

    /**
     * Generates the selectableComponentsWidget with its screen set as the matrix of all the selectable components.
     * (NOTE: At the beginning, all components are facing down because no one has flipped them yet)
     */
    private void generateSelectableComponentsWidget() {
        if (this.selectableComponentsWidget == null) {
            // Instantiating the selectableComponents widget if
            // it wasn't already initialized
            this.selectableComponentsWidget = new WidgetTUI();
        }

        List<String> allRows = new ArrayList<>();
        List<List<String>> row = new ArrayList<>();
        ClientComponent currComponent;
        List<ClientComponent> clientComponents = this.clientTUI.getModel().getState().getConstructionShipComponents();

        int availableComponents = clientComponents.size();
        int iteratedComponents = 0;

        for (int i = 0; i < it.polimi.ingsw.is25am28.TUI.ShipConstructionTUIPage.DEFAULT_COMPONENT_ROWS; i++) {
            for (int j = 0; j < it.polimi.ingsw.is25am28.TUI.ShipConstructionTUIPage.DEFAULT_COMPONENT_COLS; j++) {
                currComponent = clientComponents.get((i * DEFAULT_COMPONENT_COLS) + j);

                // Only adding something to print if the component is visible
                if (currComponent.isVisible()) {
                    List<String> screen = new ArrayList<>();

                    // Adding the current component ID at the top of the screen
                    screen.add("(" + currComponent.getID() + ")");

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
        this.selectableComponentsWidget.setScreen(allRows);
        this.selectableComponentsWidget.wrapWidgetWithBorder();
    }

    /**
     * @return The TUIPage page where it shows this player's current ship, the
     *         currently selected component and the player's reserved components.
     * (NOTE: This method only recomposes the widgets together, each widget is updated separately)
     */
    private WidgetTUI composeShipConstructionWidget() {
        // Ensuring each widget is updated
        this.generateSelectedComponentWidget();
        this.generateReservedComponentWidget();
        this.shipWidget = this.clientTUI.getModel().getShipOfPlayer(this.clientTUI.getPlayerNickname()).getShipGridWidget();

        return WidgetTUI.composeTwoWidgetsHorizontally(
            WidgetTUI.composeTwoWidgetsVertically(
                this.selectedComponentWidget,
                this.reservedComponentsWidget
            ).addPadding(0, 1, 0, 0).centerWidgetScreen(),
            this.shipWidget
        );
    }

    /**
     * @return The current player's chosen tile index
     */
    private int getTileIndex() {
        int selectableComponentsAmount = this.clientTUI.getModel().getState().getConstructionShipComponents().size();
        String input;
        int idx = -1;

        do {
            System.out.print("Enter tile index (between 0 and " + (selectableComponentsAmount - 1) + ") or 'r' for random: ");
            try {
                input = this.clientTUI.getBufferedReader().readLine().trim();

                if (input.equalsIgnoreCase("r")) {
                    idx = this.clientTUI.getRandom().nextInt(selectableComponentsAmount);
                }
                else {
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
                }

                if (idx >= 0 && !this.clientTUI.getModel().getState().getConstructionShipComponents().get(idx).isVisible()) {
                    System.out.println(PrintUtils.addColor("ERROR: This component is already selected by someone else.", ANSIColors.RED));
                    idx = -1;   // Reset to retry

                    // Print the updated component selection menu
                    clearTerminal();
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            }
            catch (IOException e) {
                System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
            }

        }
        while (idx < 0);

        return idx;
    }

    /**
     * Selects the chosen reserved component to use it
     */
    private void selectReservedTile() throws IllegalArgumentException {
        boolean componentRetrieved;
        int index = -1;

        componentRetrieved = false;

        if (this.clientTUI.getModel().getState().getReservedComponents().isEmpty()) {
            throw new IllegalArgumentException(PrintUtils.addColor("ERROR: You don't have any reserved components!", ANSIColors.RED));
        }

        do {
            System.out.print("Enter reserved tile index to select (0 = Slot1, 1 = Slot2): ");

            try {
                index = Integer.parseInt(this.clientTUI.getBufferedReader().readLine().trim());

                this.selectedComponent = this.clientTUI.getModel().getState().getReservedComponents().get(index);
                this.isSelectedTileReserved = true;
                componentRetrieved = true;
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println(PrintUtils.addColor("ERROR: Wrong reserved tile index.", ANSIColors.RED));
            }
            catch (IOException | NumberFormatException e) {
                System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
            }
        }
        while (!componentRetrieved);
    }

    /**
     * @return TRUE if the current player has confirmed that he wants to send the ship,
     *         FALSE otherwise
     */
    private boolean getShipFinishedConfirmation() {
        boolean sendShip = false;
        boolean choiceMade = false;
        String input;

        String yesMessage = "Y";
        String noMessage = "N";

        do {
            System.out.print(PrintUtils.addColor("[WARNING: This action is IRREVERSIBLE]", ANSIColors.RED));
            System.out.println();
            System.out.print("Do you want to send your ship? [" + yesMessage + "/" + noMessage + "]");

            try {
                input = this.clientTUI.getBufferedReader().readLine().trim();

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
            catch (IOException e) {
                System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
            }
        }
        while (!choiceMade);

        return sendShip;
    }

    /**
     * Stays in the menu until a valid command was given
     */
    public void getComponentSelectionCommand() {
        boolean existingCommandSelected;

        synchronized (this.clientTUI.getIoLock()) {
            do {
                System.out.println();
                System.out.println("Available component selection commands:");

                existingCommandSelected = this.componentSelectionCommandsWidget.selectCommand(DEFAULT_INPUT_PREFIX);

                if (!existingCommandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            while (!existingCommandSelected);
        }
    }

    /**
     * Stays in the menu until a valid command was given
     */
    public void getShipConstructionCommand() {
        boolean existingCommandSelected;

        synchronized (this.clientTUI.getIoLock()) {
            do {
                System.out.println();
                System.out.println("Available ship construction commands:");

                existingCommandSelected = this.shipConstructionCommandsWidget.selectCommand(DEFAULT_INPUT_PREFIX);

                if (!existingCommandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            while (!existingCommandSelected);
        }
    }

    /**
     * @param shipConstruction The DTO containing all components
     */
    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws RuntimeException {
        // Generate the current selectable component matrix from the updated component
        // data (which is then stored in the shipConstructionDTO attribute) and then prints it
        this.generateSelectableComponentsWidget();
        this.selectableComponentsWidget.printWidget();

        // Then, ask the user for a command
        this.getComponentSelectionCommand();
    }
}
