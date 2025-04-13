package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

/**
 * Interface that defines the methods that the controller calls to update the content on the clients
 * */

public interface VirtualView {

    // TODO: Think again about this implementation, i'm not sure it's correct --> it should be better to use the cmd pattern
    // TODO: Change in cmd pattern to have only one method execute(Command cmd)
    public void configureGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception;

    public void newPlayer(String playerNickname, PlayerColor playerColor) throws Exception;

    public void updateView(StateDTO state) throws Exception;

    public void updateState(StateDTO state) throws Exception;

    public void reportError(String details, StateDTO state) throws Exception;
}
