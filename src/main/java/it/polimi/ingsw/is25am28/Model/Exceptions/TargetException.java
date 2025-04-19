package it.polimi.ingsw.is25am28.Model.Exceptions;

public abstract class TargetException extends Exception {
      private final String player;

      public TargetException( String player, String message ){
            super();
            this.player = player;
      }

      public String getPlayer(){
            return player;
      }
}
