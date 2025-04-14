package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.Message;

/**
 * Interface that defines the methods that the controller calls to update the content on the clients
 * */

public interface VirtualView {

    public void sendMessage(Message message) throws Exception;

    public void updateView(StateDTO state) throws Exception;

    public void updateState(StateDTO state) throws Exception;

    public void reportError(String details, StateDTO state) throws Exception;
}
