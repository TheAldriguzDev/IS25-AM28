package it.polimi.ingsw.is25am28.Client.ClientModel;

import it.polimi.ingsw.is25am28.Model.GameModelv2.State;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;


/**
 * This class represent the client-side model. It will contain all the data that are needed to handle the game from the
 * client perspective.
 * */
public class ClientModel {
    private String nickname;
    private PlayerColor playerColor;

    // TODO: ClientBoard - ClientShip - ClientComponent --> For ships and playerColor i would store them inside Maps to identify each user data

    // Represent the currentState of the game --> it matches the state of the server model
    private State currentState;

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
}
