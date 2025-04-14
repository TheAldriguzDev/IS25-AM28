package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.TUI.Utils.PrintUtils.*;

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
            List<String> customBorderScheme = generateComponentCustomBorder();

            List<String> screen = new ArrayList<String>();
            String nameAlias = SPACE + Structural.alias;

            screen.add(nameAlias + SPACE.repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(SPACE + UnicodeCharacters.BACKGROUND_MESH.repeat(width - 2) + SPACE);
            }

            return WidgetTUI.wrapScreenWithBorder(screen, customBorderScheme);
      }
}