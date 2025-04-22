package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientStructural;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Network.Messages.SelectTile;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShipConstructionTUI extends TUI {
    // Default component matrix (row, col) dimensions
    public static final int DEFAULT_COMPONENT_ROWS = 8;
    public static final int DEFAULT_COMPONENT_COLS = 19;
    public static final int COMMANDS_PER_COLUMN = 2;

    // All widgets that compose the three distinct TUIs, which are:
    //  1) Component selection
    //  2) Ship construction
    //  3) Deck visualization
    private WidgetTUI selectableComponentsWidget;
    private WidgetTUI componentStagingAreaWidget;
    private WidgetTUI selectedComponentWidget;
    private WidgetTUI coveredComponentWidget;
    private WidgetTUI cardDeckVisualizationWidget;
    private WidgetTUI shipWidget;

    // All input widgets
    private InputWidgetTUI componentSelectionCommandsWidget;
    private InputWidgetTUI shipConstructionCommandsWidget;

    private ShipConstructionDTO shipConstructionDTO;

    // Constructor
    public ShipConstructionTUI(ClientModel model, List<ClientComponent> selectableComponents) {
        super(model);

        // Generating the covered component widget
        this.generateCoveredComponentWidget();

        // Initializing all selectable components
        this.selectableComponentsWidget = new WidgetTUI();

        // Initializing the staging area as empty
        this.componentStagingAreaWidget = new WidgetTUI();
        this.selectedComponentWidget = null;

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
        List<List<String>> row;
        ClientComponent currComponent = null;
        List<ClientComponent> clientComponents = this.model.getState().getConstructionShipComponents();
        int selectableComponentsAmount = clientComponents.size();
        int printedComponentCounter = 0;

        for (int i = 0; i < ShipConstructionTUI.DEFAULT_COMPONENT_ROWS; i++) {
            row = new ArrayList<>();

            for (int j = 0; j < ShipConstructionTUI.DEFAULT_COMPONENT_COLS; j++) {
                // Exit the printing phase if all selectable components were printed
                if (printedComponentCounter == selectableComponentsAmount) {
                    break;
                }

                currComponent = clientComponents.get((i * DEFAULT_COMPONENT_COLS) + j);
                List<String> screen = new ArrayList<>();

                // Adding the current component ID at the top of the screen
                screen.add("(" + currComponent.getID() + ")");

                if (currComponent.isFlipped()) {
                    // TODO: This is a full update, maybe there's the need to make this a differential update as well
                    //       (NOTE: It can be done by storing the generated widget and updating it only when necessary)
                    screen.addAll(currComponent.generateWidget().getScreen());
                }
                else {
                    screen.addAll(this.coveredComponentWidget.getScreen());
                }

                row.add(WidgetTUI.fillScreenWithSpaces(screen));
            }

            // Composing the current component widget row
            allRows.addAll(WidgetTUI.composeScreensHorizontally(row));

            // Exit the printing phase if all selectable components were printed
            if (printedComponentCounter == selectableComponentsAmount) {
                break;
            }
        }

        // Composing all rows into the final single matrix
        this.selectableComponentsWidget.setScreen(allRows);
        this.selectableComponentsWidget.wrapWidgetWithBorder();
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
                    // Asks for the coordinates and then selects the tile
                    try {
                        this.selectTile();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // Then goes to the ship construction TUI and
                    // waits for a command to handle the selected tile
                    this.getShipConstructionCommand();
                }
            );
            componentSelectionCommand.appendString("Select Tile");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

            // (2) - Flip Timer
            componentSelectionCommand = new CommandWidgetTUI(
                "2",
                () -> {
                    // Flips the timer (if possible), otherwise throws an error saying
                    // that time it is currently flowing (i.e.: cannot be flipped until it finishes)

                    // if hourglass.flip() == true, then notify the user and reprint

                    // TODO: Figure out where to put the hourglass
                    // TODO: Substitute the "true" here with "timer.flip()"
                    if (true) {
                        System.out.println(PrintUtils.addColor("Timer flipped successfully!", ANSIColors.BRIGHT_MAGENTA));
                    }
                    else {
                        System.out.println(PrintUtils.addColor("Someone else already flipped the timer, you must wait that it ends before flipping it again!", ANSIColors.BRIGHT_MAGENTA));
                    }

                    // Reprints the component selection widget to terminal
                    TUI.clearTerminal();
                    this.selectableComponentsWidget.printWidget();

                    // Asking the user for the next command in
                    // the component selection input widget
                    this.getComponentSelectionCommand();
                }
            );
            componentSelectionCommand.appendString("Flip Timer");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

            // (3) - Visualize Deck
            componentSelectionCommand = new CommandWidgetTUI(
                "3",
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
                    this.getComponentSelectionCommand();
                }
            );
            componentSelectionCommand.appendString("Visualize Deck");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

            // (4) - Finish Ship
            componentSelectionCommand = new CommandWidgetTUI(
                "4",
                () -> {
                    if (this.getShipFinishedConfirmation()) {
                        // TODO: Move to the next game phase since this player finished to build the ship
                    }
                    else {
                        // If player refuses to send the confirmation to conclude
                        // the ship building phase, then go back to the
                        // component selection menu
                        this.generateSelectableComponentsWidget();
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

                    // At the end, it goes back to asking again a new
                    // component selection command
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            );
            shipConstructionCommand.appendString("Deselect Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (2) - Reserve Tile in Slot 1
            shipConstructionCommand = new CommandWidgetTUI(
                "2",
                () -> {
                    // Puts this component in the reserved slot 1

                    // At the end, it goes back to asking again a new
                    // component selection command
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            );
            shipConstructionCommand.appendString("Reserve Tile in Slot 1");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (3) - Reserve Tile in Slot 2
            shipConstructionCommand = new CommandWidgetTUI(
                "3",
                () -> {
                    // Puts this component in the reserved slot 1

                    // At the end, it goes back to asking again a new
                    // component selection command
                    this.generateSelectableComponentsWidget();
                    this.selectableComponentsWidget.printWidget();
                    this.getComponentSelectionCommand();
                }
            );
            shipConstructionCommand.appendString("Reserve Tile in Slot 2");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (4) - Place Selected Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "4",
                () -> {
                    // Puts the currently selected tile in the ship
                    // Need to ask for the coordinates

                    // At the end, it goes back to asking again a new
                    // component selection command
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

        // After getting the player's chosen tile index, it gets sent to the server who
        // will then validate whether the tile can be selectable or not and, from here, the client
        // will then execute either the onSuccess or onError lambda based on the server's response
        this.currCommand = new CommandCTX(
            "selectTile",
            () -> {
                // If the selection was successful we jump to the method that handles the operations
                try {
                    this.handleSelectedTile(this.model.getState().getConstructionShipComponents().get(idx));
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            },
            () -> {
                // If an error occurred we re-execute the command
                try {
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
     * Method used to handle the selected component by the client
     */
    private void handleSelectedTile(ClientComponent selectedComponent) throws IOException {
        // Update the selected component widget to display the currently selected one
        this.selectedComponentWidget = selectedComponent.generateWidget();

        // Adding some padding and wrapping the final widget
        this.selectedComponentWidget.addPadding(2, 2, 2, 2);
        this.selectedComponentWidget.wrapWidgetWithBorder();

        // Move to the ship construction TUI by recomposing it
        TUI.clearTerminal();
        this.composeShipConstructionWidget().printWidget();

        // Ask the user what to do with the selected component
        this.getShipConstructionCommand();
    }

    /**
     * @return The TUI page where it shows this player's current ship, the
     *         currently selected component and the player's reserved components.
     * (NOTE: This method only recomposes the widgets together, each widget is updated separately)
     */
    private WidgetTUI composeShipConstructionWidget() {
        return WidgetTUI.composeTwoWidgetsHorizontally(
            WidgetTUI.composeTwoWidgetsHorizontally(
                this.selectedComponentWidget,
                this.shipWidget
            ),
            this.componentStagingAreaWidget
        );
    }

    /**
     * @return The current player's chosen tile index
     */
    private int getTileIndex() {
        int selectableComponentsAmount = this.shipConstructionDTO.getAllComponents().size();
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

                    if (tmpIndex < 0 || tmpIndex > 151) {
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
                System.out.println(PrintUtils.addColor("ERROR: This component is already selected by someone else. You must wait that other player to make it available again", ANSIColors.RED));
                idx = -1;   // Reset to retry
            }
        }
        while (idx < 0);

        return idx;
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
        this.shipConstructionDTO = shipConstruction;
        this.generateSelectableComponentsWidget();
        this.selectableComponentsWidget.printWidget();

        // Then, ask the user for a command
        this.getComponentSelectionCommand();
    }
}
