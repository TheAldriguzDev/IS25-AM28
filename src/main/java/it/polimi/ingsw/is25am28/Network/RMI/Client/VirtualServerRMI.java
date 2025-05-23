package it.polimi.ingsw.is25am28.Network.RMI.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.is25am28.Network.VirtualServer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.rmi.Remote;
import java.util.UUID;

/**
 * This interface specializes the VirtualServer interface because we could need some methods that are needed only for RMI
 * */
public interface VirtualServerRMI extends Remote, VirtualServer {
    void connectClient(VirtualViewRMI client, UUID uuid) throws Exception;

    void refreshGames(UUID uuid) throws Exception;

    void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers, UUID uuid) throws Exception;

    void joinGame(String playerNickname, PlayerColor playerColor, int gameID, UUID uuid) throws Exception;

    void selectTile(String playerNickname, int id, UUID uuid) throws Exception;

    void deselectTile(String playerNickname, int id, UUID uuid) throws Exception;

    void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation, UUID uuid) throws Exception;

    void sendShipConfirmation(String playerNickname, int reservedTiles, UUID uuid) throws Exception;

    void flipTimer(String playerNickname, UUID uuid) throws Exception;

    void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction, UUID uuid) throws Exception;

    void fixShip(String playerNickname, Integer i, Integer j, UUID uuid) throws Exception;

    void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd, UUID uuid) throws Exception;

    void playCard(String playerNickname, ActionJSON action, UUID uuid) throws Exception;

    // ========== PING METHOD ========== //
    void ping(UUID uuid) throws Exception;

    void reconnectClient(String nickname, UUID uuid) throws Exception;
}
