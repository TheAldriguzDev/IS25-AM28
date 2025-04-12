package it.polimi.ingsw.is25am28.Model.State;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;

public class ShipConstructionInitialState {

      @JsonProperty("allTiles")
      private List<Map<String,Object>> allTiles;
      /**
       * decoy deck only
       */
      @JsonProperty("deck")
      private List<CardStateJSON> deck;

      
      public List<Map<String,Object>> getAllTiles() {
            return allTiles;
      }
      
      public ShipConstructionInitialState setAllTiles(List<Map<String,Object>> allTiles) {
            this.allTiles = allTiles;
            return this;
      }


      public ShipConstructionInitialState setAllTilesFromComponentList(List<Component> list ){
            allTiles = list.stream().map( c -> c.toMap() ).toList();
            return this;
      }

      public List<CardStateJSON> getDeck() {
            return deck;
      }

      /**
       * decoy deck only
       */
      public ShipConstructionInitialState setDeck(List<CardStateJSON> deck) {
            this.deck = deck;
            return this;
      } 

      public ShipConstructionInitialState setDeckFromEventCards(List<EventCard> deck){
            this.deck = deck.stream().map( card ->{
                  card.initCardPlayers();
                  return card.generateState();
            }).toList();
            return this;
      }
}
