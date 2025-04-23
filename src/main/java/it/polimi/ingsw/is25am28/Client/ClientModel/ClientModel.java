package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent.ClientComponent;
import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

/**
 * This class represent the client-side model. It will contain all the data that are needed to handle the game from the
 * client perspective.
 * */
public class ClientModel {
    private String nickname;
    private PlayerColor playerColor;
    private ClientState currState;
    private ClientShip ship;
    private int difficultyLevel;
    // TODO: ClientBoard - ClientShip - ClientComponent --> For ships and playerColor i would store them inside Maps to identify each user data

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

    public String getNickname() {
        return nickname;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getDifficultyLevel() {
        return this.difficultyLevel;
    }

    /**
     * Stores inside the map the given ship of the given player
     */
    public void setShip(ClientShip ship) {
        this.ship = ship;
    }

    /**
     * @return The ship belonging to the given player
     */
    public ClientShip getShip() {
        return this.ship;
    }
}
