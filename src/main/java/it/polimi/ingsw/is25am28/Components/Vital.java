package it.polimi.ingsw.is25am28.Components;
import java.util.List;
import java.util.Map;

public final class Vital extends Component {
      private final VitalType vitalType;

      public Vital(List<Integer> connectors, int type ) {
            super(connectors);
            if( type == VitalType.BROWN_VITAL.ordinal() )
                  this.vitalType = VitalType.BROWN_VITAL;
            else if( type == VitalType.PURPLE_VITAL.ordinal() )
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

            map.put("type", vitalType.ordinal() );

            return map;
      }
}