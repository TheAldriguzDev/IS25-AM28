package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Client.UI.CommandCTX;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Network.Messages.PopulateShip;
import it.polimi.ingsw.is25am28.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.is25am28.Client.UI.ClientTUI_v2.clearTerminal;
import static it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType.*;

public class PopulateShipScreen extends Screen {
    private ClientShip currPlayerShip;
    private WidgetTUI availableLifeformsWidget;
    private WidgetTUI playerNameWidget;

    // Constructor
    public PopulateShipScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);

        // Retrieving the player ship
        this.model.getShipOfPlayer(this.model.getNickname()).ifPresent(
            (ClientShip ship) -> { this.currPlayerShip = ship; }
        );

        this.generateAvailableLifeformsWidget();
        this.generatePlayerNameWidget();
    }

    /**
     * Generates the available lifeforms widget to show the
     * player the available lifeforms
     */
    private void generateAvailableLifeformsWidget() {
        this.availableLifeformsWidget = new WidgetTUI()
            .appendString("(" + ASTRONAUT.ordinal() + ") Astronaut")
            .appendString("(" + PURPLE_ALIEN.ordinal() + ") Purple Alien")
            .appendString("(" + BROWN_ALIEN.ordinal() + ") Brown Alien")
            .addPadding(0, 1, 0, 1)
            .wrapWidgetWithBorder();
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
     * @return A pair of integers that represents the (row, col) indexes
     *         of the component the player wants to populate
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
            System.out.print("Insert row of the cabin to populate: ");
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
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
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
            System.out.print("Insert column of the cabin to populate: ");
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
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
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
     * @return The player's chosen lifeform type to add to his ship
     */
    private LifeformType getLifeformType() {
        LifeformType lifeformToAdd;
        boolean lifeformChosen;
        String line;
        int index;

        lifeformChosen = false;
        lifeformToAdd = null;

        do {
            try {
                System.out.println();
                System.out.println("Available lifeforms to add:");
                this.availableLifeformsWidget.printWidget();

                System.out.print("Insert lifeform to add: ");
                line = this.inputThread.waitForInput();

                if (line == null) {
                    // A forced interrupt arrived
                    return null;
                }

                index = Integer.parseInt(line);
                lifeformToAdd = Arrays.stream(values()).toList().get(index);
                lifeformChosen = true;
            }
            catch (NumberFormatException e) {
                // Ask again for a correct value
                System.out.println(PrintUtils.addColor("[ERROR] [Invalid input] Please insert a number.", ANSIColors.RED));
                continue;
            }
            catch (InterruptedException e) {
                // A forced interrupt arrived
                return null;
            }
        }
        while (!lifeformChosen);

        return lifeformToAdd;
    }

    /**
     * Asks the user for the lifeform he wants to add and also
     * the coordinates of the cabin where he wants the former
     * to live in.
     */
    private void populateShip() throws Exception {
        ComponentHelper<LifeformType> lifeformToAdd;
        Map.Entry<Integer, Integer> coordinates;
        LifeformType lifeformType;

        coordinates = this.getComponentCoordinates();
        lifeformType = this.getLifeformType();

        if (lifeformType == null) {
            return;
        }

        lifeformToAdd = new ComponentHelper<LifeformType>(
            coordinates.getKey(), coordinates.getValue()
        ).addItem(lifeformType);

        // Command context
        this.ctx = new CommandCTX(
            "populateShip",
            () -> {
                this.playerNameWidget.printWidget();
                this.currPlayerShip.getShipGridWidget().printWidget();
                this.printShipPopulateStatusWidget();

                if (!this.model.getState().getPopulateShipDTO().getPlayersReady().contains(this.model.getNickname())) {
                    try {
                        this.populateShip();
                    }
                    catch (Exception e) {
                        System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" thrown by method 'populateShip' in PopulateShipScreen" , ANSIColors.RED));
                    }
                }
            },
            () -> {
                try {
                    this.playerNameWidget.printWidget();
                    this.currPlayerShip.getShipGridWidget().printWidget();
                    this.printShipPopulateStatusWidget();
                    this.populateShip();
                }
                catch (Exception e) {
                    System.out.println(PrintUtils.addColor("[ERROR] \"" + e.getClass().getSimpleName() + "\" was thrown inside 'populateShip", ANSIColors.RED));
                }
            }
        );

        // Send message
        this.client.sendMessage(
            new PopulateShip(
                this.model.getNickname(),
                lifeformToAdd
            )
        );
    }

    @Override
    public void showShipPopulate(PopulateShipDTO populateShip) throws Exception {
        System.out.println();
        clearTerminal();

        this.playerNameWidget.printWidget();
        this.currPlayerShip.getShipGridWidget().printWidget();
        this.printShipPopulateStatusWidget();

        if (!populateShip.getPlayersReady().contains(this.model.getNickname())) {
            System.out.println("INIT POPULATE");
            this.populateShip();
        }
    }
}
