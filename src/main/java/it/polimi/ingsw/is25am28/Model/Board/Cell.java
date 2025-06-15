package it.polimi.ingsw.is25am28.Model.Board;

import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.util.Optional;

/**
 * The Cell class represents a single cell in a board game. Each cell contains an index,
 * an optional player, and links to the previous and next cells, forming a linked structure.
 */
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
     * @return the index of the cell
     */
    public int getIdx() {
        return idx;
    }

    /**
     * Retrieves the player assigned to this cell, if present.
     *
     * @return an {@link Optional<Player>} containing the player assigned to this cell,
     *         or an empty {@link Optional} if no player is set.
     */
    public Optional<Player> getPlayer() {
        return player;
    }

    /**
     * Assigns a player to this cell.
     *
     * @param player the player to assign to this cell
     */
    public void setPlayer(Player player) {
        if (player == null) {
            this.player = Optional.empty();
        } else {
            this.player = Optional.of(player);
        }
    }

    /**
     * Removes the player assigned to this cell
     * */
    public void removePlayer() {
        this.player = Optional.empty();
    }

    /**
     * @return the previous cell linked to this cell, or null if no previous cell is set
     */
    public Cell getPrevCell() {
        return prev;
    }

    /**
     * Sets the previous cell linked to this cell
     * */
    public void setPrevCell(Cell cell) {
        this.prev = cell;
    }

    /**
     * @return the next cell linked to this cell, or null if no next cell is set
     */
    public Cell getNextCell() {
        return next;
    }

    /**
     * Sets the next cell linked to this cell
     */
    public void setNextCell(Cell next) {
        this.next = next;
    }

    /**
     * Checks if the cell is empty, meaning no player is currently assigned to it.
     *
     * @return {@code true} if the cell does not contain a player; {@code false} otherwise.
     */
    public boolean isEmpty() {
        return player.isEmpty();
    }

    @Override
    public String toString() {
        return "Cell " + getIdx() + ": " + player.map(Player::getNickname).orElse("is empty");
    }
}