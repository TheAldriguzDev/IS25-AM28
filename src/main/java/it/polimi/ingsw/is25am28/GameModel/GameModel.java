package it.polimi.ingsw.is25am28.GameModel;


import java.util.*;

import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.State.*;
import it.polimi.ingsw.is25am28.State.InitialState.InitialState;
import it.polimi.ingsw.is25am28.TimeObserver.TimeEndedNotifier;
import it.polimi.ingsw.is25am28.Player.*;
import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.*;
import it.polimi.ingsw.is25am28.Deck.Deck;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Exceptions.*;
import it.polimi.ingsw.is25am28.GameModel.Session.ControlSession;
import it.polimi.ingsw.is25am28.GameModel.Session.EndGameSession;
import it.polimi.ingsw.is25am28.GameModel.Session.RoundSession;
import it.polimi.ingsw.is25am28.GameModel.Session.Session;
import it.polimi.ingsw.is25am28.GameModel.Session.ShipConstructionSession;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.GameModel.Session.SessionSubscriber;

public class GameModel implements SessionSubscriber {


      private final ResourceBank resourceBank;
      private final Map<String,Player> players = new HashMap<>();
      private final TimeEndedNotifier notifier;

      private int level;
      private Board board;
      private Session session;
      private Deck deck;




      public GameModel( TimeEndedNotifier notifier ){

            this.resourceBank = new ResourceBank();
            this.notifier = notifier;
      }


      public void onSessionEnd(){
            notifier.sendTimeEndedNotification( getPlayersNickname() );
      }

      public InitialState start(){

            deck = new Deck(
                  resourceBank, 
                  board, 
                  level
            );

            RoundSession r = new RoundSession( 
                  board,
                  level, 
                  deck.getPlayableDeck()
            );

            ControlSession c = new ControlSession( players );

            ShipConstructionSession s = new ShipConstructionSession( 
                  board,
                  players, 
                  level, 
                  this,
                  deck.getPreviewDeck()
            );

            EndGameSession e = new EndGameSession(board, players);

            s.setNextState( c );
            c.setNextState( r );
            r.setNextState( e );

            session = s;
            
            return s.init();
      }

      public InitialState start( List<EventCard> mockDeck ){

            deck = new Deck(
                  resourceBank, 
                  board, 
                  level
            );

            RoundSession r = new RoundSession( 
                  board,
                  level, 
                  mockDeck
            );

            ControlSession c = new ControlSession( players );

            ShipConstructionSession s = new ShipConstructionSession( 
                  board,
                  players, 
                  level, 
                  this,
                  deck.getPreviewDeck()
            );

            EndGameSession e = new EndGameSession(board, players);

            s.setNextState( c );
            c.setNextState( r );
            r.setNextState( e );

            session = s;
            
            return s.init();
      }

      /**
       * return available colors
       */
      public List<PlayerColor> getAvailableColors(){

            List<PlayerColor> available = new ArrayList<>();
            List<PlayerColor> used = players
            .values()
            .stream()
            .map( player -> player.getPlayerColor() )
            .toList();

            for( int i = 0; i < 4; i++ ){
                  available.add(PlayerColor.fromInteger(i));
            }

            available.removeIf( color -> used.contains(color) );

            return available;
      }

      /**
       * returns the list of nickname of *all* participant players. 
       * To prefer over getPlayers because it doesn't expose refs.
       */
      public List<String> getPlayersNickname(){
            return players.keySet().stream().toList();
      }

      /**
       * to avoid wether possible. If you are tempted to use this method, 
       * check if other methods exists, like GameModel.getPlayersNickname(),
       * GameModel.getAvailableNickname(), GameModel.setPlayerConnectionStatus()
       */
      public List<Player> getPlayers(){
            return players.values().stream().toList();
      }

      public GameModel addNewPlayer( String nickname, PlayerColor color ){
            Player player = new Player(nickname, color, level);
            
            players.put(
                  nickname, 
                  player
            );

            board.newPlayer(player);

            return this;
      }

      public GameModel setPlayerConnectionStatus( String nickname, boolean status ){
            
            players.get( nickname ).setConnected( status );

            return this;
      }

      /**
       * used for retro-compatibility. If possible, delete.
       */
      public Board getBoard(){
            return board;
      }

      public GameModel setLevel( int level ){
            this.level = level;

            if( level == 2 ){
                  board = new BoardLevel2();
                  board.buildBoard();
            }else{
                  throw new Error("board level 1 must be implemented");
            }

            return this;
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState select( String player, Integer i, Integer j ){
            return session.select(player, i, j);
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState deselect( String player, Integer i, Integer j ){
            return session.deselect(player, i, j);
      }

      /**
       * method used by the players to flip the clock and reduce times for other players
       */
      public Boolean flip( String player ) throws TimerFlipException {
            return session.flip(player);
      }

      /**
       * executed whenever player ended construction of its ship.
       */
      public Session setPlayerEnded( String playerNickname, List<ComponentJSON> shipProxy, int discarded ){
            return session.setPlayerEnded(playerNickname, shipProxy, discarded);
      }

      /**
       * fix broken ship
       */
      public Boolean fixShip( String nickname, List<ComponentHelper<Integer>> ship ){
            return session.fixShip(nickname, ship);
      }

      /**
       * populate a ship with lifeforms
       */
      public Session populateShip( String nickname, List<ComponentHelper<LifeformType>> ship ){
            return session.populateShip(nickname, ship);
      }

      /**
       * method used to play a card
       */
      public CardStateJSON playCard( ActionJSON action ){
            return session.playCard(action);
      }

      public boolean canGoToNextState(){
            return session.hasFinished();
      }

      public Object goToNextState(){
            session = session.getNextState();

            return session.init();
      }

}