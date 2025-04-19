package it.polimi.ingsw.is25am28.Model.Exceptions;

public class TimerFlipException extends TargetException {
      public TimerFlipException( String nickname ){
            super( nickname, "error in timer flip attempt");
      }
}
