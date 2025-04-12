package it.polimi.ingsw.is25am28.Model.State;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;


public class State {
      public enum GameState {

            CREATION, 
            CHECK, 
            GAME;
        
            @JsonValue
            public int getValue(){
                  return this.ordinal();
            }

            /**
             * ######### IMPORTANT NOTE ############
             * must be overwritten if the declaration changes. 
             * generally, DON'T change declaration order.
             * @param ordinal
             * @return
             */
            @JsonCreator
            public static GameState fromOrdinal( int ordinal ){
                  if( ordinal == CREATION.ordinal() ){
                        return CREATION;
                  }else if( ordinal == CHECK.ordinal() ){
                        return CHECK;
                  }else if( ordinal == GAME.ordinal() ){
                        return GAME;
                  }

                  throw new Error("[GameState.fromOrdinal] invalid ordinal value: " + ordinal );
            }
        
      }
        
      /**
       * if the action that happened was successful.
       * SCENARIO:
       * 
       * - A and B are playing
       * - A and B select together the same tile
       * - A was first
       * - B doesn't found the tile
       * - response to A action will be the new state of the board, player = 'A' and successful = true, needUpdate = true
       * - response to B action will be the new state of the board, player = 'B' and successful = false, needUpdate = false
       */
      @JsonProperty("successful")
      private Boolean successful = false;
      /**
       * whether the game state changes
       */
      @JsonProperty("needUpdate")
      private Boolean needUpdate = false;

      @JsonProperty("player")
      private String player;

      @JsonProperty("all")
      private List<Map<String,Object>> all;

      @JsonProperty("selected")
      private List<Integer> selected;

      @JsonProperty("flipped")
      private List<Integer> flipped;

      @JsonProperty("resetTimer")
      private Boolean resetTimer = false;

      @JsonProperty("timerEnded")
      private Boolean timerEnded = false;

      @JsonProperty("fixNeeded")
      private boolean fixNeeded = false;
      /**
       * decoy deck only
       */
      @JsonProperty("deck")
      private List<CardStateJSON> deck;
      /**
       * players that need to fix their ship
       */
      @JsonProperty("playersThatNeedFix")
      private Set<String> playersThatNeedFix;
      /**
       * board representation
       */
      @JsonProperty("board")
      private BoardJSON board;
      /**
       * the card state
       */
      @JsonProperty("card")
      private CardStateJSON card;
      /**
       * the key is the nickname of the player, 
       * while the Object is the state of the ship
       */
      @JsonProperty("ships")
      private Map<String,Object> ships;
      // flag used if an error happens during execution.
      // used to notify client
      @JsonProperty("isError")
      private boolean error = false;

      @JsonProperty("message")
      private String message = "";

      private GameState gameState;



      public State(){

      }

      public Boolean getSuccessful() {
            return successful;
      }
      
      public State setSuccessful(Boolean successful) {
            this.successful = successful;
            return this;
      }
      
      public Boolean getNeedUpdate() {
            return needUpdate;
      }
      
      public State setNeedUpdate(Boolean needUpdate) {
            this.needUpdate = needUpdate;
            return this;
      }
      
      public String getPlayer() {
            return player;
      }
      
      public State setPlayer(String player) {
            this.player = player;
            return this;
      }
      
      public List<Map<String,Object>> getAll() {
            return all;
      }
      
      public State setAll(List<Map<String,Object>> all) {
            this.all = all;
            return this;
      }

      public List<Integer> getSelected() {
            return selected;
      }
      
      public State setSelected(List<Integer> selected) {
            this.selected = selected;
            return this;
      }


      public State setAllFromComponentList(List<Component> list ){
            all = list.stream().map( c -> c.toMap() ).toList();
            return this;
      }
      
      public List<Integer> getFlipped() {
            return flipped;
      }
      
      public State setFlipped(List<Integer> flipped) {
            this.flipped = flipped;
            return this;
      }

      public Boolean getTimerEnded() {
            return timerEnded;
      }
      
      public State setTimerEnded(Boolean timerEnded) {
            this.timerEnded = timerEnded;
            return this;
      }
      
      public Boolean getResetTimer() {
            return resetTimer;
      }
      
      public State setResetTimer(Boolean resetTimer) {
            this.resetTimer = resetTimer;
            return this;
      }

      public Boolean getFixNeeded() {
            return fixNeeded;
      }
      
      public State setFixNeeded(Boolean fixNeeded) {
            this.fixNeeded = fixNeeded;
            return this;
      }

      public List<CardStateJSON> getDeck() {
            return deck;
      }

      /**
       * decoy deck only
       */
      public State setDeck(List<CardStateJSON> deck) {
            this.deck = deck;
            return this;
      } 

      public State setDeckFromEventCards(List<EventCard> deck){
            this.deck = deck.stream().map( card -> card.generateState() ).toList();
            return this;
      }

      public State setPlayersThatNeedFix( Set<String> playersThatNeedFix ){
            // cloning the set to prevent ref exceptions
            this.playersThatNeedFix = new HashSet<>(playersThatNeedFix);
            return this;
      }     

      public Set<String> getPlayersThatNeedFix(){
            return this.playersThatNeedFix;
      }     

      public State setBoard( BoardJSON board ){
            // cloning the set to prevent ref exceptions
            this.board = board;
            return this;
      }     

      public BoardJSON getBoard(){
            return board;
      }    

      public State setCard( CardStateJSON card ){
            // cloning the set to prevent ref exceptions
            this.card = card;
            return this;
      }     

      public CardStateJSON getCard(){
            return card;
      }    

      /**
       * the key is the nickname of the player, 
       * while the Object is the state of the ship
       */
      public State setShips( Map<String,Object> ships ){
            this.ships = ships;
            return this;
      }     

      /**
       * the key is the nickname of the player, 
       * while the Object is the state of the ship
       */
      public Map<String,Object> getShips(){
            return ships;
      }    

      public String getMessage() {
            return message;
      }

      public State setMessage(String message) {
            this.message = message;
            return this;
      }

      public State setError(boolean error) {
            this.error = error;
            return this;
      }

      public boolean getError() {
            return error;
      }

      public GameState getGameState() {
            return gameState;
      }

      public State setGameState(GameState gameState) {
            this.gameState = gameState;
            return this;
      }
      
      @Override
      public String toString(){
            return "resetTimer: " + resetTimer 
                  + ",timerEnded: " + timerEnded 
                  + ",flipped: " + flipped 
                  + ",selected: " + selected
                  + ",successful: " + successful
                  + ",player: " + player;
      }
}
