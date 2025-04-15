package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

public class TUI {
    // TUI single components
    private WidgetTUI shipGridWidget;
    private WidgetTUI shipStatsWidget;
    private WidgetTUI boardWidget;
    private WidgetTUI cardWidget;
    private WidgetTUI consoleWidget;
    private WidgetTUI tui;
    private InputWidgetTUI inputWidget;

    // Constructor
    public TUI() {
        // TODO: Init all the other widgets
        //       !!! requires the client to be built first !!!
        this.shipGridWidget = new WidgetTUI().wrapWidgetWithBorder();
        this.shipStatsWidget = new WidgetTUI().wrapWidgetWithBorder();
        this.boardWidget = new WidgetTUI().wrapWidgetWithBorder();
        this.cardWidget = new WidgetTUI().wrapWidgetWithBorder();
        this.consoleWidget = new WidgetTUI().wrapWidgetWithBorder();
        this.inputWidget = new InputWidgetTUI();

        // Composing the TUI for the first time
        this.tui = this.composeTUI();
    }

    /**
     * Defines how the final TUI widget will result graphically
     * through many composition steps.
     *
     * @return The final TUI widget, ready to print the TUI
     *         (NOTE: not the inputWidget, only the TUI)
     */
    public WidgetTUI composeTUI() {
        this.tui = WidgetTUI.composeTwoWidgetsHorizontally(
            WidgetTUI.fillScreenWithSpaces(
                WidgetTUI.composeTwoWidgetsVertically(
                    WidgetTUI.fillScreenWithSpaces(
                        WidgetTUI.composeTwoWidgetsHorizontally(
                            boardWidget.addPadding(0, 1, 0, 1),
                            cardWidget.addPadding(0, 1, 0, 1)
                        )
                    ),
                    WidgetTUI.fillScreenWithSpaces(
                        WidgetTUI.composeTwoWidgetsHorizontally(
                            consoleWidget.addPadding(0, 1, 0, 1),
                            shipStatsWidget.addPadding(0, 1, 0, 1)
                        )
                    ).addPadding(1, 0, 0, 0)
                )
            ),
            shipGridWidget
        ).wrapWidgetWithBorder();

        return tui;
    }

    public void printTUI() {

    }
}
