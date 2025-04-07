package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.ComponentAlias;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;

public final class Shield extends Component {

      public Shield(List<Integer> connectors) {
            super(connectors);
      }
      /**
       * return the two sides that are covered by the 
       * shield. they are returned with the usual standard,
       * 0: top
       * 1: right
       * 2: bottom
       * 3: left
       */
      public int[] getCoveredSide() {
            int[] covered = new int[2];

            for (int i = 0; i < 2; i++) {
                  covered[i] = (direction + i) % 4;
            }

            return covered;
      }

      @Override
      protected void setComponentScreen(WidgetTUI componentWidget) throws NullWidgetException {
            if (componentWidget == null) {
                  throw new NullWidgetException("ERROR: Given widget is null (Cannot add screen)");
            }

            int height = componentWidget.getHeight();
            int width = componentWidget.getWidth();

            List<String> screen = new ArrayList<String>();
            String nameAlias = PrintUtils.getSpace() + ComponentAlias.SHIELD.getAlias();

            screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(PrintUtils.getSpace().repeat(width));
            }

            componentWidget.setScreen(screen);
      }
}