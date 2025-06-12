package it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI;

public interface WidgetTUIGenerator {
    /**
     * Marks the class as capable to generate a widget to be displayed in a TUI
     * that can be composed with other widgets by the WidgetTUI framework to create
     * entire TUIs from a given set of WidgetTUIs.
     *
     * @return The TUI widget of the class this functional interface is applied to.
     */
    WidgetTUI generateWidget();
}
