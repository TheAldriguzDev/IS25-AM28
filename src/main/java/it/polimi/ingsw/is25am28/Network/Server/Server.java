package it.polimi.ingsw.is25am28.Network.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.ClientStatus;
import it.polimi.ingsw.is25am28.Network.RMI.Server.RMIServer;
import it.polimi.ingsw.is25am28.Network.Socket.Server.TCPServer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.util.*;
import java.util.stream.Collectors;


/**
 * This class initially spawns the networks server to listen for client requests.
 * Then it will be used to route the clients requests to the game they are playing.
 * */

public class Server {

    // Server constants
    public static final String RMIServerName = "GameRMIServer";
    public static final int RMIServerPort = 7777;
    public static final String TCPAddress = "127.0.0.1";
    public static final int TCPServerPort = 8888;

    private final RMIServer rmiServer;
    private final TCPServer tcpServer;


    // This map stores the id of the game with its instance
    private final Map<Integer, GameInstance> gameInstances;

    // This map stores the players nickname and their network connection
    private final Map<String, VirtualView> connectedClients;

    // This map stores the client nickname with the associated game
    private final Map<String, Integer> clientToGame;

    public Server() throws Exception {
        // Create the RMIServer and the TCPServer
        this.tcpServer = new TCPServer(Server.TCPAddress, Server.TCPServerPort, this);
        this.rmiServer = new RMIServer(Server.RMIServerName, Server.RMIServerPort, this);

        this.gameInstances = new HashMap<>();
        this.connectedClients = new HashMap<>();
        this.clientToGame = new HashMap<>();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
    }

    /**
     * The serve handles the client connection. First of all it retrieves all the games that has been configured.
     * Then it will update the client with this information in order to let him decide if he wants to:
     * 1. Join an active Game
     * 2. Create a new Game
     * 3. Reconnect to a Game (by sending the nickname)
     * */
    public void onClientConnection(VirtualView clientVirtualView) throws Exception {
        // Retrieve the list of available games
        Map<Integer, GameInstance> availableGames = this.searchForGames();

        // Retrieve the available games information
        List<GameInfoDTO> gameInfo = new ArrayList<>();
        for (Map.Entry<Integer, GameInstance> entry : availableGames.entrySet()) {
            gameInfo.add(
                    new GameInfoDTO()
                            .setId(entry.getKey())
                            .setLevel(entry.getValue().getLevel())
                            .setTotalPlayers(entry.getValue().getTotalPlayers())
                            .setAvailableColors(entry.getValue().getAvailableColors())
                            .setActualPlayers(entry.getValue().getCurrentPlayers())
            );
        }

        // Create the state with the available games information to the client
        StateDTO state = new AvailableGamesDTO()
                .setUsedNicknames(this.connectedClients.keySet().stream().toList())
                .setAvailableGames(gameInfo);
        state.setStateName("AvailableGamesDTO");

        clientVirtualView.updateState(state);
    }

    // TODO: Method to refresh the available games

    /**
     * @return the filtered Map with the games that can be joined from the players
     * */
    private Map<Integer, GameInstance> searchForGames() {
        synchronized (this.gameInstances) {
            return this.gameInstances.entrySet()
                    .stream()
                    .filter(e -> e.getValue().canBeJoined())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
        }
    }

    /**
     * This method will be used to create a new game when a Player request it.
     * It also the playerNickname and information to the
     * */
    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, VirtualView clientView) throws Exception {


        synchronized (this.gameInstances) {
            if (this.connectedClients.containsKey(playerNickname)) {
                throw new Exception("The selected nickname is already being used");
            }

            // Create the new game --> it will be already be configured
            this.gameInstances.put(this.gameInstances.size(), new GameInstance(playerNickname, playerColor, gameLevel, totalPlayers, clientView));
        }

        // Add the client to the connectedClients Map
        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, clientView);
        }
    }

    /**
     * This method will be used to let a player join the given game by its ID
     * */
    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID, VirtualView clientView) throws Exception {
        synchronized (this.gameInstances) {
            if (this.connectedClients.containsKey(playerNickname)) {
                throw new Exception("The selected nickname is already being used");
            }

            GameInstance game = this.gameInstances.get(gameID);
            game.addNewPlayer(playerNickname, playerColor, clientView);
        }

        // Add the client to the connectedClients Map
        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, clientView);
        }
    }

    // TODO: For all the interaction with the user we need to pass his nickname to get the associated game and then route the
    //  request

    /**
     * @return the current model state
     * */
//    public StateDTO getCurrentState() {
//        return this.controller.getCurrentState();
//    }


    // TODO: Implements the ping utility methods --> we will only have one thread pinging the clients
    //  and one thread checking the ping data results
}
