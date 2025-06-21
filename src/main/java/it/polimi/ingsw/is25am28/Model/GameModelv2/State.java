package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FastShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.List;

public abstract sealed class State permits CardRoundState, CreateGameState, EndGameState, FixShipState, InsufficientPlayerState, PopulateShipState, ShipContructionState, WaitPlayersState {
    protected GameModel model;

    /**
     * Constructs a new State with the specified game model.
     *
     * @param model the {@link GameModel} instance associated with this state
     */
    public State(GameModel model) {
        this.model = model;
    }

    /**
     * Configures the game's initial settings based on the provided parameters.
     *
     * @param playerNickname the nickname of the player initiating the configuration
     * @param playerColor the color chosen by the player
     * @param level the difficulty level of the game
     * @param numPlayers the total number of players participating in the game
     * @throws IllegalStateException if the current state does not allow game configuration
     * @throws IllegalArgumentException if any of the provided parameters are invalid
     */
    public void gameConfig(String playerNickname, PlayerColor playerColor, int level, int numPlayers) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'gameConfig' command is not allowed in the " + this + " state");
    }

    /**
     * Adds a new player to the current game state with the specified nickname and color.
     *
     * @param playerNickname the unique nickname of the player to be added
     * @param playerColor the color representing the player in the game
     * @throws IllegalStateException if the current state does not allow adding a new player
     * @throws IllegalArgumentException if the provided arguments are invalid or violate game rules
     */
    public void addNewPlayer(String playerNickname, PlayerColor playerColor) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'addNewPlayer' command is not allowed in the " + this + " state");
    }

    /**
     * Selects or deselects a subdeck for a specified player based on the provided parameters.
     *
     * @param player the identifier of the player performing the action
     * @param selectedDeck the identifier of the subdeck to be selected or deselected
     * @param isSelectAction a boolean indicating whether to select (true) or deselect (false) the subdeck
     * @return a {@link ConstructionDeckDTO} representing the updated selection state of the subdeck
     * @throws IllegalStateException if the action is not allowed in the current state
     */
    public ConstructionDeckDTO selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws IllegalStateException {
        throw new IllegalStateException("The 'selectDeselectSubdeck' command is not allowed in the " + this + " state");
    }

    /**
     * Selects a tile for the specified player based on the provided tile ID.
     *
     * @param player the identifier of the player performing the tile selection
     * @param id the unique ID of the tile to be selected
     * @return a ConstructionComponentDTO representing the tile that was selected
     * @throws IllegalStateException if the tile selection action is not permitted in the current state
     * @throws IllegalArgumentException if the provided player identifier or tile ID is invalid
     */
    public ConstructionComponentDTO selectTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'selectTile' command is not allowed in the " + this + " state");
    }

    /**
     * Deselects a tile for the specified player based on the provided tile ID.
     *
     * @param player the identifier of the player performing the tile deselection
     * @param id the unique ID of the tile to be deselected
     * @return a ConstructionComponentDTO representing the tile that was deselected
     * @throws IllegalStateException if the tile deselection action is not permitted in the current state
     * @throws IllegalArgumentException if the provided player identifier or tile ID is invalid
     */
    public ConstructionComponentDTO deselectTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'deselectTile' command is not allowed in the " + this + " state");
    }

    /**
     * Reserves a tile for the specified player based on the provided tile ID.
     *
     * @param player the identifier of the player performing the tile reservation
     * @param id the unique ID of the tile to be reserved
     * @return a ConstructionComponentDTO representing the tile that was reserved
     * @throws IllegalStateException if the tile reservation action is not permitted in the current state
     * @throws IllegalArgumentException if the provided player identifier or tile ID is invalid
     */
    public ReservedComponentDTO reserveTile(String player, Integer id) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'reserveTile' command is not allowed in the " + this + " state");
    }

    /**
     * Performs the "fast ship" action for the specified player.
     *
     * @param playerNickname the nickname of the player executing the fast shipping action
     * @return a {@link FastShipDTO} encapsulating the details of the fast shipping operation
     * @throws IllegalStateException if the action is not permitted in the current state
     * @throws IllegalArgumentException if the provided player nickname is invalid
     */
    public FastShipDTO fastShip(String playerNickname) throws IllegalStateException, IllegalArgumentException {
        throw new IllegalStateException("The 'reserveTile' command is not allowed in the " + this + " state");
    };

    /**
     * Places a tile in the game at the specified location with the given orientation.
     *
     * @param player the identifier of the player placing the tile
     * @param componentID the unique identifier of the tile to be placed
     * @param i the row index where the tile will be placed
     * @param j the column index where the tile will be placed
     * @param rotation the rotation of the tile to be placed
     * @return a PlacedComponentDTO object representing the details of the placed tile
     * @throws IllegalStateException if placing the tile is not allowed in the current state
     */
    public PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        throw new IllegalStateException("The 'placeTile' command is not allowed in the " + this + " state");
    }

    /**
     * Handles the action of a player ending the ship construction phase.
     *
     * @param player the identifier of the player executing the action
     * @param reservedTiles the number of tiles reserved and unused by the player during the construction phase
     * @return a {@link PlayerEndedShipDTO} object containing details about the player's action
     * @throws IllegalStateException if the action is not permitted in the current state
     */
    public PlayerEndedShipDTO playerEndedSendShip(String player, int reservedTiles) throws IllegalStateException {
        throw new IllegalStateException("The 'playerEndedSendShip' command is not allowed in the " + this + " state");
    }

    /**
     * Flips the timer in the ship construction phase, if it's flippable.
     *
     * @param player the identifier of the player attempting to flip the timer
     * @return a {@link TimerDTO} representing the state of the timer after the flip action
     * @throws IllegalStateException if the flip action is not permitted in the current state
     */
    public TimerDTO flipTimer(String player) throws IllegalStateException {
        throw new IllegalStateException("The 'flipTimer' command is not allowed in the " + this + " state");
    }

    /**
     * Attempts to remove a ship component at the specified location for the given player.
     *
     * @param player the identifier of the player attempting to fix the ship
     * @param i the row index of the ship component to be removed
     * @param j the column index of the ship component to be removed
     * @return a {@link FixedComponentDTO} containing details about the removed ship component
     * @throws IllegalArgumentException if the provided parameters are invalid or the action cannot be performed
     */
    public FixedComponentDTO fixShip(String player, Integer i, Integer j) throws IllegalArgumentException {
        throw new IllegalStateException("The 'fixShip' command is not allowed in the " + this + " state");
    }

    /**
     * Populates a ship with a specified lifeform component for the given player.
     *
     * @param player the identifier of the player attempting to populate the ship
     * @param lifeformToAdd the lifeform component to be added to the ship
     * @return a {@link PopulateShipComponentDTO} representing the populated ship component
     * @throws IllegalArgumentException if the provided parameters are invalid
     */
    public PopulateShipComponentDTO populateShip(String player, ComponentHelper<LifeformType> lifeformToAdd) throws IllegalArgumentException {
        throw new IllegalStateException("The 'populateShip' command is not allowed in the " + this + " state");
    }

    /**
     * Executes the play card action based on the provided parameters.
     *
     * @param action the action request details encapsulated within an ActionJSON object
     * @return a list of {@code CardRoundDTO} objects representing the results of the executed action:
     *          if the list contains one element, it is the updated state of the current card;
     *          if it contains two elements, the second is the new card that needs to be played.
     * @throws IllegalArgumentException if the provided action parameters are invalid or not allowed
     */
    public List<CardRoundDTO> playCard(ActionJSON action) throws IllegalArgumentException {
        throw new IllegalStateException("The 'playCard' command is not allowed in the " + this + " state");
    }

    /**
     * Handles the disconnection of a player during the ShipConstructionState,
     * freeing any resources locked by the player. In particular, if the player
     * has selected a subdeck, it will be deselected to allow other players to use it.
     *
     * @param player the username of the player who has disconnected
     */
    public void handlePlayerDisconnection(String player) {
        throw new IllegalStateException("The 'populateShip' command is not allowed in the " + this + " state");
    }

    /**
     * Executes the state transition when the game is ready, preventing new commands when the game needs to update its phase.
     * This method is used by most commands to safely advance the game to the next state.
     */
    public abstract void onComplete();

    /**
     * Generates and returns a new state representation as a {@code StateDTO} object,
     * setting the state name to the string representation of the current object.
     *
     * Each concrete state implementation provides its own version of this method.
     *
     * @return a {@code StateDTO} object encapsulating the state details
     */

    public StateDTO generateState() {
        StateDTO state = new StateDTO();

        state.setStateName(this.toString());

        return state;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
