package it.polimi.ingsw.is25am28.Network.RMI.Server;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Client.VirtualServerRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * RMIServer implements the VirtualViewRMI interface
 * */

public class RMIServer extends UnicastRemoteObject implements VirtualServerRMI {
    final GameController controller;
    final Queue queueHandler;

    // Ref of the all the clients that are interested in receiving updates
    final Map<UUID, VirtualViewRMI> clients;

    /**
     * Constructor used to create a new RMI Server
     * */
    public RMIServer(String serverName, int serverPort, GameController controller) throws RemoteException {
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
        System.out.println("New client connected to the server!");
        synchronized (this.clients) {
            this.clients.put(clientUUID, client);

            // Connect the client and execute it
            StateDTO state = this.controller.onClientConnection();
            client.updateState(state);
        }
    }

    // TODO: Add the messages that we need to send into a queue to separate the handling of the communication

    @Override
    public void gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, UUID uuid) throws Exception {
        StateDTO state = null;
        try {
            state = this.controller.gameConfig(nickname, playerColor, level, numPlayers);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.reportCommandError(clients.get(uuid), e.getMessage(), this.controller.getCurrentState());
            return;
        }

        // If the state is not null, then we can update the clients
        if (state != null) {
            synchronized (this.clients) {
                for (VirtualViewRMI client : this.clients.values()) {
                    client.updateState(state);
                }
            }
        }
    }

    @Override
    public void addNewPlayer(String nickname, PlayerColor playerColor, UUID uuid) throws Exception {
        List<StateDTO> states = this.controller.addNewPlayer(nickname, playerColor);

        synchronized (this.clients) {
            for (VirtualViewRMI client : this.clients.values()) {
                client.updateView(states.getFirst());

                if (states.size() == 2) {
                    client.updateState(states.getLast());
                }
            }
        }
    }

    @Override
    public void selectTile(String player, Integer i, Integer j, UUID uuid) throws RemoteException {

    }

    @Override
    public void deselectTile(String player, Integer i, Integer j, UUID uuid) throws RemoteException {

    }

    @Override
    public void playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles, UUID uuid) throws RemoteException {

    }

    @Override
    public void flipTimer(String player, UUID uuid) throws RemoteException {

    }

    @Override
    public void fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove, UUID uuid) throws RemoteException {

    }

    @Override
    public void populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd, UUID uuid) throws RemoteException {

    }

    @Override
    public void playCard(ActionJSON action, UUID uuid) throws RemoteException {

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
