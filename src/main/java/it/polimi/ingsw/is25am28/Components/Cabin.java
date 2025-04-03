package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.Lifeform.*;
import java.util.ArrayList;
import java.util.List;

public final class Cabin extends Component {
      private final boolean isCore;
      private final ArrayList<Lifeform> inhabitants;

      public Cabin(List<Integer> connectors, boolean isCore) {
            super(connectors);
            this.isCore = isCore;
            this.inhabitants = new ArrayList<>();

            if (isCore) {
                  this.inhabitants.add(new Lifeform(LifeformType.ASTRONAUT));
                  this.inhabitants.add(new Lifeform(LifeformType.ASTRONAUT));
            }
      }

      /**
       * @return If the current cabin is the ship's core
       */
      public boolean isCore() {
            return isCore;
      }

      /**
       * @return This cabin's current inhabitants
       */
      public List<Lifeform> getInhabitants() {
            return inhabitants;
      }

      /**
       * @return The amount of space available in the current cabin
       */
      public int getAvailableSpace() {
            return 2 - inhabitants
                        .stream()
                        .mapToInt(Lifeform::getRequiredSpace)
                        .sum();
      }

      /**
       * @param lifeform The lifeform that will live in this cabin
       * @throws IllegalArgumentException If anyone tries to put an alien inside the core cabin or if the
       *                                  cabin is overcrowded and thus can't house any other lifeforms
       */
      public void addInhabitant(Lifeform lifeform) throws IllegalArgumentException {
            if (isCore && (lifeform.getLifeformType() == LifeformType.PURPLE_ALIEN || lifeform.getLifeformType() == LifeformType.BROWN_ALIEN)) {
                  throw new IllegalArgumentException("ERROR: You can't add an alien in the core cabin!");
            }
            if (lifeform.getRequiredSpace() > this.getAvailableSpace()) {
                  throw new IllegalArgumentException("ERROR: No required space left!");
            }

            inhabitants.add(lifeform);
      }

      /**
       * @param lifeform The lifeform that will be removed from the current cabin
       */
      public void removeInhabitant(Lifeform lifeform) {
            inhabitants.remove(lifeform);
      }
}