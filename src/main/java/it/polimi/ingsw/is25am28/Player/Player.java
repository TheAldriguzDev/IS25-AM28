package it.polimi.ingsw.is25am28.Player;

import it.polimi.ingsw.is25am28.Board.Cell;
import it.polimi.ingsw.is25am28.Ship.Ship;

/*
* Noi crediamo che sia meglio inizializzare tutte le cose nel costruttore, non nella parte dichiarativa degli attributi
* */

public class Player {
      private final PlayerColor color;
      private final String nickname;
      private final Ship ship = new Ship();
      private int cursor = 0;
      private int credits = 0;
      private int lostPieces = 0;
      private Cell cell;
      private boolean isEliminated;

      public Player( String nickname, PlayerColor color, int cursor ){
            this.color = color;
            this.nickname = nickname;
            this.cursor = cursor;
            this.isEliminated = false;
      }

      public Player( String nickname, PlayerColor color ){
            this.color = color;
            this.nickname = nickname;
      }

      public String getNickname(){
            return nickname;
      }

      public int getCursor(){
            return cursor;
      }

      public void setCursor( int cursor ){
            this.cursor = cursor;
            // return this;
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

      public void setCredits( int credits ){
            this.credits = credits;
      }

      public void addCredits( int credits ){
            this.credits += credits;
      }

      public Ship getShip(){
            return ship;
      }

      public Cell getCurrentCell(){
            return this.cell;
      }

      public void setCurrentCell( Cell cell ) {
            this.cell = cell;
            // return this;
      }

      public boolean hasLost(){
            return cell == null;
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

      // AGGIUNTI METODI PER PLAYER ELIMINATI

      public boolean isEliminated(){
            return isEliminated;
      }

      public void eliminate(){
            this.isEliminated = true;
      }
}