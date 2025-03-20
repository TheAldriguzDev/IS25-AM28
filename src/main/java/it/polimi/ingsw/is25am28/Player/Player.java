package it.polimi.ingsw.is25am28.Player;

import it.polimi.ingsw.is25am28.Board.Cell;
import it.polimi.ingsw.is25am28.Ship.Ship;

public class Player {
      private final PlayerColor color;
      private final String nickname;
      private final Ship ship;
      private int cursor = 0;
      private int credits = 0;
      private int lostPieces = 0;
      private Cell cell;

      private boolean lost = false;

      public Player( String nickname, PlayerColor color, int level ){
            this.color = color;
            this.nickname = nickname;
            ship = new Ship(level);
      }

      public String getNickname(){
            return nickname;
      }

      public int getCursor(){
            return cursor;
      }

      public Player setCursor( int cursor ){
            this.cursor = cursor;
            return this;
      }

      public PlayerColor getPlayerColor(){
            return color;
      }

      /**
       * @return number of credits. it already includes the lost pieces penalty
       */
      public int getCredits(){
            return credits - lostPieces;
      }

      public Player setCredits( int credits ){
            this.credits = credits;
            return this;
      }

      public Player addCredits( int credits ){
            this.credits += credits;
            return this;
      }

      public Ship getShip(){
            return ship;
      }

      public Cell getCurrentCell(){
            return cell;
      }

      public Player setCurrentCell( Cell cell ){
            this.cell = cell;
            return this;
      }

      public int getLostPieces(){
            return lostPieces;
      }

      public Player setLostPieces( int lost ){
            this.lostPieces = lost;
            return this;
      }

      public Player addLostPieces( int lost ){
            this.lostPieces += lost;
            return this;
      }

      public Player eliminate(){
            this.lost = true;
            return this;
      }

      public boolean isEliminated(){
            return lost;
      }
}