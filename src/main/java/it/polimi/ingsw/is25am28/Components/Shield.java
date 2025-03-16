package it.polimi.ingsw.is25am28.Components;

public final class Shield extends Component {
      public Shield(int row, int col, int direction, int[] sides) {
            super(row, col, direction, sides);
      }

      public boolean check(Component[] nearest ) throws Error{
            return false;
      }
}