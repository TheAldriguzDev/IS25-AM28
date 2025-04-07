package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.ComponentAlias;
import it.polimi.ingsw.is25am28.TUI.Exceptions.NullWidgetException;
import it.polimi.ingsw.is25am28.TUI.PrintUtils;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Vital extends Component {
      private final VitalType vitalType;

      public Vital(List<Integer> connectors, int type) {
            super(connectors);

            if (type == VitalType.BROWN_VITAL.ordinal()) {
                  this.vitalType = VitalType.BROWN_VITAL;
            }
            else if (type == VitalType.PURPLE_VITAL.ordinal()) {
                  this.vitalType = VitalType.PURPLE_VITAL;
            }
            else {
                  throw new IllegalArgumentException("ERROR: Given vital type is not recognized");
            }
      }

      public VitalType getVitalType() {
            return vitalType;
      }

      @Override
      public Map<String,Object> toMap() {
            Map<String,Object> map = super.toMap();

            map.put("type", vitalType.ordinal());

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
            String nameAlias = PrintUtils.getSpace() + ComponentAlias.VITAL.getAlias();

            screen.add(nameAlias + PrintUtils.getSpace().repeat(width - nameAlias.length()));

            for (int i = 1; i < height; i++) {
                  screen.add(PrintUtils.getSpace().repeat(width));
            }

            componentWidget.setScreen(screen);
      }
}