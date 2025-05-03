package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.Message;

/**
 * Interface that defines the methods that the controller calls to update the content on the clients
 * */

public interface VirtualView {

    void sendMessage(Message message) throws Exception;

    void updateView(StateDTO state) throws Exception;

    void updateState(Answer answer) throws Exception;

    void reportError(ErrorAnswer error) throws Exception;
}
