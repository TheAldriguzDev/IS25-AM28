package it.polimi.ingsw.is25am28.Components;

import java.util.List;

public final class Shield extends Component {

      public Shield(List<Integer> connectors) {
            super(connectors);
      }
      /**
       * return the two sides that are covered by the 
       * shield. they are returned with the usual standard,
       * 0: top
       * 1: right
       * 2: bottom
       * 3: left
       */
      public int[] getCoveredSide() {
            int[] covered = new int[2];

            for (int i = 0; i < 2; i++) {
                  covered[i] = (direction + i) % 4;
            }

            return covered;
      }
}