package it.polimi.ingsw.is25am28.components;

public abstract sealed class Component permits Cannon, Cabin, Storage, Vitals {
      private int col;
      private int row;
      private int[] sides;
      /**
       * index between 0 to 3, 
       * that indicates which is the top side of the component
       */
      private int top;

      /**
       * coordinates into the ship
       * @return [column,row]
       */
      public int[] getPosition() {
            int[] position = new int[2];

            position[0] = col;
            position[1] = row;

            return position;
      }

      public Component rotateLeft(){
            top--;

            if( top < 0 )
                  top = 3;

            return this;
      }

      public Component rotateRight(){
            top++;

            if( top > 3 )
                  top = 0;

            return this;
      }

      public int getLeftSide(){
            return sides[sides[ (top + 3)%4 ]];
      }

      public int getRightSide(){
            return sides[ (top + 1)%4 ];
      }

      public int getTopSide(){
            return sides[top];
      }

      public int getBottomSide(){
            return sides[ (top + 2)%4 ];
      }

      /**
       * 
       * @param nearest order is top[0], right[1], bottom[2], left[3]
       * @return if the component is placed correctly
       */
      public abstract boolean check( Component[] nearest );
}
