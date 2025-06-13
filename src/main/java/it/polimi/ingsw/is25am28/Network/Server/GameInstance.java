package it.polimi.ingsw.is25am28.Network.Server;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an instance of a game, managing player connections, game state, and interactions
 * between the server and clients. Each instance corresponds to a single game configuration
 * and facilitates gameplay for multiple connected players.
 */

public class GameInstance {
    private final GameController controller;
    private final Map<String, VirtualView> connectedClients;
    private final List<VirtualView> disconnectedClients;
    private final Object virtualClientLock;

    // This flag will indicate if the game accept new clients connection
    private boolean canBeJoined;

    private final int totalPlayers;
    private final int level;
    private int currentPlayers;

    private final Queue queueHandler;

    /**
     * Constructs a new game instance of the game with the specified parameters.
     *
     * @param playerNickname the nickname of the player who initiates the game
     * @param playerColor the color chosen by the player
     * @param gameLevel the level of the game to be played
     * @param totalPlayers the total number of players who can join the game
     * @param virtualClient the client's virtual view (connection protocol) used to interact with the game
     * @throws Exception if an error occurs during the game configuration
     */
    public GameInstance(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, VirtualView virtualClient) throws Exception {
        this.controller = new GameController();
        this.connectedClients = new HashMap<>();
        this.disconnectedClients = new ArrayList<>();
        this.virtualClientLock = new Object();
        this.canBeJoined = false;
        this.totalPlayers = totalPlayers;
        this.level = gameLevel;
        this.currentPlayers = 0;

        this.queueHandler = new Queue();
        new Thread(queueHandler).start();

        this.gameConfig(playerNickname, playerColor, gameLevel, totalPlayers, virtualClient);
    }

    /**
     * Retrieves the total number of players who can join the game instance.
     *
     * @return the total number of players allowed in the game instance
     */
    public int getTotalPlayers() {
        return totalPlayers;
    }

    /**
     * Retrieves the current level of the game instance.
     *
     * @return the level of the game
     */
    public int getLevel() {
        return level;
    }

    /**
     * Determines if the game instance is currently open for new players to join.
     *
     * @return true if the game instance is available for players to join, false otherwise
     */
    public boolean canBeJoined() {
        return this.canBeJoined && !this.connectedClients.isEmpty();
    }

    /**
     * Retrieves a list of colors currently available for selection in the game.
     *
     * @return a list of strings representing the available colors in the game
     */
    public List<String> getAvailableColors() {
        return this.controller.getAvailableColors();
    }

    /**
     * Retrieves the current number of players who have joined the game instance.
     *
     * @return the number of players currently in the game instance
     */
    public int getCurrentPlayers() {
        return this.currentPlayers;
    }

    /**
     * Configures the game instance based on the provided information. This includes initializing game settings,
     * broadcasting the game state to connected clients, and allowing new connections.
     *
     * @param playerNickname the nickname of the player initiating the game configuration
     * @param playerColor the color chosen by the player
     * @param gameLevel the level of the game to be played
     * @param totalPlayers the total number of players to participate in the game
     * @param virtualClient the virtual client view used for interaction with the server
     * @throws Exception if an error occurs during the game configuration process
     */
    private void gameConfig(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, VirtualView virtualClient) throws Exception {
        StateDTO state = this.controller.gameConfig(playerNickname, playerColor, gameLevel, totalPlayers, virtualClient);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setNextState(state);

        synchronized (this.virtualClientLock) {
            this.connectedClients.put(playerNickname, virtualClient);

            // Broadcast the state to all the connected clients (should be only to the leader)
            this.broadCastUpdate(answer);
        }

        // Set the game as accepting new connection
        this.canBeJoined = true;
        this.currentPlayers++;
    }

    /**
     * Adds a new player to the game instance. This method updates the game state
     * to include the new player, broadcasts the updated game state to all connected
     * clients, and manages the connection of the new client's virtual view.
     *
     * @param playerNickname the nickname of the player joining the game
     * @param playerColor the color chosen by the player
     * @param virtualClient the client's virtual view (connection protocol) used to interact with the game
     * @throws Exception if an error occurs while adding the new player or broadcasting updates
     */
    public void addNewPlayer(String playerNickname, PlayerColor playerColor, VirtualView virtualClient) throws Exception {
        List<StateDTO> states = this.controller.addNewPlayer(playerNickname, playerColor, virtualClient);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(states.getFirst());

        // The game has been started so it won't accept new connections
        if (states.size() > 1) {
            this.canBeJoined = false;
            answer.setNextState(states.get(1));
        }

        synchronized (this.virtualClientLock) {
            this.connectedClients.put(playerNickname, virtualClient);

            // Broadcast the state to the clients
            this.broadCastUpdate(answer);
        }
        this.currentPlayers++;
    }

    /**
     * Selects a tile for the specified player and broadcasts the updated game state to all clients.
     *
     * @param playerNickname the nickname of the player selecting the tile
     * @param id the unique identifier of the tile to be selected
     * @throws Exception if an error occurs during the tile selection process
     */
    public void selectTile(String playerNickname, int id) throws Exception {
        ConstructionComponentDTO state = this.controller.selectTile(playerNickname, id);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Deselects a specific tile for a player and broadcasts the resulting update to all connected clients.
     *
     * @param playerNickname the nickname of the player who is deselecting the tile
     * @param id the unique identifier of the tile to be deselected
     * @throws Exception if an error occurs during the deselect operation
     */
    public void deselectTile(String playerNickname, int id) throws Exception {
        ConstructionComponentDTO state = this.controller.deselectTile(playerNickname, id);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Reserves a tile for the specified player and broadcasts the updated game state
     * to all connected clients.
     *
     * @param playerNickname the nickname of the player who wants to reserve the tile
     * @param id the unique identifier of the tile to be reserved
     * @throws Exception if an error occurs during the reservation process
     */
    public void reserveTile(String playerNickname, int id) throws Exception {
        ReservedComponentDTO state = this.controller.reserveTile(playerNickname, id);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Executes the fast shipping command for the specified player and broadcasts the updated game state
     * to all connected clients.
     *
     * @param playerNickname the nickname of the player for whom the fast shipping command is processed
     * @throws Exception if an error occurs during the fast shipping process or updates broadcasting
     */
    public void fastShip(String playerNickname) throws Exception {
        List<StateDTO> states = this.controller.fastShip(playerNickname);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(states.getFirst());

        if (states.size() > 1) {
            answer.setNextState(states.get(1));
        }

        this.broadCastUpdate(answer);
    }

    /**
     * Places a tile for the specified player at the given position with the specified rotation and broadcasts the
     * updated game state to all connected clients.
     *
     * @param player the nickname of the player placing the tile
     * @param componentID the unique identifier of the tile being placed
     * @param i the row index where the tile is placed
     * @param j the column index where the tile is placed
     * @param rotation the rotation of the tile being placed
     * @throws Exception if an error occurs during the tile placement or broadcasting update
     */
    public void placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception {
        PlacedComponentDTO state = this.controller.placeTile(player, componentID, i, j, rotation);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Handles the event when a player completes the ship during the ship construction phase.
     * Updates the game state based on the player's action, broadcasts the updated state to all connected clients.
     *
     * @param player the nickname of the player who finished the ship construction phase.
     * @param reservedTiles the number of tiles reserved and not used in the ship construction phase.
     * @throws Exception if an error occurs while updating the game state or broadcasting the result
     */
    public void playerEndedSendShip(String player, int reservedTiles) throws Exception {
        List<StateDTO> states = this.controller.playerEndedSendShip(player, reservedTiles);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(states.getFirst());

        if (states.size() > 1) {
            answer.setNextState(states.get(1));
        }

        this.broadCastUpdate(answer);
    }

    /**
     * Allows a player to select or deselect a specific subdeck in the game. This action updates the game state
     * to reflect the change and broadcasts the updated state to all connected clients.
     *
     * @param player the nickname of the player performing the action
     * @param selectedDeck the unique identifier of the subdeck to be selected or deselected
     * @param isSelectAction indicates whether the action is a selection (true) or deselection (false)
     * @throws Exception if an error occurs during the process of updating the game state or broadcasting the result
     */
    public void selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws Exception {
        ConstructionDeckDTO state = this.controller.selectDeselectSubdeck(player, selectedDeck, isSelectAction);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Flips the hourglass in the ship construction phase and broadcasts ù
     * the updated game state to all connected clients.
     *
     * @param playerNickname the nickname of the player whose timer state will be flipped
     * @throws Exception if an error occurs during the timer state toggle or while broadcasting the update
     */
    public void flipTimer(String playerNickname) throws Exception {
        TimerDTO state = this.controller.flipTimer(playerNickname);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Removes a ship component for the given player. This method updates the ship's and broadcasts
     * the changes to the clients.
     *
     * @param player the nickname of the player attempting to fix the ship
     * @param i the row coordinate where the ship is being fixed
     * @param j the column coordinate where the ship is being fixed
     * @throws Exception if an error occurs during the operation or
     *         broadcasting the update
     */
    public void fixShip(String player, Integer i, Integer j) throws Exception {
        List<StateDTO> states = this.controller.fixShip(player, i, j);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(states.getFirst());

        if (states.size() > 1) {
            answer.setNextState(states.get(1));
        }

        this.broadCastUpdate(answer);
    }

    /**
     * Populates the ship for the specified player with the given lifeform.
     * This method updates the game state by adding components to the player's ship
     * and broadcasts the update to all connected clients.
     *
     * @param player the nickname of the player for whom the ship is being populated
     * @param lifeFormToAdd the lifeform component to be added to the player's ship
     * @throws Exception if an error occurs during the update or broadcasting of the game state
     */
    public void populateShip(String player, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception {
        List<StateDTO> states = this.controller.populateShip(player, lifeFormToAdd);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(states.getFirst());

        if (states.size() > 1) {
            answer.setNextState(states.get(1));
        }

        this.broadCastUpdate(answer);
    }

    /**
     * Executes the action of playing a card for a specific player. Updates the game state
     * based on the action performed and broadcasts the result to all connected clients.
     *
     * @param player the nickname of the player performing the action
     * @param action the action details in JSON format with all necessary information to play the card
     * @throws Exception if an error occurs while executing the action or broadcasting the game state update
     */
    public void playCard(String player, ActionJSON action) throws Exception {
        List<StateDTO> states = this.controller.playCard(action);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(states.getFirst());

        if (states.size() > 1) {
            answer.setNextState(states.get(1));
        }

        this.broadCastUpdate(answer);
    }

    /**
     * Broadcasts an update to all connected clients except those marked as disconnected.
     *
     * @param answer The answer object containing the update to be sent to the clients.
     * @throws Exception if an error occurs while sending the update.
     */
    private void broadCastUpdate(Answer answer) throws Exception {
        synchronized (this.virtualClientLock) {
            // Broadcast the state to the clients

            for (VirtualView client : this.connectedClients.values()) {
                if (!this.disconnectedClients.contains(client)) {
                    sendUpdateWithRetries(client, answer, queueHandler, 0, 3, 2500);
                }
            }
        }
    }

    /**
     * Sends an update to the given {@link VirtualView} with retry logic. If the first attempt fails,
     * the method retries the operation up to the specified maximum number of retries, adding a delay
     * between each attempt. If all retries fail, the failure is logged.
     *
     * @param view           the virtual view where the update is intended to be sent
     * @param answer         the answer object containing the update details
     * @param queueHandler   the queue handler used for task execution
     * @param currentAttempt the current attempt number for sending the update
     * @param maxRetries     the maximum number of retry attempts
     * @param delay          the delay in milliseconds between each retry attempt
     */
    public static void sendUpdateWithRetries(VirtualView view, Answer answer, Queue queueHandler, int currentAttempt, int maxRetries, long delay) {
        queueHandler.enqueue(() -> {
            try {
                view.updateState(answer);
            } catch (Exception e) {
                if (currentAttempt + 1 < maxRetries) {
                    CompletableFuture.delayedExecutor(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
                            .execute(() -> sendUpdateWithRetries(view, answer, queueHandler, currentAttempt + 1, maxRetries, delay));
                } else {
                    ServerLogger.error("NETWORK", "Failed to send the message after " + maxRetries + " attempts");
                }
            }
        });
    }

    // ========== PING UTILITY ========== //
    /**
     * Retrieves a list of clients that are currently offline.
     *
     * @return a list of strings representing the IDs or names of disconnected clients.
     */
    public List<String> getOfflineClients() {
        return this.controller.getDisconnectedPlayers();
    }

    /**
     * Disconnects a client identified by their player nickname. It updates the game state and notifies
     * all clients about the disconnection.
     *
     * @param playerNickname the nickname of the player to disconnect
     * @throws Exception if an error occurs during the disconnection process
     */
    public void disconnectClient(String playerNickname) throws Exception {
        List<StateDTO> state = this.controller.disconnectClient(playerNickname);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state.getFirst());

        // If the game switched to the InsufficientPlayerState
        if (state.size() > 1) {
            answer.setNextState(state.get(1));
        }

        synchronized (this.virtualClientLock) {
            this.disconnectedClients.add(this.connectedClients.get(playerNickname));
            this.connectedClients.remove(playerNickname);
        }

        this.broadCastUpdate(answer);
    }

    /**
     * Reconnects a client to the server by updating its VirtualView and resuming the game state.
     *
     * @param playerNickname the nickname of the player who is reconnecting
     * @param virtualClient the new VirtualView instance for the reconnecting client
     * @throws Exception if an error occurs while updating the game state or communication
     */
    public void reconnectClient(String playerNickname, VirtualView virtualClient) throws Exception {
        // Update the client VirtualView
        synchronized (this.virtualClientLock) {
            this.disconnectedClients.remove(this.connectedClients.get(playerNickname));
            this.connectedClients.put(playerNickname, virtualClient);
        }

        // Update the client with the state to resume the game
        List<StateDTO> reconnectState = this.controller.reconnectClient(playerNickname, virtualClient);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(reconnectState.getFirst());

        if (reconnectState.size() > 1) {
            answer.setNextState(reconnectState.get(1));
        }

        this.broadCastUpdate(answer);
    }
}
