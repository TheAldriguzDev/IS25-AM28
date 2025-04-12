package it.polimi.ingsw.is25am28.Model.State;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlipActionState {
      @JsonProperty("player")
      private String player;

      @JsonProperty("i")
      private Integer i;

      @JsonProperty("j")
      private Integer j;

      @JsonProperty("selected")
      private Boolean selected;

      public FlipActionState setI(Integer i) {
            this.i = i;
            return this;
      }
      public Integer getI() {
            return i;
      }
      public FlipActionState setJ(Integer j) {
            this.j = j;
            return this;
      }
      public Integer getJ() {
            return j;
      }
      public FlipActionState setPlayer(String player) {
            this.player = player;
            return this;
      }
      public String getPlayer() {
            return player;
      }

      /**
       * indicates wether the component was selected or deselected
       */
      public FlipActionState setSelected(Boolean selected) {
            this.selected = selected;
            return this;
      }

      public Boolean getSelected(){
            return selected;
      }      
}