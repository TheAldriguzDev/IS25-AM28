package it.polimi.ingsw.is25am28.Model.Components;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.UnicodeCharacters;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils.SPACE;

public final class Structural extends Component {
      public static final String alias = "STRUCT";

      // Constructor
      public Structural(List<Integer> connectors, String path) {
            super(connectors, path);
      }

      @Override
      public List<String> getComponentScreen() {
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
