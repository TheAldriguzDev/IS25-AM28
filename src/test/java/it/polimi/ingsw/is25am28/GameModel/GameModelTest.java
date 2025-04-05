package it.polimi.ingsw.is25am28.GameModel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.ActionJSON.VisitPlanetsJSON;
import it.polimi.ingsw.is25am28.Controller.Sender;
import it.polimi.ingsw.is25am28.Exceptions.IllegalSessionStateException;
import it.polimi.ingsw.is25am28.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.State.FlipActionState;

public class GameModelTest extends GMTest {
      class Stub implements Sender {

            @Override
            public void sendTo(String nickname, Object state) {
            }

            @Override
            public void sendToAll(List<String> players, Object state) {
            }

            @Override
            public void closeConnections(List<String> players) {
            }

      }

      @Test
      void simulate_game_correctly(){
            GameModel g = new GameModel( new Stub(), 2 );

            g.addNewPlayer("A", PlayerColor.BLUE );

            assertEquals(3, g.getAvailableColors().size() );
            assertTrue( !g.getAvailableColors().contains(PlayerColor.BLUE) );

            g.addNewPlayer("B", PlayerColor.YELLOW );

            // test the deck init correctly
            g.start();

            assertThrows( IllegalSessionStateException.class, ()->{
                  g.playCard(null);
            });

            g.setDeck(initFakeDeck(g.getBoard()));



            // CONSTRUCTION


            FlipActionState state = g.selectTile("A", 1, 0 );

            assertEquals( 1, state.getI() );
            assertEquals( 0, state.getJ() );
            assertEquals( "A", state.getPlayer() );
            assertEquals( true, state.getSelected() );

            assertThrows( SelectedConcurrencyException.class,
                  ()->{
                        g.selectTile("B", 1, 0 );
                  } 
            );

            assertThrows( SelectedConcurrencyException.class,
                  ()->{
                        g.selectTile("A", 1, 0 );
                  } 
            );

            state = g.deselectTile("A", 1, 0 );

            assertEquals( 1, state.getI() );
            assertEquals( 0, state.getJ() );
            assertEquals( "A", state.getPlayer() );
            assertEquals( false, state.getSelected() );

            List<ComponentJSON> shipA = shipInit();

            shipA.add(6*12 + 5, new ComponentJSON().setId(0) );
            shipA.add(6*12 + 7, new ComponentJSON().setId(1).setRotation(2) );
            g.setPlayerEndedBuilding( "A", shipA, 2 );

            assertTrue(!g.hasShipConstructionSessionEnded());

             g.getPlayers().forEach( p -> { 
                  if( p.getNickname() == "A" )
                        assertEquals( -2, p.getCredits() );  
            });

            assertTrue(!g.hasShipConstructionSessionEnded());

            List<ComponentJSON> shipB = shipInit();

            shipB.add(6*12 + 5, new ComponentJSON().setId(0) );
            shipB.add(6*12 + 7, new ComponentJSON().setId(1).setRotation(2) );

            g.setPlayerEndedBuilding( "B", shipB, 0 );

            assertTrue(g.hasShipConstructionSessionEnded());

            // Set B ship as ok, and ship A as wrong
            setPlayerShipWrong( g.getPlayers().getFirst() );
            setPlayerShip( g.getPlayers().getLast() );


            System.out.println(g.initControlSession());

            List<ComponentJSON> ship = shipInit();
            ship.set( 6*12 + 7, new ComponentJSON().setLifeforms(LifeformType.PURPLE_ALIEN) );

            g.populateShip( "B", ship );

            g.fixShip( "A", shipInit() );
            assertTrue(!g.hasControlSessionEnded());

            ship.set( 6*12 + 7, new ComponentJSON().setLifeforms(LifeformType.ASTRONAUT) );
            g.populateShip( "B", ship );

            assertTrue(!g.hasControlSessionEnded());

            g.populateShip( "A", shipInit() );

            assertTrue(g.hasControlSessionEnded());
            
            CardStateJSON s;

            g.initRoundSession();

            s = g.playCard( new MeteorShowerJSON("A", 0, 2, null, null) );


            assertEquals( "B", s.getPlayerNickname() );

            s = g.playCard(new MeteorShowerJSON("B", 0, 2, null, null));
            assertTrue(!g.hasRoundSessionEnded());

            assertEquals( "pianeti", s.getCardName() );

            var json = new VisitPlanetsJSON(-1, null, null );

            json.setPlayerNickname("A");
            g.playCard( json );

            json.setPlayerNickname("B");
            g.playCard( json );
      
            assertTrue(g.hasRoundSessionEnded());
      }
}
