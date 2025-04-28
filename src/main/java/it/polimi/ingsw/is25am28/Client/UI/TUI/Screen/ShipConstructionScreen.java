package it.polimi.ingsw.is25am28.Client.UI.TUI.Screen;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Input.InputThread;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.List;

public class ShipConstructionScreen extends Screen {
    private WidgetTUI componentSelectionWidget;
    private WidgetTUI componentSelectionCommandsWidget;

    private WidgetTUI shipConstructionWidget;
    private WidgetTUI shipConstructionCommandsWidget;

    private WidgetTUI cardSubdeckWidget;
    private WidgetTUI cardSubdeckCommandsWidget;

    private WidgetTUI otherPlayerShipWidget;
    private WidgetTUI otherPlayerShipCommandsWidget;

    public ShipConstructionScreen(ClientModel model, InputThread inputThread) {
        super(model, inputThread);
    }

    /**
     * Initializes the widget containing all the commands
     * available during the component selection menu
     */
    public void initComponentSelectionCommands() {
        this.componentSelectionCommandsWidget = new WidgetTUI();

        this.componentSelectionCommandsWidget.appendString("(1) Select tile");
        this.componentSelectionCommandsWidget.appendString("(2) Select reserved tile");
        this.componentSelectionCommandsWidget.appendString("(3) Finish ship");
        this.componentSelectionCommandsWidget.appendString("(4) Flip timer");
        this.componentSelectionCommandsWidget.appendString("(5) Visualize sub-deck");
        this.componentSelectionCommandsWidget.appendString("(6) Visualize other ships");

        this.componentSelectionCommandsWidget.addPadding(0, 1, 0, 1);
        this.componentSelectionCommandsWidget.wrapWidgetWithBorder();
    }

    /**
     * Initializes the widget containing all the commands
     * available during the ship construction menu
     */
    public void initShipConstructionCommands() {
        WidgetTUI leftWidget = new WidgetTUI();
        WidgetTUI rightWidget = new WidgetTUI();

        leftWidget.appendString("(1) Deselect tile");
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
    public void initCardSubdeckCommandsWidget() {
        this.cardSubdeckCommandsWidget = new WidgetTUI();

        this.cardSubdeckCommandsWidget.appendString("(1) Select deck #1");
        this.cardSubdeckCommandsWidget.appendString("(2) Select deck #2");
        this.cardSubdeckCommandsWidget.appendString("(3) Select deck #3");

        this.cardSubdeckCommandsWidget.addPadding(0, 1, 0, 1);
        this.cardSubdeckCommandsWidget.wrapWidgetWithBorder();
    }

    /**
     * Initializes the widget containing all the
     * available ships that a player can look at
     */
    public void initOtherPlayerShipCommandsWidget() {
        int i, len;

        this.otherPlayerShipCommandsWidget = new WidgetTUI();
        List<String> allNicknames = this.model.getAllPlayersNicknames();
        len = allNicknames.size();

        for (i = 0; i < len; i++) {
            this.otherPlayerShipCommandsWidget.appendString("(" + i + ") " + allNicknames.get(i));
        }

        this.otherPlayerShipCommandsWidget.addPadding(0, 1, 0, 1);
        this.otherPlayerShipCommandsWidget.wrapWidgetWithBorder();
    }

    @Override
    public void showShipConstruction(ShipConstructionDTO shipConstruction) throws InterruptedException {
        System.out.println("SHIP CONSTRUCTION START");
        System.out.println("ACQUIRING INPUT");

        String line;

        do {
            line = this.inputThread.waitForInput();

            if (line != null) {
                System.out.println("NO INTERRUPT --> \"" + line + "\"");
            }
            else {
                System.out.println("INTERRUPT RECEIVED");
            }
        }
        while (line != null);
    }
}
