package it.polimi.ingsw.is25am28.Model.Components;
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
}