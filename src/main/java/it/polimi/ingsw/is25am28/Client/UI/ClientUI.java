package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.AvailableGamesDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.ShipConstructionDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.TimerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.WaitPlayersStateDTO;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.io.IOException;

public interface ClientUI {
    void setVirtualClient(VirtualView client);

    // TODO: Add more methods to display all the relevant information to play the game
    void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws Exception;
    void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers);
    void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception;
    void showShipFixing();
    void showShipPopulate();

    void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer);

    void receiveTimerDTO(TimerDTO timerDTO);
    void commitCommand(String playerNickname);
    void showError(ErrorAnswer error);

    boolean isCTXAvailable();
}
