package it.polimi.ingsw.is25am28.Model.Exceptions;

public class ShipPopulationFailException extends TargetError {
      public ShipPopulationFailException( String nickname ){
            super(nickname, "Failed to populate the ship");
      }
}
