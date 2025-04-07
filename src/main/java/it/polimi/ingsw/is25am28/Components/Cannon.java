package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.ComponentAlias;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Cannon extends Component {
      private final int force;

      public Cannon(List<Integer> connectors, int force) {
            super(connectors);
            this.force = force;
      }

      /**
       * @return This cannon's firepower by also taking into account the
       *         current rotation of the cannon
       */
      public float getFirePower(){
            return (getDirection() != 0) ? (((float) force) / 2) : force;
      }

      /**
       * @return If the current cannon is a double cannon and thus
       *         requires energy to be activated
       */
      public boolean requireEnergy(){
            return force > 1;
      }

      /**
       * @param nearest This cannon's neighbours in the following order:
       *                      top[0], right[1], bottom[2], left[3]
       * @return TRUE if the cannon is correctly placed, in terms of component connectors, and if
       *         the cannon has an empty cell directly in front of the barrel, FALSE otherwise
       */
      @Override
      public boolean check(Component[] nearest) {
            if (nearest[getDirection()] != null) {
                  return false;
            }

            return super.check(nearest);
      }

      @Override
      public Map<String,Object> toMap(){
            Map<String,Object> map = super.toMap();

            map.put("force", force );

            return map;
      }

      @Override
      protected void setComponentScreen(WidgetTUI componentWidget) throws NullWidgetException {
            if (componentWidget == null) {
                  throw new NullWidgetException("ERROR: Given widget is null (Cannot add screen)");
            }

            int height = componentWidget.getHeight();
            int width = componentWidget.getWidth();

            List<String> screen = new ArrayList<String>();
            String nameAlias = PrintUtils.getSpace() + ComponentAlias.CANNON.getAlias();

            screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(PrintUtils.getSpace().repeat(width));
            }

            componentWidget.setScreen(screen);
      }
}