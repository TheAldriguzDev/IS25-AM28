package it.polimi.ingsw.is25am28.Exceptions;

public class TimerFLipException extends TargetError {
      public TimerFLipException( String nickname ){
            super( nickname, "error in timer flip attempt");
      }
}
