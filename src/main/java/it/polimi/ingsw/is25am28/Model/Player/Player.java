package it.polimi.ingsw.is25am28.Model.Player;

import it.polimi.ingsw.is25am28.Model.Board.Cell;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

public class Player {
      private final PlayerColor color;
      private final String nickname;
      private final Ship ship;
      private Cell cell;
      private int cursor;
      private int credits;
      private int lostPieces;
      private boolean connected;
      private boolean eliminated;

      // Constructor
      public Player(String nickname, PlayerColor color, int level) {
            this.color = color;
            this.nickname = nickname;
            this.ship = new Ship(level);
            this.cell = null;
            this.cursor = 0;
            this.credits = 0;
            this.lostPieces = 0;
            this.connected = true;
            this.eliminated = false;
      }

      /**
       * @return This player's nickname
       */
      public String getNickname(){
            return this.nickname;
      }

      /**
       * @return This player's nickname
       */
      public String getColorToString() {
            return this.color.toString();
      }

      /**
       * @return This player's PlayerColor instance.
       */
      public PlayerColor getColor() {
            return this.color;
      }

      /**
       * @return This player's board cursor.
       */
      public int getCursor(){
            return this.cursor;
      }

      /**
       * @param cursor The board cursor to set to this player
       */
      public Player setCursor(int cursor) {
            this.cursor = cursor;
            return this;
      }

      /**
       * @return The actual number of credits of this player, given
       *         that each lost piece costs 1 credit.
       */
      public int getCredits() {
            return this.credits - this.lostPieces;
      }

      /**
       * @param credits The amount of credits to set as the
       *                total credits of this player.
       */
      public Player setCredits(int credits) {
            this.credits = credits;
            return this;
      }

      /**
       * @param credits The amount of credits to add to the
       *                total credits counter of this player.
       */
      public Player addCredits(int credits) {
            this.credits += credits;
            return this;
      }

      /**
       * @return This player's ship reference.
       */
      public Ship getShip() {
            return this.ship;
      }

      /**
       * @return The cell where this player is currently placed.
       *         If it's null, then the player is not on the board.
       */
      public Cell getCurrentCell() {
            return this.cell;
      }

      /**
       * @param cell The cell where this player will be placed.
       */
      public Player setCurrentCell(Cell cell) {
            this.cell = cell;
            return this;
      }

      /**
       * @return This player's total amount of lost pieces.
       */
      public int getLostPieces() {
            return this.lostPieces;
      }

      /**
       * @param lostPieces The amount of lost pieces to set as the
       *                   total lost pieces of this player.
       */
      public Player setLostPieces(int lostPieces) {
            this.lostPieces = lostPieces;
            return this;
      }

      /**
       * @param lostPieces The amount of lost pieces to add to the
       *                   total lost pieces of this player.
       */
      public Player addLostPieces(int lostPieces) {
            this.lostPieces += lostPieces;
            return this;
      }

      /**
       * Marks this player as eliminated and removes it
       * from the board cell that contains it.
       */
      public Player eliminate() {
            this.eliminated = true;
            this.cell.setPlayer(null);
            this.cell = null;

            return this;
      }

      /**
       * @return TRUE if this player is eliminated,
       *         FALSE otherwise.
       */
      public boolean isEliminated() {
            return this.eliminated;
      }

      /**
       * @return TRUE if this player is connected,
       *         FALSE otherwise.
       */
      public boolean isConnected() {
            return connected;
      }

      /**
       * @param connectionStatus The player's new connection status to set to the player.
       */
      public Player setConnected(boolean connectionStatus) {
            connected = connectionStatus;
            return this;
      }
}
