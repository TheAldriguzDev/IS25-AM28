package it.polimi.ingsw.is25am28.Model.ActionJSON.State;


import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;

/**
 * Defines the visitor interface for handling state updates received from the server.
 * The methods implemented in {@code ViewUpdater} contain the specific logic for processing
 * each concrete implementation of the {@code StateDTO}.
 */
public interface StateVisitor {
    // ===== GENERAL STATE ===== //
    void visit(StateDTO state);

    // ===== LOBBY STATE ===== //
    /**
     * Shows the available games
     */
    void visit(AvailableGamesDTO state) throws Exception;

    /**
     * Shows how many players are in the lobby while waiting for more
     */
    void visit(WaitPlayersStateDTO state) throws Exception;

    /**
     * Recreates all the attributes for the reconnecting player
     */
    void visit(ReconnectDTO state) throws Exception;

    // ===== SHIP CONSTRUCTION - FIX - POPULATE  ===== //

    /**
     * Sets the model state to the ShipConstructionState that will initialize all the components
     */
    void visit(ShipConstructionDTO state) throws Exception;

    /**
     * This method will be used to set the component as Visible or not Visible. In addition, the component will be marked
     * as flipped.
     * */
    void visit(ConstructionComponentDTO state) throws Exception;

    /**
     * This method reserve the component for the target player
     * */
    void visit(ReservedComponentDTO state) throws Exception;

    /**
     * This method is used to create in real time the players ship in the ShipConstructionState.
     * It will get the player ship based on the given nickname and add the component with the proper rotation in the
     * given coordinates (i, j)
     * */
    void visit(PlacedComponentDTO state) throws Exception;

    /**
     * Given the FastShipDTO, it overwrites the target player's client ship
     * with the one described in the former, allowing the target player to
     * skip the ship building phase altogether.
     * <br>
     * NOTE: This method is meant to be used only for demonstration purposes only.
     * NOTE: This method overwrites the target player's ship.
     */
    void visit(FastShipDTO state) throws Exception;

    /**
     * This method removes the specified component from the specified player's ship.
     * It also sets the ship as fixed if it is valid
     */
    void visit(FixedComponentDTO state) throws Exception;

    /**
     * This method adds the specified lifeForm to the specified player's ship.
     * It also sets the ship as full if needed
     */
    void visit(PopulateShipComponentDTO state) throws Exception;

    /**
     * This method sets the specified player status to indicate they have finished building their ship, then it places the player's rocket on the board (GUI)
     */
    void visit(PlayerEndedShipDTO state) throws Exception;

    /**
     * This method is used to update the timer when necessary
     */
    void visit(TimerDTO state) throws Exception;

    /**
     * This method sets the specified subDeck status
     */
    void visit(ConstructionDeckDTO state) throws Exception;

    /**
     * Sets the model state to the fixShipState
     */
    void visit(FixShipDTO state) throws Exception;

    /**
     * Sets the model state to the populateShipState
     */
    void visit(PopulateShipDTO state) throws Exception;

    // ===== CARD ROUND ===== //
    /**
     * Sets the model state to the cardRoundState, along with
     */
    void visit(CardRoundDTO state) throws Exception;

    // ===== END GAME ===== //
    /**
     * Shows the endGameState
     */
    void visit(EndGameDTO state) throws Exception;

    // ===== UTILITY ===== //

    /**
     * Notifies the user about a disconnected player
     */
    void visit(DisconnectedPlayerDTO state);

    /**
     * Shows the insufficientPlayersSTate
     */
    void visit(InsufficientPlayerDTO state);
}
