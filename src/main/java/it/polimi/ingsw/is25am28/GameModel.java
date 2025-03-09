package it.polimi.ingsw.is25am28;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
      private final List<Player> players;

      public GameModel(){
            players = new ArrayList<>();
      }

      public GameModel newPlayer( Player player ) {
            return this;
      }

      public List<EventCard> generateDeck() {
            return new ArrayList<>();
      }

      public GameModel startGame(){
            return this;
      }

      public GameModel endGame(){
            return this;
      }

      public GameModel nextRound(){
            return this;
      }
}
