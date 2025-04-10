package it.polimi.ingsw.is25am28.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.polimi.ingsw.is25am28.GameModel.GameModel;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.TimeObserver.TimeEndedNotifier;
import it.polimi.ingsw.is25am28.State.InitialState.*;

public class PlayerQueueController {
      private final List<String> waitingQueue = new ArrayList<>();
      private final TimeEndedNotifier notifier;

      /**
       * game that is being initialized
       */
      private GameModel currentGame;
      private int num = 0;


      public PlayerQueueController( TimeEndedNotifier notifier ){
            this.notifier = notifier;
      }     

      /**
       * used when the leader respond with the configuration of the game and 
       * with their color. Must be called before adding the leader
       */
      private PlayerQueueController configGame( int numOfPlayers, int lvl ){

            if( numOfPlayers <= 1 || numOfPlayers > 4 ){
                  throw new IndexOutOfBoundsException();
            } 

            currentGame.setLevel(lvl);

            num = numOfPlayers;

            return this;
      }

      /**
       * add one player to the game, configuring it if the player is the leader.
       * returns the initial state of the game if all the players are connected
       */
      public synchronized Optional<InitialState> addPlayerToGame( PlayerColor color, int numOfPlayers, int lvl ){
            String nickname = waitingQueue.getFirst();

            // the game model has not been initialized
            if( num == 0 ){
                  configGame( numOfPlayers, lvl );
            }

            currentGame.addNewPlayer( 
                  nickname, 
                  color
            );

            num--;

            if( num == 0 ){

                  return Optional.of( currentGame.start() );
            }

            return Optional.empty();
      }

      public synchronized Boolean isWaitingForSomeone(){
            return waitingQueue.size() > 1;
      }

      /**
       * return if the request of configuration must be sent instantly or the player is put into the queue  
       */
      public synchronized Boolean connectPlayer( String nickname ){

            waitingQueue.add( nickname );

            if( waitingQueue.size() > 1 ){
                  // must wait to send the request
                  return false;
            }

            // can send the request
            return true;
      }

      public synchronized Optional<String> getNextPlayerToContact(){

            
            if ( num == 0 ){
                  // no game is available right now
                  currentGame = new GameModel( notifier );
            }

            if( waitingQueue.size() > 0 )
                  return Optional.of(waitingQueue.getFirst());

            return Optional.empty();
      }

      public synchronized List<PlayerColor> getAvailableColors(){
            return currentGame.getAvailableColors();
      }

      /**
       * return true if the player that will send the configuration next must also initialize 
       * the new game. Method that must be called in network message creation
       */
      public synchronized Boolean isWaitingForNewLeader(){
            return num == 0;
      }

      public GameModel getCurrentGame(){
            return currentGame;
      }
}
