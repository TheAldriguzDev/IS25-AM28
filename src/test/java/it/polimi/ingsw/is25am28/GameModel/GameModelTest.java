package it.polimi.ingsw.is25am28.GameModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.is25am28.Controller.Sender;
import it.polimi.ingsw.is25am28.Exceptions.IllegalSessionStateException;
import it.polimi.ingsw.is25am28.Player.PlayerColor;

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

            g.start();

            assertThrows( IllegalSessionStateException.class, ()->{
                  g.playCard(null);
            });
      }
}
