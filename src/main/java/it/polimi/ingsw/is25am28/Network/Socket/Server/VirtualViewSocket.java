package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

/**
 * This interface specializes the VirtualView interface for the Socket technology. This is needed to make the
 * network abstract about the technology that we use (Socket / RMI)
 * */

public interface VirtualViewSocket extends VirtualView {
    @Override
    void updateState(Answer answer) throws JsonProcessingException;

    @Override
    void reportError(ErrorAnswer error) throws JsonProcessingException;
}
