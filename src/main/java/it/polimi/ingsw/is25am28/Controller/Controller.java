package it.polimi.ingsw.is25am28.Controller;

public class Controller {
      /*
      static private int id = 0;

      // register that hold all the games available
      private final Map<Integer,GameModel> register = new HashMap<>();
      private final Map<String,Integer> players = new HashMap<>();
      private final Sender network;
      private final ArrayList<String> waitingQueue = new ArrayList<>();

      private int num = 0;
      private int currentId;
      private ArrayList<Integer> colors;



      public Controller( Sender network ){
            this.network = network;
            initializeColors();
      }

      private Controller initializeColors(){
            colors = new ArrayList<>();

            for( int i = 0; i < 4; i++ ){
                  colors.add(i);
            }

            return this;
      }
      /**
       * if true, the leader is choosing the 
       * configuration of the game session
       *
      private Boolean isWaitingForSomeone(){
            return waitingQueue.size() > 0;
      }

      private GameModel createNewGameModel( int numOfPlayers, int lvl ){

            if( numOfPlayers <= 1 || numOfPlayers > 4 ){
                  throw new IndexOutOfBoundsException();
            } 

            GameModel model = new GameModel( network, lvl );

            currentId = id++;
            register.put( currentId, model );
            num = numOfPlayers;

            return model;
      }

      /**
       * initial configuration of the player. If the player is the first,
       * it also requires data about number of players and level of the game
       * @param nickname
       * @return
       *
      private Controller sendRequest( String nickname ){
            NetworkState state = new NetworkState();

            // if the current player is the leader
            if( num == 0 ){
                  state.setIsLeader( true );
            }

            state.setColors( colors );
            
            network.sendTo( nickname, state );

            return this;
      }

      private synchronized Controller useResponse( Object json ){

            NetworkJSON res = (NetworkJSON)json;

            if( num == 0 ){
                  createNewGameModel( 
                        res.getNumOfPlayers(), 
                        res.getLevel() 
                  );
            }

            GameModel model = register.get( currentId );
            String nickname = waitingQueue.remove(0);

            model.addNewPlayer( 
                  nickname, 
                  PlayerColor.fromInteger(res.getColor()) 
            );

            colors.remove(res.getColor());

            players.put( nickname, currentId );

            num--;

            if( num == 0 ){
                  network.sendToAll(
                        model.getPlayersNickname(),
                        model.start()
                  );
            }

            if( isWaitingForSomeone() ){
                  sendRequest(waitingQueue.getFirst());
            }

            return this;
      }

      private synchronized Controller connectPlayer( String nickname ){

            if( isWaitingForSomeone() ){
                  waitingQueue.add( nickname );
                  return this;
            }

            waitingQueue.add( nickname );
            
            return sendRequest(nickname);
      }

      private Controller play( String nickname, Object response ){
            // game model is already sync, it doesn't need to add anything
            Integer id = players.get( nickname );
            GameModel model = register.get(id);
            Object state;
            try{
                  // state = model.play( response );

                  if( state == null ){
                        // session ended
                        model.getPlayersNickname().forEach( player -> players.remove(player) );
                        register.remove(id);
                        network.closeConnections( model.getPlayersNickname() );
      
                        return this;
                  }

                  network.sendToAll( model.getPlayersNickname(), state );

            }catch( Error e ){
                  // discard the catch-ed error and notify the client
                  state = new State()
                  .setError(true)
                  .setMessage(e.getMessage());
            }

            return this;
      }

      public Controller onMessageArrived( Object request ){
            NetworkJSON json = (NetworkJSON)request;

            if( json.getAction() == NetworkJSON.Action.CONNECT ){
                  connectPlayer( json.getNickname() );
            }else if( json.getAction() == NetworkJSON.Action.CONFIG ){
                  useResponse(json);
            }else if( json.getAction() == NetworkJSON.Action.PLAY ){
                  play( json.getNickname(), json.getResponse() );
            }

            return this;
      }     

      public Controller setPlayerConnectionStatus( String nickname, boolean status ){
            Integer id = players.get( nickname );
            GameModel model = register.get(id);

            model.setPlayerConnectionStatus(nickname, status);

            return this;
      }
      */
}