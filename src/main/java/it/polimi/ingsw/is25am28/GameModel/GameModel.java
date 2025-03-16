package it.polimi.ingsw.is25am28.GameModel;


import it.polimi.ingsw.is25am28.Ship.Ship;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import it.polimi.ingsw.is25am28.Components.Battery;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Cannon;
import it.polimi.ingsw.is25am28.Components.Shield;
import it.polimi.ingsw.is25am28.Components.Storage;
import it.polimi.ingsw.is25am28.Components.Structural;
import it.polimi.ingsw.is25am28.Components.Vital;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Components.Engine;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.EventCards.EventCard;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
      private final List<Component> components = new ArrayList<>();

      public GameModel( int level ){
            players = new HashSet<>();

            deck = generateDeck( level );
            // if( level > 1 )
            board = new BoardLevel2();
      }

      /**
       * @return the deck used to play the actual game
       * for level 2 game, the length of the list is 8;
       */
      private List<EventCard> generateDeck( int level ) {
            ArrayList<EventCard> deck = new ArrayList<>();
            int size = NUM_OF_DECOY_DECKS * DECOY_DECK_SIZE + DECK_SIZE;

            for( int i = 0; i < size; i++ ){
                  
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
      public HashMap<Player, List<Component>> checkAllShips(){

            HashMap<Player, List<Component>> toFix = new HashMap<>();

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

            List<Player> sorted = new ArrayList<Player>(players);


            sorted.sort((p1,p2) -> p1.hasLost() ? -1: p1.getCursor() - p2.getCursor() );

            // add credits based on position
            for( int i = 0; i < sorted.size() || sorted.get(i).hasLost(); i++ ){
                  sorted.get(i).addCredits( 4 - i );
            }

            int min = Integer.MAX_VALUE;

            sorted.clear();

            for (Player player : players){

                  if( player.hasLost() )
                        continue;

                  Ship ship = player.getShip();
                  List<Integer> connectors = new ArrayList<Integer>();
                  int curr;

                  connectors.add(0);

                  ship.traverse( component -> {
                        Component[] nearest = ship.getNearestComponents(component);

                        if( nearest[0] == null ){
                              connectors.add( component.getTopSideConnectors() );
                        }

                        if( nearest[1] == null ){
                              connectors.add( component.getRightSideConnectors() );
                        }

                        if( nearest[2] == null ){
                              connectors.add( component.getBottomSideConnectors() );
                        }

                        if( nearest[3] == null ){
                              connectors.add( component.getLeftSideConnectors() );
                        }
                  });

                  curr = connectors.stream().reduce( 0, (p,c) -> p + c );

                  if( curr < min ){
                        sorted.clear();
                        sorted.add(player);
                        curr = min;
                  }else if( curr == min ){
                        sorted.add(player);
                  }

            }

            players.forEach( player -> {
                  int value = player.getShip().getAllItemValue();
                  player.addCredits( player.hasLost() ? (int)(value + 1)/2 : value );
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

            card.startUsingCard( new ArrayList<Player>(players) );

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
      /**
       * return all components that can be used to build the ships
       */
      public List<Component> getAllComponents(){
            if( components.size() > 0 )
                  return components;

            components.addAll(new FileLoader("../json/tiles.json").getAllComponents());
            return components;
      }
}