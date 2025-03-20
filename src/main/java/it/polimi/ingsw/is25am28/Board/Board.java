package it.polimi.ingsw.is25am28.Board;

import it.polimi.ingsw.is25am28.GameModel.GameModel;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class Board {
    private int size;
    private Cell head;
    final ArrayList<Cell> initialCells = new ArrayList<>();
    private final List<Player> players;
    private final List<Player> eliminatedPlayer;
    private int level;

    public Board() {
        this.players = new ArrayList<>();
        this.eliminatedPlayer = new ArrayList<>();
    }

    public int getSize() { return size; }

    public void setSize(int size) { this.size = size; }

    /**
     * Returns the initials cells of the game where the players will be set when they finish their ship
     * */
    protected ArrayList<Cell> getInitialCells() {
        return initialCells;
    }

    protected void setInitialCells(ArrayList<Cell> initialCells) {
        this.initialCells.clear();
        this.initialCells.addAll(initialCells);
    }

    /**
     * Returns the current playing players
     * */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the eliminated players
     * */
    public List<Player> getEliminatedPlayers() {
        return eliminatedPlayer;
    }

    protected int getLevel() { return level; }

    protected void setLevel(int level) { this.level = level; }

    /**
     * Add a new player in the game if the nickname and the color is unique in the session
     * */
    public Board newPlayer(String nickname, PlayerColor color) throws IllegalArgumentException {
        if (!players.stream().filter(p -> p.getNickname().equals(nickname) || p.getPlayerColor().equals(color)).toList().isEmpty()) {
            throw new IllegalArgumentException("The selected nickname or color has been already used");
        }

        players.add(new Player(nickname, color, this.getLevel()));
        return this;
    }

    /**
     * Eliminates the given player from the game for some other reason that ARE NOT being doubled
     * */
    public Board eliminatePlayer(Player player) throws IllegalArgumentException {
        if (players.remove(player)) {
            player.eliminate();
            eliminatedPlayer.add(player);
        } else {
            throw new IllegalArgumentException("The given player is not in the players list");
        }

        return this;
    }

    /**
     * Utility method to add Cells to the Board
     * */
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

    /**
     * This method will be implemented in each specific implementation of the board to realize the specific board for each level
     * */
    public abstract void buildBoard();

    /**
     * Add the given player to the board in the first initial cell that is currently empty.
     * It is synchronized since multiple clients can request to be added to the board in the same time
     * */
    public synchronized void addPlayerToBoard(Player player) {
        for (Cell cell : initialCells) {
            if (cell.isEmpty()) {
                cell.setPlayer(player);

                player.setCurrentCell(cell);
                player.setCursor(cell.getIdx());

                return;
            }
        }

        // Re-order the current player position since the order of being added to the board can be different
        // of the order of the client registration to the game
        this.validatePlayersPosition();
    }

    /**
     * Move the given player from its cell of the given steps
     * */
    public void movePlayerForward(Player player, int steps) {
        Cell tmpCell = player.getCurrentCell();

        while (steps > 0) {
            tmpCell = tmpCell.getNextCell();

            // Decrease the current step counter only if the cell is actually empty (no player)
            if (tmpCell.isEmpty()) {
                steps--;
            }

            player.setCursor(player.getCursor() + 1);
        }

        if (!tmpCell.equals(player.getCurrentCell())) {
            player.getCurrentCell().removePlayer();
            tmpCell.setPlayer(player);

            player.setCurrentCell(tmpCell);
        }
    }

    public void movePlayerBackwards(Player player, int steps) {
        Cell tmpCell = player.getCurrentCell();

        while (steps > 0) {
            tmpCell = tmpCell.getPrevCell();

            // Decrease the current step counter only if the cell is actually empty (no player)
            if (tmpCell.isEmpty()) {
                steps--;
            }

            player.setCursor(player.getCursor() - 1);
        }

        if (!tmpCell.equals(player.getCurrentCell())) {
            player.getCurrentCell().removePlayer();
            tmpCell.setPlayer(player);

            player.setCurrentCell(tmpCell);
        }
    }

    /**
     * Method that check the players cursor to identify eventual doubled players and eliminate them.
     * It also reset the player list to maintain a correct order
     * */
    public synchronized void validatePlayersPosition() {
        int maxCursor = players.stream()
                .mapToInt(Player::getCursor)
                .max()
                .orElse(0);

        List<Player> doubledPlayers = players
                .stream()
                .filter(player -> player.getCursor() + this.getSize() < maxCursor)
                .toList();

        // Remove the player from the current players and add it to the eliminated ones
        // Set the cell to null, since it has been removed from the board and mark the player as eliminated
        for (Player player : doubledPlayers) {
            players.remove(player);
            eliminatedPlayer.add(player);

            player.getCurrentCell().removePlayer();
            player.eliminate();
            player.setCurrentCell(null); // TODO: CAPIRE SE ESISTE UN ALTRO MODO, COSI A ME NON PIACE
        }

        // Re-order the current players by theirs cursor
        players.sort((p1, p2) -> Integer.compare(p2.getCursor(), p1.getCursor()));
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