package it.polimi.ingsw.is25am28.Model.State;

import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;

public class FirstRoundState {
      @JsonProperty("card")
      private CardStateJSON card;

      @JsonProperty("board")
      private BoardJSON board;

      @JsonProperty("ships")
      private Map<String,List<Map<String,Object>>> ships;

      public Map<String, List<Map<String, Object>>> getShips() {
            return ships;
      }

      public FirstRoundState setShips(Map<String, List<Map<String, Object>>> ships) {
            this.ships = ships;
            return this;
      }

      public BoardJSON getBoard() {
            return board;
      }

      public FirstRoundState setBoard(BoardJSON board) {
            this.board = board;
            return this;
      }

      public CardStateJSON getCard() {
            return card;
      }

      public FirstRoundState setCard(CardStateJSON card) {
            this.card = card;
            return this;
      }


}
