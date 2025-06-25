package it.polimi.ingsw.is25am28.Network.Socket.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import it.polimi.ingsw.is25am28.Network.Messages.*;
import it.polimi.ingsw.is25am28.Network.Socket.Server.VirtualViewSocket;
import it.polimi.ingsw.is25am28.Network.UpdateHandler.UpdateHandler;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.*;

public class TCPClient implements VirtualViewSocket {
    // Input channel to read from the socket
    private final BufferedReader input;

    // Output channel will be used to communicate with the server
    private final SocketServerHandler output;

    // Used to respond to msg from the Server
    private final ViewUpdater viewUpdater;

    private final ObjectMapper mapper = new ObjectMapper();

    private final UpdateHandler updateHandler;

    private final ScheduledExecutorService pingScheduler;

    /**
     * Constructs a new TCPClient instance that establishes a connection to a server
     * using the specified IP address and port. It sets up input and output streams for
     * communication, initializes handlers, and starts the communication.
     *
     * @param ipAddress the IP address of the server to connect to
     * @param port the port number of the server, must be in the range (0, 65535]
     * @param ui the ClientUI object responsible for managing user interface updates (TUI / GUI)
     * @param model the ClientModel object handling the client-side application logic
     * @throws IllegalArgumentException if the provided IP address is null or empty, or if the port is out of range
     * @throws IOException if an error occurs while establishing the server connection
     */
    public TCPClient(String ipAddress, int port, ClientUI ui, ClientModel model) throws IOException {
        // Args validation
        if (ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port OutOfBound [0, 65535]: " + port);
        }

        Socket serverSocket = new Socket(ipAddress, port);

        InputStreamReader socketInputReader = new InputStreamReader(serverSocket.getInputStream());
        OutputStreamWriter socketOutputWriter = new OutputStreamWriter(serverSocket.getOutputStream());

        this.input = new BufferedReader(socketInputReader);

        // This will be a thread that will listen to the messages coming from the server
        this.output = new SocketServerHandler(new BufferedWriter(socketOutputWriter));

        this.viewUpdater = new ViewUpdater(ui, model);

        this.updateHandler = new UpdateHandler(model, viewUpdater);

        this.pingScheduler = Executors.newSingleThreadScheduledExecutor();

        // Run the client TCPClient
        this.run();
        this.pingServer();
    }

    /**
     * Runs the TCPClient in a separate thread to listen for messages from the server.
     * If an exception is caught, it checks whether the server is offline and terminates accordingly.
     */
    private void run() {
        new Thread(() -> {
            try {
                this.runVirtualServer();
            } catch (Exception e) {
                if (e.getMessage().toLowerCase().contains("connection reset")) {
                    System.out.println(ANSIColors.YELLOW + "[Server offline] The connection with the server has been lost" + ANSIColors.RESET);
                    System.exit(1);
                } else {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * Listens for incoming messages from the server and dispatches them to the appropriate
     * handlers based on whether the response represents an error or a state update.
     */
    private void runVirtualServer() throws IOException {
        String line;

        while ((line = this.input.readLine()) != null) {
            Answer state = mapper.readValue(line, Answer.class);

            if (Objects.requireNonNull(state) instanceof ErrorAnswer error) {
                this.reportError(error);
            } else {
                this.updateState(state);
            }
        }

        // Once the loop has been exited means that the server is offline
        System.out.println(ANSIColors.YELLOW + "[Server offline] The connection with the server has been lost" + ANSIColors.RESET);
        System.exit(1);
    }

    /**
     * Initiates a periodic task to send a heartbeat (ping) to the server.
     * This method schedules a fixed-rate executor that periodically sends a ping message to the server.
     *
     * The ping task runs at a fixed interval of 5000 milliseconds.
     */
    private void pingServer() {
        this.pingScheduler.scheduleAtFixedRate(() -> {
            try {
                this.sendMessage(new Ping());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void refreshGames() throws Exception {
        this.sendMessage(new RefreshGames());
    }

    @Override
    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception {
        this.sendMessage(new ConfigGame(playerNickname, playerColor, gameLevel, totalPlayers));
    }

    @Override
    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID) throws Exception {
        this.sendMessage(new NewPlayer(playerNickname, playerColor, gameID));
    }

    @Override
    public void selectTile(String playerNickname, int id) throws Exception {
        this.sendMessage(new SelectTile(playerNickname, id));
    }

    @Override
    public void deselectTile(String playerNickname, int id) throws Exception {
        this.sendMessage(new DeselectTile(playerNickname, id));
    }

    @Override
    public void reserveTile(String playerNickname, int id) throws Exception {
        this.sendMessage(new ReserveTile(playerNickname, id));
    }

    @Override
    public void fastShip(String playerNickname) throws Exception {
        this.sendMessage(new FastShip(playerNickname));
    }

    @Override
    public void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception {
        this.sendMessage(new PlaceTile(playerNickname, componentID, i, j, rotation));
    }

    @Override
    public void sendShipConfirmation(String playerNickname, int reservedTiles) throws Exception {
        this.sendMessage(new SendShipConfirmation(playerNickname, reservedTiles));
    }

    @Override
    public void flipTimer(String playerNickname) throws Exception {
        try {
            this.sendMessage(new FlipTimer(playerNickname));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction) throws Exception {
        this.sendMessage(new SelectDeselectSubdeck(playerNickname, subdeck, isSelectAction));
    }

    @Override
    public void fixShip(String playerNickname, Integer i, Integer j) throws Exception {
        this.sendMessage(new FixShip(playerNickname, i, j));
    }

    @Override
    public void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception {
        this.sendMessage(new PopulateShip(playerNickname, lifeFormToAdd));
    }

    @Override
    public void playCard(String playerNickname, ActionJSON action) throws Exception {
        this.sendMessage(new PlayCard(playerNickname, action));
    }

    @Override
    public void reconnectClient(String nickname) throws Exception {
        this.sendMessage(new Reconnect(nickname));
    }

    @Override
    public void updateState(Answer answer) {
        this.updateHandler.processUpdate(answer);
    }

    @Override
    public void reportError(ErrorAnswer error) {
        this.updateHandler.reportErrorUpdate(error);
    }

    /**
     * Sends a message to the server.
     *
     * @param message the {@code Message} object to be sent to the server, representing
     *                client-to-server communication data
     */
    private void sendMessage(Message message) {
        try {
            this.output.sendMessage(message); // This will invoke the SocketServerHandler
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
