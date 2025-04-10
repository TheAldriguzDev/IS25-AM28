package it.polimi.ingsw.is25am28.GameModel.Session;

import java.util.*;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.TimeObserver.*;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.GameModel.FileLoader.TileLoader;
import it.polimi.ingsw.is25am28.State.*;
import it.polimi.ingsw.is25am28.State.InitialState.*;
import it.polimi.ingsw.is25am28.Ship.Ship;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Exceptions.*;

/**
 * class that implements the first part of the game, like construction of ships
 */
public final class ShipConstructionSession extends Session implements TimeSubscriber {
      private final static int FLIP_TIMES_LV2 = 1;
      private final static int TWO_MIN = 2*1000*60;
      private final static int SHIP_GRID_SIZE = 12;

      private final TimerObserver clock;
      private final SessionSubscriber controller;

      private final int level;

      private final Map<String,Player> players;
      private final List<EventCard> deck;


      private final Board board;

      // count the number of players that finished to build the ship
      private int waiting = 0;

      // all cards.
      // sent only during init. 
      // Are used client side only to render the components.
      // IMPORTANT
      // id is the index into this list.
      private final List<Component> all;
      // cards that are not available
      private List<Integer> selected = new ArrayList<>();
      // cards that are already flipped
      private HashSet<Integer> flipped = new HashSet<>();

      private int flippedTimes = 0;

      


      public ShipConstructionSession( 
            Board board,
            Map<String,Player> players, 
            int gameLevel, 
            SessionSubscriber controller,
            List<EventCard> deck
      ){
            this.deck = deck;
            this.controller = controller;
            this.board = board;

            all = TileLoader.get().read();
            Collections.shuffle(all);
            selected = new ArrayList<>();

            level = gameLevel;

            this.players = players;


            clock = new TimerObserver( TWO_MIN );

            clock.observe(this);
      }

      /**
       * used to listen for the flip of the clock. the method is sync on "clock"
       */
      public void onTimerEnd(){
            if( level != 2 )
                  return;

            synchronized(clock){

                  if( flippedTimes == FLIP_TIMES_LV2 ){
                        // force all players to go to the ENDED state. Watch Action.
                        // start the game
                        setHasFinished();
                        controller.onSessionEnd();
                        return;
                  }

                  flippedTimes++;
            }
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public synchronized FlipActionState select( String player, Integer i, Integer j ){

            Integer id = i * SHIP_GRID_SIZE + j;

            if( selected.contains(id) ){
                  throw new SelectedConcurrencyException(player);
            }

            // flip it if not already flipped
            if( !flipped.contains(id) )
                  flipped.add(id);

            selected.add(id);

            return 
                  new FlipActionState()
                  .setI(i)
                  .setJ(j)
                  .setPlayer( player )
                  .setSelected( true );
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public synchronized FlipActionState deselect( String player, Integer i, Integer j ){

            Integer id = i * SHIP_GRID_SIZE + j;


            if( !selected.contains(id) ){
                  throw new SelectedConcurrencyException(player);
            }

            selected.remove(id);

            return new FlipActionState()
            .setI(i)
            .setJ(j)
            .setPlayer( player )
            .setSelected( false );
      }

      /**
       * method used by the players to flip the clock and reduce times for other players
       */
      public Boolean flip( String player ) throws TimerFlipException {

            if( level != 2 ){
                  throw new TimerFlipException( player );
            }

            if( !clock.hasFinished() ){
                  throw new TimerFlipException( player );
            }

            //onTimerEnd();
            clock.flip();

            return true;
      }


      /**
       * executed whenever player ended construction of its ship.
       */
      public ShipConstructionSession setPlayerEnded( String playerNickname, List<ComponentJSON> shipProxy,  int discarded ){

            Player player = players.get( playerNickname );
            Ship ship = player.getShip();
            List<Component> ordered = all.stream().sorted((c1,c2) -> c1.getId() - c2.getId() ).toList();

            waiting++;


            for( int i = 0; i < shipProxy.size(); i++ ){
                  if( shipProxy.get(i) != null && shipProxy.get(i).getId() != null ){

                        Component component = ordered.get(shipProxy.get(i).getId());

                        component.setRotation( shipProxy.get(i).getRotation() );

                        ship.addComponent( 
                              component, 
                              i%ship.getGridCols(), 
                              (int)(i/ship.getGridCols()) 
                        );
                  }
            }
            
            player.addLostPieces( discarded );
            board.addPlayerToBoard(player);

            if( waiting == players.size() ){
                  setHasFinished();
            }

            return this;
      }

      /**
       * create a state to send that contains all the components onto the board,
       * represented as their simpleClassName.
       */
      public InitialState init(){

            if( level == 2 )
                  clock.flip();

            return new ShipConstructionInitialState()
                  .setAllTilesFromComponentList(all)
                  .setDeckFromEventCards( deck );
      }

      /**
       * method used only for debug purpose
       */
      public ShipConstructionSession setTimeoutTo1000ms(){
            clock.setTimeout(1000 );
            return this;
      } 
}