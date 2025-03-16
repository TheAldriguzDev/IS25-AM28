package it.polimi.ingsw.is25am28.Board;

import it.polimi.ingsw.is25am28.Player.Player;

import java.util.Optional;

public class Cell {
    private final int idx;
    private Optional<Player> player;
    private Cell prev;
    private Cell next;

    /**
     * Cell constructor when the player is not given
     * @param idx The index of the cell
     */
    public Cell(int idx) {
        this.idx = idx;
        this.player = Optional.empty();
    }

    /**
     * Cell constructor when the player is given
     * @param idx The index of the cell
     * @param player The player initially set in the cell
     */
    public Cell(int idx, Player player) {
        this.idx = idx;
        this.player = Optional.of(player);
    }


    public int getIdx() {
        return idx;
    }

    public Optional<Player> getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = Optional.of(player);
    }

    public void removePlayer() {
        this.player = Optional.empty();
    }

    public Cell getPrevCell() {
        return prev;
    }

    public void setPrevCell(Cell cell) {
        this.prev = cell;
    }

    public Cell getNextCell() {
        return next;
    }

    public void setNextCell(Cell next) {
        this.next = next;
    }

    public boolean isEmpty() {
        return player.isEmpty();
    }

    @Override
    public String toString() {
        return "Cell " + getIdx() + ": " + player.map(Player::getNickname).orElse("is empty");
    }
}