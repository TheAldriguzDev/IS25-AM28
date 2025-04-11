package it.polimi.ingsw.is25am28.GameModel.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentJSON;
import it.polimi.ingsw.is25am28.Exceptions.*;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;


public class ControlSession extends Session {
      
      private final Map<String,Player> players;
      private final HashSet<String> toFix = new HashSet<>();
      private final HashSet<String> finished = new HashSet<>();

      public ControlSession( Map<String,Player> players ){
            this.players = players;
      }

      public List<String> init() {

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
      public Boolean fixShip( String nickname, List<ComponentHelper<Integer>> toRemove ){

            if( !toFix.contains( nickname ) ){
                  throw new FixNotRequiredError(nickname);
            }

            Player player = players.get(nickname);
            Ship ship = player.getShip();


            for( int i = 0; i < toRemove.size(); i++ ){
                  ship.removeComponent( toRemove.get(i).getI(), toRemove.get(i).getJ() );
            }

            player.addLostPieces(toRemove.size());


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
      public ControlSession populateShip( String nickname, List<ComponentHelper<LifeformType>> shipProxy ){

            if( toFix.contains( nickname ) ){
                  throw new UncompletedShipException( nickname );
            }
            

            Player player = players.get(nickname);
            Ship ship = player.getShip();

            try{

                  for( int i = 0; i < shipProxy.size(); i++ ){
                        ship.addLifeformToCabin( 
                              shipProxy.get(i).getI(), 
                              shipProxy.get(i).getJ(), 
                              shipProxy.get(i).getItem().get() 
                        ); 
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

            return this;
      }
}
