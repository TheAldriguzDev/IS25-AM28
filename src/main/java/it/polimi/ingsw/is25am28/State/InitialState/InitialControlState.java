package it.polimi.ingsw.is25am28.State.InitialState;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class InitialControlState extends InitialState {
      
      @JsonProperty("fix")
      private List<String> fix;

      public void setFix(List<String> fix) {
            this.fix = fix;
      }

      public List<String> getFix() {
            return fix;
      }
}
