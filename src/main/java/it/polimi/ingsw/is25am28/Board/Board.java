package it.polimi.ingsw.is25am28.Board;

import it.polimi.ingsw.is25am28.Player.Player;

import java.util.ArrayList;
import java.util.Optional;

public abstract class Board {
    private int size;
    private Cell head;
    final ArrayList<Cell> initialCells = new ArrayList<>();

    public int getSize() { return size; }

    public void setSize(int size) { this.size = size; }

    private Cell getHead() { return head; }

    private void setHead(Cell head) { this.head = head; }

    protected ArrayList<Cell> getInitialCells() { return initialCells; }

    protected void setInitialCells(ArrayList<Cell> initialCells) {
        this.initialCells.clear();
        this.initialCells.addAll(initialCells);
    }

    protected Cell addCell(int idx) {
        Cell newCell = new Cell(idx);

        if (head == null) {
            head = newCell;
            head.setPrevCell(newCell);
            head.setNextCell(newCell);
        } else {
            Cell last = head.getPrevCell();
            last.setNextCell(newCell);
            newCell.setPrevCell(last);
            newCell.setNextCell(head);
            head.setPrevCell(newCell);
        }

        return newCell;
    }

    public abstract void buildBoard();

    public void addNewPlayer(Player player) {
        for (Cell cell : initialCells) {
            if (cell.isEmpty()) {
                cell.setPlayer(player);
                return;
            }
        }
    }

    public ArrayList<Player> movePlayerForward(Player player, Cell currCell, int steps) {
        ArrayList<Player> doubledPlayers = new ArrayList<>();
        Cell tmpCell = currCell;

        while (steps > 0) {
            tmpCell = tmpCell.getNextCell();

            // Check if the player in the current cell has been doubled by the moving one
            if (!tmpCell.isEmpty()) {
                Optional<Player> playerOptional = tmpCell.getPlayer();
                if (playerOptional.isPresent() && player.getCursor() >= 2 * playerOptional.get().getCursor()) {
                    doubledPlayers.add(playerOptional.get());
                    tmpCell.removePlayer();
                }

                continue;
            }

            player.setCursor(player.getCursor() + 1);
            steps--;
        }

        if (!tmpCell.equals(currCell)) {
            currCell.removePlayer();
            tmpCell.setPlayer(player);
        }

        return doubledPlayers;
    }

    public ArrayList<Player> movePlayerBackwards(Player player, Cell currCell, int steps) {
        ArrayList<Player> doubledPlayers = new ArrayList<>();
        Cell tmpCell = currCell;

        while (steps > 0) {
            tmpCell = tmpCell.getPrevCell();

            // Check if the moving player has been doubled by the player in the current cell
            if (!tmpCell.isEmpty()) {
                Optional<Player> playerOptional = tmpCell.getPlayer();
                if (playerOptional.isPresent() && 2 * player.getCursor() <= playerOptional.get().getCursor()) {
                    doubledPlayers.add(player);
                    currCell.removePlayer();
                    break;
                }

                continue;
            }

            player.setCursor(player.getCursor() - 1);
            steps--;
        }

        if (doubledPlayers.isEmpty() && !tmpCell.equals(currCell)) {
            currCell.removePlayer();
            tmpCell.setPlayer(player);
        }

        return doubledPlayers;
    }

    public void printBoard() {
        if (head == null) {
            System.out.println("The board is empty.");
        }

        Cell curr = head;
        do {
            System.out.println(curr.toString());
            curr = curr.getNextCell();
        } while (curr != head);
    }
}

