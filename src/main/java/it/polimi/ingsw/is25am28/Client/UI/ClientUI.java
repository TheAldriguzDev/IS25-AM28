package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

public interface ClientUI {
    void setVirtualClient(VirtualView client);

    // TODO: Add more methods to display all the relevant information to play the game
    void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws Exception;
    void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers);
    void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception;
    void showShipFixing(FixShipDTO fixShip) throws Exception;
    void showShipPopulate(PopulateShipDTO populateShip) throws Exception;
    void showCardRound(CardRoundDTO cardRound) throws Exception;
    void showEndGame(EndGameDTO endGame);

    void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer);

    void receiveTimerDTO(TimerDTO timerDTO);
    void commitCommand(String playerNickname);
    void showError(ErrorAnswer error);

    boolean isCTXAvailable();

    // Update methods, used only in the GUI
    void updateShipConstructionComponent(ConstructionComponentDTO component);
    void updateShipPlacedComponent(PlacedComponentDTO data);
    void placePlayerInTheBoard(PlayerEndedShipDTO data);
    void updateShipRemovedComponent(FixedComponentDTO data);
    void updateShipPlacedLifeForm(PopulateShipComponentDTO data);
    void updateVisuals(CardRoundDTO data);
    void handlePlayerFastShip(String playerNickname);

    // Interrupt screen, used only be the TUI
    void interruptCurrScreen();
}
