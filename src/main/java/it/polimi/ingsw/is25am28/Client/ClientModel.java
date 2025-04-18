package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Model.GameModelv2.State;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.VirtualView;


/**
 * This class represent the client-side model. It will contain all the data that are needed to handle the game from the
 * client perspective.
 * */
public class ClientModel {
    private String nickname;
    private PlayerColor playerColor;

    // private VirtualView client;

    // Represent the currentState of the game --> it matches the state of the server model
    private State currentState;

    // TODO: Board, otherPlayers, ship

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
