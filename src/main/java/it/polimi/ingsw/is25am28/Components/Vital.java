package it.polimi.ingsw.is25am28.Components;

public final class Vital extends Component {
      private final VitalType vitalType;

      public Vital(int row, int col, int[] connectors, int type ) {
            super(row, col, connectors);
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

      public boolean check(Component[] nearest ){
            return false;
      }
}