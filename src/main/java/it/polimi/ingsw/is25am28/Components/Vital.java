package it.polimi.ingsw.is25am28.Components;
import java.util.List;
import java.util.Map;

public final class Vital extends Component {
      private final VitalType vitalType;

      public Vital(List<Integer> connectors, int type ) {
            super(connectors);
            if( type == 0 )
                  this.vitalType = VitalType.BROWN_VITAL;
            else if( type == 1 )
                  this.vitalType = VitalType.PURPLE_VITAL;
            else
                  throw new Error("vital type not recognized");
      }

      public VitalType getVitalType() {
            return vitalType;
      }

      @Override
      public Map<String,Object> toMap(){
            Map<String,Object> map = super.toMap();

            if( vitalType == VitalType.BROWN_VITAL )
                  map.put("type", 0 );
            else
                  map.put("type", 1 );

            return map;
      }
}