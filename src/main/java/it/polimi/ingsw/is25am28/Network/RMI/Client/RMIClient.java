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
import it.polimi.ingsw.is25am28.Network.Server.GameInstance;
import it.polimi.ingsw.is25am28.Network.Server.PingHelper;
import it.polimi.ingsw.is25am28.Network.Server.ServerLogger;
import it.polimi.ingsw.is25am28.Network.UpdateHandler.UpdateHandler;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI {
    private final VirtualServerRMI server;
    private final Queue queueHandler;
    private final UpdateHandler updateHandler;
    private final ScheduledExecutorService pingScheduler;
    private final ScheduledExecutorService pongScheduler;
    private final UUID uuid;
    private int failedPong;

    /**
     * Initializes an RMIClient instance with the given parameters. Validates the input arguments,
     * connects to the RMI server, and initializes components required for communication with the server
     * and updates within the client system.
     *
     * @param ipAddress the IP address of the server to connect to; must not be null or empty
     * @param port the port number of the server; must be within the range [1, 65535]
     * @param uuid the unique identifier of the client; must not be null
     * @param ui the user interface implementation to interact with the client
     * @param model the client-side model representing the application's data structure
     * @throws Exception if any error occurs during connection or initialization
     * @throws IllegalArgumentException if any of the parameters ipAddress, port, or uuid are invalid
     * @throws RemoteException if the server lookup fails
     */
    public RMIClient(String ipAddress, int port, UUID uuid, ClientUI ui, ClientModel model) throws Exception {
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
        this.server = this.lookUpForServer(ipAddress, port);
        if (this.server == null) {
            throw new RemoteException("Server unavailable: " + serverName);
        }

        // Init the viewUpdater
        ViewUpdater viewUpdater = new ViewUpdater(ui, model);

        // Create the queue handler to process in a thread the communication with the server
        this.queueHandler = new Queue();
        new Thread(queueHandler).start();

        this.updateHandler = new UpdateHandler(model, viewUpdater);

        this.pingScheduler = Executors.newSingleThreadScheduledExecutor();
        this.pongScheduler = Executors.newSingleThreadScheduledExecutor();
        failedPong = 0;

        this.run();

        // Start pinging the server
        this.pingServer();
        this.listenForPongs();
    }

    /**
     * Looks up and retrieves a remote RMI server instance from the specified IP address and port.
     *
     * @param ipAddress the IP address of the RMI registry; must not be null or empty
     * @param port the port number of the RMI registry; must be within the range [0, 65535]
     * @return the {@code VirtualServerRMI} instance representing the server retrieved from the RMI registry
     * @throws RuntimeException if there is a communication issue with the registry or the server is not bound
     */
    private VirtualServerRMI lookUpForServer(String ipAddress, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress, port);
            return (VirtualServerRMI) registry.lookup("GameRMIServer");
        } catch (RemoteException e) {
            throw new RuntimeException("Server unavailable: " + e.getMessage());
        } catch (NotBoundException e) {
            throw new RuntimeException("Server not bound: " + e.getMessage());
        }
    }

    /**
     * Connects the client to the server with the RMI connection protocol
     * */
    private void run() {
        this.enqueueCommunication(() -> this.server.connectClient(this, this.uuid));
    }

    /**
     * Initiates a periodic task to send a heartbeat (ping) to the server.
     * This method schedules a fixed-rate executor that periodically sends a ping message to the server.
     * <br>
     * The ping task runs at a fixed interval of 5000 milliseconds.
     */
    private void pingServer() {
        this.pingScheduler.scheduleAtFixedRate(() -> {
            this.enqueueCommunication(() -> server.ping(this.uuid));
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    private void listenForPongs() {
        this.pongScheduler.scheduleAtFixedRate(() -> {
            synchronized (this) {
                this.failedPong ++;
                if (this.failedPong > 3) {
                    System.out.println("The connection with the server has been lost (NO INTERNET)");
                    System.exit(0);
                }
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void pong() {
        synchronized (this) {
            this.failedPong = 0;
        }
    }


    @Override
    public void refreshGames() {
        this.enqueueCommunication(() -> server.refreshGames(this.uuid));
    }

    @Override
    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) {
        this.enqueueCommunication(() -> server.createNewGame(playerNickname, playerColor, gameLevel, totalPlayers, this.uuid));
    }

    @Override
    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID) {
        this.enqueueCommunication(() -> server.joinGame(playerNickname, playerColor, gameID, this.uuid));
    }

    @Override
    public void selectTile(String playerNickname, int id) {
        this.enqueueCommunication(() -> server.selectTile(playerNickname, id, this.uuid));
    }

    @Override
    public void deselectTile(String playerNickname, int id) {
        this.enqueueCommunication(() -> server.deselectTile(playerNickname, id, this.uuid));
    }

    @Override
    public void reserveTile(String playerNickname, int id) {
        this.enqueueCommunication(() -> server.reserveTile(playerNickname, id, this.uuid));
    }

    @Override
    public void fastShip(String playerNickname) {
        this.enqueueCommunication(() -> server.fastShip(playerNickname, this.uuid));
    }

    @Override
    public void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) {
        this.enqueueCommunication(() -> server.placeTile(playerNickname, componentID, i, j, rotation, this.uuid));
    }

    @Override
    public void sendShipConfirmation(String playerNickname, int reservedTiles) {
        this.enqueueCommunication(() -> server.sendShipConfirmation(playerNickname, reservedTiles, this.uuid));
    }

    @Override
    public void flipTimer(String playerNickname) {
        this.enqueueCommunication(() -> server.flipTimer(playerNickname, this.uuid));
    }

    @Override
    public void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction) {
        this.enqueueCommunication(() -> server.selectDeselectSubdeck(playerNickname, subdeck, isSelectAction, this.uuid));
    }

    @Override
    public void fixShip(String playerNickname, Integer i, Integer j) {
        this.enqueueCommunication(() -> server.fixShip(playerNickname, i, j, this.uuid));
    }

    @Override
    public void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) {
        this.enqueueCommunication(() -> server.populateShip(playerNickname, lifeFormToAdd, this.uuid));
    }

    @Override
    public void playCard(String playerNickname, ActionJSON action) {
        this.enqueueCommunication(() -> server.playCard(playerNickname, action, this.uuid));
    }

    @Override
    public void updateState(Answer answer) {
        this.updateHandler.processUpdate(answer);
    }

    @Override
    public void reportError(ErrorAnswer error) {
        this.updateHandler.reportErrorUpdate(error);
    }

    @Override
    public void reconnectClient(String nickname) {
        this.enqueueCommunication(() -> server.reconnectClient(nickname, this.uuid));
    }

    /**
     * Adds a {@code ThrowingRunnable} task to the processing queue and ensures any exceptions
     * thrown during its execution are handled appropriately.
     * The method wraps the given task in a {@code Runnable}, catches exceptions if they occur,
     * logs a message indicating the server connection is lost, and terminates the application.
     *
     * @param runnable the task to be executed, defined as a {@code ThrowingRunnable} which can throw exceptions
     */
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
