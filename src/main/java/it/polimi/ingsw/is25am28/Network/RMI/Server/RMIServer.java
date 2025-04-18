package it.polimi.ingsw.is25am28.Network.RMI.Server;

import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.Messages.Ping;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Client.VirtualServerRMI;
import it.polimi.ingsw.is25am28.Network.Server.Server;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * RMIServer implements the VirtualViewRMI interface
 * */

public class RMIServer extends UnicastRemoteObject implements VirtualServerRMI {
    final Server controller;
    final Queue queueHandler;

    // Ref of the all the clients that are interested in receiving updates
    final Map<UUID, VirtualViewRMI> clients;

    /**
     * Constructor used to create a new RMI Server
     * */
    public RMIServer(String serverName, int serverPort, Server controller) throws RemoteException {
        super();
        this.clients = new HashMap<UUID, VirtualViewRMI>();
        this.controller = controller;

        // Create and start the actual server
        Registry registry = LocateRegistry.createRegistry(serverPort);
        registry.rebind(serverName, this);

        // Create the queue handler to process in a thread the communication with the clients
        this.queueHandler = new Queue();
        new Thread(queueHandler).start();

        System.out.println("RMI server listening on port " + serverPort);
    }

    /**
     * This method will be used to connect a client to the server --> it will be added since we need
     * to have a reference to send message back to the client
     * */
    @Override
    public void connectClient(VirtualViewRMI client, UUID clientUUID) throws Exception {
        System.out.println("New RMIClient connected to the server!");

        synchronized (this.clients) {
            this.clients.put(clientUUID, client);
        }

        // Connect the client to the ServerRouter
        this.controller.onClientConnection(client);
    }

    @Override
    public void sendMessage(Message message, UUID uuid) throws Exception {
        switch (message) {
            case ConfigGame data -> {
                this.createNewGame(data.getPlayerNickname(), data.getPlayerColor(), data.getGameLevel(), data.getTotalPlayers(), uuid);
            }
            case NewPlayer data -> {
                this.joinGame(data.getPlayerNickname(), data.getPlayerColor(), data.getGameID(), uuid);
            }
            case Ping ignored -> {
                // this.ping(uuid);
            }
            default -> {
                throw new Exception("The given Message is not supported");
            }
        }
    }

    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.createNewGame(playerNickname, playerColor, gameLevel, totalPlayers, client);
        } catch (Exception e) {
            client.reportError(new ErrorAnswer(e.getMessage()));
        }
    }

    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.joinGame(playerNickname, playerColor, gameID, client);
        } catch (Exception e) {
            client.reportError(new ErrorAnswer(e.getMessage()));
        }
    }
}
