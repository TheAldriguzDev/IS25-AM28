package it.polimi.ingsw.is25am28.Controller;

import java.util.*;

import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Exceptions.TimerFlipException;
import it.polimi.ingsw.is25am28.GameModel.GameModel;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.State.*;

public class GameController {
      static private int id = 0;

      // register that hold all the games available
      private final Map<Integer,GameModel> register = new HashMap<>();
      // associate each player to its game
      private final Map<String,Integer> players = new HashMap<>();



      public GameController(){
      }


      public synchronized GameController registerNewGame( GameModel game ){
            int currentId = id++;

            register.put( currentId, game );
            game.getPlayersNickname().forEach( player -> players.put( player, currentId ));


            return this;
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState selectTile( String player, Integer i, Integer j ){
            int id = players.get(player);
            GameModel model = register.get( id );
            
            synchronized( model ){
                  return model.select( player, i, j );
            }
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState deselectTile( String player, Integer i, Integer j ){
            int id = players.get(player);
            GameModel model = register.get( id );
            
            synchronized( model ){
                  return model.deselect( player, i, j );
            }
      }

      /**
       * method used by the players to flip the clock and reduce times for other players
       */
      public Boolean flipTimer( String player ) throws TimerFlipException {
            int id = players.get(player);
            GameModel model = register.get( id );
            
            synchronized( model ){
                  return model.flip( player );
            }
      }

      /**
       * executed whenever player ended construction of its ship.
       */
      public Optional<List<String>> setPlayerEndedBuilding( String player, List<ComponentJSON> shipProxy, int discarded ){
            int id = players.get(player);
            GameModel model = register.get( id );
            
            synchronized( model ){
                  
                  model.setPlayerEnded(player, shipProxy, discarded);

                  if( model.canGoToNextState() ){
                        return Optional.of( (List<String>)model.goToNextState() );
                  }

                  return Optional.empty();
            }
      }

      /**
       * fix broken ship
       */
      public Boolean fixShip( String nickname, List<ComponentHelper<Integer>> ship ){
            int id = players.get(nickname);
            GameModel model = register.get( id );
            
            synchronized( model ){
                  return model.fixShip(nickname, ship);
            }
      }

      /**
       * populate a ship with lifeforms
       */
      public Optional<FirstRoundState> populateShip( String nickname, List<ComponentHelper<LifeformType>> ship ){
            int id = players.get(nickname);
            GameModel model = register.get( id );
            
            synchronized( model ){
                  
                  model.populateShip(nickname, ship);

                  if( model.canGoToNextState() ){
                        return Optional.of( (FirstRoundState)model.goToNextState() );
                  }

                  return Optional.empty();
            }
      }

      /**
       * method used to play a card. if the optional is void must be called the end game reward
       */
      public Optional<CardStateJSON> playCard( ActionJSON action ){
            int id = players.get(action.getPlayerNickname());
            GameModel model = register.get( id );
            
            synchronized( model ){
                  
                  CardStateJSON state = model.playCard(action);

                  if( model.canGoToNextState() ){

                        return Optional.empty();
                  }

                  return Optional.of(state);
            }
      }

      /**
       * return the end game state, containing positions and credits.
       */
      public Map<String,Map<String,Integer>> endGameRewards( ActionJSON action ){
            int id = players.get(action.getPlayerNickname());
            GameModel model = register.get( id );
            
            synchronized( model ){

                  // do final cleanup 

                  for( String player : model.getPlayersNickname() ){
                        players.remove(player);
                  }

                  register.remove(id);

                  // send result
                  return (Map<String,Map<String,Integer>>)model.goToNextState();
            }
      }
}