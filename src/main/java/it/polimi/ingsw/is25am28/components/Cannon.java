package it.polimi.ingsw.is25am28.Components;

public final class Cannon extends Component {
      private final int force;

      public Cannon( int force ){
            this.force = force;
      }

      public float getFirePower(){
            return getDirection() != 0 ? force/2: force;
      }

      public boolean requireEnergy(){
            return force > 1;
      }

      @Override
      public boolean check( Component[] nearest ){

            if( nearest[getDirection()] != null ){
                  return false;
            }

            return super.check(nearest);
      }
}