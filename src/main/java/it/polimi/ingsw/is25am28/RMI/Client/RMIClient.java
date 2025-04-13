package it.polimi.ingsw.is25am28.RMI.Client;

import it.polimi.ingsw.is25am28.Client.ViewUpdater;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.GameModelv2.CreateGameState;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.RMI.Server.RMIServer;
import it.polimi.ingsw.is25am28.RMI.Server.VirtualViewRMI;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI {
    final VirtualServerRMI server;
    final Object displayLock;
    final UUID uuid;

    final ViewUpdater viewUpdater;

    public RMIClient(String ipAddress, int port, UUID uuid) throws Exception, RemoteException {
        super();
        VirtualServerRMI server = null;
        final String serverName = "GameRMIServer";

        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress, port); // args[0] is the IP address that identify the RMI registry

             server = (VirtualServerRMI) registry.lookup(serverName);

        } catch (RemoteException e) {
            System.err.println("Server unavailable: " + e.getMessage());
        } catch (NotBoundException e) {
            System.err.println("Cannot bound the registry: " + e.getMessage());
        }

        if (server == null) {
            throw new RemoteException("Server unavailable: " + serverName);
        }

        this.server = server;
        displayLock = new Object();

        // Assign to the viewUpdater the client RMI handler
        this.viewUpdater = new ViewUpdater(this);
        this.uuid = uuid;

        this.run();
    }

    /**
     * Method used to connect the client to the server
     * */
    private void run() throws Exception, RemoteException {
        this.server.connectClient(this, this.uuid);
    }

    // TODO: Change in cmd pattern to have only one method execute(Command cmd)
    public void configureGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception {
        server.gameConfig(playerNickname, playerColor, gameLevel, totalPlayers, this.uuid);
    }

    public void newPlayer(String playerNickname, PlayerColor playerColor) throws Exception {
        server.addNewPlayer(playerNickname, playerColor, this.uuid);
    }

    /**
     * Method used to update the client view (display the new content)
     * TODO: make a lock to prevent data race between user input and server updates
     * TODO: think about the best way to print out the content given by the server --> overload or what else?
     * */
    @Override
    public void updateView(StateDTO state) throws RemoteException {
        synchronized (this.displayLock) {
            System.out.println("Received a message from server (updateView)");
        }
    }

    @Override
    public void updateState(StateDTO state) throws Exception {
        synchronized (this.displayLock) {

            new Thread(() -> {
                try {
                    state.accept(viewUpdater);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

            // System.out.println("Received a message from server (updateState) " + state.getStateName());
        }
    }

    @Override
    public void reportError(String details) throws RemoteException {
        synchronized (this.displayLock) {
            System.err.println("ERROR: " + details);
        }
    }
}
