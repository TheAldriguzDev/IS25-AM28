package it.polimi.ingsw.is25am28.Network.RMI.Client;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.ViewUpdater;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.is25am28.Network.RMI.ThrowingRunnable;
import it.polimi.ingsw.is25am28.Network.UpdateHandler.UpdateHandler;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI {
    private VirtualServerRMI server;
    private final ViewUpdater viewUpdater;
    private final Queue queueHandler;

    private final UpdateHandler updateHandler;

    private final ScheduledExecutorService pingScheduler;

    private final UUID uuid;


    /**
     * Constructor used to create the RMIClient and starts it
     * */
    public RMIClient(String ipAddress, int port, UUID uuid, ClientUI ui, ClientModel model) throws Exception, RemoteException {
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
        this.viewUpdater = new ViewUpdater(ui, model);

        // Create the queue handler to process in a thread the communication with the server
        this.queueHandler = new Queue();
        new Thread(queueHandler).start();

        this.updateHandler = new UpdateHandler(model, this.viewUpdater);

        this.pingScheduler = Executors.newSingleThreadScheduledExecutor();

        this.run();

        // Method used by the client to ping the server
        this.pingServer();
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
        this.enqueueCommunication(() -> this.server.connectClient(this, this.uuid));
    }

    /**
     * This method will ping every 5 seconds the server
     * */
    private void pingServer() {
        this.pingScheduler.scheduleAtFixedRate(() -> {
            this.enqueueCommunication(() -> server.ping(this.uuid));
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    // TODO: Maybe is better to put the interrupting events on a separate thread, so in every moment that they arrive, we can switch without any issue

    @Override
    public void refreshGames() throws Exception {
        this.enqueueCommunication(() -> server.refreshGames(this.uuid));
    }

    @Override
    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception {
        this.enqueueCommunication(() -> server.createNewGame(playerNickname, playerColor, gameLevel, totalPlayers, this.uuid));
    }

    @Override
    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID) throws Exception {
        this.enqueueCommunication(() -> server.joinGame(playerNickname, playerColor, gameID, this.uuid));
    }

    @Override
    public void selectTile(String playerNickname, int id) throws Exception {
        this.enqueueCommunication(() -> server.selectTile(playerNickname, id, this.uuid));
    }

    @Override
    public void deselectTile(String playerNickname, int id) throws Exception {
        this.enqueueCommunication(() -> server.deselectTile(playerNickname, id, this.uuid));
    }

    @Override
    public void reserveTile(String playerNickname, int id) throws Exception {
        this.enqueueCommunication(() -> server.reserveTile(playerNickname, id, this.uuid));
    }

    @Override
    public void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception {
        this.enqueueCommunication(() -> server.placeTile(playerNickname, componentID, i, j, rotation, this.uuid));
    }

    @Override
    public void sendShipConfirmation(String playerNickname, int reservedTiles) throws Exception {
        this.enqueueCommunication(() -> server.sendShipConfirmation(playerNickname, reservedTiles, this.uuid));
    }

    @Override
    public void flipTimer(String playerNickname) throws Exception {
        this.enqueueCommunication(() -> server.flipTimer(playerNickname, this.uuid));
    }

    @Override
    public void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction) throws Exception {
        this.enqueueCommunication(() -> server.selectDeselectSubdeck(playerNickname, subdeck, isSelectAction, this.uuid));
    }

    @Override
    public void fixShip(String playerNickname, Integer i, Integer j) throws Exception {
        this.enqueueCommunication(() -> server.fixShip(playerNickname, i, j, this.uuid));
    }

    @Override
    public void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception {
        this.enqueueCommunication(() -> server.populateShip(playerNickname, lifeFormToAdd, this.uuid));
    }

    @Override
    public void playCard(String playerNickname, ActionJSON action) throws Exception {
        this.enqueueCommunication(() -> server.playCard(playerNickname, action, this.uuid));
    }

    @Override
    public void updateState(Answer answer) {
        this.updateHandler.processUpdate(answer);
    }

    @Override
    public void reportError(ErrorAnswer error) throws RemoteException {
        this.updateHandler.reportErrorUpdate(error);
    }

    @Override
    public void reconnectClient(String nickname) throws Exception {
        this.enqueueCommunication(() -> server.reconnectClient(nickname, this.uuid));
    }

    private void enqueueCommunication(ThrowingRunnable runnable) {
        queueHandler.enqueue(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                System.out.println(ANSIColors.YELLOW + "[Server offline] The connection with the server has been lost" + ANSIColors.RESET);

                System.exit(1);
            }
        });
    }
}
