package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class represent the client-side model. It will contain all the data that are needed to handle the game from the
 * client perspective.
 * */
public class ClientModel {
    // Nickname of the client
    private String nickname;
    // Game Level
    private int difficultyLevel;

    // TODO REMOVE FROM HERE --> PUT IN THE STATE
    private Map<String, Boolean> playersFinishedBuildingShip;

    // Map that stores the client nicknames with their ClientPlayer data structure
    private final Map<String, ClientPlayer> players;

    // Current state of the game
    private ClientState currState;

    // TODO: ClientBoard - ClientShip - ClientComponent --> For ships and playerColor i would store them inside Maps to identify each user data

    public ClientModel() {
        players = new HashMap<>();
        playersFinishedBuildingShip = new HashMap<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getDifficultyLevel() {
        return this.difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * @return the current client state
     * */
    public ClientState getState() {
        return this.currState;
    }

    /**
     * Sets the current state of the model
     * */
    public void setState(ClientState state) {
        this.currState = state;
    }

    /**
     * Add the given player to the game
     * */
    public void addNewPlayer(String nickname, PlayerColor color) {
        synchronized (this.players) {
            if (!this.players.containsKey(nickname)) {
                this.players.put(nickname, new ClientPlayer(nickname, color, this.difficultyLevel));
            }
        }

        this.playersFinishedBuildingShip.put(nickname, false);
    }

    /**
     * @return The ship belonging to the given player
     */
    public Optional<ClientShip> getShipOfPlayer(String playerNickname) {
        synchronized (this.players) {
            return Optional.ofNullable(this.players.get(playerNickname)).map(ClientPlayer::getShip);
        }
    }

    /**
     * Flags all players as "has NOT finished building his ship"
     */
    public void initPlayersFinishedBuildingShip() {
        if (this.playersFinishedBuildingShip == null) {
            this.playersFinishedBuildingShip = new HashMap<>();
        }

        Set<String> players = this.players.keySet();

        for (String player : players) {
            this.playersFinishedBuildingShip.put(player, false);
        }
    }

    /**
     * @return TRUE if the current player was marked as "has finished building his ship", FALSE otherwise.
     */
    public boolean getPlayerFinishedBuildingShip(String playerNickname) throws IllegalArgumentException {
        if (playerNickname != null && !playerNickname.isEmpty()) {
            if (this.playersFinishedBuildingShip != null) {
                return this.playersFinishedBuildingShip.getOrDefault(playerNickname, null);
            }
            else {
                throw new IllegalArgumentException("ERROR: \"" + playerNickname + "\" is not in the map (init failed)");
            }
        }
        else {
            throw new IllegalArgumentException("ERROR: Given playerNickname is either empty or null");
        }
    }


    /**
    * @return Returns the clientPlayers map(nickname, data)
    * */
    public Map<String, ClientPlayer> getAllClientPlayers() {
        return players;
    }
}
