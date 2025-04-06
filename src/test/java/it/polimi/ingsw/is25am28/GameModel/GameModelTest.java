package it.polimi.ingsw.is25am28.GameModel;

import static it.polimi.ingsw.is25am28.Connector.THREE_PIPES;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.ActionJSON.MeteorShowerJSON;
import it.polimi.ingsw.is25am28.ActionJSON.VisitPlanetsJSON;
import it.polimi.ingsw.is25am28.Components.Storage;
import it.polimi.ingsw.is25am28.Exceptions.IllegalSessionStateException;
import it.polimi.ingsw.is25am28.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Exceptions.ShipPopulationFailException;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.State.FlipActionState;
import it.polimi.ingsw.is25am28.TimeObserver.TimeEndedNotifier;

public class GameModelTest extends GMTest {

   

      class Stub implements TimeEndedNotifier {

            @Override
            public void sendTimeEndedNotification(List<String> players) {}
      }

      @Test
      void simulate_game_correctly(){
            GameModel g = new GameModel( new Stub() );

            g.setLevel(2 );

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


            g.initControlSession();

            List<ComponentHelper<LifeformType>> ship = new ArrayList<>();
            ship.add( new ComponentHelper<LifeformType>(6,5).addItem(LifeformType.PURPLE_ALIEN) );

            assertThrows( ShipPopulationFailException.class, () -> g.populateShip( "B", ship ) );

            assertTrue( g.fixShip( "A", basicFix() ) );

            g.populateShip( "A", new ArrayList<>() );
            assertTrue(!g.hasControlSessionEnded());

            ship.add( new ComponentHelper<LifeformType>(6,7).addItem(LifeformType.ASTRONAUT) );

            ship.removeFirst();

            g.populateShip( "B", ship );

            assertTrue(g.hasControlSessionEnded());
            
            CardStateJSON s;

            List<Integer> connectors = new ArrayList<>();

            for( int i = 0; i < 4; i++ ){
                  connectors.add(THREE_PIPES.ordinal());
            }

            g.getPlayers().getFirst().getShip().addComponent(new Storage( connectors, 3, true), 6, 5);
            g.initRoundSession();

            s = g.playCard( new MeteorShowerJSON("A", 0, 2, null, null) );


            assertEquals( "B", s.getPlayerNickname() );

            s = g.playCard(new MeteorShowerJSON("B", 0, 2, null, null));
            assertTrue(!g.hasRoundSessionEnded());

            assertEquals( "pianeti", s.getCardName() );

            List<ComponentHelper<ItemColor>> it = new ArrayList<>();

            for( int i = 0; i < 3; i++)
                  it.add( new ComponentHelper<ItemColor>( 6, 5 ).addItem(ItemColor.RED) );

            var json = new VisitPlanetsJSON(0, new ArrayList<>(), it  );

            json.setPlayerNickname("A");
            g.playCard( json );

            assertTrue(g.hasRoundSessionEnded());

            /*
             * A
             * - 18 connectors -> + 0
             * - 5 lost pieces -> -5
             * - red(4) storage (x3) -> + 12
             * - first -> + 4
             * TOTAL: 13
             */


             /*
             * B
             * - 12 connectors -> + 2
             * - 0 lost pieces -> - 0
             * - storage (x0) -> + 0
             * - last -> +3
             * 
             * TOTAL: 5
             */
            
            var res = g.endGameRewards();


            assertEquals( 13, res.get("A").get("credits"));
            assertEquals( 1, res.get("A").get("position"));

            assertEquals( 5, res.get("B").get("credits"));
            assertEquals( 2, res.get("B").get("position"));
      }

      @Test 
      void test_rewards_with_eliminated_players(){
            GameModel g = new GameModel( new Stub() );
            g.setLevel(2 );

            g.addNewPlayer("A", PlayerColor.BLUE );
            g.addNewPlayer("B", PlayerColor.YELLOW );

            var A = g.getPlayers().getFirst();
            var B = g.getPlayers().getLast();

            g.start();

            g.setPlayerEndedBuilding( "A", shipInit(), 0 );
            g.setPlayerEndedBuilding( "B", shipInit(), 0 );

            g.initControlSession();

            g.populateShip("A", new ArrayList<>());
            g.populateShip("B", new ArrayList<>());

            List<Integer> connectors = new ArrayList<>();

            for( int i = 0; i < 4; i++ ){
                  connectors.add(THREE_PIPES.ordinal());
            }

            setPlayerShip( A );
            setPlayerShip( B );
            B.getShip().addComponent(new Storage( connectors, 3, true), 6, 5);

            g.initRoundSession();


            B.getShip().getStorageList().getFirst().storeItem( new Item(ItemColor.RED) ); 
            B.getShip().getStorageList().getFirst().storeItem( new Item(ItemColor.RED) ); 

            g.getBoard().eliminatePlayer(B);

            A.addCredits(-5); // some lost pieces


            /*
             * A
             * - 9 connectors -> + 2
             * - 5 lost pieces -> - 5
             * - storage (x0) -> + 0
             * - first -> + 4
             * TOTAL: 1
             */

             /*
             * B
             * - 9 connectors -> + 0
             * - 0 lost pieces -> - 0
             * - red(4) storage (x2) but has lost -> + 4
             * - lost -> + 0
             * 
             * TOTAL: 6
             */

            var res = g.endGameRewards();

            assertEquals( 1, res.get("A").get("credits"));
            assertEquals( 1, res.get("A").get("position"));

            assertEquals( 4, res.get("B").get("credits"));
            assertEquals( -1, res.get("B").get("position"));
      }

      @Test 
      void test_rewards_with_players_with_same_exposed(){
            GameModel g = new GameModel( new Stub() );
            g.setLevel(2 );

            g.addNewPlayer("A", PlayerColor.BLUE );
            g.addNewPlayer("B", PlayerColor.YELLOW );

            var A = g.getPlayers().getFirst();
            var B = g.getPlayers().getLast();

            g.start();

            g.setPlayerEndedBuilding( "A", shipInit(), 0 );
            g.setPlayerEndedBuilding( "B", shipInit(), 0 );

            g.initControlSession();

            g.populateShip("A", new ArrayList<>());
            g.populateShip("B", new ArrayList<>());

            List<Integer> connectors = new ArrayList<>();

            for( int i = 0; i < 4; i++ ){
                  connectors.add(THREE_PIPES.ordinal());
            }

            connectors.set(3, 1);

            setPlayerShip( A );
            setPlayerShip( B );

            A.getShip().addComponent(new Storage( connectors, 3, true), 6, 5);
            B.getShip().addComponent(new Storage( connectors, 3, true), 6, 5);

            g.initRoundSession();


            B.getShip().getStorageList().getFirst().storeItem( new Item(ItemColor.RED) ); 
            B.getShip().getStorageList().getFirst().storeItem( new Item(ItemColor.RED) ); 


            A.addCredits(-5); // some lost pieces


            /*
             * A
             * - 9 connectors -> + 2
             * - 5 lost pieces -> - 5
             * - storage (x0) -> + 0
             * - first -> + 4
             * TOTAL: 1
             */

             /*
             * B
             * - 9 connectors -> + 2
             * - 0 lost pieces -> - 0
             * - red(4) storage (x2) -> + 8
             * - last -> + 3
             * 
             * TOTAL: 13
             */

            var res = g.endGameRewards();

            assertEquals( 1, res.get("A").get("credits"));
            assertEquals( 1, res.get("A").get("position"));

            assertEquals( 13, res.get("B").get("credits"));
            assertEquals( 2, res.get("B").get("position"));
      }
}
