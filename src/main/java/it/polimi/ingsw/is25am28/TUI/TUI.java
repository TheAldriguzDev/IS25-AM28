package it.polimi.ingsw.is25am28.TUI;

import it.polimi.ingsw.is25am28.TUI.WidgetTUI.InputWidgetTUI;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TUI {
    private Map<String, WidgetTUI> widgetMap;
    private WidgetTUI tuiWidget;
    private InputWidgetTUI inputWidget;

    /*
        [W1[W4, W5], W2, W3]
        [W6,         W7, W8]

        Map<String, WidgetTUI> map;

        public void updateComponent(JSON) {
            map.get("component").generateWidget(JSON);
            tui.compose().printWidget();
        }

     */

    /*
        TUI  or  GUI
         |        |
         ViewUpdater               ...
             |                      |
           QUEUE                  QUEUE
             |                      |
          Network -------------- Network

       ==============

           TUI---MAP
            |
        ---------
        |   |   |   ...
        W1  W2  W3  ...
     */


    // Constructor
    public TUI() {
        this.tuiWidget = new WidgetTUI();
        this.inputWidget = new InputWidgetTUI();
        this.widgetMap = new HashMap<>();

        // Setting the input widget's scanner to scan the stdin input stream
        this.inputWidget.setNewScanner(System.in);

        // TODO: Add inputWidget commands
        // this.inputWidget.addCommand(new CommandWidgetTUI(...));

        // TODO: Add all the widgets to the map and the TUI widget sublist



    }

    /**
     * Recursive method that updates this widget map by
     */
    private void compileWidgetMap() {
        for (List<WidgetTUI> widgetRow : this.tuiWidget.getAllWidgetComponents()) {
            for (WidgetTUI widget : widgetRow) {
                this.widgetMap.put(widget.getWidgetId() , widget);
            }
        }
    }

}
