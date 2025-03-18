package it.polimi.ingsw.is25am28.GameModel;


import it.polimi.ingsw.is25am28.Ship.Ship;
import it.polimi.ingsw.is25am28.TimeObserver.TimeSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.EventCards.EventCard;

import it.polimi.ingsw.is25am28.GameModel.ShipConstructionSession;

public class GameModel {

      // CONSTANTS
      static private final int DECOY_DECK_SIZE = 2;
      static private final int NUM_OF_DECOY_DECKS = 3;
      static private final int DECK_SIZE = 8;


      //private final HashSet<Player> players;

      private final List<EventCard> deck;
      private final Board board;

      private final List<Component> components = new ArrayList<>();
      private final int level;

      // indicate the round number and the card to draw from the deck
      private int round = 0;
      private ShipConstructionSession session;

      public GameModel( int level ){
            this.level = level;
            deck = generateDeck( level );
            // if( level > 1 )
            board = new BoardLevel2();
      }

      /**
       * @return the deck used to play the actual game
       * for level 2 game, the length of the list is 8;
       */
      private List<EventCard> generateDeck( int level ) {

            if( deck != null && round != DECK_SIZE  )
                  return deck;

            List<EventCard> deck = new FileLoader("./json/cards.json").getAllCards( board );
            // random sort
            deck.sort((a,b) -> (int)( (Math.random() - Math.random())*1000 ) );
            
            return deck;
      }

      public GameModel newPlayer( Player player ) {
            board.addPlayerToBoard(player);
            return this;
      }

      public GameModel removePlayer( Player player ){
            board.eliminatePlayer(player);
            return this;
      }

      /**
       * show the "decoy" deck used in ship-building phase.
       * the int parameter could be 0,1 or 2. if none of these values is passed,
       * an error is thrown
       * @return
       */
      public List<EventCard> showDeck( int deckIndex ) throws IndexOutOfBoundsException {

            if( deckIndex > NUM_OF_DECOY_DECKS - 1 || deckIndex < 0 )
                  throw new IndexOutOfBoundsException("deck index must be a value between 0 and 2 (inclusive).");

            return new ArrayList<>( deck.subList( deckIndex * DECOY_DECK_SIZE, deckIndex * DECOY_DECK_SIZE + DECOY_DECK_SIZE ) );
      }

      /**
       * @return the list of players that needs to fix their ships.
       * for later use, check if the size of the result is equal to 0,
       * then, all player's ships are ok.
       */
      public HashMap<Player, List<Component>> checkAllShips(){

            HashMap<Player, List<Component>> toFix = new HashMap<>();
            List<Player> players = board.getPlayers();

            for( Player player : players ) {

                  List<Component> wrongs = player.getShip().getWrongComponents();

                  if( wrongs.size() > 0 )
                        toFix.put( player, wrongs );
            }

            return toFix;
      }

      /**
       * add credits to each player, based on the
       * number of rewards obtained at the end of the game
       */
      public GameModel endGameRewards(){

            List<Player> players = board
            .getPlayers()
            .stream()
            .sorted((p1,p2) -> p1.getCursor() - p2.getCursor() )
            .toList();

            // add credits based on position
            for( int i = 0; i < players.size(); i++ ){
                  players.get(i).addCredits( 4 - i );
            }

            

            List<Player> withTheBestShip = new ArrayList<>();
            int min = Integer.MAX_VALUE;

            players.clear();
            players.addAll(board.getPlayers());

            for (Player player : players){

                  int curr = player.getShip().getExposedConnectors();

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

            players.addAll(board.getEliminatedPlayers());

            // add credits for storage 
            players.forEach( player -> {
                  int value = player.getShip().getAllItemValue();
                  player.addCredits( player.isEliminated() ? (int)(value + 1)/2 : value );
            });

            return this;
      }

      /**
       *
       * @return next event card. If the cards ended, null is returned instead
       */
      public EventCard nextRound(){

            round++;

            if( round == DECK_SIZE )
                  return null;

            EventCard card = deck.get( NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE + round );

            card.startUsingCard( new ArrayList<Player>(board.getPlayers()) );

            return card;
      }

      public GameModel updateState( Object response ){
            EventCard card = deck.get( NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE + round );

            if( card.hasFinished() ){
                  throw new Error("state updated when the card has finished to apply its effects to all players");
            }

            card.useCard( response );

            return this;
      }

      public boolean hasCurrentCardFinished(){
            EventCard card = deck.get( NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE + round );

            return card.hasFinished();
      }


      public Object useCurrentCard(){

            EventCard card = deck.get( NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE + round );

            if( card.hasFinished() ){
                  throw new Error("usage of card when the card has finished to apply its effects to all players");
            }

            return card.generateState();
      }

      public Board getBoard(){
            return board;
      }

      public JSONArray startBuildSession( SessionSubscriber controller ){

            session = new ShipConstructionSession( board.getPlayers(), level, controller );
            session.flip();

            return session.generateInitialBoardState();
      }

      public GameModel selectComponent( int id ){
            session.select(id);
            return this;
      }

      public GameModel deselectComponent( int id ){
            session.deselect(id);
            return this;
      }

      public GameModel flip(){
            session.flip();
            return this;
      }
}