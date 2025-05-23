package it.polimi.ingsw.is25am28.Network.Server;

import it.polimi.ingsw.is25am28.Client.UI.TUI.Utils.ANSIColors;
import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ReconnectDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionDeckDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlacedComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This class is a helper to control the Game. It will handle all the communications between the players and the GameModel.
 * */

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

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public int getLevel() {
        return level;
    }

    /**
     * @return true if the game has been configured and is waiting for players connections
     * */
    public boolean canBeJoined() {
        return this.canBeJoined;
    }

    public List<String> getAvailableColors() {
        return this.controller.getAvailableColors();
    }

    public int getCurrentPlayers() {
        return this.currentPlayers;
    }

    /**
     * gameConfig(...) will execute the command on the GameModel. Once it has been configured it will update
     * the connected clients (the leader) and it will open the lobby to wait for more players.
     * */
    public void gameConfig(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, VirtualView virtualClient) throws Exception {
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
     * addNewPlayer(...) will execute the command on the GameModel. Once it reaches the game maxPlayer it will close the lobby to
     * do no accept new clients connections.
     * */
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

    public void selectTile(String playerNickname, int id) throws Exception {
        ConstructionComponentDTO state = this.controller.selectTile(playerNickname, id);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    public void deselectTile(String playerNickname, int id) throws Exception {
        ConstructionComponentDTO state = this.controller.deselectTile(playerNickname, id);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Command used to place a tile to the given player ship
     * */
    public void placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception {
        PlacedComponentDTO state = this.controller.placeTile(player, componentID, i, j, rotation);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Command used when a player decides to end his ship construction or when the time is over
     * */
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

    public void selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws Exception {
        ConstructionDeckDTO state = this.controller.selectDeselectSubdeck(player, selectedDeck, isSelectAction);

        Answer answer = new Answer()
                .setPlayerNickname(player)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    /**
     * Command used by the given player to flip the timer
     * */
    public void flipTimer(String playerNickname) throws Exception {
        TimerDTO state = this.controller.flipTimer(playerNickname);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

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
     * Method used to broadcast any server Answer to the clients
     * */
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
     * @return the nicknames list of the disconnected client for this gameInstance
     * */
    public List<String> getOfflineClients() {
        return this.controller.getDisconnectedPlayers();
    }

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
        }

        this.broadCastUpdate(answer);
    }

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
