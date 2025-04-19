package it.polimi.ingsw.is25am28.Model.Exceptions;

public class FixNotRequiredError extends TargetException {
      public FixNotRequiredError( String player ){
            super(player, player + " doesn't need any fix to they ship");
      }
}
