package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientEventCards.ClientEventCard;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Network.Messages.PlayCard;
import it.polimi.ingsw.is25am28.Network.Messages.SelectDeselectSubdeck;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.ConsoleWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;
import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.SPACE;

// TODO: Fix the following bugs:
//      - (1) (ShipConstructionScreen) When the ViewUpdater receives a ConstructionDeckDTO, it needs to set
//        a flag inside the ClientShipConstructionState of all players to indicate that a certain subdeck
//        is either being observer or free to watch (thus avoiding to send a message to the server)
//        .
//      - (2) (ShipConstructionScreen) Subdeck widgets are centered somewhere and leads to teared card widgets
//        .
//      - (3) If two or more players have their ships that are both validated and already full, the GameModel
//        apparently doesn't set the new state, which in turn doesn't invoke the ViewUpdater and thus the
//        entry point "showCardRound" is not invoked, leaving the TUI hanging.
//        .
//      - (4) Sometimes the action of populating gets stuck (mostly with Astronauts)

public class CardRoundScreen extends Screen {
    private static final int CONSOLE_WIDGET_MAX_HEIGHT = 6;
    private static final int CONSOLE_WIDGET_MAX_WIDTH = 40;

    private WidgetTUI boardWidget;
    private WidgetTUI currEventCardWidget;
    private WidgetTUI shipGridWidget;
    private WidgetTUI shipStatsWidget;
    private WidgetTUI playerNameWidget;

    private WidgetTUI otherPlayerShipWidget;
    private WidgetTUI otherPlayerShipCommandsWidget;

    private WidgetTUI cardSubdeckWidget;
    private WidgetTUI cardSubdeckCommandsWidget;

    private WidgetTUI cardRoundCommandsWidget;
    private ConsoleWidgetTUI consoleWidget;

    private ClientEventCard currEventCard;

    // Constructor
    public CardRoundScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Widgets initializations
        this.generatePlayerNameWidget();
        this.generateShipWidgets();
        this.generateCardRoundCommandsWidget();
        this.generateCardSubdeckCommandsWidget();
        this.generateOtherPlayerShipCommandsWidget();

        this.boardWidget = this.model.getClientBoard().generateWidget();

        this.consoleWidget = new ConsoleWidgetTUI(
            CONSOLE_WIDGET_MAX_HEIGHT,
            CONSOLE_WIDGET_MAX_WIDTH
        );
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
                // (1) - Play card
                try {
                    this.playCard();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] playCard::sendMessage threw \"" + e.getClass().getSimpleName() + "\"", ANSIColors.RED));
                }
            }
            case 2 -> {
                // (2) - Visualize subdecks
                try {
                    // Getting the player's chosen subdeck and
                    // prints it directly to terminal
                    this.getCardSubdeckCommand();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("ERROR: \"" + e.getClass().getSimpleName() + "\" exception was thrown. Please try again.", ANSIColors.RED));
                }
            }
            case 3 -> {
                // (3) - Visualize ships
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
     * Books a subdeck to observe and prints it
     * to the user that requests it
     */
    private void getCardSubdeckCommand() throws Exception {
        int subdeckId, subdeckSize, visibleSubdecks;
        String line;

        subdeckSize = this.model.getClientEventCards().size() / 4;
        visibleSubdecks = 3;

        do {
            subdeckId = -1;

            System.out.println();
            System.out.println("Choose a subdeck to view:");
            this.cardSubdeckCommandsWidget.printWidget();

            try {
                System.out.print(DEFAULT_COMMAND_PREFIX);
                line = this.inputThread.waitForInput();

                // A force interrupt arrived
                if (line == null) return;

                subdeckId = Integer.parseInt(line);

                if (subdeckId == -1) {
                    // Go back to the card round screen
                    this.getCardRoundCommand();
                    return;
                }

                if (subdeckId < 1 || subdeckId > visibleSubdecks) {
                    System.out.println(UNKNOWN_COMMAND_ERROR);
                    subdeckId = -1;
                }
            }
            catch (NumberFormatException e) {
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
            }
            catch (InterruptedException e) {
                // A force interrupt arrived
                return;
            }
        }
        while (subdeckId < 1 || subdeckId > visibleSubdecks);

        int subdeckIndex = subdeckId - 1;
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
                    // Show an error if the selected subdeck is
                    // currently in the hands of another player
                    System.out.println(PrintUtils.addColor("[ERROR] Selected deck #" + (subdeckIndex + 1) + " is currently observed by another player. You must wait.", ANSIColors.RED));

                    // Go back to the card round screen
                    this.getCardRoundCommand();
                }
        );

        this.client.sendMessage(
            new SelectDeselectSubdeck(
                this.model.getNickname(),
                subdeckIndex,
                true
            )
        );
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
            this::getCardRoundCommand,
            () -> {
                // Make the user choose another subdeck command
                try {
                    this.getCardSubdeckCommand();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] getCardSubdeckCommand::sendMessage threw \"" + e.getClass().getSimpleName() + "\"", ANSIColors.RED));
                }
            }
        );

        this.client.sendMessage(
            new SelectDeselectSubdeck(
                this.model.getNickname(),
                subdeckIndex,
                false
            )
        );
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
                  // TODO: Implement onError (if it needs to do something)
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
