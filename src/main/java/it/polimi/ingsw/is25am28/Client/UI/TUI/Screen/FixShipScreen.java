package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler.clearTerminal;

public class FixShipScreen extends Screen {
    private ClientShip currPlayerShip;

    // Constructor
    public FixShipScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Retrieving the player ship
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> { this.currPlayerShip = ship; }
        );
    }

    /**
     * Generates a widget that shows to this player his
     * ship's current validity status
     */
    private void printShipFixStatusWidget(boolean isShipValid) {
        WidgetTUI shipValidityStatusWidget = new WidgetTUI();

        if (isShipValid) {
            shipValidityStatusWidget
                    .appendString(COMPUTER_MSG_TAG + "Your ship is " + PrintUtils.addColor("VALID", ANSIColors.BRIGHT_GREEN) + "! Please wait for all players to fix theirs.")
                    .addPadding(1, 1, 1, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }
        else {
            shipValidityStatusWidget
                    .appendString(COMPUTER_MSG_TAG + "Your ship is " + PrintUtils.addColor("INVALID", ANSIColors.BRIGHT_RED) + "! Fix your ship.")
                    .addPadding(1, 1, 1, 1)
                    .wrapWidgetWithBorder()
                    .printWidget();

            // Now the player with the correct ship will wait
            // here until the state change is forced
        }
    }

    /**
     * Asks the player which component he wants to remove to fix his ship
     * and then sends the latter to the server for validation
     */
    private void removeComponent() throws Exception {
        CoordinatePair coordinates;

        try {
            coordinates = this.getComponentCoordinates();
        }
        catch (InterruptedException e) {
            // A forced interrupt arrived
            return;
        }

        this.ctx = new CommandCTX(
        "fixShip",
            () -> {
                try {
                    this.showShipFixing(null);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            },
            () -> {
                try {
                    this.removeComponent();
                }
                catch (InterruptedException e) {
                    // A forced interrupt arrived
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" was thrown inside 'removeComponent", ANSIColors.RED));
                }
            }
        );

        this.client.fixShip(
            this.model.getNickname(),
            coordinates.getI(),
            coordinates.getJ()
        );
    }

    /**
     * @return A pair of integers that represents the (row, col) indexes
     *         of the component the player wants to remove in an attempt
     *         to fix his ship
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

    @Override
    public void showShipFixing(FixShipDTO fixShip) throws Exception {
        System.out.println();
        clearTerminal();

        String playerNickname = this.model.getNickname();
        String playerColorString = this.model.getAllClientPlayers().get(this.model.getNickname()).getColor().getColorString();

        new WidgetTUI()
                .appendString(PrintUtils.addColor(COMPUTER_MSG_TAG, ANSIColors.BRIGHT_CYAN) + "Viewing your ship")
                .appendString("Player: " + PrintUtils.addColor(playerNickname, playerColorString))
                .centerWidgetScreen()
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder()
                .printWidget();

        this.currPlayerShip.getShipGridWidget().printWidget();

        if (fixShip == null) {
            fixShip = this.model.getState().getFixShipDTO();
        }

        if (fixShip.getPlayerWithInvalidShip().contains(playerNickname)) {
            // This player's ship was deemed as invalid, therefore he must
            // fix it before the game can move on
            this.printShipFixStatusWidget(false);
            this.removeComponent();
        }
        else {
            // If this player's ship, when validated, results as correct, then
            // he must wait for all other players (if there are any in the first place)
            // to finish fixing their own ship
            this.printShipFixStatusWidget(true);
        }
    }
}
