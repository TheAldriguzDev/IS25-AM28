package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Controller.*;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.State.ShipConstructionInitialState;

import java.util.*;

/**
 * Class used to show how controller workflow works.
 * for simplicity all messages will be represented as strings
 * THIS CLASS IS AN ABSTRACTION, MEANT <B>ONLY TO SHOW WORKFLOW</B>
 */
public class ExampleNetwork {

      private final PlayerQueueController queueController;
      private final GameController gameController;

      public ExampleNetwork(){
            queueController = new PlayerQueueController();
            gameController = new GameController();
      }

      private void sendInitialState( ShipConstructionInitialState state ){

      }

      private void sendConfigRequest(){
            // to add connection handling

            Map<String,Object> messageToSend = new HashMap<>();

            messageToSend.put("availableColors", queueController.getAvailableColors() );
            messageToSend.put("player", queueController.getNextPlayerToContact().get() );
            messageToSend.put( "isLeader", queueController.isWaitingForNewLeader() );

      }

      public void onConnectionOpened( Map<String,String> message ){
            //...message conversion or stuffs

            if( queueController.connectPlayer( message.get("nickname") ) ){
                  sendConfigRequest();
            }
      }

      public void onConfigArrived( Map<String,Integer> message  ){
            Optional<ShipConstructionInitialState> state = queueController.addPlayerToGame( 
                  PlayerColor.fromInteger(message.get("color")), 
                  message.get("numOfPlayers"), 
                  message.get("lvl")
            );

            if( state.isPresent() ){
                  gameController.registerNewGame( queueController.getCurrentGame() );
                  sendInitialState( state.get() );
            }

            if( queueController.isWaitingForSomeone() ){
                  sendConfigRequest();
            }
      }

      // ... handle messages that regards game itself (ex. select, deselect ...)
}