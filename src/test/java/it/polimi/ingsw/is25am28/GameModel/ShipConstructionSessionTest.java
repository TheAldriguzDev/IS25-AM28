package it.polimi.ingsw.is25am28.GameModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Exceptions.TimerFlipException;
import it.polimi.ingsw.is25am28.GameModel.Session.SessionSubscriber;
import it.polimi.ingsw.is25am28.GameModel.Session.ShipConstructionSession;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.State.FlipActionState;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.Components.Cabin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class ShipConstructionSessionTest extends GMTest {
      private ShipConstructionSession s;
      private HashMap<String,Player> players;

      private class Listener implements SessionSubscriber {
            private long t;
            public Listener(){
                  t = ZonedDateTime.now().toInstant().toEpochMilli();
            }

            public void onSessionEnd(){
                  long t = ZonedDateTime.now().toInstant().toEpochMilli();
                  
                  assertFalse( t - this.t < 2000 );
            }
      }

      @BeforeEach
      void init(){
            players = new HashMap<>();
            BoardLevel2 board = new BoardLevel2();

            players.put( "A", new Player("A", PlayerColor.BLUE, 2) );
            players.put( "B", new Player("B", PlayerColor.RED, 2) );

            board.newPlayer(players.get("A"));
            board.newPlayer(players.get("B"));

            s = new ShipConstructionSession(
                  board,
                  players, 
                  2, 
                  new Listener(),
                  new ArrayList<>()
            );

            s.setTimeoutTo1000ms();

            s.init();
      }

      @Test
      public void test_multiple_selections_deselection() {
            
            FlipActionState state = s.select("A", 1, 0 );

            assertEquals( 1, state.getI() );
            assertEquals( 0, state.getJ() );
            assertEquals( "A", state.getPlayer() );
            assertEquals( true, state.getSelected() );

            assertThrows( SelectedConcurrencyException.class,
                  ()->{
                        s.select("B", 1, 0 );
                  } 
            );

            assertThrows( SelectedConcurrencyException.class,
                  ()->{
                        s.select("A", 1, 0 );
                  } 
            );

            state = s.deselect("A", 1, 0 );

            assertEquals( 1, state.getI() );
            assertEquals( 0, state.getJ() );
            assertEquals( "A", state.getPlayer() );
            assertEquals( false, state.getSelected() );

            assertThrows( SelectedConcurrencyException.class,
                  ()->{
                        s.deselect("A", 1, 0 );
                  } 
            );

            assertThrows( SelectedConcurrencyException.class,
                  ()->{
                        s.deselect("B", 1, 0 );
                  } 
            );

            state = s.select("B", 1, 0 );

            assertEquals( 1, state.getI() );
            assertEquals( 0, state.getJ() );
            assertEquals( "B", state.getPlayer() );
            assertEquals( true, state.getSelected() );
      }

      @Test
      public void multiple_flip_before_clock_end()
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

            assertThrows( TimerFlipException.class, () -> s.flip("A") );

            try{
                  // 300 to prevent thread errors
                  TimeUnit.MILLISECONDS.sleep( 300 + 1000  );
            }catch(InterruptedException _ ){

            }

            assertTrue(s.flip("A"));
            assertTrue(!s.hasFinished());

            try{
                  // 300 to prevent thread errors
                  TimeUnit.MILLISECONDS.sleep( 300 + 1000  );
            }catch(InterruptedException _ ){

            }

            assertTrue(s.hasFinished());
      }

      @Test
      public void create_ship_correctly()
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            List<ComponentJSON> shipA = shipInit();

            shipA.add(6*12 + 5, new ComponentJSON().setId(0) );
            shipA.add(6*12 + 7, new ComponentJSON().setId(1).setRotation(2) );
            s.setPlayerEnded( "A", shipA, 2 );

            assertTrue(!s.hasFinished());
            assertEquals( -2, players.get("A").getCredits() );

            for( int i = 0; i < 12; i++ ){
                  for( int j = 0; j < 12; j++ ){
                        if( i == 5 && j == 6 ){
                              int x = i;
                              int y = j;

                              assertTrue( players.get("A").getShip().getComponent(i, j) != null );
                              assertEquals(0, players.get("A").getShip().getComponent(x, y).getTypeId());
                              assertEquals(0, players.get("A").getShip().getComponent(x, y).getId());
                        }else if( i == 6 && j == 6 ){
                              int x = i;
                              int y = j;

                              assertTrue( players.get("A").getShip().getComponent(i, j) != null );
                              assertEquals(1, players.get("A").getShip().getComponent(x, y).getTypeId());
                              assertTrue(((Cabin)players.get("A").getShip().getComponent(x, y)).isCore());
                        }else if( i == 7 && j == 6 ){
                              int x = i;
                              int y = j;
                              // if present, it already has correct orientation
                              assertTrue( players.get("A").getShip().getComponent(x, y) != null );
                              assertEquals(0, players.get("A").getShip().getComponent(x, y).getTypeId());
                              assertEquals(1, players.get("A").getShip().getComponent(x, y).getId());

                        }else{
                              assertTrue( players.get("A").getShip().getComponent(i, j) == null );
                        }
                  }
            }
      }


      @Test
      public void end_on_all_players_ended()
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            List<ComponentJSON> ship = shipInit();

            s.setPlayerEnded( "A", ship, 2 );

            assertTrue(!s.hasFinished());

            s.setPlayerEnded( "B", ship, 2 );

            assertTrue(s.hasFinished());
      }
}
