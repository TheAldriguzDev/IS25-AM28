package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientStructural;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Network.Messages.SelectTile;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ShipConstructionTUI extends TUI {
    // Testing
    public static void main(String[] args) {
        List<ClientComponent> selectableComponents = new ArrayList<>();

        List<Integer> connectors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            connectors.add(3);
        }

        for (int i = 0; i < 152; i++) {
            selectableComponents.add(new ClientStructural(i, connectors));
        }

        ShipConstructionTUI shipConstructionTUI = new ShipConstructionTUI(selectableComponents);
        shipConstructionTUI.showTUI();
    }

    // Default component matrix (row, col) dimensions
    public static final int DEFAULT_COMPONENT_ROWS = 8;
    public static final int DEFAULT_COMPONENT_COLS = 19;
    public static final int COMMANDS_PER_COLUMN = 2;

    private WidgetTUI selectableComponentsWidget;
    private WidgetTUI componentStagingAreaWidget;
    private WidgetTUI selectedComponentWidget;
    private WidgetTUI coveredComponentWidget;
    private WidgetTUI cardDeckVisualizationWidget;
    private WidgetTUI shipWidget;
    private InputWidgetTUI shipConstructionCommandsWidget;

    private List<ClientComponent> selectableComponents;
    private Scanner scanner;
    private Random random;

    // TODO: 1) 19x8 (=152) Component selection matrix
    //       2) Ship insertion view + Component staging area
    //       3) Deck visualizer
    //       4) Command panel (at the bottom)

    // Constructor
    public ShipConstructionTUI(List<ClientComponent> selectableComponents) {
        // TODO:
        super(null);

        // Some initializations
        this.scanner = new Scanner(System.in);
        this.random = new Random();

        // Generating the covered component widget
        this.generateCoveredComponentWidget();

        // Initializing all components
        this.selectableComponents = selectableComponents;

        // Initializing all the selectable components as covered
        this.selectableComponentsWidget = new WidgetTUI();
        this.generateComponentMatrix();

        // Initializing the staging area as empty
        this.componentStagingAreaWidget = new WidgetTUI();
        this.selectedComponentWidget = null;

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
    private void generateComponentMatrix() {
        if (
                (ShipConstructionTUI.DEFAULT_COMPONENT_ROWS > 0)
             && (ShipConstructionTUI.DEFAULT_COMPONENT_COLS > 0)
             && ((ShipConstructionTUI.DEFAULT_COMPONENT_ROWS * ShipConstructionTUI.DEFAULT_COMPONENT_COLS) == this.selectableComponents.size())
        ) {
            List<String> allRows = new ArrayList<>();
            List<List<String>> row;

            for (int i = 0; i < ShipConstructionTUI.DEFAULT_COMPONENT_ROWS; i++) {
                row = new ArrayList<>();

                for (int j = 0; j < ShipConstructionTUI.DEFAULT_COMPONENT_COLS; j++) {
                    ClientComponent currComponent = this.selectableComponents.get(((19 * i) + j));
                    List<String> screen = new ArrayList<>();

                    // Adding the current component ID at the top of the screen
                    screen.add("(" + currComponent.getID() + ")");

                    if (currComponent.isFlipped()) {
                        // TODO: This is a full update, maybe there's the need to make this a differential update as well
                        screen.addAll(currComponent.generateWidget().getScreen());
                    }
                    else {
                        screen.addAll(this.coveredComponentWidget.getScreen());
                    }

                    row.add(WidgetTUI.fillScreenWithSpaces(screen));
                }

                // Composing the current component widget row
                allRows.addAll(WidgetTUI.composeScreensHorizontally(row));
            }

            // Composing all rows into the final single matrix
            this.selectableComponentsWidget.setScreen(allRows);
            this.selectableComponentsWidget.wrapWidgetWithBorder();
        }
    }

    /**
     * Initializes the commands in the ship construction input widget
     */
    private void initShipConstructionCommands() {
        // Initializes (only once!) the commands inside the ship construction inpuy widget
        if (this.shipConstructionCommandsWidget.getCommandMap() == null || this.shipConstructionCommandsWidget.getCommandMap().isEmpty()) {
            CommandWidgetTUI shipConstructionCommand;

            // (1) - Select Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "1",
                () -> {
                    // Asks for the coordinates and then selects the tile
                    try {
                        this.selectNewTile();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Select Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (1) - Deselect Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "2",
                () -> {
                    // Deselects the component that is currently taken by this user

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Deselect Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (3) - Reserve Tile in Slot 1
            shipConstructionCommand = new CommandWidgetTUI(
                "3",
                () -> {
                    // Puts this component in the reserved slot 1

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Reserve Tile in Slot 1");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (4) - Reserve Tile in Slot 2
            shipConstructionCommand = new CommandWidgetTUI(
                "4",
                () -> {
                    // Puts this component in the reserved slot 1

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Reserve Tile in Slot 2");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (5) - Place Selected Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "5",
                () -> {
                    // Puts the currently selected tile in the ship
                    // Need to ask for the coordinates

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Place Selected Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (6) - Place Reserved Tile from Slot 1
            shipConstructionCommand = new CommandWidgetTUI(
                "6",
                () -> {
                    // Puts the currently selected tile in the ship
                    // Need to ask for the coordinates

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Place Reserved Tile from Slot 1");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (7) - Place Reserved Tile from Slot 2
            shipConstructionCommand = new CommandWidgetTUI(
                "7",
                () -> {
                    // Puts the currently selected tile in the ship
                    // Need to ask for the coordinates

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Place Reserved Tile from Slot 1");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (8) - Flip Timer
            shipConstructionCommand = new CommandWidgetTUI(
                "8",
                () -> {
                    // Flips the timer (if possible), otherwise throws an error saying
                    // that time it is currently flowing (i.e.: cannot be flipped until it finishes)

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Flip Timer");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (9) - Visualize Deck
            shipConstructionCommand = new CommandWidgetTUI(
                "9",
                () -> {
                    // Visualizes the selected deck from the board (if someone is not already observing it)
                    boolean existingCommandSelected;
                    Scanner scanner = new Scanner(System.in);

                    // TODO: Synchronize on the ClientDeck when a player is observing a particular

                    TUI.clearTerminal();
                    this.cardDeckVisualizationWidget.printWidget();
                    System.out.println("Enter any key to go back...");
                    scanner.nextLine();

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Flip Timer");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

            // (10) - Finish Ship
            shipConstructionCommand = new CommandWidgetTUI(
                "10",
                () -> {
                    // REQUIRE CONFIRMATION BEFORE COMPLETING AND SENDING THE SHIP

                    // If confirmed, then send the ship and wait for the others

                    // At the end, it goes back to asking again a new
                    // ship construction command
                    this.getShipConstructionCommand();
                }
            );
            shipConstructionCommand.appendString("Finish Ship");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);
        }
    }

    private void selectNewTile() throws Exception {
        String input;
        int idx = -1;

        do {
            System.out.print("Enter tile index (0-151) or 'r' for random: ");
            input = this.scanner.nextLine().trim();
            idx = -1;

            if (input.equalsIgnoreCase("r")) {
                idx = random.nextInt(152);
            } else {
                try {
                    int tmpIndex = Integer.parseInt(input);
                    if (tmpIndex < 0 || tmpIndex > 151) {
                        System.out.println("Invalid index: must be between 0 and 151.");
                    } else {
                        idx = tmpIndex;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: enter a number 0–151 or 'r'.");
                }
            }

            if (idx >= 0 && !this.model.getState().getConstructionShipComponents().get(idx).isVisible()) {
                System.out.println("Invalid index: the given component is already selected by someone else.");
                idx = -1; // reset to retry
            }
        } while (idx < 0);

        // We have the index of the tile that we want to use --> we need to send the command to the server and if the tile
        // is selectable we can execute the correct command to continue the player input

        int finalIdx = idx;
        this.currCommand = new CommandCTX(
            "selectTile",
            () -> {
                // If the selection was successful we jump to the method that handles the operations
                try {
                    this.handleSelectedTile(this.model.getState().getConstructionShipComponents().get(finalIdx));
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            },
            () -> {
                // If an error occurred we re-execute the command
                try {
                    this.selectNewTile();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );

        int construction_i = idx / 19;
        int construction_j = idx % 19;

        this.client.sendMessage(new SelectTile(this.playerNickname, construction_i, construction_j));
    }

    /**
     * Method used to handle the selected component by the client
     * */
    private void handleSelectedTile(ClientComponent selectedComponent) throws IOException {
        System.out.println("Perfect, we can handle the selected tile! HURRAAAAA");
    }

    /**
     * Stays in the menu until a valid command was given
     */
    public void getShipConstructionCommand() {
        boolean existingCommandSelected;

        synchronized (this.ioLock) {
            do {
                System.out.println();
                System.out.println("Available Ship construction commands:");

                existingCommandSelected = this.shipConstructionCommandsWidget.selectCommand(DEFAULT_INPUT_PREFIX);

                if (!existingCommandSelected) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            while (!existingCommandSelected);
        }
    }

    @Override
    public void showTUI() {
        this.tui = WidgetTUI.composeTwoWidgetsHorizontally(
            WidgetTUI.composeTwoWidgetsHorizontally(
                this.selectedComponentWidget,
                this.selectableComponentsWidget
            ),
            this.componentStagingAreaWidget
        );

        this.tui.printWidget();
        this.getShipConstructionCommand();
    }
}
