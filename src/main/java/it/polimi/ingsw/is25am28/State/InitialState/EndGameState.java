package it.polimi.ingsw.is25am28.State.InitialState;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class EndGameState extends InitialState {
      @JsonProperty("position")
      private Map<String,Integer> position;

      @JsonProperty("credits")
      private Map<String,Integer> credits;

      public Map<String, Integer> getCredits() {
            return credits;
      }

      public void setCredits(Map<String, Integer> credits) {
            this.credits = credits;
      }

      public Map<String, Integer> getPosition() {
            return position;
      }

      public void setPosition(Map<String, Integer> position) {
            this.position = position;
      }

}
