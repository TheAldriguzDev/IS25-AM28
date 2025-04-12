package it.polimi.ingsw.is25am28.RMI.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Model.Exceptions.FixNotRequiredError;
import it.polimi.ingsw.is25am28.Model.Exceptions.SelectedConcurrencyException;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.is25am28.VirtualServer;
import it.polimi.ingsw.is25am28.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


/**
 * This interface specializes the VirtualServer interface because we could need some methods that are needed only for RMI
 * */
public interface VirtualServerRMI extends Remote, VirtualServer {

    /**
     * Method used to connect the clients to the server.
     * @param client is needed to know which clients needs to be notified when there are the updates
     * */
    void connectClient(VirtualViewRMI client) throws RemoteException;

    @Override
    public StateJSON gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers) throws RemoteException;

    public List<StateJSON> addNewPlayer(String nickname, PlayerColor playerColor) throws RemoteException;

    public ConstructionComponentDTO selectTile(String player, Integer i, Integer j) throws RemoteException;

    public ConstructionComponentDTO deselectTile(String player, Integer i, Integer j) throws RemoteException;

    public List<StateJSON> playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles) throws RemoteException;

    public TimerDTO flipTimer(String player) throws RemoteException;

    public List<StateJSON> fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove) throws RemoteException;

    public List<StateJSON> populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd) throws RemoteException;

    public List<StateJSON> playCard(ActionJSON action) throws RemoteException;
}
