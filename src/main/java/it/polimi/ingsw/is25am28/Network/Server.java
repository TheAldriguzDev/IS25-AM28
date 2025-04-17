package it.polimi.ingsw.is25am28.Network;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.RMI.Server.RMIServer;
import it.polimi.ingsw.is25am28.Network.Socket.Server.TCPServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to spawn both the RMI and the Socket server. In addition, it will handle all the communication between
 * the controller and the network servers.
 * */


/**
 * La logica di questa classe dovrebbe essere: ritorna lo stato SOLO SE deve essere aggiornato un solo client.
 * Se più client devono essere aggiornati, allora l'update di questi client (broadcast), viene invocato
 * direttamente da questa classe
 * */

// TODO: This class needs to implement the controller class

public class Server {

    // Server constants
    public static final String RMIServerName = "GameRMIServer";
    public static final int RMIServerPort = 7777;
    public static final String TCPAddress = "127.0.0.1";
    public static final int TCPServerPort = 8888;

    // Controller and networks servers
    private final GameController controller;
    private final RMIServer rmiServer;
    private final TCPServer tcpServer;

    // Lobby utility
    private final Map<String, ClientStatus> connectedClients;

    public Server() throws Exception {
        this.controller = new GameController();

        // Create the RMIServer and the TCPServer
        this.tcpServer = new TCPServer(Server.TCPAddress, Server.TCPServerPort, this);
        this.rmiServer = new RMIServer(Server.RMIServerName, Server.RMIServerPort, this);
        this.connectedClients = new HashMap<>();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
    }

    public StateDTO onClientConnection() {
        return this.controller.onClientConnection();
    }

    /**
     * @return the current model state
     * */
    public StateDTO getCurrentState() {
        return this.controller.getCurrentState();
    }

    /**
     * This method will invoke the controller method to configure the game. When the response arrives,
     * all the clients will be notified, even if they are using different types of networks.
     *
     * If the controller throws an exception it will be propagated
     * */
    public void configGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws JsonProcessingException, IllegalArgumentException, IllegalStateException {
        StateDTO state = this.controller.gameConfig(playerNickname, playerColor, gameLevel, totalPlayers);

        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, new ClientStatus(playerNickname));
        }

        // If there are no errors we can update all the clients
        this.tcpServer.broadCastUpdateState(state);
        this.rmiServer.broadCastUpdateState(state);
    }

    public void addNewPlayer(String playerNickname, PlayerColor playerColor) throws JsonProcessingException, IllegalStateException, IllegalArgumentException {
        List<StateDTO> states = this.controller.addNewPlayer(playerNickname, playerColor);

        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, new ClientStatus(playerNickname));
        }

        for (StateDTO state : states) {
            this.tcpServer.broadCastUpdateState(state);
            this.rmiServer.broadCastUpdateState(state);
        }
    }



    // Ping utility methods --> Will ping both through RMI and Socket
}
