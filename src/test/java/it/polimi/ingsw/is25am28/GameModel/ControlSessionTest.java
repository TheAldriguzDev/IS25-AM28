package it.polimi.ingsw.is25am28.GameModel;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Exceptions.ShipPopulationFailException;
import it.polimi.ingsw.is25am28.Exceptions.UncompletedShipException;
import it.polimi.ingsw.is25am28.GameModel.Session.ControlSession;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;


public class ControlSessionTest extends GMTest {
      
      private ControlSession c;
      private HashMap<String,Player> players = new HashMap<>();

      @Nested
      class TestOneWrong{
            @BeforeEach
            void init(){
                  players.put( "A", new Player("A", PlayerColor.BLUE, 2) );
                  players.put( "B", new Player("B", PlayerColor.RED, 2) );
      
                  // set to A a wrong ship
                  setPlayerShipWrong( players.get("A") );
                  setPlayerShip( players.get("B") );
      
      
                  c = new ControlSession( players );
                  List<String> p = c.init();
                  
                  assertEquals(1, p.size());
                  assertTrue(p.contains("A"));

            }
      
            /**
             * expected that initial state signals to 
             * correct player that their ship need fix
             */
            @Test
            void correct_fix_required(){
                  assertThrows( FixNotRequiredError.class, () -> c.fixShip( "B", null ) );
                  assertDoesNotThrow(() -> c.fixShip( "A", basicFix() ));
            }
      
            /**
             * expected that initial state signals to 
             * correct player that they need to complete the ship before continuing
             */
            @Test
            void correct_population_required(){
                  assertThrows( UncompletedShipException.class, () -> c.populateShip( "A", null ) );
                  assertDoesNotThrow(() -> c.populateShip( "B", new ArrayList<>() ));
            }
      
      
            @Test
            void correct_population_added(){
                  List<ComponentHelper<LifeformType>> ship = new ArrayList<>();
                  ship.add(new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT));
      
                  c.populateShip( "B", ship );
      
                  players.get("B")
                  .getShip()
                  .getCabinList()
                  .stream()
                  .forEach( cabin -> {
                        assertTrue( cabin.getInhabitants().size() == 2 );
                  });
                  
            }
      
            @Test
            void wrong_alien_throws_but_can_be_reset_and_finish_correctly(){
                  List<ComponentHelper<LifeformType>> ship = new ArrayList<>();
                  ship.add( new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.PURPLE_ALIEN) );
      
                  assertThrows( ShipPopulationFailException.class, () -> c.populateShip( "B", ship ) );
      
                  c.fixShip( "A", basicFix() );

                  c.populateShip( "A", new ArrayList<>() );
                  assertTrue(!c.hasFinished());

                  ship.add( new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT) );

                  ship.removeFirst();
      
                  c.populateShip( "B", ship );
      
                  players.get("B")
                  .getShip()
                  .getCabinList()
                  .stream()
                  .forEach( cabin -> {
                        assertTrue( cabin.getInhabitants().size() == 2 );
                  });
      
                  assertTrue(c.hasFinished());
            }
      
            @Test
            void non_populated_ship_fallback(){
      
                  c.populateShip( "B", new ArrayList<>() );
      
                  players.get("B")
                  .getShip()
                  .getCabinList()
                  .stream()
                  .forEach( cabin -> {
                        assertTrue( cabin.getInhabitants().size() == 2 );
                  });
            }
      
            @Test
            void wait_for_all_to_finish(){
                  List<ComponentHelper<LifeformType>> ship = new ArrayList<>();
                  ship.add( new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.PURPLE_ALIEN) );
      
                  c.populateShip( "B", ship );
      
                  c.fixShip( "A", basicFix() );
                  assertTrue(!c.hasFinished());
      
                  ship.add( new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT) );
                  ship.removeFirst();

                  c.populateShip( "B", ship );
      
                  assertTrue(!c.hasFinished());
      
                  c.populateShip( "A", new ArrayList<>() );
      
                  assertTrue(c.hasFinished());
            }
      }

      @Nested
      class TestTwoCorrect{
            @BeforeEach
            void init(){
                  players.put( "A", new Player("A", PlayerColor.BLUE, 2) );
                  players.put( "B", new Player("B", PlayerColor.RED, 2) );
      
                  // set to A a wrong ship
                  setPlayerShip( players.get("A") );
                  setPlayerShip( players.get("B") );
      
      
                  c = new ControlSession( players );
                  List<String> p = c.init();

                  assertEquals(0, p.size());
            }
      
            /**
             * expected that initial state signals to 
             * correct player that their ship need fix
             */
            @Test
            void no_fix_required(){
                  assertThrows( FixNotRequiredError.class, () -> c.fixShip( "B", null ) );
                  assertThrows( FixNotRequiredError.class, () -> c.fixShip( "A", null ) );
            }
      
            @Test
            void wait_for_all_to_finish(){
                  List<ComponentHelper<LifeformType>> ship = new ArrayList<>();
                  assertTrue(!c.hasFinished());
                  
                  c.populateShip( "B", ship );

                  assertTrue(!c.hasFinished());
      
                  c.populateShip( "B", ship );
      
                  assertTrue(!c.hasFinished());
      
                  c.populateShip( "A", ship );
      
                  assertTrue(c.hasFinished());
            }
      }
      
}
