package it.polimi.ingsw.is25am28.Network.RMI.Client;

import it.polimi.ingsw.is25am28.Client.ViewUpdater;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI {
    private VirtualServerRMI server;
    private final ViewUpdater viewUpdater;
    private final Queue queueHandler;

    private final UUID uuid;

    /**
     * Constructor used to create the RMIClient and starts it
     * */
    public RMIClient(String ipAddress, int port, UUID uuid) throws Exception, RemoteException {
        super();

        // Args validation
        if (ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port OutOfBound [0, 65535]: " + port);
        }
        if (uuid == null) {
            throw new IllegalArgumentException("UUI cannot be null");
        }

        this.uuid = uuid;

        final String serverName = "GameRMIServer";
        this.server = this.lookUpForServer(ipAddress, port, serverName);
        if (this.server == null) {
            throw new RemoteException("Server unavailable: " + serverName);
        }

        // Init the viewUpdater
        this.viewUpdater = new ViewUpdater(this);

        // Create the queue handler to process in a thread the communication with the server
        this.queueHandler = new Queue();
        new Thread(queueHandler).start();

        this.run();
    }

    private VirtualServerRMI lookUpForServer(String ipAddress, int port, String serverName) {
        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress, port);
            return (VirtualServerRMI) registry.lookup(serverName);
        } catch (RemoteException e) {
            System.err.println("Server unavailable: " + e.getMessage());
        } catch (NotBoundException e) {
            System.err.println("Server not bound: " + e.getMessage());
        }
        return null;
    }

    /**
     * Method used to connect the client to the server
     * */
    private void run() throws Exception, RemoteException {
        queueHandler.enqueue(() -> {
            try {
                this.server.connectClient(this, this.uuid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // TODO: Change in cmd pattern to have only one method execute(Command cmd)
    public void configureGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception {
        queueHandler.enqueue(() -> {
            try {
                server.gameConfig(playerNickname, playerColor, gameLevel, totalPlayers, this.uuid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void newPlayer(String playerNickname, PlayerColor playerColor) throws Exception {
        queueHandler.enqueue(() -> {
            try {
                server.addNewPlayer(playerNickname, playerColor, this.uuid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Method used to update the client view (display the new content)
     * TODO: make a lock to prevent data race between user input and server updates
     * TODO: think about the best way to print out the content given by the server --> overload or what else?
     * */
    @Override
    public void updateView(StateDTO state) throws RemoteException {
        new Thread(() -> {
            try {
                state.accept(viewUpdater);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void updateState(StateDTO state) throws Exception {
        new Thread(() -> {
            try {
                state.accept(viewUpdater);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void reportError(String details, StateDTO state) throws RemoteException {
        System.err.println("ERROR: " + details);
        new Thread(() -> {
            try {
                state.accept(viewUpdater);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
