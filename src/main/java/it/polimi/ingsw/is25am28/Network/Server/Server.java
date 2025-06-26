package it.polimi.ingsw.is25am28.Network.Server;

import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.PrintUtils;
import it.polimi.ingsw.is25am28.Client.UI.TUI.WidgetTUI.WidgetTUI;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Server.RMIServer;
import it.polimi.ingsw.is25am28.Network.Socket.Server.TCPServer;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.Utils.ValidateIP;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for starting the network server and listening for incoming client requests.
 * It manages multiple concurrent games and routes each client request to the correct game instance
 * based on the client's name. It ensures that actions are executed within the appropriate game context.
 */
public class Server {
    // Server constants
    public static final String RMIServerName = "GameRMIServer";
    public static final int RMIServerPort = 7777;
    public static final int TCPServerPort = 8888;

    // This map stores the id of the game with its instance
    private final Map<Integer, GameInstance> gameInstances;

    // This map stores the players nickname and their network connection (nickname --> virtualView)
    private final Map<String, VirtualView> connectedClients;

    // This map will store the virtualView of each client with the pingHelper utility data
    private final Map<VirtualView, PingHelper> viewToPingHelper;

    // This map stores the client nickname with the associated game
    private final Map<String, Integer> clientToGame;

    // Ping scheduler thread that will check for client disconnections
    private final ScheduledExecutorService pingScheduler;

    private final ExecutorService pongPool;

    /**
     * Initializes a new instance of the Server class.
     * This constructor sets up the server by prompting the user to input a valid IPv4 address,
     * initializes the TCP and RMI servers, and prepares various data structures
     * required for managing game instances, connected clients, and scheduling tasks.
     * <br>
     * The constructor also initiates a periodic check to monitor client connectivity by collecting pings.
     *
     * @throws Exception if an error occurs during the server setup or initialization process
     */
    public Server() throws Exception {
        // Create the RMIServer and the TCPServer
        Scanner scanner = new Scanner(System.in);
        String ipAddress;

        TUIHandler.clearTerminal();

        new WidgetTUI()
                .appendString("[SETUP SERVER'S IPv4 ADDRESS]")
                .addPadding(0, 1, 0, 1)
                .wrapWidgetWithBorder()
                .printWidget();

        while (true) {
            System.out.println();
            System.out.print("Enter a valid IPv4 address: ");
            ipAddress = scanner.nextLine().trim();

            if (ValidateIP.validateIPAddress(ipAddress)) {
                break;
            }
            else {
                System.out.println(
                        PrintUtils.addColor(
                                "[ERROR] Given string does not represent an IPv4 address.",
                                ANSIColors.RED
                        )
                );

                System.out.println("\t(IPv4 format is: x.y.z.w -> [0-255].[0.255].[0-255].[0-255])");
            }
        }

        new TCPServer(ipAddress, Server.TCPServerPort, this);
        new RMIServer(Server.RMIServerName, Server.RMIServerPort, this);

        this.gameInstances = new HashMap<>();
        this.connectedClients = new HashMap<>();
        this.clientToGame = new HashMap<>();
        this.viewToPingHelper = new HashMap<>();
        this.pingScheduler = new ScheduledThreadPoolExecutor(1);
        this.pongPool = Executors.newCachedThreadPool();

        // Starts to check for client disconnections
        this.checkClientsConnection();
    }

    // Main
    public static void main(String[] args) throws Exception {
        new Server();
    }

    /**
     * Handles a new client connection by retrieving all currently configured games and sending this information
     * to the client. This allows the client to choose one of the following actions:
     * <ul>
     *     <li>Join an existing game</li>
     *     <li>Create a new game</li>
     *     <li>Reconnect to a previous game using a nickname</li>
     * </ul>
     *
     * @param clientVirtualView the virtual view associated with the connected client
     * @throws Exception if an error occurs while retrieving or sending game information
     */
    public void onClientConnection(VirtualView clientVirtualView) throws Exception {
        // Retrieve the list of available games
        Map<Integer, GameInstance> availableGames = this.searchForGames();

        // Retrieve the available games information
        List<GameInfoDTO> gameInfo = new ArrayList<>();
        for (Map.Entry<Integer, GameInstance> entry : availableGames.entrySet()) {
            gameInfo.add(
                    new GameInfoDTO()
                            .setId(entry.getKey())
                            .setLevel(entry.getValue().getLevel())
                            .setTotalPlayers(entry.getValue().getTotalPlayers())
                            .setAvailableColors(entry.getValue().getAvailableColors())
                            .setActualPlayers(entry.getValue().getCurrentPlayers())
            );
        }

        // Create the state with the available games information to the client
        StateDTO state = new AvailableGamesDTO()
                .setUsedNicknames(this.connectedClients.keySet().stream().toList())
                .setAvailableGames(gameInfo);
        state.setStateName("AvailableGamesDTO");

        Answer answer = new Answer()
                .setState(state);

        clientVirtualView.updateState(answer);
    }

    /**
     * Filters and retrieves a map of game instances that are available to be joined by players.
     * The method ensures thread safety by synchronizing on the gameInstances collection.
     *
     * @return a map where the key is the game ID (Integer) and the value is the game instance (GameInstance)
     *         of all currently available games that can be joined.
     */
    private Map<Integer, GameInstance> searchForGames() {
        synchronized (this.gameInstances) {
            return this.gameInstances.entrySet()
                    .stream()
                    .filter(e -> e.getValue().canBeJoined())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
        }
    }

    /**
     * Creates a new game and initializes its configuration, adding the specified player
     * to the game and mapping relevant client information to manage the game instance.
     * This method ensures thread safety for the involved data structures.
     *
     * @param playerNickname the nickname of the player creating the game
     * @param playerColor the color chosen by the player
     * @param gameLevel the difficulty level of the game
     * @param totalPlayers the total number of players for the game
     * @param clientView the virtual view associated with the client creating the game
     * @throws Exception if the specified nickname is already being used or if an error occurs during game creation
     */
    public void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, VirtualView clientView) throws Exception {
        synchronized (this.gameInstances) {
            if (this.connectedClients.containsKey(playerNickname)) {
                ServerLogger.error("ROUTER", "The selected nickname is already being used");
                throw new Exception("The selected nickname is already being used");
            }

            // Create the new game --> it will be already be configured
            int gameID = this.gameInstances.size();
            ServerLogger.info("ROUTER", String.valueOf(gameID), "New game has been created");
            this.gameInstances.put(gameID, new GameInstance(playerNickname, playerColor, gameLevel, totalPlayers, clientView));
            this.clientToGame.put(playerNickname, gameID);

            ServerLogger.info("ROUTER", String.valueOf(gameID), "Added " + playerNickname + " to the game");
        }

        // Add the client to the connectedClients Map
        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, clientView);
        }

        // Add the virtualView of the client with his pingHelper utility data
        synchronized (this.viewToPingHelper) {
            this.viewToPingHelper.put(clientView, new PingHelper(playerNickname));
        }
    }

    /**
     * Allows a player to join an existing game instance. This method manages the addition of a new player
     * to the specified game, ensuring thread-safe operations for handling connected clients, game instances,
     * and ping utilities associated with the client.
     *
     * @param playerNickname the nickname of the player attempting to join the game
     * @param playerColor the color chosen by the player
     * @param gameID the unique identifier of the game the player intends to join
     * @param clientView the virtual view associated with the client
     * @throws Exception if the player's nickname is already in use or if an error occurs while adding the player to the game
     */
    public void joinGame(String playerNickname, PlayerColor playerColor, int gameID, VirtualView clientView) throws Exception {
        synchronized (this.gameInstances) {
            if (this.connectedClients.containsKey(playerNickname)) {
                ServerLogger.error("ROUTER", "The selected nickname is already being used");
                throw new Exception("The selected nickname is already being used");
            }

            GameInstance game = this.gameInstances.get(gameID);
            game.addNewPlayer(playerNickname, playerColor, clientView);
            this.clientToGame.put(playerNickname, gameID);
            ServerLogger.info("ROUTER", String.valueOf(gameID), "Added " + playerNickname + " to the game");
        }

        // Add the client to the connectedClients Map
        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, clientView);
        }

        // Add the virtualView of the client with his pingHelper utility data
        synchronized (this.viewToPingHelper) {
            this.viewToPingHelper.put(clientView, new PingHelper(playerNickname));
        }
    }

    /**
     * Processes the selection of a tile by a player.
     * This method ensures thread safety while retrieving the game instance associated
     * with the specified player and updating the game state based on the tile selection.
     *
     * @param playerNickname the nickname of the player selecting the tile
     * @param id the unique identifier of the tile being selected
     * @throws Exception if an error occurs during the tile selection process
     */
    public void selectTile(String playerNickname, int id) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.selectTile(playerNickname, id);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " selected the tile with ID=" + id);
        }
    }

    /**
     * Processes the deselection of a tile by a player.
     * This method ensures thread safety while retrieving the game instance associated
     * with the specified player and updating the game state based on the tile deselection.
     *
     * @param playerNickname the nickname of the player deselecting the tile
     * @param id the unique identifier of the tile being deselected
     * @throws Exception if an error occurs during the tile deselection process
     */
    public void deselectTile(String playerNickname, int id) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.deselectTile(playerNickname, id);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " deselected the tile with ID=" + id);
        }
    }

    /**
     * Processes the reservation of a tile by a player.
     * This method ensures thread safety while retrieving the game instance associated
     * with the specified player and updating the game state based on the tile reservation.
     *
     * @param playerNickname the nickname of the player reserving the tile
     * @param id the unique identifier of the tile being reserved
     * @throws Exception if an error occurs during the tile reservation process
     */
    public void reserveTile(String playerNickname, int id) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.reserveTile(playerNickname, id);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " reserved the tile with ID=" + id);
        }
    }

    /**
     * Configures a fast ship for a player in the game they are currently participating in.
     * This method ensures thread safety while accessing the game instance associated with the player.
     *
     * @param playerNickname the nickname of the player requesting a fast ship configuration
     * @throws Exception if the game instance cannot be found or if an error occurs during the operation
     */
    public void fastShip(String playerNickname) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.fastShip(playerNickname);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " requested a fast ship configuration");
        }
    }

    /**
     * Places a tile on the game board for a specified player. This method ensures thread safety
     * while accessing the game instance associated with the player, updating the game state
     * accordingly. The placement includes the tile's position (row and column) and orientation.
     *
     * @param playerNickname the nickname of the player placing the tile
     * @param componentID the unique identifier of the tile being placed
     * @param i the row index where the tile is placed
     * @param j the column index where the tile is placed
     * @param rotation the rotation applied to the tile during placement
     * @throws Exception if an error occurs during the tile placement process
     */
    public void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.placeTile(playerNickname, componentID, i, j, rotation);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " placed the ("+ componentID +") tile at (" + i + "," + j + ") tile");
        }
    }

    /**
     * Notifies the server that a player has ended their ship creation process and indicates the number of reserved tiles.
     * This method ensures thread safety while accessing game instances and updates the game's state accordingly.
     *
     * @param playerNickname the nickname of the player signaling the end of their ship creation
     * @param reservedTiles the number of tiles reserved and not used by the player during ship creation
     * @throws Exception if an error occurs while processing the player's action or accessing the game data structures
     */
    public void playerEndedSendShip(String playerNickname, int reservedTiles) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.playerEndedSendShip(playerNickname, reservedTiles);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " ended his ship with " + reservedTiles + " reserved tile(s)");
        }
    }

    /**
     * Flips the timer during the ship construction process.
     *
     * @param playerNickname the nickname of the player requesting to flip the timer
     * @throws Exception if the game instance cannot be found, the player is not mapped
     *                   to a game, or an error occurs during the timer flip operation
     */
    public void flipTimer(String playerNickname) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.flipTimer(playerNickname);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " flipped the timer");
        }
    }

    /**
     * Handles the selection or deselection of a specific subdeck for a player.
     * This method retrieves the game instance associated with the player's nickname
     * and updates the game state based on the action (select or deselect).
     * Thread safety is ensured by synchronizing access to the game instances data structure.
     *
     * @param playerNickname the nickname of the player performing the action
     * @param subdeck the identifier of the subdeck being selected or deselected
     * @param isSelectAction a boolean value indicating the action to be performed;
     *                       true for selecting the subdeck, false for deselecting it
     * @throws Exception if an error occurs during the process, such as the inability to
     *                   retrieve the game instance or invalid player actions
     */
    public void selectDeselectSubdeck(String playerNickname, int subdeck, boolean isSelectAction) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.selectDeselectSubdeck(playerNickname, subdeck, isSelectAction);

            if (isSelectAction) {
                ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " selected the subdeck #" + (subdeck + 1));
            }
            else {
                ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " deselected the subdeck #" + (subdeck + 1));
            }
        }
    }

    /**
     * Removes a specified ship component for a player in the game instance
     * associated with the given player's nickname.
     *
     * @param playerNickname the nickname of the player requesting to fix their ship
     * @param i the row index of the ship component to be fixed or removed
     * @param j the column index of the ship component to be fixed or removed
     * @throws Exception if there is an issue retrieving the game instance or performing the operation
     */
    public void fixShip(String playerNickname, Integer i, Integer j) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.fixShip(playerNickname, i, j);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " removed a component from his ship (" + i + "," + j + ")" );
        }
    }

    /**
     * Populates a player's ship with a specified lifeform component in the game instance
     * associated with the player.
     *
     * @param playerNickname The nickname of the player who is populating their ship.
     * @param lifeFormToAdd  The component of the ship to be populated, represented as
     *                       an instance of ComponentHelper containing the lifeform type and
     *                       position.
     * @throws Exception if an error occurs during the process of populating the ship
     *                   in the game instance or if the provided player nickname is not
     *                   associated with any active game instance.
     */
    public void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.populateShip(playerNickname, lifeFormToAdd);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " populated a component of his ship (" + lifeFormToAdd.getI() + "," + lifeFormToAdd.getJ() + ") with " + lifeFormToAdd.getItem().toString());
        }
    }

    /**
     * Executes the playCard action for a specific player within the game instance they belong to.
     *
     * @param playerNickname the nickname of the player performing the action
     * @param action the action details represented in an ActionJSON object
     * @throws Exception if any error occurs during the playCard execution
     */
    public void playCard(String playerNickname, ActionJSON action) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.playCard(playerNickname, action);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " played the card");
        }
    }

    // ========== PING METHOD ========== //

    /**
     * Retrieves a list of offline client identifiers from all game instances.
     *
     * @return a list of strings representing the identifiers of offline clients
     */
    private List<String> getOfflineClients() {
        List<String> offlineClients;
        synchronized (this.gameInstances) {
            offlineClients = this.gameInstances.values()
                    .stream()
                    .flatMap(g -> g.getOfflineClients().stream())
                    .toList();
        }

        return offlineClients;
    }

    /**
     * Handles the ping operation for the provided client.
     * It resets the ping counter for the given client if a corresponding PingHelper exists.
     *
     * It also sends back a Pong message to the clients in order to let them know if there is any network issue
     *
     * @param clientView the VirtualView instance representing the client's view to be pinged
     * @throws Exception if an error occurs during the operation
     */
    public void clientPing(VirtualView clientView) throws Exception {
        synchronized (this.viewToPingHelper) {
            PingHelper pingHelper = viewToPingHelper.get(clientView);
            if (pingHelper != null) {
                pingHelper.resetPings();
            }

            pongPool.submit(() -> {
                try {
                    clientView.pong();
                } catch (Exception e) {
                    ServerLogger.error("SERVER", "Detected network issue!");
                }
            });
        }
    }

    /**
     * Periodically checks the connection status of all connected clients every 5 seconds.
     * If a client is found to be disconnected, it is marked as disconnected in the game it was participating in.
     */
    private void checkClientsConnection() {
        this.pingScheduler.scheduleAtFixedRate(() -> {
            synchronized (this.viewToPingHelper) {
                for (Map.Entry<VirtualView, PingHelper> entry : this.viewToPingHelper.entrySet()) {
                    PingHelper pingHelper = entry.getValue();

                    // If the player is connected evaluate the controls
                    if (pingHelper.isConnected()) {
                        pingHelper.incrementPing();

                        // Check if the client is disconnected
                        if (pingHelper.getFailedPings() > 3) {
                            // Get the game id
                            int gameID;
                            synchronized (this.clientToGame) {
                                gameID = this.clientToGame.get(pingHelper.getNickname());
                            }
                            // Get the actual game
                            GameInstance game;
                            synchronized (this.gameInstances) {
                                game = this.gameInstances.get(gameID);
                            }

                            // Disconnect the player from the game
                            if (game != null) {
                                pingHelper.setConnected(false);
                                try {
                                    game.disconnectClient(pingHelper.getNickname());
                                } catch (Exception e) {
                                    ServerLogger.error("ROUTER", String.valueOf(gameID), "An error occurred while disconnecting the client: " + e.getMessage());
                                }

                                ServerLogger.warn("ROUTER", String.valueOf(gameID), "Client " + pingHelper.getNickname() + " has been disconnected");
                            }
                        }
                    }
                }
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Reconnects a client to an ongoing game, notifying the clients connected to the game and updating the data structures.
     *
     * @param nickname the nickname of the client to reconnect
     * @param clientView the new VirtualView instance for the client
     * @throws Exception if an error occurs while processing the reconnection
     */
    public void reconnectClient(String nickname, VirtualView clientView) throws Exception {
        if (!this.getOfflineClients().contains(nickname)) {
            clientView.reportError(new ErrorAnswer("The given nickname does not appear in the disconnected clients list"));
            return;
        }

        int gameID;
        synchronized (this.clientToGame) {
            gameID = this.clientToGame.get(nickname);
        }

        if (gameID == -1) {
            clientView.reportError(new ErrorAnswer("The given nickname is not playing any game at the moment"));
            return;
        }

        GameInstance game;
        synchronized (this.gameInstances) {
            game = this.gameInstances.get(gameID);
        }

        if (game == null) {
            clientView.reportError(new ErrorAnswer("No games were found!"));
            return;
        }

        game.reconnectClient(nickname, clientView);

        // Get the old VirtualView to update the values
        VirtualView oldView;

        // Update the connectedClient virtualView
        synchronized (this.connectedClients) {
            oldView = this.connectedClients.get(nickname);
            this.connectedClients.put(nickname, clientView);
        }

        // Update the clientView associated with it's pingHelper
        synchronized (this.viewToPingHelper) {
            PingHelper pingHelper = viewToPingHelper.get(oldView);
            pingHelper.resetPings(); // Reset the pings

            this.viewToPingHelper.remove(oldView);
            this.viewToPingHelper.put(clientView, pingHelper);
        }
    }

    // TODO: MAKE THIS METHOD PUBLIC IN ORDER TO BE INVOKED BY THE GAME MODEL TO END THE GAME
    private void deleteGame(int gameID) {
        // Remove the reference to the gameInstance to be removed by the Garbage Collector
        this.gameInstances.put(gameID, null);

        // Get all the clients connected to the given game
        List<String> clientsToRemove = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : clientToGame.entrySet()) {
            if (entry.getValue() == gameID) {
                clientsToRemove.add(entry.getKey());
            }
        }

        // Remove the clients from the Objects
        for (String nickname : clientsToRemove) {
            VirtualView view = connectedClients.remove(nickname);
            if (view != null) {
                viewToPingHelper.remove(view);
            }
            clientToGame.remove(nickname);
        }
    }
}
