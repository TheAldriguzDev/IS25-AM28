package it.polimi.ingsw.is25am28;

import it.polimi.ingsw.is25am28.Board.Cell;
import it.polimi.ingsw.is25am28.Ship.Ship;



public class Player {
      private final Color color;
      private final String nickname;
      private final Ship ship = new Ship();
      private int cursor = 0;
      private int credits = 0;
      private int lostPieces = 0;
      private Cell cell;

      public Player( String nickname, Color color, int cursor ){
            this.color = color;
            this.cursor = cursor;
            this.nickname = nickname;
      }

      public Player( String nickname, Color color ){
            this.color = color;
            this.nickname = nickname;
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

      public Color getColor(){
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
}
