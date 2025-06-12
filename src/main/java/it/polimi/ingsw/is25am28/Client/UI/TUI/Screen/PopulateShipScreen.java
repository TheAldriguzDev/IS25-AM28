package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientCabin;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.CommandWidgetTUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfGridException;
import it.polimi.ingsw.is25am28.Model.Exceptions.OutOfShipException;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;

public class PopulateShipScreen extends Screen {
    private ClientShip currPlayerShip;

    private InputWidgetTUI availableLifeformsWidget;
    private InputWidgetTUI availableCabinsWidget;

    private WidgetTUI playerNameWidget;

    private final AtomicReference<ClientCabin> chosenCabin;
    private final AtomicReference<LifeformType> chosenLifeform;

    // Constructor
    public PopulateShipScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Retrieving the player ship
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> { this.currPlayerShip = ship; }
        );

        this.chosenCabin = new AtomicReference<>(null);
        this.chosenLifeform = new AtomicReference<>(null);

        this.generateAvailableLifeformsWidget(this.chosenLifeform);
        this.generatePlayerNameWidget();
    }

    /**
     * Generates an input widget containing all the available
     * lifeforms that the player can select.
     */
    private void generateAvailableLifeformsWidget(AtomicReference<LifeformType> chosenLifeform) {
        CommandWidgetTUI command;

        if (chosenLifeform == null) {
            throw new RuntimeException("ERROR: Given atomic reference \"chosenLifeform\" cannot be null (cannot store the player's choice)");
        }

        this.availableLifeformsWidget = new InputWidgetTUI(this.inputThread);

        for (LifeformType lfType : LifeformType.values()) {
            command = new CommandWidgetTUI(
                Integer.toString(lfType.ordinal()),
                () -> {
                    chosenLifeform.set(lfType);
                }
            );
            command.appendString(lfType.toString());
            this.availableLifeformsWidget.addCommand(command);
        }

        // (-1) - Go back
        command = new CommandWidgetTUI(
            "-1",
            () -> {
                chosenLifeform.set(null);
            }
        );
        command.appendString("Go back");
        this.availableLifeformsWidget.addCommand(command);

        this.availableLifeformsWidget.setColumnGroupingAmount(
            this.availableLifeformsWidget.getCommandMap().size()
        );
    }

    /**
     * Generates an input widget containing all the cabins
     * that the player can populate
     */
    private void generateAvailableCabinsWidget(AtomicReference<ClientCabin> chosenCabin) {
        CommandWidgetTUI command;
        List<ClientCabin> cabinList;
        int i, len;

        if (chosenCabin == null) {
            throw new RuntimeException("ERROR: Given atomic reference \"chosenCabin\" cannot be null (cannot store the player's choice)");
        }

        this.availableCabinsWidget = new InputWidgetTUI(this.inputThread);
        cabinList = this.currPlayerShip.getCabinList();

        cabinList = cabinList.stream()
                .filter(c -> c.getAvailableSpace() > 0)
                .toList();

        len = cabinList.size();

        // (n) - All Selectable Cabins
        for (i = 0; i < len; i++) {
            ClientCabin cabin = cabinList.get(i);

            command = new CommandWidgetTUI(
                Integer.toString(i),
                () -> {
                    chosenCabin.set(cabin);
                }
            );
            command.appendString("Cabin @ (row=" + (cabin.getI() + 1) + ", col=" + (cabin.getJ() + 1) + ")");
            this.availableCabinsWidget.addCommand(command);
        }

        this.availableCabinsWidget.setColumnGroupingAmount(
            this.availableCabinsWidget.getCommandMap().size()
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
     * Generates a widget that shows to this player if his
     * ship can still be populated or not
     */
    private void printShipPopulateStatusWidget() {
        WidgetTUI availableLifeformsToAddWidget = new WidgetTUI();

        // Creating the widget that shows the available lifeforms that
        // this player can currently add to his ship
        if (!this.model.getState().getPopulateShipDTO().getPlayersReady().contains(this.model.getNickname())) {
            availableLifeformsToAddWidget
                    .appendString(COMPUTER_MSG_TAG + "Your ship has still some " + PrintUtils.addColor("VACANT", ANSIColors.BRIGHT_GREEN) + " cabins. You must occupy all of them to continue.");
        }
        else {
            availableLifeformsToAddWidget
                    .appendString(COMPUTER_MSG_TAG + "Your ship's cabins are all " + PrintUtils.addColor("FULL", ANSIColors.RED) + ". You must now wait for all players to populate their ships.");
        }

        availableLifeformsToAddWidget
                .addPadding(1, 1, 1, 1)
                .wrapWidgetWithBorder()
                .printWidget();
    }

    /**
     *  Keeps asking the player to select a cabin
     *  among the ones that can still be populated.
     */
    private void getAvailableCabinsCommand() throws InterruptedException {
        boolean commandExecuted;

        this.generateAvailableCabinsWidget(this.chosenCabin);

        do {
            System.out.println();
            System.out.println("Available cabins to populate:");

            commandExecuted = this.availableCabinsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

            if (!commandExecuted) {
                System.out.println(UNKNOWN_COMMAND_ERROR);
            }
        }
        while (!commandExecuted);
    }

    /**
     *  Keeps asking the player to select a lifeform
     *  among the available ones.
     */
    private void getAvailableLifeformsCommand() throws InterruptedException {
        boolean commandExecuted;

        do {
            System.out.println();
            System.out.println("Available lifeforms to add:");

            commandExecuted = this.availableLifeformsWidget.selectCommand(DEFAULT_COMMAND_PREFIX);

            if (!commandExecuted) {
                System.out.println(UNKNOWN_COMMAND_ERROR);
            }
        }
        while (!commandExecuted);
    }

    /**
     * Asks the user for the lifeform he wants to add and also
     * the coordinates of the cabin where he wants the former
     * to live in.
     */
    private void populateShip() throws Exception {
        ComponentHelper<LifeformType> lifeformToAdd;
        boolean isActionValid;

        isActionValid = false;

        System.out.println();
        clearTerminal();

        do {
            this.playerNameWidget.printWidget();
            this.currPlayerShip.getShipGridWidget().printWidget();
            this.printShipPopulateStatusWidget();

            try {
                this.getAvailableCabinsCommand();
                this.getAvailableLifeformsCommand();
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return;
            }

            // Player chose "Go back" command to select another cabin
            // to populate instead of the one he's currently selected.
            if (this.chosenLifeform.get() == null) {
                System.out.println();
                clearTerminal();

                continue;
            }

            try {
                isActionValid = this.currPlayerShip.addLifeformVerifier(
                    this.chosenCabin.get().getI(),
                    this.chosenCabin.get().getJ(),
                    this.chosenLifeform.get()
                );
            }
            catch (IllegalArgumentException | OutOfGridException | OutOfShipException e) {
                // isActionValid = false (default value)
            }

            if (!isActionValid) {
                System.out.println();
                clearTerminal();

                new WidgetTUI()
                    .appendString(
                        COMPUTER_MSG_TAG
                        + PrintUtils.addColor(
                                "[ERROR] " + this.chosenLifeform.get().toString() + " cannot be added at (" + (this.chosenCabin.get().getI() + 1) + ", " + (this.chosenCabin.get().getJ() + 1) + ").",
                                ANSIColors.RED
                        )
                    )
                    .addPadding(0, 1, 0, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
            }
        }
        while (!isActionValid);

        // Creating the component helper of the lifeform to add
        // where (I, J) of the former point to the cabin where
        // the latter will be placed.
        lifeformToAdd = new ComponentHelper<LifeformType>(
                this.chosenCabin.get().getI(),
                this.chosenCabin.get().getJ()
        ).addItem(this.chosenLifeform.get());

        // Command context
        this.ctx = new CommandCTX(
            "populateShip",
            () -> {
                if (this.model.getState().getPopulateShipDTO().getPlayersReady().contains(this.model.getNickname())) {
                    System.out.println();
                    clearTerminal();

                    this.playerNameWidget.printWidget();
                    this.currPlayerShip.getShipGridWidget().printWidget();
                    this.printShipPopulateStatusWidget();
                }
                else {
                    try {
                        this.populateShip();
                    }
                    catch (Exception e) {
                        System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" thrown by method 'populateShip' in PopulateShipScreen" , ANSIColors.RED));
                    }
                }
            },
            () -> {
                System.out.println();
                clearTerminal();

                this.playerNameWidget.printWidget();
                this.currPlayerShip.getShipGridWidget().printWidget();
                this.printShipPopulateStatusWidget();

                try {
                    this.populateShip();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" was thrown inside 'populateShip", ANSIColors.RED));
                }
            }
        );

        // Send message
        this.client.populateShip(
            this.model.getNickname(),
            lifeformToAdd
        );
    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {
        if (populateShip.getPlayersReady().contains(this.model.getNickname())) {
            System.out.println();
            clearTerminal();

            this.playerNameWidget.printWidget();
            this.currPlayerShip.getShipGridWidget().printWidget();
            this.printShipPopulateStatusWidget();
        }
        else {
            this.populateShip();
        }
    }
}
