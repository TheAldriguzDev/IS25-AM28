package it.polimi.ingsw.is25am28.Network.Socket.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.VirtualServer;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * This interface specializes the VirtualView interface for the Socket technology
 * */

public interface VirtualServerSocket extends VirtualServer {

    @Override
    public void gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, UUID uuid);

    @Override
    public void addNewPlayer(String nickname, PlayerColor playerColor, UUID uuid);

    @Override
    public void selectTile(String player, Integer i, Integer j, UUID uuid);

    @Override
    public void deselectTile(String player, Integer i, Integer j, UUID uuid);

    @Override
    public void playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles, UUID uuid);

    @Override
    public void flipTimer(String player, UUID uuid);

    @Override
    public void fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove, UUID uuid);

    @Override
    public void populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd, UUID uuid);

    @Override
    public void playCard(ActionJSON action, UUID uuid);
}
