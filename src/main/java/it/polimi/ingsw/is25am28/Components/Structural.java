package it.polimi.ingsw.is25am28.Components;


import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public final class Structural extends Component {
      public static final String alias = "STRUCT";

      public Structural(List<Integer> connectors) {
            super(connectors);
      }

      @Override
      public List<String> getComponentScreen() {
            // TODO: Understand better these indexes
            int scale = 3;
            int height = scale;
            int width = 3 * height + 2;

            // Creating the custom border character list that will be
            // used by the wrapper to create the border
            //List<String> customBorderScheme = new ArrayList<String>(WidgetTUI.defaultBorderCharacters);
            List<String> customBorderScheme = generateComponentCustomBorder();

            // Adding this component's connectors to the border scheme
//            customBorderScheme.set(8, "" + this.getTopSide().ordinal());
//            customBorderScheme.set(9, "" + this.getRightSide().ordinal());
//            customBorderScheme.set(10, "" + this.getBottomSide().ordinal());
//            customBorderScheme.set(11, "" + this.getLeftSide().ordinal());

            List<String> screen = new ArrayList<String>();
            String nameAlias = PrintUtils.getSpace() + Structural.alias;

            screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(PrintUtils.getSpace() + "\u2591".repeat(width - 2) + PrintUtils.getSpace());
            }

            return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
      }
}