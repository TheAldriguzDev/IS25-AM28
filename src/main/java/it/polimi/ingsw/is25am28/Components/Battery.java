package it.polimi.ingsw.is25am28.Components;

import java.util.List;
import java.util.Map;

public final class Battery extends Component {
      private final int maxAvailability;
      private int available;

      public Battery(List<Integer> connectors, int maxAvailability ){
            super(connectors);
            this.maxAvailability = maxAvailability;
            available = maxAvailability;
      }

      public int getAvailability(){
            return available;
      }

      public int getMaxAvailability(){
            return maxAvailability;
      }

      /**
       * set the availability to a specific value
       * @param battery
       * @return
       */
      public Battery setAvailability( int battery ){
            available = battery;

            return this;
      }


      /**
       *  used to remove specific qty of battery
       * @param battery
       * @throws Error if the availability goes under 0
       */
      public Battery useBattery( int battery ) throws Error {
            available -= battery;

            if( available < 0 ){
                  throw new Error("removed too much battery");
            }

            return this;
      }

      @Override
      public Map<String,Object> toMap(){
            Map<String,Object> map = super.toMap();

            map.put("capacity", maxAvailability );
            map.put("available", available );


            return map;
      }

}