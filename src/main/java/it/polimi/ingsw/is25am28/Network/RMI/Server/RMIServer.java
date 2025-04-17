package it.polimi.ingsw.is25am28.Network.RMI.Server;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.ConfigGame;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.Messages.NewPlayer;
import it.polimi.ingsw.is25am28.Network.Messages.Ping;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Client.VirtualServerRMI;
import it.polimi.ingsw.is25am28.Network.Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * RMIServer implements the VirtualViewRMI interface
 * */

public class RMIServer extends UnicastRemoteObject implements VirtualServerRMI {

    class ClientStatus {
        protected UUID uuid; // Client uuid
        protected String nickName;
        protected int failedPings = 0;
        protected boolean isConnected;

        protected ClientStatus(UUID uuid) {
            this.uuid = uuid;
            this.nickName = "";
            this.isConnected = true;
        }

        protected void setNickName(String nickName) {
            this.nickName = nickName;
        }

        protected void resetPings() {
            this.failedPings = 0;
            this.isConnected = true;
        }
    }

    final Server controller;
    final Queue queueHandler;

    // Ref of the all the clients that are interested in receiving updates
    final Map<UUID, VirtualViewRMI> clients;
    final Map<UUID, ClientStatus> clientsStatus = new HashMap<>();

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

        this.runPingHandler();
    }

    /**
     * Method that spawn a Thread to check if any clients is disconnected
     * TODO: Esiste un modo migliore che evita di fare busy-waiting?
     * */
    private void runPingHandler() {
        new Thread(() -> {
            while (true) {
                synchronized (this.clientsStatus) {
                    for (ClientStatus clientStatus : this.clientsStatus.values()) {
                        clientStatus.failedPings++;

                        System.out.println("Controllo napoletano" + clientStatus.nickName);

                        // If the client already registered himself with his nickname, then we can start to check his ping
                        if (clientStatus.failedPings >= 3 && clientStatus.nickName != null && !clientStatus.nickName.isEmpty() && clientStatus.isConnected) {
                            clientStatus.isConnected = false;

                            // this.controller.disconnectClient(clientStatus.nickName);
                        }
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * This method will be used to connect a client to the server --> it will be added since we need
     * to have a reference to send message back to the client
     * */
    @Override
    public void connectClient(VirtualViewRMI client, UUID clientUUID) throws Exception {
        System.out.println("New client connected to the server!");
        synchronized (this.clients) {
            this.clients.put(clientUUID, client);

            // Connect the client and execute it
            StateDTO state = this.controller.onClientConnection();
            client.updateState(state);
        }

        // Add the client to the ping handler data structure
        synchronized (this.clientsStatus) {
            this.clientsStatus.put(clientUUID, new ClientStatus(clientUUID));
        }
    }

    @Override
    public void sendMessage(Message message, UUID uuid) throws Exception {
        switch (message) {
            case ConfigGame data -> {
                this.gameConfig(data.getPlayerNickname(), data.getPlayerColor(), data.getGameLevel(), data.getTotalPlayers(), uuid);
            }
            case NewPlayer data -> {
                this.addNewPlayer(data.getPlayerNickname(), data.getPlayerColor(), uuid);
            }
            case Ping ignored -> {
                this.ping(uuid);
            }
            default -> {
                throw new Exception("The given Message is not supported");
            }
        }
    }

    /**
     * Method used by the client to ping the server. It will reset the ping counter
     * */
    private void ping(UUID uuid) {
        synchronized (this.clientsStatus) {
            this.clientsStatus.get(uuid).resetPings();
        }
    }

    private void reconnectClient(String nickName, UUID uuid) {

    }

    private void gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, UUID uuid) throws Exception {
        try {
            this.controller.configGame(nickname, playerColor, level, numPlayers);
        } catch (Exception e) {
            this.reportCommandError(this.clients.get(uuid), e.getMessage(), this.controller.getCurrentState());
            return;
        }

        // TODO: I think that it's better to handle the lobby in the "Server" class to avoid duplicated code in all the network
        //  used
        synchronized (this.clientsStatus) {
            this.clientsStatus.get(uuid).setNickName(nickname);
        }
    }

    private void addNewPlayer(String nickname, PlayerColor playerColor, UUID uuid) throws Exception {
        try {
            this.controller.addNewPlayer(nickname, playerColor);
        } catch (Exception e) {
            this.reportCommandError(this.clients.get(uuid), e.getMessage(), this.controller.getCurrentState());
        }

        // Add the client nickname to the server
        synchronized (this.clientsStatus) {
            this.clientsStatus.get(uuid).setNickName(nickname);
        }
    }

    private void selectTile(String player, Integer i, Integer j, UUID uuid) throws RemoteException {

    }

    private void deselectTile(String player, Integer i, Integer j, UUID uuid) throws RemoteException {

    }

    private void playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles, UUID uuid) throws RemoteException {

    }

    private void flipTimer(String player, UUID uuid) throws RemoteException {

    }

    private void fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove, UUID uuid) throws RemoteException {

    }


    private void populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd, UUID uuid) throws RemoteException {

    }


    private void playCard(ActionJSON action, UUID uuid) throws RemoteException {

    }

    /**
     * Broadcast a stateUpdate to all the clients
     * */
    public void broadCastUpdateState(StateDTO state) {
        synchronized (this.clients) {
            for (VirtualViewRMI client : this.clients.values()) {
                this.queueHandler.enqueue(() -> {
                    try {
                        client.updateView(state);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private void reportCommandError(VirtualViewRMI client, String msg, StateDTO state) {
        queueHandler.enqueue(() -> {
            try {
                client.reportError(msg, state);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
