package it.polimi.ingsw.is25am28.GameModel.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.Exceptions.*;


public class ControlSession extends Session {
      
      private final Map<String,Player> players;
      private final HashSet<String> toFix = new HashSet<>();
      private final HashSet<String> finished = new HashSet<>();

      public ControlSession( Map<String,Player> players ){
            this.players = players;
      }

      public List<String> init(){

            for( Player player : players.values() ) {

                  if( !player.getShip().validateShip() ){
                        toFix.add( player.getNickname() );
                  }
            }

            return toFix.stream().toList();
      }

      /**
       * return true if the fix is successful
       */
      public Boolean fixShip( String nickname, List<ComponentJSON> shipProxy ){

            if( !toFix.contains( nickname ) ){
                  throw new FixNotRequiredError(nickname);
            }

            Player player = players.get(nickname);
            Ship ship = player.getShip();


            for( int i = 0; i < shipProxy.size(); i++ ){
                  int x = (int)(i/ship.getGridCols());
                  int y = i%ship.getGridCols();

                  if( 
                        ( shipProxy.get(i) == null || shipProxy.get(i).getId() == null ) && 
                        ( 
                              ship.getComponent( x, y ) != null &&
                              ship.getCore() != ship.getComponent( x, y )
                        ) ){

                        ship.removeSingleComponent( x, y );
                        player.addLostPieces(1);
                  }
            }

            if( ship.validateShip() ){
                  toFix.remove( player.getNickname() );
                  return true;
            }
            
            return false;
      }

      /**
       * used to add lifeforms to ship.
       * return the state of the ship
       */
      public List<Map<String, Object>> populateShip( String nickname, List<ComponentJSON> shipProxy ){

            if(  toFix.contains( nickname ) ){
                  throw new UncompletedShipException( nickname );
            }
            

            Player player = players.get(nickname);
            Ship ship = player.getShip();

            try{

                  for( int i = 0; i < shipProxy.size(); i++ ){
                        int x = (int)(i/ship.getGridCols());
                        int y = i%ship.getGridCols();
      
                        if( shipProxy.get(i) != null && shipProxy.get(i).getLifeforms() != null ){
                              ship.addLifeformToCabin( x, y, shipProxy.get(i).getLifeforms() );
                        }                  
                  }
            }catch(Error e){
                  // prevent waiting increment
                  throw new ShipPopulationFailException(nickname);
            }catch(TooManyAliensException e){
                  throw new ShipPopulationFailException(nickname);
            }catch(IllegalArgumentException e){
                  throw new ShipPopulationFailException(nickname);
            }catch(OutOfGridException e){
                  throw new ShipPopulationFailException(nickname);
            }catch(OutOfShipException e){
                  throw new ShipPopulationFailException(nickname);
            }
            
            finished.add(nickname);

            if( finished.size() == players.size() ){
                  setHasFinished();
            }

            return ship.generateState();
      }
}
