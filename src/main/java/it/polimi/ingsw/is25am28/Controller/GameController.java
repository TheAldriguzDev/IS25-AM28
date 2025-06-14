package it.polimi.ingsw.is25am28.Controller;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.FastShipDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ReconnectDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.GameModelv2.*;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.util.List;

/**
 * The GameController class handles the dispatching of client-triggered events to the GameModel
 * */

public class GameController {
    private final GameModel model;

    public GameController() {
        this.model = new GameModel();
    }

    /**
     * Returns the current state of the game model.
     *
     * @return the current {@code StateDTO} representing the game state
     */
    public StateDTO getCurrentState() {
        synchronized (this.model) {
            return this.model.getCurrentState().generateState();
        }
    }

    /**
     * Returns a list of colors that are still available for players to choose.
     *
     * @return a list of available color names
     */
    public List<String> getAvailableColors() {
        synchronized (this.model) {
            return this.model.getAvailableColors();
        }
    }

    /**
     * Returns the list of nicknames of all currently disconnected players.
     *
     * @return a list of disconnected player nicknames
     */
    public List<String> getDisconnectedPlayers() {
        synchronized (this.model) {
            return this.model.getDisconnectedPlayers();
        }
    }

    /**
     * Marks the player with the given nickname as disconnected.
     *
     * @param nickname the nickname of the player to disconnect
     * @return a list of {@code StateDTO} objects representing the resulting game state updates
     */
    public List<StateDTO> disconnectClient(String nickname) {
        synchronized (this.model) {
            return this.model.disconnectClient(nickname);
        }
    }

    /**
     * Reconnects a client to the game by associating the provided {@code VirtualView} with the given player nickname.
     * It updates the game model to reflect the reconnection and returns the resulting game state updates.
     *
     * @param nickname the nickname of the player to reconnect
     * @param clientView the {@link VirtualView} instance representing the client's network protocol
     * @return a list of {@link StateDTO} objects representing the updated game state
     * @throws Exception if the reconnection process encounters any issue
     */
    public List<StateDTO> reconnectClient(String nickname, VirtualView clientView) throws Exception {
        synchronized (this.model) {
            return this.model.reconnectClient(nickname, clientView);
        }
    }

    /**
     * Configures the game with the provided player details and game settings.
     *
     * @param nickname the nickname of the player to be added to the game
     * @param playerColor the {@link PlayerColor} representing the color choice of the player
     * @param level the difficulty level of the game (0 and 2 are the supported levels)
     * @param numPlayers the total number of players for the game
     * @param clientView the {@link VirtualView} instance representing the player's view
     * @return the updated {@link StateDTO} representing the current state of the game after configuration
     * @throws IllegalStateException if the game configuration cannot be completed due to the current state
     * @throws IllegalArgumentException if the provided parameters are invalid
     */
    public StateDTO gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, VirtualView clientView) throws IllegalStateException, IllegalArgumentException {
        synchronized (this.model) {
            return this.model.gameConfig(nickname, playerColor, level, numPlayers, clientView);
        }
    }

    /**
     * Adds a new player to the game with the specified nickname, player color, and client view.
     *
     * @param nickname the nickname of the new player to be added
     * @param playerColor the color associated with the new player
     * @param clientView the {@link VirtualView} instance representing the player's client
     * Returns a {@link List} of {@code StateDTO} objects where:
     * <ul>
     *   <li>the first element is always present and represents the response to the command,</li>
     *   <li>the second element, if present, represents the new state of the game.</li>
     * </ul>
     *
     * @return a list containing the response and optionally the new game state
     * @throws IllegalStateException if the new player cannot be added due to the current state of the game
     * @throws IllegalArgumentException if the provided parameters are invalid
     */
    public List<StateDTO> addNewPlayer(String nickname, PlayerColor playerColor, VirtualView clientView) throws IllegalStateException, IllegalArgumentException {
        synchronized (this.model) {
            return this.model.addNewPlayer(nickname, playerColor, clientView);
        }
    }

    /**
     * Allows a player to select or deselect a specific subdeck in the game.
     *
     * @param player the nickname of the player performing the action
     * @param selectedDeck the unique identifier of the subdeck to be selected or deselected
     * @param isSelectAction indicates whether the action is a selection (true) or deselection (false)
     * @return a {@link ConstructionDeckDTO} containing the information about the selected or deselected subdeck
     * @throws IllegalStateException if the action is not allowed in the current state
     */
    public ConstructionDeckDTO selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws IllegalStateException {
        synchronized (this.model) {
            return this.model.selectDeselectSubdeck(player, selectedDeck, isSelectAction);
        }
    }

    /**
     * Selects a tile for the specified player.
     *
     * @param player the nickname of the player selecting the tile
     * @param id the unique identifier of the tile to be selected
     * @return a {@link ConstructionComponentDTO} containing the information about the selected tile
     * @throws IllegalArgumentException if an error occurs during the tile selection process
     */
    public ConstructionComponentDTO selectTile(String player, Integer id) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.selectTile(player, id);
        }
    }

    /**
     * Deselects a tile for the specified player.
     *
     * @param player the nickname of the player deselecting the tile
     * @param id the unique identifier of the tile to be deselected
     * @return a {@link ConstructionComponentDTO} containing the information about the deselected tile
     * @throws IllegalArgumentException if an error occurs during the tile deselection process
     */
    public ConstructionComponentDTO deselectTile(String player, Integer id) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.deselectTile(player, id);
        }
    }

    /**
     * Reserve a tile for the specified player.
     *
     * @param playerNickname the nickname of the player reserving the tile
     * @param id the unique identifier of the tile to be reserved
     * @return a {@link ReservedComponentDTO} containing the information about the reserved tile
     * @throws IllegalArgumentException if an error occurs during the tile reservation process
     */
    public ReservedComponentDTO reserveTile(String playerNickname, int id) {
        synchronized (this.model) {
            return this.model.reserveTile(playerNickname, id);
        }
    }

    /**
     * Requests a fast ship configuration for the given player
     *
     * @param playerNickname the nickname of the player reserving the tile
     * Returns a {@link List} of {@code StateDTO} objects where:
     * <ul>
     *   <li>the first element is always present and represents the response to the command,</li>
     *   <li>the second element, if present, represents the new state of the game.</li>
     * </ul>
     *
     * @return a list containing the response and optionally the new game state
     * */
    public List<StateDTO> fastShip(String playerNickname) {
        synchronized (this.model) {
            return this.model.fastShip(playerNickname);
        }
    }

    /**
     * Places a tile for the specified player on the game board at the given position with the specified rotation.
     *
     * @param player the nickname of the player placing the tile
     * @param componentID the unique identifier of the tile being placed
     * @param i the row index where the tile is placed
     * @param j the column index where the tile is placed
     * @param rotation the rotation of the tile being placed
     * @return a {@link PlacedComponentDTO} containing the information about the placed tile
     */
    public PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        synchronized (this.model) {
            return this.model.placeTile(player, componentID, i, j, rotation);
        }
    }

    /**
     * Confirms the ship configuration for the given player, specifying the number of reserved and unused tiles.
     *
     * @param player the nickname of the player placing the tile
     * @param reservedTiles the reserved and unused amount of reserved tiles
     *
     * Returns a {@link List} of {@code StateDTO} objects where:
     * <ul>
     *   <li>the first element is always present and represents the response to the command,</li>
     *   <li>the second element, if present, represents the new state of the game.</li>
     * </ul>
     *
     * @return a list containing the response and optionally the new game state
     */
    public List<StateDTO> playerEndedSendShip(String player, int reservedTiles) {
        synchronized (this.model) {
            return this.model.playerEndedSendShip(player, reservedTiles);
        }
    }

    /**
     * Flips the timer for the ship construction phase
     *
     * @return {@link TimerDTO} containing the information about the timer flip
     * */
    public TimerDTO flipTimer(String player) throws IllegalStateException {
        synchronized (this.model) {
            return this.model.flipTimer(player);
        }
    }

    /**
     * Removes a component from the given player ship at the given (i, j) coordinates.
     *
     * @param player the players that has requested the fix ship operation
     * @param i the 'i' coordinates of the removed component
     * @param j the 'j' coordinates of the removed component
     *
     * Returns a {@link List} of {@code StateDTO} objects where:
     * <ul>
     *   <li>the first element is always present and represents the response to the command,</li>
     *   <li>the second element, if present, represents the new state of the game.</li>
     * </ul>
     *
     * @return a list containing the response and optionally the new game state
     */
    public List<StateDTO> fixShip(String player, Integer i, Integer j) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.fixShip(player, i, j);
        }
    }

    /**
     * Adds a lifeform to the specified component for the given player.
     *
     * @param player the players that has requested the fix ship operation
     * @param lifeFormToAdd the lifeform component to be added to the player's ship
     *
     * Returns a {@link List} of {@code StateDTO} objects where:
     * <ul>
     *   <li>the first element is always present and represents the response to the command,</li>
     *   <li>the second element, if present, represents the new state of the game.</li>
     * </ul>
     *
     * @return a list containing the response and optionally the new game state
     */
    public List<StateDTO> populateShip(String player, ComponentHelper<LifeformType> lifeFormToAdd) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.populateShip(player, lifeFormToAdd);
        }
    }

    /**
     * Executes the action of playing a card for a specific player
     *
     * @param action the action details in JSON format with all necessary information to play the card
     *
     * Returns a {@link List} of {@code StateDTO} objects where:
     * <ul>
     *   <li>the first element is always present and represents the response to the command,</li>
     *   <li>the second element, if present, represents the new state of the game.</li>
     * </ul>
     *
     * @return a list containing the response and optionally the new game state
     */
    public List<StateDTO> playCard(ActionJSON action) throws IllegalArgumentException {
        synchronized (this.model) {
            return this.model.playCard(action);
        }
    }
}