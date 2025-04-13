package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ConstructionComponentDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.List;
import java.util.UUID;

/**
 * Interface that defines the server's methods that needs to be accessible to run the game
 * */
public interface VirtualServer {

    public void gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, UUID uuid) throws Exception;

    public void addNewPlayer(String nickname, PlayerColor playerColor, UUID uuid) throws Exception;

    public void selectTile(String player, Integer i, Integer j, UUID uuid) throws Exception;

    public void deselectTile(String player, Integer i, Integer j, UUID uuid) throws Exception;

    public void playerEndedSendShip(String player, List<ComponentHelper<ConstructionComponentDTO>> playerShip, int reservedTiles, UUID uuid) throws Exception;

    public void flipTimer(String player, UUID uuid) throws Exception;

    public void fixShip(String player, List<ComponentHelper<Integer>> componentsToRemove, UUID uuid) throws Exception;

    public void populateShip(String player, List<ComponentHelper<LifeformType>> lifeFormToAdd, UUID uuid) throws Exception;

    public void playCard(ActionJSON action, UUID uuid) throws Exception;
}
