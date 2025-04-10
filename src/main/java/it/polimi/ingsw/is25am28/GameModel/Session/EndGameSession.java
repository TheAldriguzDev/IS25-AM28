package it.polimi.ingsw.is25am28.GameModel.Session;

import java.util.*;

import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.State.InitialState.EndGameState;

public final class EndGameSession extends Session {
      private final Board board;
      private final Map<String,Player> players;

      public EndGameSession( Board board, Map<String,Player> players ){
            super();

            this.players = players;
            this.board = board;
      }

      /**
       * add credits to each player, based on the
       * number of rewards obtained at the end of the game
       */
      public EndGameState init(){
            List<Player> players = board.getPlayers();

            // add credits based on position
            for( int i = 0; i < players.size(); i++ ){
                  players.get(i).addCredits( 4 - i );
            }



            HashSet<Player> withTheBestShip = new HashSet<>();
            EndGameState state = new EndGameState();
            int min = 0; // used only if no player win

            if( players.size() > 0 ){
                  min = players.get(0).getShip().getExposedConnectorAmount();
                  withTheBestShip.add(players.get(0));

            }

            for( int i = 0; i < players.size(); i++ ){
                  Player player = players.get(i);
                  int curr = player.getShip().getExposedConnectorAmount();

                  if( curr < min ){
                        withTheBestShip.clear();
                        withTheBestShip.add(player);
                        curr = min;
                  }else if( curr == min ){
                        withTheBestShip.add(player);
                  }
            }

            // add 2 credits to all the players with the best ship
            withTheBestShip.forEach(player -> player.addCredits(2));

            players = this.players.values().stream().toList();

            // add credits for storage
            players.forEach( player -> {
                  int value = player.getShip().getAllItemValue();
                  player.addCredits( player.isEliminated() ? (int)(value + 1)/2 : value );
            });

            Map<String,Integer> credits = new HashMap<>();
            Map<String,Integer> position = new HashMap<>();

            this.players.forEach( (k,player) -> {
                  
                  credits.put( k, player.getCredits() );

                  if( board.getEliminatedPlayers().contains(player) ){
                        position.put( k, -1 );
                  }else{
                        position.put(k, board.getPlayers().indexOf(player) + 1 );
                  }
            });

            setHasFinished();

            state.setCredits(credits);
            state.setPosition(position);

            return state;
      }
}
