package it.polimi.ingsw.is25am28.Model.Board;

import java.util.ArrayList;

public class BoardTestFlight extends Board {
    // Constructor
    public BoardTestFlight() {
        super();
        this.setSize(18);
        this.setLevel(0);
    }

    /**
     * buildBoard builds the board and sets the cells where the players can be placed.
     */
    public void buildBoard() {
        ArrayList<Cell> initialCells = new ArrayList<>();

        for (int i = 0; i < getSize(); i++) {
            Cell newCell = this.addCell(i);

            if (i == 0 || i == 1 || i == 2 || i == 4) {
                initialCells.addFirst(newCell);
            }
        }

        this.setInitialCells(initialCells);
    }
}
