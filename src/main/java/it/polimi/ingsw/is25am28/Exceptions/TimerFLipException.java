package it.polimi.ingsw.is25am28.Exceptions;

public class TimerFlipException extends TargetError {
      public TimerFlipException( String nickname ){
            super( nickname, "error in timer flip attempt");
      }
}
