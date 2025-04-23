package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShipConstructionState;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.Components.*;
import it.polimi.ingsw.is25am28.Network.Messages.SelectTile;
import it.polimi.ingsw.is25am28.Network.Messages.SendShipConfirmation;
import it.polimi.ingsw.is25am28.Network.RMI.Client.RMIClient;
import it.polimi.ingsw.is25am28.Network.Socket.Client.TCPClient;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShipConstructionTUI extends TUI {
    // Testing
    public static void main(String[] args) {
        List<Integer> connectors = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            connectors.add(3);
        }

        ShipConstructionDTO shipConstructionDTO = new ShipConstructionDTO();
        List<Component> components = new ArrayList<>();

        components.add(new Battery(connectors, 2));
        components.add(new Battery(connectors, 3));
        components.add(new Cabin(connectors, false));
        components.add(new Cannon(connectors, 1));
        components.add(new Cannon(connectors, 2));
        components.add(new Engine(connectors, 1));
        components.add(new Engine(connectors, 2));
        components.add(new Shield(connectors));
        components.add(new Storage(connectors, 2, false));
        components.add(new Storage(connectors, 3, false));
        components.add(new Storage(connectors, 1, true));
        components.add(new Storage(connectors, 2, true));
        components.add(new Structural(connectors));
        components.add(new Vital(connectors, 0));
        components.add(new Vital(connectors, 1));

        int id = 0;
        for (Component c : components) {
            c.setId(id);
            id++;
        }

        ClientModel model = new ClientModel();
        model.setState(new ClientShipConstructionState(model, components.stream().map(Component::toMap).toList()));
        ShipConstructionTUI shipConstructionTUI = new ShipConstructionTUI(model);

        int connectionType = 1;

        if (connectionType == 1) {
            try {
                shipConstructionTUI.setVirtualClient(new RMIClient("127.0.0.1", 7777, UUID.randomUUID(), shipConstructionTUI, model));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                shipConstructionTUI.setVirtualClient(new TCPClient("127.0.0.1", 8888, shipConstructionTUI, model));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        shipConstructionDTO.setAllComponents(null);
        shipConstructionTUI.showShipConstruction(shipConstructionDTO);
    }

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
    private WidgetTUI cardDeckVisualizationWidget;
    private WidgetTUI shipWidget;

    // All input widgets
    private InputWidgetTUI componentSelectionCommandsWidget;
    private InputWidgetTUI shipConstructionCommandsWidget;

    // Placeholder for the currently selected component
    private ClientComponent selectedComponent;
    private boolean isSelectedTileReserved;

    // Constructor
    public ShipConstructionTUI(ClientModel model) {
        super(model);

        // Initializing the selected component
        this.selectedComponent = null;
        this.selectedComponentWidget = null;
        this.isSelectedTileReserved = false;

        // TODO: Set the model difficulty elsewhere
        this.model.setDifficultyLevel(2);

        // Initializing this player's ClientShip
        this.model.setShip(new ClientShip(this.model.getDifficultyLevel()));

        // Generating the covered component widget
        this.generateCoveredComponentWidget();

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

                    // Then goes to the ship construction TUI and
                    // waits for a command to handle the selected tile
                    TUI.clearTerminal();
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
                        // go to the ship construction TUI and wait for a command
                        // to handle the selected tile
                        TUI.clearTerminal();
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
                    TUI.clearTerminal();
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
                    TUI.clearTerminal();
                    this.cardDeckVisualizationWidget.printWidget();
                    System.out.println("Enter any key to go back...");
                    this.scanner.nextLine();

                    // Asking the user for the next command in
                    // the component selection input widget
                    TUI.clearTerminal();
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
                        // building phase, then go back to the ship construction menu
                        TUI.clearTerminal();
                        this.composeShipConstructionWidget().printWidget();
                        this.getShipConstructionCommand();
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
                    this.deselectTile();

                    // At the end, it goes back to asking again a new
                    // component selection command
                    TUI.clearTerminal();
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
                    List<ClientComponent> reservedComponents = this.model.getState().getReservedComponents();

                    // If the currently selected tile is from the reserved components, then
                    // just don't do anything and leave it in the reserved components list
                    if (!this.isSelectedTileReserved) {
                        // Puts the selected component in the reserved tiles
                        // If it can't, then it'll ask the user to do something else
                        if (reservedComponents.size() < 2) {
                            // Adding the currently selected component to the reserved component list
                            this.model.getState().reserveTile(this.selectedComponent);

                            // Setting the currently selected tile as non-visible, since the player decided
                            // to reserve it and thus place it in the near future
                            this.selectedComponent.setIsVisible(false);
                            this.selectedComponent = null;

                            // At the end, it goes back to asking again a new
                            // component selection command
                            TUI.clearTerminal();
                            this.generateSelectableComponentsWidget();
                            this.selectableComponentsWidget.printWidget();
                            this.getComponentSelectionCommand();
                        }
                        else {
                            // Otherwise, it means that the user already has 2 reserved tiles,
                            // therefore he cannot store any additional ones
                            System.out.println(PrintUtils.addColor("ERROR: You already have 2 (max) reserved tiles! Use them before storing others!", ANSIColors.RED));

                            // Go back to asking the user what to do with the selected component
                            TUI.clearTerminal();
                            this.composeShipConstructionWidget().printWidget();
                            this.getShipConstructionCommand();
                        }
                    }
                    else {
                        // Since the current component is from the reserved component list, then
                        // just go ahead and remove it from the selected component attribute
                        this.selectedComponent = null;

                        // And go back to the component selection screen
                        TUI.clearTerminal();
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

                    // Deselecting the tile so that it clears the currently
                    // selected component (since it just got placed)
                    this.deselectTile();

                    // At the end, it goes back to asking again a new
                    // component selection command
                    TUI.clearTerminal();
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            );
            shipConstructionCommand.appendString("Place Selected Tile");
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
        this.selectedComponent = this.model.getState().getConstructionShipComponents().get(idx);

        // Then generate the selected component widget
        this.generateSelectedComponentWidget();

        // After getting the player's chosen tile index, it gets sent to the server who
        // will then validate whether the tile can be selectable or not and, from here, the client
        // will then execute either the onSuccess or onError lambda based on the server's response
        this.currCommand = new CommandCTX(
            "selectTile",
            () -> {
                // Setting the selected component as flipped since it was
                // just uncovered by the current player
                this.selectedComponent.setAsFlipped();

                // Move to the ship construction TUI by recomposing it and
                // ask the user what to do with the selected component
                TUI.clearTerminal();
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
        );

        int construction_i = idx / DEFAULT_COMPONENT_COLS;
        int construction_j = idx % DEFAULT_COMPONENT_COLS;

        this.client.sendMessage(new SelectTile(this.playerNickname, construction_i, construction_j));
    }

    /**
     * Deselects the currently selected tile, leaving it available for other players
     */
    private void deselectTile() {
        this.selectedComponent = null;
        this.selectableComponentsWidget = null;
        this.isSelectedTileReserved = false;
    }

    /**
     * Handles the addition of the selected component to the current player's client ship
     */
    private void handleClientComponentAddition() {
        Pair<Integer, Integer> componentPosition;

        // Adding the client component both to the client ship and
        // to the ClientShipConstructionState to send it later when the
        // ship is completed and needs to be sent to the server for validation
        if (this.selectedComponent != null) {
            // Getting the component's coordinates
            componentPosition = this.getComponentCoordinates();

            // Adding the client component to the ship descriptor inside ClientShipConstructionState
            this.model.getState().placeTile(
                this.selectedComponent,
                componentPosition.getKey(),
                componentPosition.getValue()
            );

            // And then adding the currently selected component at those coordinates
            // in the current player's ship
            this.model.getShip().addComponent(
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
                this.model.getState().getReservedComponents().remove(this.selectedComponent);
            }

            // Updating the ship widget
            this.shipWidget = this.model.getShip().generateWidget();
        }
    }

    /**
     * @return A pair containing the current player's chosen component coordinates to use
     */
    private Pair<Integer, Integer> getComponentCoordinates() {
        boolean validCoordinate;
        int i, j;

        // Getting the row --> i
        do {
            System.out.print("Insert row where to put the selected component: ");

            i = this.scanner.nextInt();
            validCoordinate = (i > 0 && i < ClientShip.getGridDimensions().getKey());

            if (!validCoordinate) {
                System.out.println(PrintUtils.addColor("ERROR: Given row is out of bounds (range is [0, " + ClientShip.getGridDimensions().getKey() + "))", ANSIColors.RED));
            }
        }
        while (!validCoordinate);

        // Getting the col --> j
        do {
            System.out.print("Insert column where to put the selected component: ");

            j = this.scanner.nextInt();
            validCoordinate = (j > 0 || j < ClientShip.getGridDimensions().getValue());

            if (!validCoordinate) {
                System.out.println(PrintUtils.addColor("ERROR: Given column is out of bounds (range is [0, " + ClientShip.getGridDimensions().getValue() + "))", ANSIColors.RED));
            }
        }
        while (!validCoordinate);

        return new Pair<>(i, j);
    }

    /**
     * Sends the ship to the server for evaluation
     */
    private void sendShipConfirmation() throws Exception {
        this.currCommand = new CommandCTX(
            "sendShipConfirmation",
            () -> {
                // TODO: onSuccess --> Go to PopulateShip phase
                System.out.println(PrintUtils.addColor("SHIP IS VALID --> Next: PopulateShip", ANSIColors.BRIGHT_GREEN));
            },
            () -> {
                // TODO: onError --> Go to FixShip phase
                System.out.println(PrintUtils.addColor("SHIP IS INVALID --> Next: FixShip", ANSIColors.BRIGHT_RED));
            }
        );

        this.client.sendMessage(
            new SendShipConfirmation(
                this.model.getNickname(),
                this.model.getState().getCreatedShip()
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
        int reservedComponentAmount = this.model.getState().getReservedComponents().size();
        for (i = 0; i < reservedComponentAmount; i++) {
            // Adding a single space just for padding
            this.reservedComponentsWidget.appendString(PrintUtils.SPACE);
            this.reservedComponentsWidget.appendString("[SLOT " + (i + 1) + "]");
            this.reservedComponentsWidget.appendScreen(this.model.getState().getReservedComponents().get(i).generateWidget().getScreen());
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
        List<ClientComponent> clientComponents = this.model.getState().getConstructionShipComponents();

        int availableComponents = clientComponents.size();
        int iteratedComponents = 0;

        for (int i = 0; i < ShipConstructionTUI.DEFAULT_COMPONENT_ROWS; i++) {
            for (int j = 0; j < ShipConstructionTUI.DEFAULT_COMPONENT_COLS; j++) {
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
     * @return The TUI page where it shows this player's current ship, the
     *         currently selected component and the player's reserved components.
     * (NOTE: This method only recomposes the widgets together, each widget is updated separately)
     */
    private WidgetTUI composeShipConstructionWidget() {
        // Ensuring each widget is updated
        this.generateSelectedComponentWidget();
        this.generateReservedComponentWidget();
        this.shipWidget = this.model.getShip().getShipGridWidget();

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
        int selectableComponentsAmount = this.model.getState().getConstructionShipComponents().size();
        String input;
        int idx = -1;

        do {
            System.out.print("Enter tile index (between 0 and " + (selectableComponentsAmount - 1) + ") or 'r' for random: ");
            input = this.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("r")) {
                idx = random.nextInt(selectableComponentsAmount);
            }
            else {
                try {
                    int tmpIndex = Integer.parseInt(input);

                    if (tmpIndex < 0 || tmpIndex > selectableComponentsAmount) {
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

            if (idx >= 0 && !this.model.getState().getConstructionShipComponents().get(idx).isVisible()) {
                System.out.println(PrintUtils.addColor("ERROR: This component is already selected by someone else.", ANSIColors.RED));
                idx = -1;   // Reset to retry
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

        componentRetrieved = false;

        if (this.model.getState().getReservedComponents().isEmpty()) {
            throw new IllegalArgumentException(PrintUtils.addColor("ERROR: You don't have any reserved components!", ANSIColors.RED));
        }

        do {
            System.out.print("Enter reserved tile index to select (0 = Slot1, 1 = Slot2): ");

            try {
                this.selectedComponent = this.model.getState().getReservedComponents().get(this.scanner.nextInt());
                this.isSelectedTileReserved = true;
                componentRetrieved = true;
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println(PrintUtils.addColor("ERROR: Wrong reserved tile index.", ANSIColors.RED));
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
            input = this.scanner.nextLine().trim();

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
     * Stays in the menu until a valid command was given
     */
    public void getComponentSelectionCommand() {
        boolean existingCommandSelected;

        synchronized (this.ioLock) {
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

        synchronized (this.ioLock) {
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
