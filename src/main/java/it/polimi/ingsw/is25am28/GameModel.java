package it.polimi.ingsw.is25am28;

import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.EventCards.EventCard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameModel {

      // CONSTANTS 
      static private final int DECOY_DECK_SIZE = 2;
      static private final int NUM_OF_DECOY_DECKS = 3;
      static private final int DECK_SIZE = 8;


      private final HashSet<Player> players;
      private final List<EventCard> deck;
      private final Board board;
      // indicate the round number and the card to draw from the deck
      private int round = 0;

      public GameModel( int level ){
            players = new HashSet<>();
            deck = generateDeck( level );
            board = new BoardLevel2(); // We need to have different constructor for different types of games
      }

      /**
       * @return the deck used to play the actual game
       * for level 2 game, the length of the list is 8;
       */
      private List<EventCard> generateDeck(int level ) {
            ArrayList<EventCard> deck = new ArrayList<>();
            int size = NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE + DECK_SIZE;

            for( int i = 0; i < size; i++ ){
                  // TODO implement
                  deck.add(new EventCard());
            }

            // random sort
            deck.sort((_,_) -> (int)( (Math.random() - Math.random())*1000 ) );

            return deck;
      }

      public GameModel newPlayer( Player player ) {

            players.add(player);
            return this;
      }

      public GameModel removePlayer( Player player ){
            players.remove(player);
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
      public List<Player> checkAllShips(){

            List<Player> toFix = new ArrayList<>();

            for( Player player : players ) {
                  if( !player.getShip().check() )
                        toFix.add(player);
            }

            return toFix;
      }

      public GameModel endGame(){

            List<Player> sorted = new ArrayList<Player>(players);

            sorted.sort((p1,p2)-> p1.getCursor() - p2.getCursor() );


            // add credits based on position
            for( int i = 0; i < sorted.size(); i++ ){
                  sorted.get(i).addCredits( 4 - i );
            }

            throw new Error("Andrea fix!!!");

            //return this;
      }

      /**
       * 
       * @return next event card. If the cards ended, null is returned instead
       */
      public EventCard nextRound(){

            round++;

            if( round == DECK_SIZE )
                  return null;

            return deck.get( NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE + round );
      } 
}
