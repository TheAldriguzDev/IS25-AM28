package it.polimi.ingsw.is25am28.Board;

import java.util.ArrayList;

public class BoardLevel2 extends Board {

    public BoardLevel2() {
        super();
        this.setSize(24);
        this.setLevel(2);
    }

    /**
     * buildBoard builds the board and sets the cells where the players can be placed.
     * */
    public void buildBoard() {
        ArrayList<Cell> initialCells = new ArrayList<>();

        for (int i = 0; i < getSize(); i++) {
            Cell newCell = this.addCell(i);

            if (i == 0 || i == 1 || i == 3 || i == 6) {
                initialCells.addFirst(newCell);
            }
        }

        this.setInitialCells(initialCells);
    }
}