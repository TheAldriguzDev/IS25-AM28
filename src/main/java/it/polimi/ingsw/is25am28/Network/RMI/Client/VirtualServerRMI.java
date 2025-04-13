package it.polimi.ingsw.is25am28.Network.RMI.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
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
     * @param client is needed to know which clients needs to be notified when there are the updates
     * */
    void connectClient(VirtualViewRMI client, UUID uuid) throws Exception;

    public void gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, UUID uuid) throws Exception;

    public void addNewPlayer(String nickname, PlayerColor playerColor, UUID uuid) throws Exception;

    public void selectTile(String player, Integer i, Integer j, UUID uuid) throws RemoteException;

    public void deselectTile(String player, Integer i, Integer j, UUID uuid) throws RemoteException;

    public void playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles, UUID uuid) throws RemoteException;

    public void flipTimer(String player, UUID uuid) throws RemoteException;

    public void fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove, UUID uuid) throws RemoteException;

    public void populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd, UUID uuid) throws RemoteException;

    public void playCard(ActionJSON action, UUID uuid) throws RemoteException;
}
