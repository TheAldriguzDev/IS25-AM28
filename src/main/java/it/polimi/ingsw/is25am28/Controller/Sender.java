package it.polimi.ingsw.is25am28.Controller;

import java.util.List;

public interface Sender {
      // used to send broadcast
      //void send(Object state);  
      // used to send to a specific player
      void sendTo( String nickname, Object state );   
      // used to send broadcast
      void sendToAll( List<String> players, Object state ); 
      void closeConnections( List<String> players );  
}
