package it.polimi.ingsw.is25am28.Network.Server;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a helper to control the Game. It will handle all the communications between the players and the GameModel.
 * */

public class GameInstance {
    private final GameController controller;

    private final Map<String, VirtualView> connectedClients;

    // This flag will indicate if the game has been configured by the leader and is open to new clients connections
    private boolean hasBeenConfigured;

    // This flag will indicate if the game accept new clients connection
    private boolean canBeJoined;

    private final int totalPlayers;
    private final int level;
    private int currentPlayers;

    public GameInstance(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, VirtualView virtualClient) throws Exception {
        this.controller = new GameController();
        this.connectedClients = new HashMap<>();
        this.hasBeenConfigured = false;
        this.canBeJoined = false;
        this.totalPlayers = totalPlayers;
        this.level = gameLevel;
        this.currentPlayers = 0;

        this.gameConfig(playerNickname, playerColor, gameLevel, totalPlayers, virtualClient);
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public int getLevel() {
        return level;
    }

    /**
     * @return true if the game has been configured
     * */
    public boolean hasBeenConfigured() {
        return this.hasBeenConfigured;
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
        StateDTO state = this.controller.gameConfig(playerNickname, playerColor, gameLevel, totalPlayers);

        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, virtualClient);

            // Broadcast the state to all the connected clients (should be only to the leader)
            for (VirtualView client : this.connectedClients.values()) {
                client.updateState(state);
            }
        }

        // Set the game as accepting new connection
        this.hasBeenConfigured = true;
        this.canBeJoined = true;
        this.currentPlayers++;
    }

    /**
     * addNewPlayer(...) will execute the command on the GameModel. Once it reaches the game maxPlayer it will close the lobby to
     * do no accept new clients connections.
     * */
    public void addNewPlayer(String playerNickname, PlayerColor playerColor, VirtualView virtualClient) throws Exception {
        List<StateDTO> states = this.controller.addNewPlayer(playerNickname, playerColor);

        // The game has been started so it won't accept new connections
        if (states.size() > 1) {
            this.canBeJoined = false;
        }

        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, virtualClient);

            // Broadcast the state to the clients
            for (VirtualView client : this.connectedClients.values()) {
                for (StateDTO state : states) {
                    client.updateState(state);
                }
            }
        }
        this.currentPlayers++;
    }
}
