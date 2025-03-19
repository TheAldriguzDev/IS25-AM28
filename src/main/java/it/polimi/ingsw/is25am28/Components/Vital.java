package it.polimi.ingsw.is25am28.Components;

public final class Vital extends Component {
      private final VitalType vitalType;

      public Vital(int[] connectors, int type ) {
            super(connectors);

            switch (type) {
                  case 0 -> this.vitalType = VitalType.PURPLE_VITAL;
                  case 1 -> this.vitalType = VitalType.BROWN_VITAL;
                  default -> throw new Error("ERROR: Given vital type not recognised");
            }
      }

      public VitalType getVitalType() {
            return vitalType;
      }
}