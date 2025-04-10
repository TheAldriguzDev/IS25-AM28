package it.polimi.ingsw.is25am28.GameModel.Session;

import java.util.*;

import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Exceptions.IllegalSessionStateException;
import it.polimi.ingsw.is25am28.Exceptions.TimerFlipException;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.State.FlipActionState;
import it.polimi.ingsw.is25am28.State.InitialState.InitialState;

/**
 * class that implements the first part of the game, like construction of ships
 */
public abstract sealed class Session permits RoundSession, ControlSession, EndGameSession, ShipConstructionSession {

      private boolean hasFinished = false;
      private Session nextState;

      Session(){
            Arrays.asList(
                  this
                  .getClass()
                  .getMethods()
            )
            .stream()
            .forEach( method -> {
                  method.isAnnotationPresent(Override.class );
            });

      }

      protected final void setHasFinished(){
            hasFinished = true;
      }

      public final Session setNextState( Session session ){

            nextState = session;

            return this;
      }

      public final Boolean hasFinished(){
            return hasFinished;
      }

      public abstract InitialState init();

      /**
       * default stays onto the same state
       */
      public Session getNextState(){
            return nextState;
      }

      
      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState select( String player, Integer i, Integer j ){
            throw new IllegalSessionStateException();
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState deselect( String player, Integer i, Integer j ){
            throw new IllegalSessionStateException();
      }

      /**
       * method used by the players to flip the clock and reduce times for other players
       */
      public Boolean flip( String player ) throws TimerFlipException {
            throw new IllegalSessionStateException();
      }

      /**
       * executed whenever player ended construction of its ship.
       */
      public Session setPlayerEnded( String playerNickname, List<ComponentJSON> shipProxy, int discarded ){
            throw new IllegalSessionStateException();
      }

      /**
       * fix broken ship
       */
      public Boolean fixShip( String nickname, List<ComponentHelper<Integer>> ship ){
            throw new IllegalSessionStateException();
      }

      /**
       * populate a ship with lifeforms
       */
      public Session populateShip( String nickname, List<ComponentHelper<LifeformType>> ship ){
            throw new IllegalSessionStateException();
      }

      /**
       * method used to play a card
       */
      public CardStateJSON playCard( ActionJSON action ){
            throw new IllegalSessionStateException();
      }
}
