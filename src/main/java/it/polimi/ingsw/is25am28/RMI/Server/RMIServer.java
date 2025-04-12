package it.polimi.ingsw.is25am28.RMI.Server;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.RMI.Client.VirtualServerRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * RMIServer implements the VirtualViewRMI interface
 * */

public class RMIServer extends UnicastRemoteObject implements VirtualServerRMI {
    final GameController controller;

    // Ref of the all the clients that are interested in receiving updates
    final List<VirtualViewRMI> clients;

    public RMIServer() throws RemoteException {
        super();
        this.clients = new ArrayList<>();
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
    public void connectClient(VirtualViewRMI client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add(client);

            StateJSON state = this.controller.onClientConnection();
            // TODO: send the state back to the client (sentTo, NO sendToAll)
        }
    }

    @Override
    public StateJSON gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers) throws RemoteException {
        return null;
    }

    @Override
    public List<StateJSON> addNewPlayer(String nickname, PlayerColor playerColor) throws RemoteException {
        return List.of();
    }

    @Override
    public ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws RemoteException {
        return null;
    }

    @Override
    public ConstructionComponentDTO deselectTile(String player, Integer i, Integer j) throws RemoteException {
        return null;
    }

    @Override
    public List<StateJSON> playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles) throws RemoteException {
        return List.of();
    }

    @Override
    public TimerDTO flipTimer(String player) throws RemoteException {
        return null;
    }

    @Override
    public List<StateJSON> fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws RemoteException {
        return List.of();
    }

    @Override
    public List<StateJSON> populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws RemoteException {
        return List.of();
    }

    @Override
    public List<StateJSON> playCard(ActionJSON action) throws RemoteException {
        return List.of();
    }
}
