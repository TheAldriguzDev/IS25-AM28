package it.polimi.ingsw.is25am28.Client.UI;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.VirtualView;

public interface ClientUI {

    // ============ Game Phases ============ //

    /**
     * TUI/GUI entry point for the game menu.
     *
     * @param availableGames The list of available games from which the player can choose one to join.
     * @param isFirstAccess Flag put to TRUE only on the first request to print the game title.
     */
    void showLobbies(AvailableGamesDTO availableGames, boolean isFirstAccess) throws Exception;

    /**
     * Puts the current player's client into the waiting state
     * and shows how many players are currently connected and
     * also how many are left before the game can start.
     */
    void showWaitingForPlayers(WaitPlayersStateDTO waitingForPlayers);

    /**
     * TUI screen entry point for the ship construction phase
     */
    void showShipConstruction(ShipConstructionDTO shipConstruction) throws Exception;

    /**
     * TUI screen entry point for the ship fix phase
     */
    void showShipFixing(FixShipDTO fixShip) throws Exception;

    /**
     * TUI screen entry point for the ship populate phase
     */
    void showShipPopulate(PopulateShipDTO populateShip) throws Exception;

    /**
     * TUI screen entry point for the card round game phase
     */
    void showCardRound(CardRoundDTO cardRound) throws Exception;

    /**
     * TUI screen entry point for the end game phase
     */
    void showEndGame(EndGameDTO endGame);

    /**
     * TUI screen entry point for the insufficient players condition
     */
    void showInsufficientPlayer(InsufficientPlayerDTO insufficientPlayer);

    // ============ Generics & Utilities ============ //

    /**
     * @param client The virtual view that will be connected to the
     *               player's chosen client (TUI/GUI) to enable
     *               communication with the server.
     */
    void setVirtualClient(VirtualView client);

    /**
     * Method used to update the TimerDTO when either a player flips the timer
     * or when the previous timer runs out and there's the need to notify all
     * players about the status of the timer.
     * <br>
     * This method is used exclusively during the ShipConstruction phase
     * since it's the only place where the timer is relevant for the game.
     *
     * @param timerDTO The TimerDTO received by the client from the server
     */
    void receiveTimerDTO(TimerDTO timerDTO);

    /**
     * Method used to trigger the onSuccess task set in the currently active
     * command context, which was set up before sending the command to the server.
     *
     * @param playerNickname The player whose onSuccess needs to be executed.
     *                       Other players different from the given one won't
     *                       execute the onSuccess task in their command context.
     */
    void commitCommand(String playerNickname);

    /**
     * Method used to trigger the onError task set in the currently active
     * command context, which was set up before sending the command to the server.
     *
     * @param error The error message explaining what went wrong in the
     *              previously performed action. It'll be displayed to the
     *              player to inform him about what happened and possibly what
     *              he has to do to send a correct command.
     */
    void showError(ErrorAnswer error);

    /**
     * @return TRUE if the client it's invoked in has a command context setup,
     *         FALSE otherwise.
     */
    boolean isCTXAvailable();

    /**
     * <b>(NOTE: This method is used <u>only</u> in the TUI)</b>
     * <br>
     * Forces the inputThread to be interrupted when the current screen is blocked
     * waiting for user input, allowing the game state to be transition to the next one.
     */
    void interruptCurrScreen();

    // ============ GUI Graphics Update Methods ============ //

    /**
     * Updates the selected component's graphics in the component matrix that are
     * selectable by each player during the ship construction phase when using the GUI.
     *
     * @param component The component that was picked from the selectable ones.
     */
    void updateShipConstructionComponent(ConstructionComponentDTO component);

    /**
     * Updates for each player in the game the GUI graphics of the ship belonging
     * to the player who placed the given component. This is done so that each player
     * can see in-real-time how that particular player is building his ship.
     *
     * @param data The data of a component placed by a player.
     */
    void updateShipPlacedComponent(PlacedComponentDTO data);

    /**
     * Updates the board GUI graphics to show a player on the board in the correct
     * position when said player decides to finish building his ship.
     *
     * @param playerNickname The player that was placed on the board following his
     *                       request to conclude the ship construction phase.
     */
    void placePlayerInTheBoard(String playerNickname);

    /**
     * Updates for each player in the game the GUI graphics of the ship belonging
     * to the player who removed the given component when fixing his ship. This is done
     * so that each player can see in-real-time how that particular player is fixing his ship.
     *
     * @param data The data of a component removed by a player when fixing his ship.
     */
    void updateShipRemovedComponent(FixedComponentDTO data);

    /**
     * Updates for each player in the game the GUI graphics of the ship belonging
     * to the player who populated the given component when populating his ship. This is done
     * so that each player can see in-real-time how that particular player is populating his ship.
     *
     * @param data The data of a component populated by a player when populating his ship.
     */
    void updateShipPlacedLifeForm(PopulateShipComponentDTO data);

    /**
     * Update the GUI visuals only during the CardRound game phase to reflect
     * any changes caused by the use of an event card (e.g.: item withdraw/removal,
     * destroyed components, lifeform removal, etc...).
     */
    void updateVisuals(CardRoundDTO data);

    /**
     * Updates for each player in the game the GUI graphics of the ship belonging
     * to the player who decided to fast build his ship.
     *
     * @param playerNickname The nickname of the player that fast built his ship.
     */
    void handlePlayerFastShip(String playerNickname);
}
