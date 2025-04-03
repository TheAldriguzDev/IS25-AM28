package it.polimi.ingsw.is25am28.Exceptions;

public class UncompletedShipException extends TargetError {
      public UncompletedShipException( String nickname ){
            super(nickname, "cannot go on with uncompleted ship");
      }
}
