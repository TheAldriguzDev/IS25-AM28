package it.polimi.ingsw.is25am28.Components;
import java.util.List;
import java.util.Map;

public final class Engine extends Component {
      private final int speed;

      public Engine( List<Integer> connectors, int speed ){
            super( connectors );
            this.speed = speed;
      }

      public int getSpeed(){
            return speed;
      }

      public boolean requireEnergy(){
            return speed > 1;
      }

      @Override
      public boolean check( Component[] nearest ){

            // if it is rotated
            if( getDirection() != 0 ){
                  return false;
            }

            // if the bottom cell is not void
            if( nearest[2] != null ){
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