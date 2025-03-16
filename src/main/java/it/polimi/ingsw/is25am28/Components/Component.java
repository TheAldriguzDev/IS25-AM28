package it.polimi.ingsw.is25am28.Components;

public abstract sealed class Component permits Cannon, Cabin, Storage, Vital, Engine, Battery, Shield, Structural {
      private int col;
      private int row;

      protected int[] sides;
      /**
       * index between 0 and 3,
       * that indicates which is the direction side of the component
       */
      protected int direction;


      public Component(int row, int col, int direction, int[] sides) {
            this.row = row;
            this.col = col;
            this.direction = direction;
            this.sides = sides;
      }

      /**
       *
       * @param nearest order is direction[0], right[1], bottom[2], left[3]
       * @return if the component is placed correctly
       */
      public boolean check( Component[] nearest ) {



            if(
                    nearest[0] != null && (
                            ( getTopSide() == 0 && nearest[0].getBottomSide() != 0 ) || // they are not both 0
                                    ( getTopSide() != 3 && nearest[0].getBottomSide() != 3 && getTopSide() != nearest[0].getBottomSide() ) // they are not equals (even with 3 piped conjunction)
                    )
            ){
                  return false;
            }

            if(
                    nearest[1] != null && (
                            ( getRightSide() == 0 && nearest[1].getLeftSide() != 0 ) || // they are not both 0
                                    ( getRightSide() != 3 && nearest[1].getLeftSide() != 3 && getRightSide() != nearest[1].getLeftSide() ) // they are not equals (even with 3 piped conjunction)
                    )
            ){
                  return false;
            }

            if(
                    nearest[2] != null && (
                            ( getBottomSide() == 0 && nearest[2].getTopSide() != 0 ) || // they are not both 0
                                    ( getBottomSide() != 3 && nearest[2].getTopSide() != 3 && getBottomSide() != nearest[2].getTopSide() ) // they are not equals (even with 3 piped conjunction)
                    )
            ){
                  return false;
            }

            if(
                    nearest[3] != null && (
                            ( getLeftSide() == 0 && nearest[3].getRightSide() != 0 ) || // they are not both 0
                                    ( getLeftSide() != 3 && nearest[3].getRightSide() != 3 && getLeftSide() != nearest[3].getRightSide() ) // they are not equals (even with 3 piped conjunction)
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

      public int getLeftSide(){
            return sides[sides[ (direction + 3)%4 ]];
      }

      public int getRightSide(){
            return sides[ (direction + 1)%4 ];
      }

      public int getTopSide(){
            return sides[direction];
      }

      public int getBottomSide(){
            return sides[ (direction + 2)%4 ];
      }


}