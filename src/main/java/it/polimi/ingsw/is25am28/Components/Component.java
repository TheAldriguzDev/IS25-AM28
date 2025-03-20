package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.Connector;

public abstract sealed class Component permits Cannon, Cabin, Storage, Vital, Engine, Battery, Shield, Structural {
      private int col;
      private int row;

      protected Connector[] sides;
      /**
       * index between 0 and 3,
       * that indicates which is the direction side of the component
       */
      protected int direction;


      public Component( int[] connectors ) {
            sides = new Connector[4];

            for( int i = 0; i < 4; i++ ){
                  switch(connectors[i]){
                        case 0: sides[i] = Connector.ZERO_PIPES;
                              break;
                        case 1: sides[i] = Connector.ONE_PIPE;
                              break;
                        case 2: sides[i] = Connector.TWO_PIPES;
                              break;
                        case 3: sides[i] = Connector.THREE_PIPES;
                              break;
                        default: throw new Error("invalid connections " + sides[i] );
                  }
            }
      }

      /**
       *
       * @param nearest order is direction[0], right[1], bottom[2], left[3]
       * @return if the component is placed correctly
       */
      public boolean check( Component[] nearest ) {
            if(
                    nearest[0] != null && (
                            ( getTopSide() == Connector.ZERO_PIPES && nearest[0].getBottomSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                    ( getTopSide() != Connector.THREE_PIPES && nearest[0].getBottomSide() != Connector.THREE_PIPES && getTopSide() != nearest[0].getBottomSide() ) // they are not equals (even with 3 piped conjunction)
                    )
            ){
                  return false;
            }

            if(
                    nearest[1] != null && (
                            ( getRightSide() == Connector.ZERO_PIPES && nearest[1].getLeftSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                    ( getRightSide() != Connector.THREE_PIPES && nearest[1].getLeftSide() != Connector.THREE_PIPES && getRightSide() != nearest[1].getLeftSide() ) // they are not equals (even with 3 piped conjunction)
                    )
            ){
                  return false;
            }

            if(
                    nearest[2] != null && (
                            ( getBottomSide() == Connector.ZERO_PIPES && nearest[2].getTopSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                    ( getBottomSide() != Connector.THREE_PIPES && nearest[2].getTopSide() != Connector.THREE_PIPES && getBottomSide() != nearest[2].getTopSide() ) // they are not equals (even with 3 piped conjunction)
                    )
            ){
                  return false;
            }

            if(
                    nearest[3] != null && (
                            ( getLeftSide() == Connector.ZERO_PIPES && nearest[3].getRightSide() != Connector.ZERO_PIPES ) || // they are not both 0
                                    ( getLeftSide() != Connector.THREE_PIPES && nearest[3].getRightSide() != Connector.THREE_PIPES && getLeftSide() != nearest[3].getRightSide() ) // they are not equals (even with 3 piped conjunction)
                    )
            ){
                  return false;
            }

            return true;
      }

      // Returns the component's direction
      public int getDirection() {
            return this.direction;
      }

      /**
       * coordinates into the ship
       * @return [row, col]
       */
      public int[] getPosition() {
            int[] position = new int[2];

            position[0] = row;
            position[1] = col;

            return position;
      }

      public void setPosition(int row, int col) {
            this.row = row;
            this.col = col;
      }

      public Component rotateLeft(){
            direction--;

            if( direction < 0 )
                  direction = 3;

            return this;
      }

      public Component rotateRight(){
            direction++;

            if( direction > 3 )
                  direction = 0;

            return this;
      }

      public Connector getLeftSide(){
            return sides[ (direction + 3)%4 ];
      }

      public Connector getRightSide(){
            return sides[ (direction + 1)%4 ];
      }

      public Connector getTopSide(){
            return sides[direction];
      }
      public Connector getBottomSide(){
            return sides[ (direction + 2)%4 ];
      }
}