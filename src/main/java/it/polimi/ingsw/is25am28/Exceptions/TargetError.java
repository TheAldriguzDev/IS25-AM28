package it.polimi.ingsw.is25am28.Exceptions;

public abstract class TargetError extends Error {
      private final String player;

      public TargetError( String player, String message ){
            super();
            this.player = player;
      }

      public String getPlayer(){
            return player;
      }
}
