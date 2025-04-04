package it.polimi.ingsw.is25am28.Components;

import java.util.List;
import java.util.Map;

public final class Battery extends Component {
      private final int maxAvailability;
      private int available;

      public Battery(List<Integer> connectors, int maxAvailability) {
            super(connectors);
            this.maxAvailability = maxAvailability;
            available = maxAvailability;
      }

      /**
       * @return The currently available energy amount stored inside the battery
       */
      public int getAvailability() {
            return available;
      }

      /**
       * @return The maximum charge this battery can hold when full
       */
      public int getMaxAvailability() {
            return maxAvailability;
      }

      /**
       * @param energyLevel The energy level that this battery will be set to
       * @throws IllegalArgumentException If anyone tries to overcharge the battery or set its charge level to a negative value
       */
      public void setAvailability(int energyLevel) throws IllegalArgumentException {
            if (energyLevel < 0 || energyLevel > maxAvailability) {
                  throw new IllegalArgumentException("ERROR: Battery energy level must be between 0 and maxAvailability");
            }
            else {
                  available = energyLevel;
            }
      }

      /**
       * @param energyToConsume The units of charge that will be consumed from this battery
       * @throws IllegalArgumentException If anyone attempts to discharge the battery for more than its currently storing
       */
      public void useBattery(int energyToConsume) throws IllegalArgumentException {
            if (available >= energyToConsume) {
                  available -= energyToConsume;
            }
            else {
                  throw new IllegalArgumentException("ERROR: Cannot consume more charge than available");
            }
      }

      @Override
      public Map<String,Object> toMap() {
            Map<String,Object> map = super.toMap();

            map.put("capacity", maxAvailability );
            map.put("available", available );


            return map;
      }
}