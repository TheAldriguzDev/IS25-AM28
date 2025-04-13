package it.polimi.ingsw.is25am28.Network.RMI.Server;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRMI extends Remote, VirtualView {

    @Override
    public void updateView(StateDTO state) throws RemoteException;

    @Override
    public void updateState(StateDTO state) throws Exception;

    @Override
    public void reportError(String details, StateDTO state) throws RemoteException;
}
