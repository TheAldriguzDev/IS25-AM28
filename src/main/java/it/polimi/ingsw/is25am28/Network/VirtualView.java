package it.polimi.ingsw.is25am28.Network;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;

/**
 * This interface represents a virtual layer facilitating communication between the client and the server
 * for managing and interacting with the game state. It provides methods to create games, join games,
 * manage tile interactions, confirm actions, and receive updates or errors from the server.
 */
public interface VirtualView {
    /**
     * Requests the server to provide an updated AvailableGamesDTO.
     * <br>
     * This method interacts with the server to refresh and retrieve the current state of all available games.
     *
     * @throws Exception if any error occurs during the server communication or refresh process.
     */
    void refreshGames() throws Exception;

    /**
     * Initializes and creates a new game with the specified parameters.
     * This method requests the server to set up a game session with the given game and player attributes.
     *
     * @param playerNickname the nickname of the player creating the game
     * @param playerColor the color chosen by the player
     * @param gameLevel the level or difficulty of the game
     * @param totalPlayers the total number of players participating in the game
     * @throws Exception if there is an issue during game creation
     */
    void createNewGame(String playerNickname, PlayerColor playerColor, int gameLevel, int totalPlayers) throws Exception;

    /**
     * Allows a player to join an existing game session with the specified attributes.
     * This method is invoked to register the player into the game identified by the provided game ID.
     *
     * @param playerNickname the nickname of the player joining the game
     * @param playerColor the color chosen by the player
     * @param gameID the unique identifier of the game the player wants to join
     * @throws Exception if an error occurs during the process of joining the game
     */
    void joinGame(String playerNickname, PlayerColor playerColor, int gameID) throws Exception;

    /**
     * Selects a tile identified by its ID and associates it with the player.
     *
     * @param playerNickname the nickname of the player performing the tile selection
     * @param id the unique identifier of the tile to be selected
     * @throws Exception if there is an error during the tile selection process
     */
    void selectTile(String playerNickname, int id) throws Exception;

    /**
     * Deselects a tile that was previously selected, making it available for other players
     * or for further actions by the same player.
     *
     * @param playerNickname the nickname of the player requesting the tile deselection
     * @param id the unique identifier of the tile to be deselected
     * @throws Exception if an error occurs during the tile deselection process
     */
    void deselectTile(String playerNickname, int id) throws Exception;

    /**
     * Reserves a specific tile for a player. Reserved tiles cannot be deselected.
     * A maximum of two tiles can be reserved at the same time
     *
     * @param playerNickname the nickname of the player requesting to reserve the tile
     * @param id the unique identifier of the tile to be reserved
     * @throws Exception if an error occurs during the reservation process
     */
    void reserveTile(String playerNickname, int id) throws Exception;

    /**
     * Allows the player to skip the ship-building phase and assigns a pre-made ship to the player.
     * This method is typically used for demonstration purposes and should be used with caution
     * as it overwrites the player's current ship configuration.
     *
     * @param playerNickname the nickname of the player requesting the pre-made ship
     * @throws Exception if an error occurs while processing the request
     */
    void fastShip(String playerNickname) throws Exception;

    /**
     * Places a tile on the game board at the specified coordinates with the given rotation.
     *
     * @param playerNickname the nickname of the player attempting to place the tile
     * @param componentID the unique identifier of the tile being placed
     * @param i the row index on the board where the tile is to be placed
     * @param j the column index on the board where the tile is to be placed
     * @param rotation the rotation angle/position of the tile being placed
     * @throws Exception if there is an error during the placement of the tile
     */
    void placeTile(String playerNickname, Integer componentID, Integer i, Integer j, Integer rotation) throws Exception;

    /**
     * Sends a confirmation indicating that the player has completed the ship-building phase.
     * This method communicates the player's readiness and the count of their reserved tiles to the server.
     *
     * @param playerNickname the nickname of the player confirming their ship completion
     * @param reservedTiles the number of tiles reserved by the player during the ship-building phase that weren't used
     * @throws Exception if an error occurs while processing the ship confirmation
     */
    void sendShipConfirmation(String playerNickname, int reservedTiles) throws Exception;

    /**
     * Flips the hourglass in the ship construction phase.
     *
     * @param playerNickname the nickname of the player that requested the flip
     * @throws Exception if there is an issue while processing the flip
     */
    void flipTimer(String playerNickname) throws Exception;

    /**
     * Allows a player to select or deselect a subdeck during the game.
     * The action is determined by the isSelectAction parameter.
     * This method interacts with the server to perform the requested
     * selection or deselection operation.
     *
     * @param playerNickname the nickname of the player performing the operation
     * @param subdeck the unique identifier of the subdeck to be selected or deselected
     * @param isSelectAction a Boolean indicating the action to perform:
     *                       true to select the subdeck, false to deselect it
     * @throws Exception if an error occurs while processing the request
     */
    void selectDeselectSubdeck(String playerNickname, Integer subdeck, Boolean isSelectAction) throws Exception;

    /**
     * Removes a component from the player ship in order to fix detected issue in the control phase.
     *
     * @param playerNickname the nickname of the player requesting the fix
     * @param i the row index on the ship grid where the fix should be performed
     * @param j the column index on the ship grid where the fix should be performed
     * @throws Exception if an error occurs during the fix process
     */
    void fixShip(String playerNickname, Integer i, Integer j) throws Exception;

    /**
     * Populates the player's ship with the specified lifeform.
     * This method places a lifeform specified in the ComponentHelper object
     * onto the player's ship at a specific position defined in the componentHelper.
     *
     * @param playerNickname the nickname of the player whose ship is being populated
     * @param lifeFormToAdd  an instance of ComponentHelper containing the type and position of the lifeform to be added
     * @throws Exception if an error occurs during the ship population process
     */
    void populateShip(String playerNickname, ComponentHelper<LifeformType> lifeFormToAdd) throws Exception;

    /**
     * Executes a player's action by playing a card during the card round phase.
     * This method processes the action represented by the provided ActionJSON object
     * and associates it with the specified player.
     *
     * @param playerNickname the nickname of the player playing the card
     * @param action an instance of ActionJSON representing the details of the action to be performed. Specific subclasses of
     *               ActionJSON will be used.
     * @throws Exception if an error occurs during the processing of the action
     */
    void playCard(String playerNickname, ActionJSON action) throws Exception;

    /**
     * Attempts to reconnect a client to the server using the provided nickname.
     * This method is typically called to re-establish a player's session after a disconnection.
     *
     * @param nickname the nickname of the client attempting to reconnect
     * @throws Exception if an error occurs during the reconnection process
     */
    void reconnectClient(String nickname) throws Exception;

    // CALLBACKS USED BY THE SERVER

    /**
     * Updates the current state of the game or application based on the provided server answer.
     * This method processes the given Answer object and refreshes the game model and/or the UI.
     *
     * @param answer an instance of Answer containing state and contextual information
     *               to update the current game or application state
     * @throws Exception if an error occurs during the state update process
     */
    void updateState(Answer answer) throws Exception;

    /**
     * Reports an error to the client using the provided ErrorAnswer object.
     * This method communicates error details to the client for appropriate handling.
     *
     * @param error an instance of ErrorAnswer containing the details of the error to be reported
     * @throws Exception if an error occurs during the process of sending the error message to the client
     */
    void reportError(ErrorAnswer error) throws Exception;
}
