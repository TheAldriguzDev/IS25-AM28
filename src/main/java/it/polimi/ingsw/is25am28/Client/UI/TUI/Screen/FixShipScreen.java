package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FixShipDTO;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.HashMap;
import java.util.Map;

public class FixShipScreen extends Screen {
    private WidgetTUI shipValidityStatusWidget;

    public FixShipScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    /**
     * Generates a widget that shows to this player his
     * ship's current validity status
     */
    private void printShipStatusWidget(boolean isShipValid) {
        this.shipValidityStatusWidget = new WidgetTUI();

        if (isShipValid) {
            this.shipValidityStatusWidget
                    .appendString(COMPUTER_MSG_TAG + "Your ship is " + PrintUtils.addColor("VALID", ANSIColors.BRIGHT_GREEN) + "! Please wait for all players to fix theirs.")
                    .addPadding(3, 3, 3, 3)
                    .wrapWidgetWithBorder()
                    .printWidget();
        }
        else {
            this.shipValidityStatusWidget
                    .appendString(COMPUTER_MSG_TAG + "Your ship is " + PrintUtils.addColor("INVALID", ANSIColors.BRIGHT_RED) + "! Fix your ship.")
                    .addPadding(0, 1, 0, 1)
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
    private void removeSingleComponent() {
        Map.Entry<Integer, Integer> coordinates;

        System.out.print("Insert coordinates of component to remove: ");
        coordinates = this.getComponentCoordinates();

        this.ctx = new CommandCTX(
        "fixShip",
            () -> {

            },
            () -> {

            }
        );

        // Send message
    }

    /**
     * @return A pair of integers that represents the (row, col) indexes
     *         of the component the player wants to remove in an attempt
     *         to fix his ship
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

    @Override
    public void showShipFixing(FixShipDTO fixShip) {
        if (fixShip.getPlayerWithInvalidShip().contains(this.model.getNickname())) {
            // This player's ship was deemed as invalid, therefore he must
            // fix it before the game can move on
            this.printShipStatusWidget(false);
            this.removeSingleComponent();
        }
        else {
            // If this player's ship, when validated, results as correct, then
            // he must wait for all other players (if there are any in the first place)
            // to finish fixing their own ship
            this.printShipStatusWidget(true);
        }
    }
}
