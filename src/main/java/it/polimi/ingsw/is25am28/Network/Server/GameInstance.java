package it.polimi.ingsw.is25am28.Network.Server;

import it.polimi.ingsw.is25am28.Controller.GameController;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ReconnectDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.PlacedComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
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

    // This flag will indicate if the game accept new clients connection
    private boolean canBeJoined;

    private final int totalPlayers;
    private final int level;
    private int currentPlayers;

    public GameInstance(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, VirtualView virtualClient) throws Exception {
        this.controller = new GameController();
        this.connectedClients = new HashMap<>();
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

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setNextState(state);

        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, virtualClient);

            // Broadcast the state to all the connected clients (should be only to the leader)
            for (VirtualView client : this.connectedClients.values()) {
                client.updateState(answer);
            }
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
        List<StateDTO> states = this.controller.addNewPlayer(playerNickname, playerColor);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(states.getFirst());

        // The game has been started so it won't accept new connections
        if (states.size() > 1) {
            this.canBeJoined = false;
            answer.setNextState(states.get(1));
        }

        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, virtualClient);

            // Broadcast the state to the clients
            for (VirtualView client : this.connectedClients.values()) {
                client.updateState(answer);
            }
        }
        this.currentPlayers++;
    }

    public void selectTile(String playerNickname, int i, int j) throws Exception {
        ConstructionComponentDTO state = this.controller.selectTile(playerNickname, i, j);

        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(state);

        this.broadCastUpdate(answer);
    }

    public void deselectTile(String playerNickname, int i, int j) throws Exception {
        ConstructionComponentDTO state = this.controller.deselectTile(playerNickname, i, j);

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

    /**
     * Method used to broadcast any server Answer to the clients
     * */
    private void broadCastUpdate(Answer answer) throws Exception {
        synchronized (this.connectedClients) {
            // Broadcast the state to the clients
            for (VirtualView client : this.connectedClients.values()) {
                client.updateState(answer);
            }
        }
    }

    // ========== PING UTILITY ========== //
    /**
     * @return the nicknames list of the disconnected client for this gameInstance
     * */
    public List<String> getOfflineClients() {
        return this.controller.getDisconnectedPlayers();
    }

    public void disconnectClient(String playerNickname) {
        this.controller.disconnectClient(playerNickname);
    }

    public void reconnectClient(String playerNickname, VirtualView virtualClient) throws Exception {
        // Update the client VirtualView
        synchronized (this.connectedClients) {
            this.connectedClients.put(playerNickname, virtualClient);
        }

        // Update the client with the state to resume the game
        ReconnectDTO reconnectState = this.controller.reconnectClient(playerNickname);
        Answer answer = new Answer()
                .setPlayerNickname(playerNickname)
                .setState(reconnectState);

        virtualClient.updateState(answer);
    }
}
