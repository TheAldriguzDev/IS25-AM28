package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.TUI.Printable;

import java.util.ArrayList;
import java.util.List;

public final class Structural extends Component implements Printable {
      public Structural(List<Integer> connectors) {
            super(connectors);
      }


      //Creates a 5x5
      public List<String> print() {
            List<String> screen = new ArrayList<>();
            int width = 5;
            int height = 5;
            String tmpString = "";
            tmpString += "\\U+250F";

            System.out.println(tmpString);





            return null;
      }
}