package it.polimi.ingsw.is25am28.Components;

public final class Vital extends Component {
      private final VitalType vitalType;

      public Vital(VitalType vitalType,  int row, int col, int top, int[] sides) {
            super(row, col, top, sides);
            this.vitalType = vitalType;
      }

      public VitalType getVitalType() {
            return vitalType;
      }

      public boolean check(Component[] nearest ){
            return false;
      }
}