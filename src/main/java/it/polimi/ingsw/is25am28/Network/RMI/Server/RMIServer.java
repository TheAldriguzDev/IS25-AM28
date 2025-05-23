package it.polimi.ingsw.is25am28.Network.RMI.Server;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.*;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Client.VirtualServerRMI;
import it.polimi.ingsw.is25am28.Network.Server.Server;
import it.polimi.ingsw.is25am28.Network.Server.ServerLogger;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

        ServerLogger.info("RMI SERVER", "RMI server listening on port " + serverPort);
    }

    /**
     * This method will be used to connect a client to the server --> it will be added since we need
     * to have a reference to send message back to the client
     * */
    @Override
    public void connectClient(VirtualViewRMI client, UUID clientUUID) throws Exception {
        ServerLogger.info("RMI SERVER", "New client connected");

        synchronized (this.clients) {
            this.clients.put(clientUUID, client);
        }

        // Connect the client to the ServerRouter
        this.controller.onClientConnection(client);
    }

    @Override
    public void refreshGames(UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.onClientConnection(client);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.createNewGame(playerNickname, playerColor, gameLevel, totalPlayers, client);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.joinGame(playerNickname, playerColor, gameID, client);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void selectTile(String playerNickname, int id, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.selectTile(playerNickname, id);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void deselectTile(String playerNickname, int id, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.deselectTile(playerNickname, id);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.placeTile(playerNickname, componentID, i, j, rotation);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void sendShipConfirmation(String playerNickname, int reservedTiles, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.playerEndedSendShip(playerNickname, reservedTiles);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void flipTimer(String playerNickname, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.flipTimer(playerNickname);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.selectDeselectSubdeck(playerNickname, subdeck, isSelectAction);
        }
        catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void fixShip(String playerNickname, Integer i, Integer j, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.fixShip(playerNickname, i, j);
        }
        catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.populateShip(playerNickname, lifeFormToAdd);
        }
        catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    @Override
    public void playCard(String playerNickname, ActionJSON action, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.playCard(playerNickname, action);
        }
        catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    // ========== PING METHOD ========== //
    @Override
    public void ping(UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        this.controller.clientPing(client);
    }

    @Override
    public void reconnectClient(String nickname, UUID uuid) throws Exception {
        VirtualView client = this.clients.get(uuid);

        try {
            this.controller.reconnectClient(nickname, client);
        } catch (Exception e) {
            this.reportError(client, new ErrorAnswer(e.getMessage()));
        }
    }

    private void reportError(VirtualView client, ErrorAnswer answer) {
        this.queueHandler.enqueue(() -> {
            try {
                client.reportError(answer);
            } catch (Exception e) {
                ServerLogger.error("NETWORK", "Failed to send the error message");
                throw new RuntimeException(e);
            }
        });
    }
}
