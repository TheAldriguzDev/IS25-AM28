package it.polimi.ingsw.is25am28.GameModel.Session;


/**
 * class that implements the first part of the game, like construction of ships
 */
public abstract class Session {

      private boolean hasFinished = false;
      
      protected final void setHasFinished(){
            hasFinished = true;
      }

      public final Boolean hasFinished(){
            return hasFinished;
      }
}
