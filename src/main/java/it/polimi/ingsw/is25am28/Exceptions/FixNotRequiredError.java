package it.polimi.ingsw.is25am28.Exceptions;

public class FixNotRequiredError extends TargetError {
      public FixNotRequiredError( String player ){
            super(player, player + " doesn't need any fix to they ship");
      }
}
