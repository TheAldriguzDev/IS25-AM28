package it.polimi.ingsw.is25am28;

public class Player {
      public String getNickname(){
            return "";
      }

      public int getCursor(){
            return 0;
      }

      public Player setCursor( int cursor ){
            return this;
      }

      public String getColor(){
            return "";
      }

      public Player setColor( String color ){
            return this;
      }

      public int getCredits(){
            return 0;
      }

      public Player setCredits( int credits ){
            return this;
      }

      public Ship getShip(){
            throw new Error(); 
      }

      public Cell getCurrentCell(){
            throw new Error(); 
      }

      public Player setCurrentCell( Cell cell ){
            return this;
      }
}
