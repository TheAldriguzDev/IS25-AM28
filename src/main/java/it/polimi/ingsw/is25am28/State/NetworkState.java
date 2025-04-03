package it.polimi.ingsw.is25am28.State;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkState {

      @JsonProperty("isLeader")
      private Boolean isLeader = false;

      @JsonProperty("colors")
      private List<Integer> colors;

      public List<Integer> getColors() {
            return colors;
      }

      public NetworkState setColors(List<Integer> colors) {
            this.colors = colors;
            return this;
      }

      public Boolean getIsLeader() {
            return isLeader;
      }

      public NetworkState setIsLeader(Boolean isLeader) {
            this.isLeader = isLeader;
            return this;
      }

      public String toString(){
            return " colors: " + colors +
                   ", isLeader: " + isLeader;
      }
}
