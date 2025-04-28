package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Network.Messages.*;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    private WidgetTUI shipWidget;
    private WidgetTUI cardDeckVisualizationWidget;

    // TODO: Create network message to lock the subdeck cards when a player is watching them

    // TODO: Implement what happens when a player reconnects in a game (retrieve his information and others' as well)
    // TODO: Implement these widgets & relative functionalities

    private WidgetTUI otherPlayerShipWidget;        // TODO: + Add the show other ship command
    private WidgetTUI playersThatFinishedWidget;

    // All input widgets
    private InputWidgetTUI componentSelectionCommandsWidget;
    private InputWidgetTUI shipConstructionCommandsWidget;
    private InputWidgetTUI cardDeckSelectionWidget;

    // Placeholder for the currently selected component
    private ClientComponent selectedComponent;
    private boolean isSelectedTileReserved;
    private List<ClientEventCard> selectedSubdeck;

    // Constructor
    public ShipConstructionTUIPage(ClientTUI_v2 clientTUI) {
        super(clientTUI);

        // Initializing the selected component
        this.selectedComponent = null;
        this.selectedComponentWidget = null;
        this.isSelectedTileReserved = false;

        // Initializing the subdeck view
        this.selectedSubdeck = null;

        // Initializing various widgets
        this.selectableComponentsWidget = null;
        this.reservedComponentsWidget = null;
        this.cardDeckVisualizationWidget = null;
        this.otherPlayerShipWidget = null;
        this.playersThatFinishedWidget = null;

        // Generating the covered component widget and the empty component widget
        this.generateCoveredComponentWidget();
        this.generateEmptyComponentWidget();

        // Initializing the component selection command widget
        this.componentSelectionCommandsWidget = new InputWidgetTUI();
        this.componentSelectionCommandsWidget.setNewReader(System.in);
        this.componentSelectionCommandsWidget.setColumnGroupingAmount(COMMANDS_PER_COLUMN);
        this.initComponentSelectionCommands();

        // Initializing the ship construction command widget
        this.shipConstructionCommandsWidget = new InputWidgetTUI();
        this.shipConstructionCommandsWidget.setNewReader(System.in);
        this.shipConstructionCommandsWidget.setColumnGroupingAmount(COMMANDS_PER_COLUMN);
        this.initShipConstructionCommands();

        // Initializing the card deck selection command widget
        this.cardDeckSelectionWidget = new InputWidgetTUI();
        this.cardDeckSelectionWidget.setNewReader(System.in);
        this.cardDeckSelectionWidget.setColumnGroupingAmount(COMMANDS_PER_COLUMN);
        this.initCardDeckSelectionCommands();
    }

    /**
     * Initializes the commands in the component selection input widget
     */
    private void initComponentSelectionCommands() {
        CommandWidgetTUI componentSelectionCommand;

        if (this.componentSelectionCommandsWidget == null) {
            // Initializing a new component selection command input widget
            this.componentSelectionCommandsWidget = new InputWidgetTUI();
        }
        else {
            // Resetting the command widget and keeping only the command grouping factor
            if (this.componentSelectionCommandsWidget.getCommandMap() != null) {
                this.componentSelectionCommandsWidget.getCommandMap().clear();
            }

            this.componentSelectionCommandsWidget.resetScreenAndDimensions();
        }

        // Commands 1 through 4 are excluded for players that
        // already finished building their ship
        if (!this.clientTUI.getModel().getState().getPlayerFinishedBuildingShip(this.clientTUI.getPlayerNickname())) {
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
                        this.goToShipConstructionMenu();

                        // Then regenerate the ship construction commands widget
                        // to restore any commands that were not supposed to be seen
                        // when the selected component was a reserved component
                        this.initShipConstructionCommands();
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

            // (3) - Finish Ship
            componentSelectionCommand = new CommandWidgetTUI(
                "3",
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
                        this.goToComponentSelectionMenu();
                    }
                }
            );
            componentSelectionCommand.appendString("Finish Ship");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);
        }

        // Command is added only if the current game
        // difficulty level is not 0 (i.e.: Test Flight)
        if (this.clientTUI.getModel().getDifficultyLevel() != 0) {
            // (4) - Flip Timer
            componentSelectionCommand = new CommandWidgetTUI(
                "4",
                () -> {
                    try {
                        this.flipTimer();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            );
            componentSelectionCommand.appendString("Flip Timer");
            this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);
        }

        // (5) - Visualize Deck
        componentSelectionCommand = new CommandWidgetTUI(
            "5",
            () -> {
                // TODO: Write the Network message SelectSubDeck to select the EventCard IDs
                //       to lock when a player is observing them

                // Getting the player's chosen subdeck to see
                this.getCardDeckSelectionCommand();

                // Printing the selected subdeck
                this.generateCardDeckVisualizationWidget();
                this.cardDeckVisualizationWidget.printWidget();

                System.out.print("Press [ANY KEY] and the press [ENTER] to go back...");
                Scanner s = new Scanner(System.in);
                s.next();

                // Go back to the component selection menu
                this.goToComponentSelectionMenu();
            }
        );
        componentSelectionCommand.appendString("Visualize Deck");
        this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);

        // (6) - Visualize Other Ships
        componentSelectionCommand = new CommandWidgetTUI(
            "6",
            () -> {
                // TODO
            }
        );
        componentSelectionCommand.appendString("Visualize Other Ships");
        this.componentSelectionCommandsWidget.addCommand(componentSelectionCommand);
    }

    /**
     * Initializes the commands in the ship construction input widget
     */
    private void initShipConstructionCommands() {
        CommandWidgetTUI shipConstructionCommand;

        if (this.shipConstructionCommandsWidget == null) {
            // Initializing a new ship construction command input widget
            this.shipConstructionCommandsWidget = new InputWidgetTUI();
        }
        else {
            // Resetting the command widget and keeping only the command grouping factor
            if (this.shipConstructionCommandsWidget.getCommandMap() != null) {
                this.shipConstructionCommandsWidget.getCommandMap().clear();
            }
            this.shipConstructionCommandsWidget.resetScreenAndDimensions();
        }

        // Command is added only if the currently
        // selected tile is not a reserved tile
        if (!this.isSelectedTileReserved) {
            // (1) - Deselect Tile
            shipConstructionCommand = new CommandWidgetTUI(
                "1",
                () -> {
                    // Deselects the component that is currently taken by this user
                    try {
                        this.deselectTile();
                    }
                    catch (Exception e) {
                        // System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                        System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));

                        // Go back to the component selection menu and ask for a new command
                        this.goToComponentSelectionMenu();
                    }
                }
            );
            shipConstructionCommand.appendString("Deselect Tile");
            this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);
        }

        // (2) - Reserve Tile
        shipConstructionCommand = new CommandWidgetTUI(
            "2",
            () -> {
                List<ClientComponent> reservedComponents = this.clientTUI.getModel().getState().getReservedComponents();

                // Puts the selected component in the reserved tiles
                // If it can't, then it'll ask the user to do something else
                if (reservedComponents.size() < 2) {
                    // Adding the currently selected component to the reserved component list
                    this.clientTUI.getModel().getState().reserveTile(this.selectedComponent);

                    // Removing the selected tile from the selected component slot
                    // since the user decided to reserve it for the future
                    this.selectedComponent = null;

                    // At the end, it goes back to asking again a new
                    // component selection command
                    this.goToComponentSelectionMenu();
                }
                else {
                    // Otherwise, it means that the user already has 2 reserved tiles,
                    // therefore he cannot store any additional ones
                    System.out.println(PrintUtils.addColor("ERROR: You already have 2 (max) reserved tiles! Use them before storing others!", ANSIColors.RED));

                    // Go back to asking the user what to do with the selected component
                    this.goToShipConstructionMenu();
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
                try {
                    this.handleClientComponentAddition();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // At the end, it goes back to asking again a new
                // component selection command
                this.goToComponentSelectionMenu();
            }
        );
        shipConstructionCommand.appendString("Place Selected Tile");
        this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

        // (4) - Rotate Right
        shipConstructionCommand = new CommandWidgetTUI(
            "4",
            () -> {
                // Rotates the current component
                this.selectedComponent.rotateRight();

                // And then it goes back to the ship construction menu
                this.goToShipConstructionMenu();
            }
        );
        shipConstructionCommand.appendString("Rotate Right");
        this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);

        // (5) - Rotate Left
        shipConstructionCommand = new CommandWidgetTUI(
            "5",
            () -> {
                // Rotates the current component
                this.selectedComponent.rotateLeft();

                // And then it goes back to the ship construction menu
                this.goToShipConstructionMenu();
            }
        );
        shipConstructionCommand.appendString("Rotate Left");
        this.shipConstructionCommandsWidget.addCommand(shipConstructionCommand);
    }

    /**
     * Initializes all commands to select the decks in
     * the ship construction state
     */
    private void initCardDeckSelectionCommands() {
        CommandWidgetTUI deckCommand;
        int subdeckAmount = 4;
        int visibleSubdecks = subdeckAmount - 1;
        int subdeckSize = this.clientTUI.getModel().getState().getEventCards().size() / subdeckAmount;

        if (this.cardDeckSelectionWidget == null) {
            // Initializing a new card deck selection command input widget
            this.cardDeckSelectionWidget = new InputWidgetTUI();
        }
        else {
            // Resetting the command widget and keeping only the command grouping factor
            if (this.cardDeckSelectionWidget.getCommandMap() != null) {
                this.cardDeckSelectionWidget.getCommandMap().clear();
            }

            this.cardDeckSelectionWidget.resetScreenAndDimensions();
        }

        // Adding default command to go back to the component selection menu
        deckCommand = new CommandWidgetTUI(
                "-1",
                this::goToComponentSelectionMenu
        );
        deckCommand.appendString("Go back");
        this.cardDeckSelectionWidget.addCommand(deckCommand);

        // Returning for each selected subdeck the corresponding
        // view of the cards that comprises each
        for (int i = 0; i < visibleSubdecks; i++) {
            // (i) - Select subdeck #(i + 1)
            int finalI = i;

            deckCommand = new CommandWidgetTUI(
                "" + i,
                () -> {
                    int start = (finalI * subdeckSize);
                    int end = start + subdeckSize;

                    // View of only the cards belonging to that subdeck
                    this.selectedSubdeck = this.clientTUI.getModel().getState().getEventCards().subList(start, end);
                }
            );
            deckCommand.appendString("Select subdeck #" + (i + 1));
            this.cardDeckSelectionWidget.addCommand(deckCommand);
        }
    }

    /**
     * Prints the widget and the commands to go back
     * to the component selection menu
     */
    private void goToComponentSelectionMenu() {
        clearTerminal();
        this.composeSelectableComponentsMenuWidget().printWidget();
        this.getComponentSelectionCommand();
    }

    /**
     * Prints the widget and the commands to go back
     * to the ship construction menu
     */
    private void goToShipConstructionMenu() {
        clearTerminal();
        this.composeShipConstructionWidget().printWidget();
        this.getShipConstructionCommand();
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

        this.clientTUI.getVirtualView().sendMessage(
            new SelectTile(
                this.clientTUI.getPlayerNickname(),
                construction_i,
                construction_j
            )
        );
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

                    // At the end, it goes back to asking again a new
                    // component selection command
                    clearTerminal();
                    this.composeSelectableComponentsMenuWidget().printWidget();
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

        this.clientTUI.getVirtualView().sendMessage(
            new DeselectTile(
                this.clientTUI.getPlayerNickname(),
                construction_i,
                construction_j
            )
        );
    }

    /**
     * Handles the addition of the selected component to the current player's client ship
     */
    private void handleClientComponentAddition() throws Exception {
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
                    Optional<ClientShip> optionalShip = this.clientTUI.getModel().getShipOfPlayer(this.clientTUI.getPlayerNickname());

                    if (optionalShip.isPresent()) {
                        optionalShip.get().addComponent(
                            this.selectedComponent,
                            componentPosition.getKey(),
                            componentPosition.getValue()
                        );
                    }

                    // Exit the loop only if the component can actually
                    // be placed at the given coordinates
                    correctCoordinates = true;
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor(e.getMessage(), ANSIColors.RED));
                }
            }
            while (!correctCoordinates);

            // Broadcasting to all players that the current player placed
            // his currently selected tile on his ship
            this.clientTUI.getVirtualView().sendMessage(
                new PlaceTile(
                    this.clientTUI.getPlayerNickname(),
                    this.selectedComponent.getID(),
                    this.selectedComponent.getI(),
                    this.selectedComponent.getJ(),
                    this.selectedComponent.getDirection()
                )
            );

            // Since the component was added, it is now unusable for other
            // players thus they must not be able to see it
            this.selectedComponent.setIsVisible(false);

            // If the currently selected tile is from the reserve, then it needs
            // to be removed from the reserved component list since it was just placed
            if (this.isSelectedTileReserved) {
                this.clientTUI.getModel().getState().getReservedComponents().remove(this.selectedComponent);
                this.isSelectedTileReserved = false;
            }

            // Removing the component from the selected slot, since it was placed on the ship
            this.selectedComponent = null;

            // Updating the ship widget
            Optional<ClientShip> optionalShip = this.clientTUI.getModel().getShipOfPlayer(this.clientTUI.getPlayerNickname());
            optionalShip.ifPresent(clientShip -> this.shipWidget = clientShip.getShipGridWidget());
        }
    }

    /**
     * @return A pair of integers that represents the (row, col) indexes
     *         where the player wants to place the selected component.
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
        coordinates.put(--i, --j);
        return coordinates.entrySet().stream().toList().getFirst();
    }

    /**
     * Flips the timer and sends the relative message to broadcast it to other players
     */
    private void flipTimer() throws Exception {
        this.clientTUI.setCurrCommand(
            new CommandCTX(
                "flipTimer",
                () -> {
                    // Set the timer as flipped inside the state
                    this.clientTUI.getModel().getState().flipTimer();

                    // Go back to the component selection menu
                    clearTerminal();
                    this.composeSelectableComponentsMenuWidget().printWidget();
                    System.out.println(PrintUtils.addColor("Timer flipped successfully!", ANSIColors.BRIGHT_MAGENTA));
                    this.getComponentSelectionCommand();
                },
                () -> {
                    // Go back to the component selection menu
                    // NOTE: Error is communicated to the player through the showError() method
                    this.goToComponentSelectionMenu();
                }
            )
        );

        this.clientTUI.getVirtualView().sendMessage(
            new FlipTimer(this.clientTUI.getPlayerNickname())
        );
    }

    /**
     * Sends the ship to the server for evaluation
     */
    private void sendShipConfirmation() throws Exception {
        this.clientTUI.setCurrCommand(
            new CommandCTX(
                "sendShipConfirmation",
                () -> {
                    // If successful, then the player needs to wait that all players
                    // finish building or the hourglass timer runs out (with all flips consumed)

                    // The current player gets marked as "has sent his ship"
                    this.clientTUI.getModel().getState().setPlayerFinishedBuildingShip(this.clientTUI.getPlayerNickname());

                    // Go back to the component selection command menu where the only available
                    // commands will be: 1) Show other ship, 2) Flip timer, 3) Show card deck
                    this.goToComponentSelectionMenu();
                },
                () -> {
                    // Otherwise, if the command fails, then the player goes back to the
                    // component selection commands menu

                    // NOTE: Error is communicated to the player through the showError() method
                    this.goToComponentSelectionMenu();
                }
            )
        );

        // Sends the current player's ship when he
        // decides to finish building it
        this.clientTUI.getVirtualView().sendMessage(
            new SendShipConfirmation(
                this.clientTUI.getModel().getNickname(),
                this.clientTUI.getModel().getState().getReservedComponents().size()
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
            this.selectedComponentWidget.addPadding(0, 2, 1, 3);
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
     * Generates the cardDeckVisualizationWidget containing the player's
     * chosen cards to visualize
     */
    private void generateCardDeckVisualizationWidget() {
        // Only generate the widget if the subdeck was chosen
        if (this.selectedSubdeck != null && !this.selectedSubdeck.isEmpty()) {
            List<List<String>> allCardsScreens;
            WidgetTUI tmpCardWidget;

            // Initializations
            this.cardDeckVisualizationWidget = new WidgetTUI();
            allCardsScreens = new ArrayList<>();
            tmpCardWidget = new WidgetTUI();

            for (ClientEventCard card : this.selectedSubdeck) {
                 allCardsScreens.add(
                     card.generateWidget()
                         .addPadding(0, 1, 0, 0)
                         .getScreen()
                 );
            }

            // Composing all card widget's screens into one
            tmpCardWidget.setScreen(WidgetTUI.composeScreensHorizontally(allCardsScreens));

            // Adding a centered title
            this.cardDeckVisualizationWidget.appendString("==== SELECTED SUBDECK ====");
            this.cardDeckVisualizationWidget.setWidth(tmpCardWidget.getWidth());
            this.cardDeckVisualizationWidget.centerWidgetScreen();

            // Adding the widget screen of all the cards
            // that belong to the selected subdeck
            this.cardDeckVisualizationWidget.appendScreen(tmpCardWidget.getScreen());
            this.cardDeckVisualizationWidget.addPadding(0, 0, 0, 1);
            this.cardDeckVisualizationWidget.wrapWidgetWithBorder();
        }
    }

    /**
     * @return The widget containing the selectable components widget and the
     *         currently reserved component for the current player, thus giving
     *         the latter a full view of all the components he can interact with.
     */
    private WidgetTUI composeSelectableComponentsMenuWidget() {
        this.generateSelectableComponentsWidget();
        this.generateReservedComponentWidget();

        WidgetTUI selectableComponentsWidget =
                WidgetTUI.composeTwoWidgetsHorizontally(
                    this.reservedComponentsWidget,
                    this.selectableComponentsWidget
                );

        // If the timer can be flipped, then print it to the player
        // by appending a message to the selectable components widget
        if ( !this.clientTUI.getModel().getState().isTimeRunning()) {
            selectableComponentsWidget.appendString(
                PrintUtils.addColor("==== Hourglass can now be flipped! ====", ANSIColors.BRIGHT_MAGENTA)
            );
        }

        return selectableComponentsWidget;
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
        Optional<ClientShip> optionalShip = this.clientTUI.getModel().getShipOfPlayer(this.clientTUI.getPlayerNickname());
        optionalShip.ifPresent(clientShip -> this.shipWidget = clientShip.getShipGridWidget());

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
                    this.goToComponentSelectionMenu();
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

                // Selecting the chosen reserved component
                this.selectedComponent = this.clientTUI.getModel().getState().getReservedComponents().get(index);

                // Then removing it from the reserved component list
                // (to avoid duplicate widgets, which could confuse the player)
                if (!this.clientTUI.getModel().getState().getReservedComponents().isEmpty()) {
                    this.clientTUI.getModel().getState().getReservedComponents().remove(this.selectedComponent);
                }

                this.isSelectedTileReserved = true;
                componentRetrieved = true;

                // Regenerate the ship construction widget which removes the commands
                // that should not be seen when using a reserved tile as the selected tile
                this.initShipConstructionCommands();
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
     * Stays in the subdeck selection menu until a valid command was given
     */
    private void getCardDeckSelectionCommand() {
        boolean existingCommandSelected;

        synchronized (this.clientTUI.getIoLock()) {
            do {
                System.out.println();
                System.out.println("Available component selection commands:");

                existingCommandSelected = this.cardDeckSelectionWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

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
    public void getComponentSelectionCommand() {
        boolean existingCommandSelected;

        synchronized (this.clientTUI.getIoLock()) {
            do {
                System.out.println();
                System.out.println("Available component selection commands:");

                existingCommandSelected = this.componentSelectionCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

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

                existingCommandSelected = this.shipConstructionCommandsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

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
        this.goToComponentSelectionMenu();
    }

    @Override
    public void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer) {

    }

    @Override
    public boolean isCTXAvailable() {
        return false;
    }
}
