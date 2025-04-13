package it.polimi.ingsw.is25am28.RMI.Server;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRMI extends Remote, VirtualView {

    @Override
    public void updateView(StateDTO state) throws RemoteException;

    @Override
    public void updateState(StateDTO state) throws Exception;

    @Override
    public void reportError(String details) throws RemoteException;
}
