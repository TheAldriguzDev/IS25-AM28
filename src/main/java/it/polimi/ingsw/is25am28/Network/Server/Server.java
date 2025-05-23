package it.polimi.ingsw.is25am28.Network.Server;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.GameInfoDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.RMI.Server.RMIServer;
import it.polimi.ingsw.is25am28.Network.Socket.Server.TCPServer;
import it.polimi.ingsw.is25am28.Network.VirtualView;
import it.polimi.ingsw.is25am28.Utils.ValidateIP;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * This class initially spawns the networks server to listen for client requests.
 * Then it will be used to route the clients requests to the game they are playing.
 * */

public class Server {

    // Server constants
    public static final String RMIServerName = "GameRMIServer";
    public static final int RMIServerPort = 7777;
    public static final int TCPServerPort = 8888;

    private final RMIServer rmiServer;
    private final TCPServer tcpServer;


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

    public Server() throws Exception {
        // Create the RMIServer and the TCPServer
        Scanner scanner = new Scanner(System.in);
        String ipAddress;

        while (true) {
            System.out.print("Enter a valid IPv4 address: ");
            ipAddress = scanner.nextLine().trim();

            if (ValidateIP.validateIPAddress(ipAddress)) {
                break;
            } else {
                System.out.println(ANSIColors.RED + "Invalid IP address. Try again." + ANSIColors.RESET);
            }
        }

        this.tcpServer = new TCPServer(ipAddress, Server.TCPServerPort, this);
        this.rmiServer = new RMIServer(Server.RMIServerName, Server.RMIServerPort, this);

        this.gameInstances = new HashMap<>();
        this.connectedClients = new HashMap<>();
        this.clientToGame = new HashMap<>();
        this.viewToPingHelper = new HashMap<>();
        this.pingScheduler = new ScheduledThreadPoolExecutor(1);

        // Starts to check for client disconnections
        this.checkClientsConnection();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
    }

    /**
     * The serve handles the client connection. First of all it retrieves all the games that has been configured.
     * Then it will update the client with this information in order to let him decide if he wants to:
     * 1. Join an active Game
     * 2. Create a new Game
     * 3. Reconnect to a Game (by sending the nickname)
     * */
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

    // TODO: Method to refresh the available games

    /**
     * @return the filtered Map with the games that can be joined from the players
     * */
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

    public void refreshGames() {

    }

    /**
     * This method will be used to create a new game when a Player request it.
     * It also the playerNickname and information to the
     * */
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
     * This method will be used to let a player join the given game by its ID
     * */
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

    public void selectTile(String playerNickname, int id) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.selectTile(playerNickname, id);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " selected the tile with ID=" + id);
        }
    }

    public void deselectTile(String playerNickname, int id) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.deselectTile(playerNickname, id);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " deselected the tile with ID=" + id);
        }
    }

    public void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.placeTile(playerNickname, componentID, i, j, rotation);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " placed the ("+ componentID +") tile at (" + i + "," + j + ") tile");
        }
    }

    public void playerEndedSendShip(String playerNickname, int reservedTiles) throws Exception {
        synchronized (this.gameInstances) {
            // Get the game where the player is playing
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.playerEndedSendShip(playerNickname, reservedTiles);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " ended his ship with " + reservedTiles + " reserved tile(s)");
        }
    }

    public void flipTimer(String playerNickname) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.flipTimer(playerNickname);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " flipped the timer");
        }
    }

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

    public void fixShip(String playerNickname, Integer i, Integer j) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.fixShip(playerNickname, i, j);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " removed a component from his ship (" + i + "," + j + ")" );
        }
    }

    public void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            game.populateShip(playerNickname, lifeFormToAdd);
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " populated a component of his ship (" + lifeFormToAdd.getI() + "," + lifeFormToAdd.getJ() + ") with " + lifeFormToAdd.getItem().toString());
        }
    }

    public void playCard(String playerNickname, ActionJSON action) throws Exception {
        synchronized (this.gameInstances) {
            int gameID = this.clientToGame.get(playerNickname);
            GameInstance game = this.gameInstances.get(gameID);

            try {
                game.playCard(playerNickname, action);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            ServerLogger.info("ROUTER", String.valueOf(gameID), playerNickname + " played the card");
        }
    }

    // TODO: For all the interaction with the user we need to pass his nickname to get the associated game and then route the
    //  request

    // TODO: Implements the ping utility methods --> we will only have one thread pinging the clients
    //  and one thread checking the ping data results
    // ========== PING METHOD ========== //

    /**
     * @return the list of offline clients from all the games
     * */
    private List<String> getOfflineClients() {
        List<String> offlineClients = new ArrayList<>();
        synchronized (this.gameInstances) {
            offlineClients = this.gameInstances.values()
                    .stream()
                    .flatMap(g -> g.getOfflineClients().stream())
                    .toList();
        }

        return offlineClients;
    }

    /**
     * Method invoked by the client when they send a successful ping to the server
     * */
    public void clientPing(VirtualView clientView) throws Exception {
        synchronized (this.viewToPingHelper) {
            PingHelper pingHelper = viewToPingHelper.get(clientView);
            if (pingHelper != null) {
                pingHelper.resetPings();
            }
        }
    }

    /**
     * This method will check every 5 seconds if the clients are still connected. Otherwise, it will disconnect them from
     * the game where they are playing.
     * */
    private void checkClientsConnection() {
        this.pingScheduler.scheduleAtFixedRate(() -> {
            synchronized (this.viewToPingHelper) {
                for (Map.Entry<VirtualView, PingHelper> entry : this.viewToPingHelper.entrySet()) {
                    PingHelper pingHelper = entry.getValue();

                    // If the player is connected evaluate the controls
                    if (pingHelper.isConnected()) {
                        pingHelper.incrementPing();

                        // Check if the client is disconnected
                        if (pingHelper.getFailedPings() > 2) { // TODO: adjust right pings number
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
     * This method is used to reconnect the given client to his game. It will also modify all the needed values to continue the game
     * */
    public void reconnectClient(String nickname, VirtualView clientView) throws Exception {
        if (!this.getOfflineClients().contains(nickname)) {
            clientView.reportError(new ErrorAnswer("The given nickname does not appear in the disconnected clients list"));
            return;
        }

        int gameID = -1;
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
        VirtualView oldView = null;

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
}
