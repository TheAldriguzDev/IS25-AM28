package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.Lifeform.*;
import java.util.ArrayList;

public final class Cabin extends Component {
      private final boolean isCore;
      private final ArrayList<Lifeform> inhabitants;

      public Cabin( int[] connectors, boolean isCore ) {
            super(connectors);
            this.isCore = isCore;
            this.inhabitants = new ArrayList<>();

            if (isCore) {
                  this.inhabitants.add(new Lifeform(LifeformType.ASTRONAUT));
                  this.inhabitants.add(new Lifeform(LifeformType.ASTRONAUT));
            }
      }

      public boolean isCore() {
            return isCore;
      }

      public ArrayList<Lifeform> getInhabitants() {
            return inhabitants;
      }

      public int getAvailableSpace() {
            return 2 - inhabitants
                        .stream()
                        .mapToInt(Lifeform::getRequiredSpace)
                        .sum();
      }

      public void addInhabitant(Lifeform lifeform) throws IllegalArgumentException {
            if (isCore && (lifeform.getLifeformType() == LifeformType.PURPLE_ALIEN || lifeform.getLifeformType() == LifeformType.BROWN_ALIEN)) {
                  throw new IllegalArgumentException("You can't add an alien in the core cabin!");
            }

            if (lifeform.getRequiredSpace() > this.getAvailableSpace()) {
                  throw new IllegalArgumentException("No required space left!");
            }

            inhabitants.add(lifeform);
      }

      public void removeInhabitant(Lifeform lifeform) {
            inhabitants.remove(lifeform);
      }


}