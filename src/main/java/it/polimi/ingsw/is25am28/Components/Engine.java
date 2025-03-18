package it.polimi.ingsw.is25am28.Components;

public final class Engine extends Component {
      private final int speed;

      public Engine( int[] connectors, int speed ){
            super( connectors );
            this.speed = speed;
      }

      public int getSpeed(){
            return speed;
      }

      public boolean requireEnergy(){
            return speed > 1;
      }

      @Override
      public boolean check( Component[] nearest ){

            // if it is rotated
            if( getDirection() != 0 ){
                  return false;
            }

            // if the bottom cell is not void
            if( nearest[2] != null ){
                  return false;
            }

            return super.check(nearest);
      }
}