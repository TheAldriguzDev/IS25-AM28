package it.polimi.ingsw.is25am28.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.GameModel.FileLoader.CardLoader;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;

public class Deck {

      private final List<EventCard> deck;
      private final ResourceBank resourceBank;

      private final int level; // The level range can be 0 - 1 - 2 - 3
      private final Board board;

      private final Random random = new Random();

      public Deck( ResourceBank bank, Board board, int level ){
            this.board = board;
            this.resourceBank = bank;
            this.level = level;
            this.deck = new ArrayList<>();

            createDeck();
      }

      private void createDeck(){
            // If the level is equal to 0, then we have loaded all the cards for the test flight
            // Otherwise we need to get the right amount of card for the selected flight level
            List<EventCard> tempDeck = CardLoader.get().read(this.board, this.resourceBank, this.level);

            List<EventCard> levelOneDeck = new ArrayList<>(tempDeck.stream().filter(c -> c.getCardLevel() == 1).toList());
            List<EventCard> levelTwoDeck = new ArrayList<>(tempDeck.stream().filter( c -> c.getCardLevel() == 2).toList());
            List<EventCard> levelThreeDeck = new ArrayList<>(tempDeck.stream().filter( c -> c.getCardLevel() == 3).toList());

            switch (this.level) {
                  case 0:
                        this.deck.addAll(tempDeck);
                        break;
                  case 1:
                        // For the level 1 we have a deck made of 4 sub-decks that contains two lvl one cards
                        for (int i = 0; i < 4; i++) {
                              this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                              this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                        }
                        break;
                  case 2:
                        // For the level 2 we have a deck made of 4 sub-decks that contains two lvl two cards and one lvl card
                        for (int i = 0; i < 4; i++) {
                              this.deck.add(levelTwoDeck.remove(random.nextInt(levelTwoDeck.size())));
                              this.deck.add(levelTwoDeck.remove(random.nextInt(levelTwoDeck.size())));
                              this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                        }
                        break;
                  case 3:
                        // For the level 3 we have a deck made of 4 sub-decks that contains two lvl three, a lvl two and a lvl one card
                        for (int i = 0; i < 4; i++) {
                              this.deck.add(levelThreeDeck.remove(random.nextInt(levelThreeDeck.size())));
                              this.deck.add(levelThreeDeck.remove(random.nextInt(levelThreeDeck.size())));
                              this.deck.add(levelTwoDeck.remove(random.nextInt(levelTwoDeck.size())));
                              this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                        }
                        break;
                  default:
                        throw new IllegalStateException("The given game level (" + this.level + ") is not valid");
            }
      }

      private  Integer getPileSize(){
            return this.deck.size()/4;
      }

      public List<EventCard> getPreviewDeck(){
            return deck.subList( 0, getPileSize() );
      }

      public List<EventCard> getPlayableDeck(){
            return deck.subList( getPileSize(), deck.size() );
      }
}
