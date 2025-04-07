package it.polimi.ingsw.is25am28.Components;
import it.polimi.ingsw.is25am28.TUI.ComponentAlias;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Engine extends Component {
      private final int speed;

      public Engine(List<Integer> connectors, int speed) {
            super(connectors);
            this.speed = speed;
      }

      public int getSpeed() {
            return speed;
      }

      public boolean requireEnergy() {
            return speed > 1;
      }

      @Override
      public boolean check(Component[] nearest) {
            // Engine is wrongly placed if the thruster is not facing backwards
            // (i.e.: the engine's direction is not pointing towards the thrust vector)
            if (getDirection() != 0) {
                  return false;
            }

            // Engine is wrongly placed if the thruster is blocked by a component
            if (nearest[2] != null) {
                  return false;
            }

            return super.check(nearest);
      }

      @Override
      public Map<String,Object> toMap(){
            Map<String,Object> map = super.toMap();

            map.put("speed", speed );

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
            String nameAlias = PrintUtils.getSpace() + ComponentAlias.ENGINE.getAlias();

            screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(PrintUtils.getSpace().repeat(width));
            }

            componentWidget.setScreen(screen);
      }
}