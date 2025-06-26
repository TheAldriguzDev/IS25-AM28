package it.polimi.ingsw.is25am28.Network.RMI.Server;

import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRMI extends Remote, VirtualView {
    @Override
    void pong() throws Exception;

    @Override
    void updateState(Answer answer) throws Exception;

    @Override
    void reportError(ErrorAnswer error) throws RemoteException;
}
