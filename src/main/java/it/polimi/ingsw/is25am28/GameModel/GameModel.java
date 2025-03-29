package it.polimi.ingsw.is25am28.GameModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polimi.ingsw.is25am28.GameModel.FileLoader.CardLoader;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import org.json.simple.JSONArray;

import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.GameModel.Session.RoundSession;
import it.polimi.ingsw.is25am28.GameModel.Session.SessionSubscriber;
import it.polimi.ingsw.is25am28.GameModel.Session.ShipConstructionSession;

public class GameModel {

      // CONSTANTS
      static private final int DECOY_DECK_SIZE = 2;
      static private final int NUM_OF_DECOY_DECKS = 3;


      //private final HashSet<Player> players;

      private final List<EventCard> deck;
      private final Board board;
      private final ResourceBank resourceBank;

      private final int level;

      // indicate the round number and the card to draw from the deck
      //private int round = 0;
      private ShipConstructionSession builder;
      private RoundSession round;

      public GameModel( int level ){
            this.level = level;
            deck = generateDeck( level );
            // if( level > 1 )
            board = new BoardLevel2();
            this.resourceBank = new ResourceBank();
      }

      /**
       * @return the deck used to play the actual game
       * for level 2 game, the length of the list is 8;
       */
      private List<EventCard> generateDeck( int level ) {

            if( deck != null )
                  return deck;

            List<EventCard> deck = CardLoader.get().read(this.board, this.resourceBank);

            // random sort
            deck.sort((_,_) -> (int)( (Math.random() - Math.random())*1000 ) );

            return deck;
      }

      public GameModel newPlayer( Player player ) {
            board.addPlayerToBoard(player);
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

            players.addAll(board.getEliminatedPlayers());

            // add credits for storage
            players.forEach( player -> {
                  int value = player.getShip().getAllItemValue();
                  player.addCredits( player.isEliminated() ? (int)(value + 1)/2 : value );
            });

            return this;
      }

      public GameModel startRoundSession(){
            round = new RoundSession( level, deck.subList( NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE, deck.size() ) );
            return this;
      }

      public Object useCurrentCard( Object response ){
            Object cardState = round.play( response );

            //TODO wrap card state into global state

            return cardState;
      }

      public Board getBoard(){
            return board;
      }

      public JSONArray startBuildSession( SessionSubscriber controller ){

            builder = new ShipConstructionSession( board.getPlayers(), level, controller );
            builder.flip();

            return builder.generateInitialBoardState();
      }

      public GameModel selectComponent( int id ){
            builder.select(id);
            return this;
      }

      public GameModel deselectComponent( int id ){
            builder.deselect(id);
            return this;
      }

      public GameModel flip(){
            builder.flip();
            return this;
      }
}