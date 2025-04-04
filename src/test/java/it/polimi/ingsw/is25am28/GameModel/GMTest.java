package it.polimi.ingsw.is25am28.GameModel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Components.Structural;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Ship.Ship;


public abstract class GMTest {
      protected Board initBoard( HashMap<String,Player> players ){
            players = new HashMap<>();
            BoardLevel2 board = new BoardLevel2();

            players.put( "A", new Player("A", PlayerColor.BLUE, 2) );
            players.put( "B", new Player("B", PlayerColor.RED, 2) );

            board.newPlayer(players.get("A"));
            board.newPlayer(players.get("B"));

            board.buildBoard();

            return board;
      }

      @SuppressWarnings("all")
      protected ActionJSON jsonGenerator( Class Type, String player ) 
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
           
            ActionJSON json = null;
            json = (ActionJSON)(Type.getConstructor(null)).newInstance();
            json.setPlayerNickname(player);
            

            return json;
      }

      protected List<ComponentJSON> shipInit(){
            List<ComponentJSON> ship = new ArrayList<>();

            for( int i = 0; i < 12; i++ ){
                  for( int j = 0; j < 12; j++ ){
                        ship.add( null );
                  }
            }

            return ship;
      }

      protected Ship setPlayerShipWrong( Player player ){

            Ship ship = player.getShip();
            List<Integer> connectors = new ArrayList<>();

            for( int i = 0; i < 4; i++ ){
                  connectors.add(3);
            }

            ship.addComponent( new Structural(connectors), 5, 5);
            ship.addComponent( new Structural(connectors), 6, 5);
            connectors.set(1, 0);
            ship.addComponent( new Structural(connectors), 6, 4);

            return ship;
      }

      protected Ship setPlayerShip( Player player ){

            Ship ship = player.getShip();
            List<Integer> connectors = new ArrayList<>();

            for( int i = 0; i < 4; i++ ){
                  connectors.add(3);
            }

            //ship.addComponent( new Structural(connectors), 6, 5);
            ship.addComponent( new Cabin(connectors, false), 6, 7 );

            return ship;
      }
}
