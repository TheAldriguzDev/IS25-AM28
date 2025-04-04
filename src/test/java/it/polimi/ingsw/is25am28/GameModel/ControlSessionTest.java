package it.polimi.ingsw.is25am28.GameModel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Exceptions.ShipPopulationFailException;
import it.polimi.ingsw.is25am28.Exceptions.UncompletedShipException;
import it.polimi.ingsw.is25am28.GameModel.Session.ControlSession;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;


public class ControlSessionTest extends GMTest {
      
      private ControlSession c;
      private HashMap<String,Player> players = new HashMap<>();

      @BeforeEach
      void init(){
            players.put( "A", new Player("A", PlayerColor.BLUE, 2) );
            players.put( "B", new Player("B", PlayerColor.RED, 2) );

            // set to A a wrong ship
            setPlayerShipWrong( players.get("A") );
            setPlayerShip( players.get("B") );


            c = new ControlSession( players );
            c.init();
      }

      /**
       * expected that initial state signals to 
       * correct player that their ship need fix
       */
      @Test
      void correct_fix_required(){
            assertThrows( FixNotRequiredError.class, () -> c.fixShip( "B", null ) );
            assertDoesNotThrow(() -> c.fixShip( "A", shipInit() ));
      }

      /**
       * expected that initial state signals to 
       * correct player that they need to complete the ship before continuing
       */
      @Test
      void correct_population_required(){
            assertThrows( UncompletedShipException.class, () -> c.populateShip( "A", null ) );
            assertDoesNotThrow(() -> c.populateShip( "B", shipInit() ));
      }


      @Test
      void correct_population_added(){
            List<ComponentJSON> ship = shipInit();
            ship.set( 6*12 + 7, new ComponentJSON().setLifeforms(LifeformType.ASTRONAUT) );

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
            List<ComponentJSON> ship = shipInit();
            ship.set( 6*12 + 7, new ComponentJSON().setLifeforms(LifeformType.PURPLE_ALIEN) );

            assertThrows( ShipPopulationFailException.class, () -> c.populateShip( "B", ship ) );

            c.fixShip( "A", shipInit() );
            c.populateShip( "A", shipInit() );
            assertTrue(!c.hasFinished());

            ship.set( 6*12 + 7, new ComponentJSON().setLifeforms(LifeformType.ASTRONAUT) );
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

            c.populateShip( "B", shipInit() );

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
            List<ComponentJSON> ship = shipInit();
            ship.set( 6*12 + 7, new ComponentJSON().setLifeforms(LifeformType.PURPLE_ALIEN) );

            assertThrows( ShipPopulationFailException.class, () -> c.populateShip( "B", ship ) );

            c.fixShip( "A", shipInit() );
            assertTrue(!c.hasFinished());

            ship.set( 6*12 + 7, new ComponentJSON().setLifeforms(LifeformType.ASTRONAUT) );
            c.populateShip( "B", ship );

            assertTrue(!c.hasFinished());

            c.populateShip( "A", shipInit() );

            assertTrue(c.hasFinished());
      }
}
