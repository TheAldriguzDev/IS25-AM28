package it.polimi.ingsw.is25am28.Network.RMI.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.is25am28.Network.VirtualServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;


/**
 * This interface specializes the VirtualServer interface because we could need some methods that are needed only for RMI
 * */
public interface VirtualServerRMI extends Remote, VirtualServer {

    /**
     * Method used to connect the clients to the server.
     *
     * @param client is needed to know which clients needs to be notified when there are the updates
     */
    void connectClient(VirtualViewRMI client, UUID uuid) throws Exception;

    public void sendMessage(Message message, UUID uuid) throws Exception;
}
