package it.polimi.ingsw.is25am28.GameModel;


import java.util.*;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.State.*;
import it.polimi.ingsw.is25am28.TimeObserver.TimeEndedNotifier;
import it.polimi.ingsw.is25am28.Player.*;
import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.*;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Exceptions.*;
import it.polimi.ingsw.is25am28.GameModel.FileLoader.CardLoader;
import it.polimi.ingsw.is25am28.GameModel.Session.*;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;

public class GameModel implements SessionSubscriber {

      // CONSTANTS
      static private final int DECOY_DECK_SIZE = 2;

      private final List<EventCard> deck;
      private final ResourceBank resourceBank;
      private final Map<String,Player> players = new HashMap<>();
      private final TimeEndedNotifier notifier;

      private int level;
      private Board board;

      private ControlSession control;
      private RoundSession roundHandler;
      private ShipConstructionSession construction;

      public GameModel( TimeEndedNotifier notifier ){

            this.resourceBank = new ResourceBank();
            this.notifier = notifier;
            deck = new ArrayList<>();
      }

      private void isInShipConstructionSession(){
            if( construction.hasFinished() )
                  throw new IllegalSessionStateException();
      }

      private void isInControlSession(){
            if( !construction.hasFinished() || control.hasFinished() )
                  throw new IllegalSessionStateException();
            
      }

      private void isInRoundSession(){
            if( !construction.hasFinished() || !control.hasFinished() )
                  throw new IllegalSessionStateException();
      }

      /**
       * @return the deck used to play the actual game
       * for level 2 game, the length of the list is 8;
       */
      private List<EventCard> generateDeck( int level ) {

            List<EventCard> deck = CardLoader.get().read( board, this.resourceBank, level );

            Collections.shuffle(deck);
            // random sort
            deck.sort((_,_) -> (int)( (Math.random() - Math.random())*1000 ) );

            return deck;
      }


      public void onSessionEnd(){
            notifier.sendTimeEndedNotification( getPlayersNickname() );
      }

      public ShipConstructionInitialState start(){
            deck.addAll( generateDeck( level ) );

            roundHandler = new RoundSession( 
                  board,
                  level, 
                  deck.subList( 3 * DECOY_DECK_SIZE, deck.size() )
            );

            control = new ControlSession( players );

            construction = new ShipConstructionSession( 
                  board,
                  players, 
                  level, 
                  this,
                  deck.subList(0, 3 * DECOY_DECK_SIZE )
            );

            return construction.init();
      }
      
      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState selectTile( String player, Integer i, Integer j ){

            isInShipConstructionSession();

            return construction.select( player, i, j );
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public FlipActionState deselectTile( String player, Integer i, Integer j ){

            isInShipConstructionSession();

            return construction.deselect( player, i, j );
      }

      /**
       * method used by the players to flip the clock and reduce times for other players
       */
      public Boolean flipTimer( String player ) throws TimerFlipException {

            isInShipConstructionSession();

            return construction.flip( player );
      }

      /**
       * executed whenever player ended construction of its ship.
       */
      public GameModel setPlayerEndedBuilding( String playerNickname, List<ComponentJSON> shipProxy, int discarded ){

            isInShipConstructionSession();

            construction.setPlayerEnded( playerNickname, shipProxy,  discarded );

            return this;
      }

      /**
       * fix broken ship
       */
      public Boolean fixShip( String nickname, List<ComponentHelper<Integer>> ship ){

            isInControlSession();

            return control.fixShip( nickname, ship );
      }

      /**
       * populate a ship with lifeforms
       */
      public GameModel populateShip( String nickname, List<ComponentHelper<LifeformType>> ship ){

            isInControlSession();

            control.populateShip( nickname, ship );

            return this;
      }

      /**
       * method used to play a card
       */
      public CardStateJSON playCard( ActionJSON action ){
            
            isInRoundSession();

            return roundHandler.playCard(action);
      }

      /**
       * add credits to each player, based on the
       * number of rewards obtained at the end of the game
       */
      public Map<String,Map<String,Integer>> endGameRewards(){
            List<Player> players = board.getPlayers();

            // add credits based on position
            for( int i = 0; i < players.size(); i++ ){
                  players.get(i).addCredits( 4 - i );
            }



            HashSet<Player> withTheBestShip = new HashSet<>();
            Map<String,Map<String,Integer>> map = new HashMap<>();
            int min = 0; // used only if no player win

            if( players.size() > 0 ){
                  min = players.get(0).getShip().getExposedConnectorAmount();
                  withTheBestShip.add(players.get(0));

            }

            for( int i = 0; i < players.size(); i++ ){
                  Player player = players.get(i);
                  int curr = player.getShip().getExposedConnectorAmount();

                  if( curr < min ){
                        withTheBestShip.clear();
                        withTheBestShip.add(player);
                        curr = min;
                  }else if( curr == min ){
                        withTheBestShip.add(player);
                  }
            }

            // add 2 credits to all the players with the best ship
            withTheBestShip.forEach(player -> player.addCredits(2));

            players = this.players.values().stream().toList();

            // add credits for storage
            players.forEach( player -> {
                  int value = player.getShip().getAllItemValue();
                  player.addCredits( player.isEliminated() ? (int)(value + 1)/2 : value );
            });

            this.players.forEach( (k,player) -> {
                  Map<String,Integer> descriptor = new HashMap<>();
                  
                  descriptor.put("credits", player.getCredits() );

                  if( board.getEliminatedPlayers().contains(player) ){
                        descriptor.put("position", -1 );
                  }else{
                        descriptor.put("position", board.getPlayers().indexOf(player) + 1 );
                  }
                  
                  map.put( k, descriptor );
            });

            return map;
      }

      public boolean hasControlSessionEnded(){
            return control.hasFinished();
      }

      public boolean hasRoundSessionEnded(){
            return roundHandler.hasFinished();
      }

      public boolean hasShipConstructionSessionEnded(){
            return construction.hasFinished();
      }

      /**
       * initialize the control state. Must be called at the end of construction session
       */
      public List<String> initControlSession(){

            isInControlSession();
            
            return control.init();
      }
      /**
       * initialize the round. Must be called at the end of control session
       */
      public FirstRoundState initRoundSession(){

            isInRoundSession();

            return roundHandler.init();
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

      /**
       * debug only
       */
      public GameModel setDeck( List<EventCard> deck ){
            roundHandler.setDeck(deck);
            return this;
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
}