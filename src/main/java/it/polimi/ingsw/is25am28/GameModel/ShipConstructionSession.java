package it.polimi.ingsw.is25am28.GameModel;

import java.util.Optional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.TimeObserver.TimeSubscriber;
import it.polimi.ingsw.is25am28.TimeObserver.TimerObserver;
import it.polimi.ingsw.is25am28.Components.Component;

/**
 * class that implements the first part of the game, like construction of ships
 */
public class ShipConstructionSession implements TimeSubscriber {
      private final static int FLIP_TIMES_LV2 = 1;
      // cards that are currently available
      private List<Component> available;
      // cards that are not available
      private List<Integer> selected;
      // cards that are already flipped
      private HashSet<Integer> flipped;

      private final Optional<TimerObserver> clock;
      private int flippedTimes = 0;


      public ShipConstructionSession( List<Player> players, int gameLevel ){
            available = new FileLoader("./json/tiles.json").getAllComponents();
            available.sort((a,b) -> (int)( (Math.random() - Math.random())*1000 ) );
            selected = new ArrayList<>();

            if( gameLevel == 2 ){
                  clock = Optional.of(new TimerObserver());
                  clock.get().observe(this);
            }else{
                  clock = Optional.empty();
            }
      } 

      /**
       * create a state to send that contains all the components onto the board, 
       * represented as their simpleClassName.
       */
      public synchronized JSONArray generateInitialBoardState(){

            JSONArray state = new JSONArray();

            for( Component comp : available ){
                  state.add(comp.getClass().getSimpleName());
            }

            return state;
      }

      /**
       * sent every time one player choose to use one component or free one component
       * @return
       */
      public synchronized JSONObject generateState(){

            JSONObject state = new JSONObject();
            JSONArray selected = new JSONArray();
            JSONArray flipped = new JSONArray();

            for( Integer sel : this.selected ){
                  selected.add(sel);
            }

            for( Integer flip : this.flipped ){
                  flipped.add(flip);
            }

            state.put("flipped", flipped );
            state.put("selected", selected );

            return state;
      }

      /**
       * used to listen for the flip of the clock. the method is sync on "clock"
       * @private
       * @hidden
       */
      public void onTimerEnd(){
            if( !clock.isPresent() )
                  throw new Error("component not selected");

            synchronized(clock){
            
                  if( flippedTimes == FLIP_TIMES_LV2 ){
                        // start the game
                        return;
                  }

                  flippedTimes++;
                  clock.get().flip();
            }
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public synchronized ShipConstructionSession select( Integer id ){

            if( selected.contains(id) )
                  throw new Error("component already selected");
      
            // flip it if not already flipped
            if( !flipped.contains(id) )
                  flipped.add(id);

            selected.add(id);

            return this;
      }

      /**
       * the id is the position in the array, also sent to client
       */
      public synchronized ShipConstructionSession deselect( Integer id ){
            
            if( !selected.contains(id) )
                  throw new Error("component not selected");

            selected.remove(id);

            return this;
      }

      /**
       * method used by the players to flip the clock and reduce times for other players
       */
      public ShipConstructionSession flip(){

            onTimerEnd();

            return this;
      }
}
