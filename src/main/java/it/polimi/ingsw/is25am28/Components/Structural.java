package it.polimi.ingsw.is25am28.Components;


import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;

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

            List<String> screen = new ArrayList<String>();
            String nameAlias = PrintUtils.getSpace() + Structural.alias;

            screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(PrintUtils.getSpace().repeat(width));
            }

            return screen;
      }
}