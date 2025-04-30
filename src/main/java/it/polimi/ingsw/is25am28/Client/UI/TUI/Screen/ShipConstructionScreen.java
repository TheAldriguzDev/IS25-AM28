package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Network.Messages.*;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.*;

import static it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2.clearTerminal;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

public class ShipConstructionScreen extends Screen {
    // Default component matrix (row, col) dimensions
    public static final int DEFAULT_COMPONENT_ROWS = 8;
    public static final int DEFAULT_COMPONENT_COLS = 19;

    private WidgetTUI componentSelectionWidget;
    private WidgetTUI reservedComponentsWidget;
    private WidgetTUI componentSelectionCommandsWidget;

    private WidgetTUI selectedComponentWidget;
    private WidgetTUI currPlayerShipWidget;
    private WidgetTUI shipConstructionCommandsWidget;

    private WidgetTUI cardSubdeckWidget;
    private WidgetTUI cardSubdeckCommandsWidget;

    private WidgetTUI otherPlayerShipWidget;
    private WidgetTUI otherPlayerShipCommandsWidget;

    private WidgetTUI coveredComponentWidget;
    private WidgetTUI emptyComponentWidget;

    private ClientComponent selectedComponent;
    private ClientShip currPlayerShip;
    private boolean isSelectedTileReserved;

    public ShipConstructionScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Retrieving this player's ship
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> { this.currPlayerShip = ship; }
        );

        // Other initializations
        this.selectedComponent = null;
        this.isSelectedTileReserved = false;

        // Creating the covered and empty component widgets
        this.generateCoveredComponentWidget();
        this.generateEmptyComponentWidget();

        // Generate all the widgets with the commands to
        // display to the current player
        this.generateShipConstructionCommands();
        this.generateCardSubdeckCommandsWidget();
        this.generateComponentSelectionCommands();
        this.generateOtherPlayerShipCommandsWidget();
    }

    /**
     * Initializes the widget containing all the commands
     * available during the component selection menu
     */
    private void generateComponentSelectionCommands() {
        this.componentSelectionCommandsWidget = new WidgetTUI();

        if (!this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
            this.componentSelectionCommandsWidget.appendString("(1) Select tile");
            this.componentSelectionCommandsWidget.appendString("(2) Select reserved tile");
            this.componentSelectionCommandsWidget.appendString("(3) Finish ship");
        }

        this.componentSelectionCommandsWidget.appendString("(4) Flip timer");
        this.componentSelectionCommandsWidget.appendString("(5) Visualize subdeck");
        this.componentSelectionCommandsWidget.appendString("(6) Visualize other ships");

        this.componentSelectionCommandsWidget.addPadding(0, 1, 0, 1);
        this.componentSelectionCommandsWidget.wrapWidgetWithBorder();
    }

    /**
     * Initializes the widget containing all the commands
     * available during the ship construction menu
     */
    private void generateShipConstructionCommands() {
        WidgetTUI leftWidget = new WidgetTUI();
        WidgetTUI rightWidget = new WidgetTUI();

        if (!this.isSelectedTileReserved) {
            leftWidget.appendString("(1) Deselect tile");
        }
        leftWidget.appendString("(2) Reserve tile");
        leftWidget.addPadding(0, 1, 0, 0);

        rightWidget.appendString("(3) Place selected tile");
        rightWidget.appendString("(4) Rotate right");
        rightWidget.addPadding(0, 1, 0, 0);

        leftWidget =
                WidgetTUI.composeTwoWidgetsHorizontally(
                        leftWidget, rightWidget
                );

        rightWidget = new WidgetTUI();
        rightWidget.appendString("(5) Rotate left");
        rightWidget.addPadding(0, 1, 0, 0);

        leftWidget =
            WidgetTUI.composeTwoWidgetsHorizontally(
                    leftWidget, rightWidget
            );

        this.shipConstructionCommandsWidget = leftWidget;
        this.shipConstructionCommandsWidget.addPadding(0, 0, 0, 1);
        this.shipConstructionCommandsWidget.wrapWidgetWithBorder();
    }

    /**
     * Initializes the widget containing all the available
     * subdecks that a player can choose from
     */
    private void generateCardSubdeckCommandsWidget() {
        this.cardSubdeckCommandsWidget = new WidgetTUI();

        this.cardSubdeckCommandsWidget.appendString("(1) Select deck #1");
        this.cardSubdeckCommandsWidget.appendString("(2) Select deck #2");
        this.cardSubdeckCommandsWidget.appendString("(3) Select deck #3");
        this.cardSubdeckCommandsWidget.appendString("(-1) Go back");

        this.cardSubdeckCommandsWidget.addPadding(0, 1, 0, 1);
        this.cardSubdeckCommandsWidget.wrapWidgetWithBorder();
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
            this.otherPlayerShipCommandsWidget.appendString("(" + i + ") Show ship of \"" + allNicknames.get(i) + "\"");
        }

        this.otherPlayerShipCommandsWidget.appendString("(-1) Go back");

        this.otherPlayerShipCommandsWidget.addPadding(0, 1, 0, 1);
        this.otherPlayerShipCommandsWidget.wrapWidgetWithBorder();
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
            List<List<String>> allCardsScreens;
            WidgetTUI tmpCardWidget;

            // Initializations
            this.cardSubdeckWidget = new WidgetTUI();
            allCardsScreens = new ArrayList<>();
            tmpCardWidget = new WidgetTUI();

            for (ClientEventCard card : selectedSubdeck) {
                allCardsScreens.add(
                    card.generateWidget()
                        .addPadding(0, 1, 0, 0)
                        .getScreen()
                );
            }

            // Composing all card widget's screens into one
            tmpCardWidget.setScreen(WidgetTUI.composeScreensHorizontally(allCardsScreens));

            // Adding a centered title
            this.cardSubdeckWidget.appendString("==== SELECTED SUBDECK ====");
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
     * @return A widget that is the result of the composition of the
     *         widgets that make up the component selection screen
     */
    private WidgetTUI composeComponentSelectionWidgets() {
        // Ensuring each widget is updated
        this.generateReservedComponentsWidget();
        this.generateComponentSelectionWidget();

        return WidgetTUI.composeTwoWidgetsHorizontally(
            this.reservedComponentsWidget,
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
        this.currPlayerShipWidget = this.currPlayerShip.getShipGridWidget();

        return WidgetTUI.composeTwoWidgetsHorizontally(
            WidgetTUI.composeTwoWidgetsVertically(
                    this.selectedComponentWidget,
                    this.reservedComponentsWidget
            ).addPadding(0, 1, 0, 0).centerWidgetScreen(),
            this.currPlayerShipWidget
        );
    }

    /**
     * Asks the player to choose from one of the available
     * commands in the component selection screen
     */
    private void getComponentSelectionCommand() {
        String line;
        int choice;

        clearTerminal();

        // Show all the available commands
        if (this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
            System.out.println(PrintUtils.addColor("[COMPUTER] Your ship was sent! Wait until either all other players have finished or the timer to runs out!", ANSIColors.BRIGHT_CYAN));
        }
        else {
            this.composeComponentSelectionWidgets().printWidget();
        }

        // If it's not null, it means that it's available to be flipped
        if (this.model.getTimerDTO() != null) {
            if (this.model.getTimerDTO().getCanBeFlipped()) {
                System.out.println(PrintUtils.addColor("[!] Hourglass can now be flipped", ANSIColors.BRIGHT_MAGENTA));
            }
        }

        System.out.println();
        System.out.println("Available commands:");
        this.generateComponentSelectionCommands();
        this.componentSelectionCommandsWidget.printWidget();

        System.out.print(DEFAULT_COMMAND_PREFIX);
        try {
            line = this.inputThread.waitForInput();
        }
        catch (InterruptedException e) {
            // InputThread was interrupted due to
            // it receiving a force interrupt
            return;
        }

        if (line == null) {
            // A forced interrupt arrived, therefore the
            // current action is blocked
            return;
        }

        try {
            choice = Integer.parseInt(line);
        }
        catch (NumberFormatException e) {
            System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
            this.getComponentSelectionCommand();
            return;
        }

        switch (choice) {
            case 1 -> {
                // (1) - Select Tile
                if (!this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
                    try {
                        this.selectTile();
                    }
                    catch (Exception e) {
                        System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                    }
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            case 2 -> {
                // (2) - Select Reserved Tile
                if (!this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
                    try {
                        this.selectReservedTile();

                        // Go to the ship construction screen
                        this.getShipConstructionCommand();
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        this.getComponentSelectionCommand();
                    }
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            case 3 -> {
                // (3) - Finish Ship
                if (!this.model.getState().getPlayerFinishedBuildingShip(this.model.getNickname())) {
                    if (this.getShipFinishedConfirmation()) {
                        try {
                            // Sending the ship to the server
                            this.sendFinishedShip();
                        }
                        catch (Exception e) {
                            System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                        }
                    }
                }
                else {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                }
            }
            case 4 -> {
                // (4) - Flip Timer
                try {
                    this.flipTimer();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                }
            }
            case 5 -> {
                // (5) - Visualize Subdeck
                try {
                    // Getting the player's chosen subdeck and
                    // prints it directly to terminal
                    this.getCardSubdeckCommand();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                }
            }
            case 6 -> {
                // (6) - Visualize Other Ships
                this.getOtherShipCommand();
                this.getComponentSelectionCommand();
            }
            default -> {
                // Loopback and ask for a valid command
                System.out.println(UNKNOWN_COMMAND_ERROR);
            }
        }
    }

    /**
     * Stays in the menu until a valid command was given
     */
    private void getShipConstructionCommand() {
        String line;
        int choice;

        // Show the ship construction screen
        clearTerminal();
        this.composeShipConstructionWidgets().printWidget();

        System.out.println();
        System.out.println("Available ship construction commands:");
        this.generateShipConstructionCommands();
        this.shipConstructionCommandsWidget.printWidget();
        System.out.print(DEFAULT_COMMAND_PREFIX);

        try {
            line = this.inputThread.waitForInput();
        }
        catch (InterruptedException e) {
            // A force interrupt arrived
            return;
        }

        if (line == null) {
            // A force interrupt arrived
            return;
        }

        try {
            choice = Integer.parseInt(line);
        }
        catch (NumberFormatException e) {
            System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
            this.getShipConstructionCommand();
            return;
        }

        switch (choice) {
            case 1 -> {
                if (!this.isSelectedTileReserved) {
                    // (1) - Deselect tile
                    try {
                        // Deselects the component that is currently taken by this user
                        this.deselectTile();

                        // Go to the component selection screen
                        this.getComponentSelectionCommand();
                    }
                    catch (Exception e) {
                        System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                    }
                }
                else {
                    // If the selected component is reserved, then it can't be
                    // deselected and thus, even if the player puts "1" to deselect
                    // the tile, the client will show that this command, theoretically,
                    // for the state it's currently in, does not exist.
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                    this.getShipConstructionCommand();
                }
            }
            case 2 -> {
                // (2) - Reserve tile
                List<ClientComponent> reservedComponents = this.model.getState().getReservedComponents();

                // Puts the selected component in the reserved tiles
                // If it can't, then it'll ask the user to do something else
                if (reservedComponents.size() < 2) {
                    // Adding the currently selected component to the reserved component list
                    this.model.getState().reserveTile(this.selectedComponent);

                    // Removing the selected tile from the selected component slot
                    // since the user decided to reserve it for the future
                    this.selectedComponent = null;
                    this.selectedComponentWidget = null;

                    // At the end, it goes back to asking again a new
                    // component selection command
                    this.getComponentSelectionCommand();
                }
                else {
                    // Otherwise, it means that the user already has 2 reserved tiles,
                    // therefore he cannot store any additional ones
                    System.out.println(PrintUtils.addColor("ERROR: You already have 2 (max) reserved tiles! Use them before storing others!", ANSIColors.RED));
                    this.getShipConstructionCommand();
                }
            }
            case 3 -> {
                // (3) - Place selected tile
                try {
                    // Puts the currently selected tile in the ship and in the
                    // ClientShipConstructionState when the ship will be sent to
                    // the server for validation
                    this.placeSelectedTile();

                    // At the end, it goes back to asking again a new
                    // component selection command
                    this.getComponentSelectionCommand();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                    this.getShipConstructionCommand();
                }
            }
            case 4 -> {
                // (4) - Rotate right
                this.selectedComponent.rotateRight();

                // NOTE: Default behavior after rotation is to go
                //       back to the ship construction screen
                this.getShipConstructionCommand();
            }
            case 5 -> {
                // (5) - Rotate left
                this.selectedComponent.rotateLeft();

                // NOTE: Default behavior after rotation is to go
                //       back to the ship construction screen
                this.getShipConstructionCommand();
            }
            default -> {
                // Loopback and ask for a valid command
                System.out.println(UNKNOWN_COMMAND_ERROR);
                this.getShipConstructionCommand();
            }
        }

        // Resetting the selected component reserved status
        this.isSelectedTileReserved = false;
    }

    /**
     * Books a subdeck to observe and prints it
     * to the user that requests it
     */
    private void getCardSubdeckCommand() throws Exception {
        int subdeckIdx, subdeckSize, subdeckAmount;
        String line;

        subdeckAmount = this.cardSubdeckCommandsWidget.getHeight() - 2 * this.cardSubdeckCommandsWidget.getBorderCount() - 1;
        subdeckSize = this.model.getState().getEventCards().size() / subdeckAmount;

        do {
            subdeckIdx = -1;

            System.out.println();
            System.out.println("Choose a subdeck to view:");
            this.cardSubdeckCommandsWidget.printWidget();

            System.out.print(DEFAULT_COMMAND_PREFIX);
            try {
                line = this.inputThread.waitForInput();

                if (line == null) {
                    // A force interrupt arrived
                    return;
                }

                subdeckIdx = Integer.parseInt(line);

                if (subdeckIdx == -1) {
                    // Go back to the component selection screen
                    this.getComponentSelectionCommand();
                    return;
                }

                if (subdeckIdx < 1 || subdeckIdx > subdeckAmount) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                    subdeckIdx = -1;
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
            }
            catch (InterruptedException e) {
                // A force interrupt arrived
                return;
            }
        }
        while (subdeckIdx < 1 || subdeckIdx > subdeckAmount);

        int finalSubdeckIdx = subdeckIdx - 1;
        int start = (subdeckIdx * subdeckSize);
        int end = (start + subdeckSize);

        this.ctx = new CommandCTX(
            "selectDeselectSubdeck",
            () -> {
                String input;

                // When the server gives the OK to lock the subdeck, then
                // proceed to generate and show the corresponding widget
                this.generateCardSubdeckWidget(
                    this.model.getState().getEventCards().subList(start, end)
                );

                clearTerminal();
                this.cardSubdeckWidget.printWidget();

                try {
                    System.out.print("Press any key and then press [ENTER] to go back...");
                    input = this.inputThread.waitForInput();

                    // Then deselect the deck by sending a message to the server
                    try {
                        // Go back to the component selection screen
                        this.ctx = new CommandCTX(
                            "selectDeselectSubdeck",
                            this::getComponentSelectionCommand,
                            () -> {
                                // Make the user choose another subdeck command
                                try {
                                    this.getCardSubdeckCommand();
                                }
                                catch (Exception e) {
                                    System.out.println(PrintUtils.addColor("ERROR: getCardSubdeckCommand::sendMessage threw \"" + e.getClass().getSimpleName() + "\"", ANSIColors.RED));
                                }
                            }
                        );

                        this.client.sendMessage(
                            new SelectDeselectSubdeck(
                                this.model.getNickname(),
                                finalSubdeckIdx,
                                false
                            )
                        );
                    }
                    catch (Exception e) {
                        System.out.println(PrintUtils.addColor("ERROR: Subdeck " + (finalSubdeckIdx + 1) + " deselection failed for player \"" + this.model.getNickname() + "\"", ANSIColors.RED));
                        throw new RuntimeException(e);
                    }
                }
                catch (InterruptedException e) {
                    // A force interrupt arrived
                }

                this.cardSubdeckWidget = null;
            },
            () -> {
                // Show an error if the selected subdeck is
                // currently in the hands of another player
                System.out.println(PrintUtils.addColor("ERROR: Selected deck #" + (finalSubdeckIdx + 1) + " is currently observed by another player. You must wait.", ANSIColors.RED));

                // Go back to the component selection screen
                this.getComponentSelectionCommand();
            }
        );

        this.client.sendMessage(
            new SelectDeselectSubdeck(
                this.model.getNickname(),
                finalSubdeckIdx,
                true
            )
        );
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

            System.out.print(DEFAULT_COMMAND_PREFIX);
            try {
                line = this.inputThread.waitForInput();

                if (line == null) {
                    // A force interrupt arrived
                    return;
                }

                chosenShip = Integer.parseInt(line);
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
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

            System.out.println(PrintUtils.addColor("[COMPUTER]", ANSIColors.BRIGHT_CYAN) + SPACE + "You're now viewing \"" + this.model.getAllPlayersNicknames().get(chosenShip) + "\"'s ship");
            System.out.println();

            try {
                System.out.print("Press any key and then press [ENTER] to go back...");
                line = this.inputThread.waitForInput();
            }
            catch (InterruptedException e) {
                // A force interrupt arrived
            }

            this.otherPlayerShipWidget = null;
        }
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
            System.out.print("Do you want to send your ship? [" + yesMessage + "/" + noMessage + "] ");

            try {
                input = this.inputThread.waitForInput();

                if (input == null) {
                    // A force interrupt arrived
                    break;
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
            catch (InterruptedException e) {
                // A force interrupt arrived
                break;
            }
        }
        while (!choiceMade);

        return sendShip;
    }

    /**
     * @return The current player's chosen tile index
     */
    private int getTileIndex() {
        int selectableComponentsAmount = this.model.getState().getConstructionShipComponents().size();
        String input;
        int idx = -1;

        do {
            System.out.print("Enter tile index (between 0 and " + (selectableComponentsAmount - 1) + "): ");

            try {
                input = this.inputThread.waitForInput();

                if (input == null) {
                    // A force interrupt arrived
                    break;
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
            catch (InterruptedException e) {
                // A force interrupt arrived
                break;
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
                // Move to the ship construction screen by recomposing it and
                // ask the user what to do with the selected component

                // On success, generate the selected component widget
                this.generateSelectedComponentWidget();

                // And go to the ship construction screen
                this.getShipConstructionCommand();
            },
            () -> {
                // If an error occurred we re-execute the command and reset
                // the currently selected component attribute and widget
                try {
                    this.selectedComponent = null;
                    this.selectedComponentWidget = null;

                    clearTerminal();
                    this.composeComponentSelectionWidgets().printWidget();
                    this.selectTile();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );

        int construction_i = idx / DEFAULT_COMPONENT_COLS;
        int construction_j = idx % DEFAULT_COMPONENT_COLS;

        this.client.sendMessage(
            new SelectTile(
                this.model.getNickname(),
                construction_i,
                construction_j
            )
        );
    }

    /**
     * Deselects the currently selected tile, leaving it available for other players
     */
    private void deselectTile() throws Exception {
        this.ctx = new CommandCTX(
            "deselectTile",
            () -> {
                // Once we have deselected the tile we can return to
                // the component selection menu
                this.selectedComponent = null;

                // At the end, it goes back to asking again a new
                // component selection command
                this.getComponentSelectionCommand();
            },
            () -> {
                // If an error occurred we go back to the
                // ship construction menu
                this.getShipConstructionCommand();
            }
        );

        int id = this.selectedComponent.getID();
        int construction_i = id / 19;
        int construction_j = id % 19;

        this.client.sendMessage(
            new DeselectTile(
                this.model.getNickname(),
                construction_i,
                construction_j
            )
        );
    }

    /**
     * Selects the chosen reserved component to use it
     */
    private void selectReservedTile() throws IllegalArgumentException {
        boolean componentRetrieved;
        String line;
        int index;

        componentRetrieved = false;

        if (this.model.getState().getReservedComponents().isEmpty()) {
            throw new IllegalArgumentException(PrintUtils.addColor("ERROR: You don't have any reserved components!", ANSIColors.RED));
        }

        do {
            System.out.print("Enter reserved tile index to select (0 = Slot1, 1 = Slot2): ");

            try {
                line = this.inputThread.waitForInput();
            }
            catch (InterruptedException e) {
                // A force interrupt arrived
                break;
            }

            if (line == null) {
                // A force interrupt arrived
                break;
            }

            try {
                index = Integer.parseInt(line);

                // Selecting the chosen reserved component
                this.selectedComponent = this.model.getState().getReservedComponents().get(index);

                // Then removing it from the reserved component list
                // (to avoid duplicate widgets, which could confuse the player)
                this.model.getState().getReservedComponents().remove(this.selectedComponent);

                this.isSelectedTileReserved = true;
                componentRetrieved = true;

                // Regenerate the ship construction widget which removes the commands
                // that should not be seen when using a reserved tile as the selected tile
                this.generateShipConstructionCommands();
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println(PrintUtils.addColor("ERROR: Wrong reserved tile index.", ANSIColors.RED));
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("ERROR: Invalid input. Please insert a number.", ANSIColors.RED));
            }
        }
        while (!componentRetrieved);
    }

    /**
     * Handles the placement of the selected component to the current player's client ship
     */
    private void placeSelectedTile() throws Exception {
        Map.Entry<Integer, Integer> componentPosition;

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
                    }

                    // Removing the component from the selected slot, since it was placed on the ship
                    this.selectedComponent = null;

                    // Updating the ship widget
                    Optional<ClientShip> optionalShip = this.model.getShipOfPlayer(this.model.getNickname());
                    optionalShip.ifPresent(clientShip -> this.currPlayerShipWidget = clientShip.getShipGridWidget());
                },
                () -> {
                    // On failure, go back to the ship construction screen
                    this.getShipConstructionCommand();
                }
            );

            // Broadcasting to all players that the current player placed
            // his currently selected tile on his ship
            this.client.sendMessage(
                new PlaceTile(
                    this.model.getNickname(),
                    this.selectedComponent.getID(),
                    componentPosition.getKey(),
                    componentPosition.getValue(),
                    this.selectedComponent.getDirection()
                )
            );
        }
    }

    /**
     * @return A pair of integers that represents the (row, col) indexes
     *         where the player wants to place the selected component.
     */
    private Map.Entry<Integer, Integer> getComponentCoordinates() {
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
            System.out.print("Insert row where to put the selected component: ");
            try {
                line = this.inputThread.waitForInput();

                if (line == null) {
                    // A force interrupt arrived
                    break;
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
            catch (InterruptedException e) {
                // A force interrupt arrived
                break;
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
                    break;
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
            catch (InterruptedException e) {
                // A force interrupt arrived
                break;
            }
        }
        while (!validCoordinate);

        // Reducing both by 1 since they will then be used as
        // indexes inside the client ship component matrix
        coordinates.put(--i, --j);
        return coordinates.entrySet().stream().toList().getFirst();
    }

    /**
     * Sends the ship to the server for evaluation
     */
    private void sendFinishedShip() throws Exception {
        this.ctx = new CommandCTX(
            "sendShipConfirmation",
            () -> {
                // If successful, then the player needs to wait that all players
                // finish building or the hourglass timer runs out (with all flips consumed)

                // The current player gets marked as "has sent his ship"
                this.model.getState().setPlayerFinishedBuildingShip(this.model.getNickname());

                // And also update the component selection commands
                // with the only commands available for his state
                this.generateComponentSelectionCommands();

                // Go back to the component selection command menu where the only available
                // commands will be: 1) Show other ship, 2) Flip timer, 3) Show card deck
                try {
                    this.getComponentSelectionCommand();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
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
                    throw new RuntimeException(e);
                }
            }
        );

        // Sends the current player's ship when he
        // decides to finish building it
        this.client.sendMessage(
            new SendShipConfirmation(
                this.model.getNickname(),
                this.model.getState().getReservedComponents().size()
            )
        );
    }

    /**
     * Flips the timer and sends the relative message to broadcast it to other players
     */
    private void flipTimer() throws Exception {
        // Both onSuccess and onFailure will route the player
        // back to the component selection screen, regardless
        // of the result of the command
        this.ctx = new CommandCTX(
            "flipTimer",
            () -> {
                System.out.println(PrintUtils.addColor("Timer flipped successfully!", ANSIColors.BRIGHT_MAGENTA));
                this.getComponentSelectionCommand();
            },
            this::getComponentSelectionCommand
        );

        this.client.sendMessage(
            new FlipTimer(this.model.getNickname())
        );
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception {
        // Show all selectable components grid as well as the reserved
        // components to display any components that the player might
        // choose to reserve during this phase
        this.getComponentSelectionCommand();
    }
}
