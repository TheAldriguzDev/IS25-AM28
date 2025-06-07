package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;

import java.util.UUID;

/**
 * Interface that defines the methods that the controller calls to update the content on the clients
 * */

public interface VirtualView {
    // METHODS USED BY THE CLIENT
    void refreshGames() throws Exception;

    void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception;

    void joinGame(String playerNickname, PlayerColor playerColor, int gameID) throws Exception;

    void selectTile(String playerNickname, int id) throws Exception;

    void deselectTile(String playerNickname, int id) throws Exception;

    void reserveTile(String playerNickname, int id) throws Exception;

    void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception;

    void sendShipConfirmation(String playerNickname, int reservedTiles) throws Exception;

    void flipTimer(String playerNickname) throws Exception;

    void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction) throws Exception;

    void fixShip(String playerNickname, Integer i, Integer j) throws Exception;

    void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception;

    void playCard(String playerNickname, ActionJSON action) throws Exception;

    void reconnectClient(String nickname) throws Exception;

    // CALLBACKS USED BY THE SERVER
    void updateState(Answer answer) throws Exception;

    void reportError(ErrorAnswer error) throws Exception;
}
