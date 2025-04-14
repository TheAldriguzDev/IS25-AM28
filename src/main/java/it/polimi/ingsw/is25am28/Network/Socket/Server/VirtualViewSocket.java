package it.polimi.ingsw.is25am28.Network.Socket.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.rmi.RemoteException;

/**
 * This interface specializes the VirtualView interface for the Socket technology. This is needed to make the
 * network abstract about the technology that we use (Socket / RMI)
 * */

public interface VirtualViewSocket extends VirtualView {
    @Override
    public void updateView(StateDTO state) throws JsonProcessingException;

    @Override
    public void updateState(StateDTO state) throws JsonProcessingException;

    @Override
    public void reportError(String details, StateDTO state) throws JsonProcessingException;
}
