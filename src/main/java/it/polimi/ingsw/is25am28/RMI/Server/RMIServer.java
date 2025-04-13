package it.polimi.ingsw.is25am28.RMI.Server;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.RMI.Client.VirtualServerRMI;
import it.polimi.ingsw.is25am28.VirtualView;

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

    // Ref of the all the clients that are interested in receiving updates
    final Map<UUID, VirtualViewRMI> clients;

    public RMIServer() throws RemoteException {
        super();
        this.clients = new HashMap<UUID, VirtualViewRMI>();
        this.controller = new GameController();
    }

    // TODO: Understand if we need to create a dedicate class to startup the server
    public static void main(String[] args) throws RemoteException {
        final String serverName = "GameRMIServer";

        VirtualServerRMI server = new RMIServer();


        Registry registry = LocateRegistry.createRegistry(7777);
        registry.rebind(serverName, server); // Bind of the remote object to the given port

        System.out.println("RMI Server online");
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
            clients.get(uuid).reportError(e.getMessage());
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
}
