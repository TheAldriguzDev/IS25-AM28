package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer.ClientPlayer;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.*;

/**
 * This class represent the client-side model. It will contain all the data that are needed to handle the game from the
 * client perspective.
 * */
public class ClientModel {
    // Nickname of the client
    private String nickname;
    private int difficultyLevel;
    private ClientState currState;
    private TimerDTO timerDTO;

    // Map that stores the client nicknames with their ClientPlayer data structure
    private final Map<String, ClientPlayer> players;

    // TODO: ClientBoard - ClientShip - ClientComponent --> For ships and playerColor i would store them inside Maps to identify each user data

    public ClientModel() {
        this.players = new HashMap<>();
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
     * @return A list of all players by their nickname
     */
    public List<String> getAllPlayersNicknames() {
        return this.players.keySet().stream().toList();
    }

    /**
    * @return Returns the clientPlayers map(nickname, data)
    * */
    public Map<String, ClientPlayer> getAllClientPlayers() {
        return players;
    }

    /**
     * @param timerDTO The timerDTO containing all the data about the
     *                 last received timer event from the server-side
     *                 ShipConstructionState
     */
    public void setTimerDTO(TimerDTO timerDTO) {
        this.timerDTO = timerDTO;
    }

    /**
     * @return The earliest timerDTO arrived to this client
     */
    public TimerDTO getTimerDTO() {
        return this.timerDTO;
    }

    /**
     * Updates the current clientPlayer stats (only the credits, the other stats are updated by the clientShip
     * (Batteries, lostComponents, dropped/taken resources, removedLifeforms) or clientBoard
     * (Cursor, eliminatedPlayers)) (if needed)
     * */
    public void updatePlayers(CardStateJSON cardStateJSON) {
        if (cardStateJSON.getNeedsUpdatedCredits()) {
            for (String playerNickname : cardStateJSON.getUpdatedCredits().keySet()) {
                this.players.get(playerNickname).setCredits(cardStateJSON.getUpdatedCredits().get(playerNickname));
            }
        }
    }
}
