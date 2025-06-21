package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Loader.CardLoader;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ReconnectDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Model.Board.BoardTestFlight;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.Server.GameInstance;
import it.polimi.ingsw.is25am28.Network.VirtualView;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GameModel {
    private int level; // The level range can be 0 - 1 - 2 - 3
    private Board board;
    private final List<EventCard> deck;
    private ResourceBank resourceBank;
    private int numPlayers;
    private final Map<String, Player> players;
    private final Map<String, VirtualView> playerVirtualViews;
    private State currentState;
    private final Queue queueHandler;

    private final Random random = new Random();

    /**
     * Constructor for the GameModel class.
     * <p>
     * Initializes the state and data structures of the game model.
     * Specifically:
     * - Initializes the game deck as an empty collection.
     * - Initializes the players map to store player data.
     * - Sets the default number of players to 2.
     * - Sets the initial state as an instance of CreateGameState.
     * - Initializes the mapping for player virtual views.
     * - Sets up the queue handler for managing game events and starts its execution in a separate thread.
     * This constructor is the starting point for creating a new GameModel instance
     * and establishing the game's initial state and logic pipeline.
     */
    public GameModel() {
        this.deck = new ArrayList<>();
        this.players = new HashMap<>();
        this.numPlayers = 2; // min value
        this.currentState = new CreateGameState(this);
        this.playerVirtualViews = new HashMap<>();

        this.queueHandler = new Queue();
        new Thread(queueHandler).start();
    }

    /**
     * Retrieves the current state of the game.
     *
     * @return the current state of the game represented as a {@code State} object.
     */
    public State getCurrentState() {
        return this.currentState;
    }

    /**
     * Sets the current state of the game in order to make a state transition (state pattern).
     *
     * @param currentState the state to be set as the current state. It represents the
     *                     ongoing phase or scenario of the game and must be a valid
     *                     {@code State} object.
     */
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    /**
     * Generates a JSON object representing the current phase of the game by invoking
     * the {@code generateState()} method of the current state.
     *
     * @return a {@code StateDTO} object encapsulating the current game state
     */
    public StateDTO generateState() {
        return this.currentState.generateState();
    }

    /**
     * Disconnects a client from the game based on their nickname. Updates the player's
     * connection status and modifies the current game state if necessary.
     *
     * @param nickname the nickname of the player to disconnect. Must match the name of
     *                 an existing player in the game.
     * @return a list of {@code StateDTO} objects representing the game's updated state,
     *         including the player's disconnection and potential state transitions to {@link InsufficientPlayerState} caused
     *         by an insufficient number of players.
     * @throws IllegalArgumentException if the given player does not exist in the game.
     */
    public List<StateDTO> disconnectClient(String nickname) throws IllegalArgumentException{
        Player p = this.players.get(nickname);
        if (p == null) {
            throw new IllegalArgumentException("The given player cannot be disconnected since it doesn't exist");
        }

        // Set the player connection to false
        p.setConnected(false);

        List<StateDTO> states = new ArrayList<>();
        states.add(new DisconnectedPlayerDTO().setNickname(nickname));

        this.currentState.handlePlayerDisconnection(nickname);

        List<Player> connectedPlayers = this.players.values().stream().filter(Player::isConnected).toList();
        if (connectedPlayers.size() <= 1) {
            this.setCurrentState(new InsufficientPlayerState(this, this.currentState));
            states.add(this.currentState.generateState());
        }

        return states;
    }

    /**
     * Retrieves a list of nicknames for all players who are currently disconnected from the game.
     *
     * @return a list of nicknames of disconnected players. If no players are disconnected, this list will be empty.
     */
    public List<String> getDisconnectedPlayers() {
        return this.players.values()
                .stream()
                .filter(p -> !p.isConnected())
                .map(Player::getNickname).toList();
    }

    /**
     * Handles the reconnection of a disconnected client to the server. This method verifies if the provided
     * nickname belongs to a disconnected player, updates the player's connected status, assigns the new client
     * view, and returns a list of game states required to synchronize the client with the current game phase.
     *
     * @param nickname the nickname of the player attempting to reconnect
     * @param clientView the instance of the VirtualView corresponding to the reconnecting client
     * @return a list of StateDTO objects representing the current state of the game, including any reconnect-specific information
     * @throws IllegalArgumentException if the provided nickname does not exist in the disconnected players list or the game
     * @throws Exception if an unexpected error occurs during the reconnection process
     */
    public List<StateDTO> reconnectClient(String nickname, VirtualView clientView) throws Exception {
        if (!this.getDisconnectedPlayers().contains(nickname)) {
            throw new IllegalArgumentException("The given nickname does not exist in the disconnected players");
        }

        Player p = this.players.get(nickname);
        if (p == null) {
            throw new IllegalArgumentException("The given nickname does not exist");
        }
        p.setConnected(true);
        this.playerVirtualViews.put(nickname, clientView);

        List<StateDTO> states = new ArrayList<>();

        ReconnectDTO state = new ReconnectDTO();

        if (this.currentState instanceof InsufficientPlayerState) {
            this.currentState.onComplete();
            state.setWasInsufficientState(true);
        } else {
            state.setWasInsufficientState(false);
        }

        state.setTargetNickname(nickname)
            .setBoard(this.board.generateState())
            .setResourceBank(this.resourceBank.getResources())
            .setGameLevel(this.level)
            .setCards(this.deck.stream().map(EventCard::generateStaticState).toList())
            .setPlayers(this.players.values().stream().map(p2 -> PlayerJSON.fromPlayer(p2, true)).toList());

        states.add(state);

        states.add(this.currentState.generateState());

        return states;
    }

    /**
     * Configures the game with the provided parameters and sets the initial game state.
     *
     * @param nickname the nickname of the leader player
     * @param playerColor the selected color by the leader player
     * @param level the game level
     * @param numPlayers the number of players participating in the game
     * @param clientView the virtual view associated with the leader player
     * @return the generated state containing the updated game configuration
     * @throws IllegalStateException if the current state does not allow game configuration
     * @throws IllegalArgumentException if any of the provided parameters are invalid
     */
    public StateDTO gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers, VirtualView clientView) throws IllegalStateException, IllegalArgumentException {
        // Set the game configuration sent by the leader
        this.currentState.gameConfig(nickname, playerColor, level, numPlayers);

        this.resourceBank = new ResourceBank(level);
        this.createBoard();

        // Generate the deck after the bank and the board since these values are needed in the loader
        this.generateDeck();

        this.playerVirtualViews.put(nickname, clientView);

        // If all the previous operations are validated we can make the state transition
        this.currentState.onComplete();
        return this.currentState.generateState();
    }

    /**
     * Creates the concrete {@link Board} object based on the game level
     * */
    private void createBoard() {
        switch (this.level) {
            case 0 -> this.board = new BoardTestFlight();
            case 2 -> this.board = new BoardLevel2();
            default -> throw new IllegalStateException("The given game level (" + this.level + ") is not supported");
        }

        this.board.buildBoard();
    }

    /**
     * Constructs and initializes the deck of cards based on the current game level.
     *
     * This method loads event cards from an external source using {@code CardLoader}. The cards are
     * filtered and categorized into different levels based on their data. Depending on the
     * game level, specific subsets of cards are added to create the deck:
     *
     * - Level 0: All available cards are added to the deck.
     * - Level 1: The deck is divided into 4 sub-decks, each containing two level 1 cards.
     * - Level 2: The deck is divided into 4 sub-decks, each containing two level 2 cards and one level 1 card.
     * - Level 3: The deck is divided into 4 sub-decks, each containing two level 3 cards, one level 2 card, and one level 1 card.
     *
     * If the specified game level is invalid, an {@code IllegalStateException} is thrown. After the deck
     * is constructed, it is randomized using {@code Collections.shuffle()}.
     *
     * @throws IllegalStateException if the specified game level is not valid.
     * @throws RuntimeException if an error occurs while reading the JSON file used for loading the cards.
     */
    private void generateDeck() throws IllegalStateException {
        // If the level is equal to 0, then we have loaded all the cards for the test flight
        // Otherwise we need to get the right amount of card for the selected flight level
        CardLoader cardLoader;
        try {
            cardLoader = new CardLoader();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while reading the json file: " + e);
        }

        List<EventCard> tempDeck = cardLoader.getCards(this.board, this.resourceBank, this.level);

        List<EventCard> levelOneDeck = new ArrayList<>(tempDeck.stream().filter(c -> c.getCardLevel() == 1).toList());
        List<EventCard> levelTwoDeck = new ArrayList<>(tempDeck.stream().filter( c -> c.getCardLevel() == 2).toList());
        List<EventCard> levelThreeDeck = new ArrayList<>(tempDeck.stream().filter( c -> c.getCardLevel() == 3).toList());

        switch (this.level) {
            case 0:
                this.deck.addAll(tempDeck);
                break;
            case 1:
                // For the level 1 we have a deck made of 4 sub-decks that contains two lvl one cards
                for (int i = 0; i < 4; i++) {
                    this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                    this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                }
                break;
            case 2:
                // For the level 2 we have a deck made of 4 sub-decks that contains two lvl two cards and one lvl card
                for (int i = 0; i < 4; i++) {
                    this.deck.add(levelTwoDeck.remove(random.nextInt(levelTwoDeck.size())));
                    this.deck.add(levelTwoDeck.remove(random.nextInt(levelTwoDeck.size())));
                    this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                }
                break;
            case 3:
                // For the level 3 we have a deck made of 4 sub-decks that contains two lvl three, a lvl two and a lvl one card
                for (int i = 0; i < 4; i++) {
                    this.deck.add(levelThreeDeck.remove(random.nextInt(levelThreeDeck.size())));
                    this.deck.add(levelThreeDeck.remove(random.nextInt(levelThreeDeck.size())));
                    this.deck.add(levelTwoDeck.remove(random.nextInt(levelOneDeck.size())));
                    this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                }
                break;
            default:
                throw new IllegalStateException("The given game level (" + this.level + ") is not valid");
        }

        Collections.shuffle(this.deck);

//        List<EventCard> AllCards = cardLoader.getCards(getBoard(), this.resourceBank, this.level);
//        List<EventCard> fakeDeck = new ArrayList<>();

//        fakeDeck.add(AllCards.get(0)); // AbandonedShip OK
//        fakeDeck.add(AllCards.get(1)); // AbandonedShip
//        fakeDeck.add(AllCards.get(2)); // AbandonedShip
//        fakeDeck.add(AllCards.get(3)); // AbandonedShip
//        fakeDeck.add(AllCards.get(4)); // AbandonedStation OK
//        fakeDeck.add(AllCards.get(5)); // AbandonedStation
//        fakeDeck.add(AllCards.get(6)); // AbandonedStation
//        fakeDeck.add(AllCards.get(7)); // AbandonedStation
//        fakeDeck.add(AllCards.get(8)); // MeteorShower OK
//        fakeDeck.add(AllCards.get(9)); // MeteorShower
//        fakeDeck.add(AllCards.get(10)); // MeteorShower
//        fakeDeck.add(AllCards.get(11)); // MeteorShower
//        fakeDeck.add(AllCards.get(12)); // MeteorShower
//        fakeDeck.add(AllCards.get(13)); // MeteorShower
//        fakeDeck.add(AllCards.get(14)); // Pirates OK
//        fakeDeck.add(AllCards.get(15)); // Pirates
//        fakeDeck.add(AllCards.get(16)); // VisitPlanets OK
//        fakeDeck.add(AllCards.get(17)); // VisitPlanets
//        fakeDeck.add(AllCards.get(18)); // VisitPlanet
//        fakeDeck.add(AllCards.get(19)); // VisitPlanets
//        fakeDeck.add(AllCards.get(20)); // VisitPlanets
//        fakeDeck.add(AllCards.get(21)); // VisitPlanets
//        fakeDeck.add(AllCards.get(22)); // VisitPlanets
//        fakeDeck.add(AllCards.get(23)); // VisitPlanets
//        fakeDeck.add(AllCards.get(24)); // OpenSpace OK
//        fakeDeck.add(AllCards.get(25)); // OpenSpace
//        fakeDeck.add(AllCards.get(26)); // OpenSpace
//        fakeDeck.add(AllCards.get(27)); // OpenSpace
//        fakeDeck.add(AllCards.get(28)); // OpenSpace
//        fakeDeck.add(AllCards.get(29)); // OpenSpace
//        fakeDeck.add(AllCards.get(30)); // OpenSpace
//        fakeDeck.add(AllCards.get(31)); // Epidemy OK
//        fakeDeck.add(AllCards.get(32)); // Smugglers OK
//        fakeDeck.add(AllCards.get(33)); // Smugglers
//        fakeDeck.add(AllCards.get(34)); // Slavers OK
//        fakeDeck.add(AllCards.get(35)); // Slavers
//        fakeDeck.add(AllCards.get(38)); // Warzone OK
//        fakeDeck.add(AllCards.get(39)); // Warzone
//        fakeDeck.add(AllCards.get(36)); // Stardust OK
//        fakeDeck.add(AllCards.get(37)); // Stardust

//          this.deck.clear();
//          this.deck.addAll(fakeDeck);
    }

    /**
     * Adds a new player to the current game state with the specified nickname and color,
     * and associates the player with a corresponding virtual view (network protocol).
     *
     * @param nickname the unique nickname of the player to be added
     * @param playerColor the color representing the player in the game
     * @param clientView the virtual view object associated with the player
     * @return a list of state representations ({@code StateDTO}) showing the changes made to the game state:
     *          the first element is always the response to the command,
     *          the second element, if present, is the transition to the next state ({@link ShipConstructionDTO})
     * @throws IllegalStateException if the current game state does not allow adding a new player
     * @throws IllegalArgumentException if the provided arguments are invalid or violate game rules
     */
    public List<StateDTO> addNewPlayer(String nickname, PlayerColor playerColor, VirtualView clientView) throws IllegalStateException, IllegalArgumentException {
        List<StateDTO> states = new ArrayList<>();

        this.currentState.addNewPlayer(nickname, playerColor);
        this.playerVirtualViews.put(nickname, clientView);

        states.add(this.currentState.generateState());

        State prev = this.currentState;
        this.currentState.onComplete();

        if (!prev.equals(this.currentState)) {
            states.add(this.currentState.generateState());
        }
        return states;
    }

    /**
     * Selects or deselects a subdeck for a given player based on the provided parameters.
     *
     * @param player the identifier of the player performing the action
     * @param selectedDeck the identifier of the subdeck to be selected or deselected
     * @param isSelectAction a boolean indicating whether to select (true) or deselect (false) the subdeck
     * @return a ConstructionDeckDTO representing the updated state after the action
     * @throws IllegalStateException if the action is not allowed in the current state
     */
    public ConstructionDeckDTO selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws IllegalStateException {
        return this.currentState.selectDeselectSubdeck(player, selectedDeck, isSelectAction);
    }

    /**
     * Selects a tile based on the specified player and tile ID.
     *
     * @param player the identifier of the player selecting the tile
     * @param id the ID of the tile to be selected
     * @return a ConstructionComponentDTO representing the selected tile and its associated data
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    public ConstructionComponentDTO selectTile(String player, Integer id) throws IllegalArgumentException {
        return currentState.selectTile(player, id);
    }

    /**
     * Deselects a tile based on the specified player and tile ID.
     *
     * @param player the identifier of the player deselecting the tile
     * @param id the ID of the tile to be deselected
     * @return a ConstructionComponentDTO representing the deselected tile and its associated data
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    public ConstructionComponentDTO deselectTile(String player, Integer id) throws IllegalArgumentException {
        return currentState.deselectTile(player, id);
    }

    /**
     * Reserve a tile based on the specified player and tile ID.
     *
     * @param playerNickname the identifier of the player reserving the tile
     * @param id the ID of the tile to be reserved
     * @return a ReservedComponentDTO representing the reserved tile and its associated data
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    public ReservedComponentDTO reserveTile(String playerNickname, Integer id) {
        return currentState.reserveTile(playerNickname, id);
    }

    /**
     * Executes the fast shipping operation for the given player and updates the game state accordingly.
     *
     * @param playerNickname the nickname of the player performing the fast shipping action
     * @return a list of {@link StateDTO} objects representing the states generated by the action execution:
     *          the first element is always the response to the command;
     *          the second element, if present, can be:
     *          {@code FixShipDTO} if a player's ship is invalid,
     *          {@code PopulateShipDTO} if all ships are valid but some are missing lifeforms,
     *          or {@code CardRoundDTO} otherwise
     */
    public List<StateDTO> fastShip(String playerNickname) {
        List<StateDTO> states = new ArrayList<>();

        // Execute the command
        StateDTO tmpState = this.currentState.fastShip(playerNickname);
        if (tmpState != null) {
            states.add(tmpState);
        }

        State prev = this.currentState;
        this.currentState.onComplete();
        if (!this.currentState.equals(prev)) {
            states.add(this.currentState.generateState());
        }

        System.out.println(states.size());

        return states;
    }

    /**
     * Places a tile in the game at the specified location and orientation.
     *
     * @param player the identifier of the player placing the tile
     * @param componentID the unique identifier of the tile being placed
     * @param i the row index where the tile will be placed
     * @param j the column index where the tile will be placed
     * @param rotation the rotation of the tile to be placed
     * @return a PlacedComponentDTO object representing the details of the placed tile
     */
    public PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        return currentState.placeTile(player, componentID, i, j, rotation);
    }

    /**
     * Handles the player's action of ending the ship construction phase
     * and returns the new states resulting from the action.
     *
     * @param player the identifier of the player who has ended the ship sending phase
     * @param reservedTiles the number of tiles reserved during the action
     * @return a list of {@link StateDTO} objects representing the states generated by the action execution:
     *            the first element is always the response to the command;
     *            the second element, if present, can be:
     *            {@code FixShipDTO} if a player's ship is invalid,
     *            {@code PopulateShipDTO} if all ships are valid but some are missing lifeforms,
     *            or {@code CardRoundDTO} otherwise
     */
    public List<StateDTO> playerEndedSendShip(String player, Integer reservedTiles) {
        List<StateDTO> states = new ArrayList<>();

        // Execute the command
        StateDTO tmpState = this.currentState.playerEndedSendShip(player, reservedTiles);
        if (tmpState != null) {
            states.add(tmpState);
        }

        State prev = this.currentState;
        this.currentState.onComplete();
        if (!this.currentState.equals(prev)) {
            states.add(this.currentState.generateState());
        }

        return states;
    }

    /**
     * Command used to flip the timer
     * @return TimerDTO that has:
     * - hasBeenFlipped --> if the clock has been flipped
     * - canBeFlipped --> if the clock can be flipped at least another time
     * */
    public TimerDTO flipTimer(String player) throws IllegalStateException {
        return this.currentState.flipTimer(player);
    }

    /**
     * Removes a ship component for the specified player at the given coordinates, updates the game state,
     * and returns a list of resulting state representations.
     *
     * @param player the identifier of the player whose ship is being fixed
     * @param i the 'i' of the component to be removed
     * @param j the 'j' of the component to be removed
     * @return a list of {@link StateDTO} objects representing the states generated by the action execution:
     *             the first element is always the response to the command;
     *             the second element, if present, can be:
     *             {@code PopulateShipDTO} if all ships are valid but some are missing lifeforms,
     *             or {@code CardRoundDTO} otherwise
     * @throws IllegalArgumentException if the input arguments are invalid
     */
    public List<StateDTO> fixShip(String player, Integer i, Integer j) throws IllegalArgumentException {
        List<StateDTO> states = new ArrayList<>();

        StateDTO tmpState = this.currentState.fixShip(player, i, j);
        if (tmpState != null) {
            states.add(tmpState);
        }

        State prev = this.currentState;
        this.currentState.onComplete();
        if (!this.currentState.equals(prev)) {
            states.add(this.currentState.generateState());
        }

        return states;
    }

    /**
     * Populates a ship with a specific lifeform and updates the states accordingly.
     *
     * @param player The name or identifier of the player performing the action.
     * @param lifeformToAdd The lifeform component to be added to the ship.
     * @return a list of updated state objects:
     *          the first element is always the response to the command,
     *          the second element, if present, is a {@code CardRoundDTO}
     * @throws IllegalArgumentException if the provided inputs are invalid or cause an error during execution.
     */
    public List<StateDTO> populateShip(String player, ComponentHelper<LifeformType> lifeformToAdd) throws IllegalArgumentException {
        List<StateDTO> states = new ArrayList<>();

        StateDTO tmpState = this.currentState.populateShip(player, lifeformToAdd);
        if (tmpState != null) {
            states.add(tmpState);
        }

        State prev = this.currentState;
        this.currentState.onComplete();
        if (!this.currentState.equals(prev)) {
            states.add(this.currentState.generateState());
        }

        return states;
    }

    /**
     * Processes the action of playing a card in the current game state.
     * Updates the current state and returns a list of state data transfer objects.
     *
     * @param action the action object that contains the details of the card being played
     * @return a list of {@code StateDTO} objects representing the updated states after the operation has been executed:
     *          the first element is always the response to the command,
     *          the second element, if present, is an {@code EndGameDTO}
     * @throws IllegalArgumentException if the provided action is invalid
     */
    public List<StateDTO> playCard(ActionJSON action) throws IllegalArgumentException {
        List<StateDTO> states = new ArrayList<>();

        List<CardRoundDTO> tmpState = this.currentState.playCard(action);
        if (tmpState != null) {
            states.addAll(tmpState);
        }

        State prev = this.currentState;
        this.currentState.onComplete();
        if (!this.currentState.equals(prev)) {
            states.add(this.currentState.generateState());
        }

        return states;
    }


    // ========================================
    // PACKAGE PRIVATE METHODS --> used by the concrete states
    // ========================================

    // Getters
    /**
     * Retrieves the current game level.
     *
     * @return the current level of the game as an integer
     */
    int getGameLevel() {
        return this.level;
    }

    /**
     * Retrieves the game deck, which is a collection of EventCard objects.
     *
     * @return a List containing the EventCard objects representing the game deck.
     */
    List<EventCard> getGameDeck() {
        return this.deck;
    }

    /**
     * Retrieves the number of players currently in the game.
     *
     * @return the number of players
     */
    int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Retrieves a map of players where the key is a string identifier
     * and the value is the corresponding Player object.
     *
     * @return a map containing player identifiers as keys and Player objects as values
     */
    Map<String, Player> getPlayers() {
        return this.players;
    }

    /**
     * Sets the game level to the specified value.
     *
     * @param level the game level to set, must be between 0 and 3 inclusive
     * @throws IllegalArgumentException if the specified level is outside the valid range (0-3)
     */
    void setGameLevel(int level) throws IllegalArgumentException {
        if (level < 0 || level > 3) {
            throw new IllegalArgumentException("Level must be between 0 and 3");
        }

        this.level = level;
    }

    /**
     * Sets the number of players for the game.
     *
     * @param numPlayers the number of players to set, must be between 2 and 4 inclusive
     * @throws IllegalArgumentException if the number of players is less than 2 or greater than 4
     */
    void setGamePlayersNumber(int numPlayers) throws IllegalArgumentException {
        if (numPlayers < 2 || numPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");
        }

        this.numPlayers = numPlayers;
    }

    /**
     * Retrieves a list of available colors that are not currently in use by players.
     *
     * @return a list of strings representing available colors
     */
    public List<String> getAvailableColors() {
        // Creates a set of already chosen colors
        Set<PlayerColor> assignedColors = new HashSet<>();
        for (Player player : players.values()) {
            assignedColors.add(player.getColor());
        }

        // List with all the possible colors
        List<String> result = new ArrayList<>();
        for (int idx = 0; idx < 4; idx++) {
            PlayerColor candidate = PlayerColor.fromInteger(idx);
            if (!assignedColors.contains(candidate)) {
                result.add(candidate.toString());
            }
        }

        return result;
    }

    /**
     * Adds a new player to the game with the specified nickname and color.
     * Validates that the nickname is not null, not empty, and not already in use,
     * and that the color is not already assigned to another player.
     *
     * @param nickName the nickname of the player to be added
     * @param playerColor the color assigned to the player
     * @throws IllegalArgumentException if the nickname is null, empty, already in use,
     *                                  or if the specified color is already assigned to another player
     */
    void addPlayer(String nickName, PlayerColor playerColor) throws IllegalArgumentException {
        if (nickName == null || playerColor == null || nickName.isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }

        if (this.players.containsKey(nickName)) {
            throw new IllegalArgumentException("Nickname " + nickName + " is already used");
        }

        if (!getAvailableColors().contains(playerColor.toString())) {
            throw new IllegalArgumentException("Color " + playerColor + " is already used");
        }

        Player p = new Player(nickName, playerColor, this.level);
        this.players.put(nickName, p);

    }

    /**
     * Adds a player to the game board.
     *
     * @param player The nickname of the player that needs to be added on the board
     * @throws IllegalArgumentException If the player does not exist or
     *                                  cannot be added to the board.
     */
    void addPlayerToBoard(String player) throws IllegalArgumentException {
        Player p = this.players.get(player);

        this.board.newPlayer(p);
        this.board.addPlayerToBoard(p);
    }

    /**
     * Retrieves the current board.
     *
     * @return the current instance of the Board object
     */
    Board getBoard() {
        return this.board;
    }

    /**
     * Broadcasts an update to all connected players by sending the provided answer information
     * to their respective virtual views.
     *
     * Useful for messages generated by the server (e.g., timer events) that are not
     * related to actions from the clients.
     *
     * @param answer the data to broadcast to all connected players
     */
    void broadCastUpdate(Answer answer) {
        for (Map.Entry<String, VirtualView> entry : this.playerVirtualViews.entrySet()) {
            if (this.players.get(entry.getKey()).isConnected()) {
                this.sendUpdateWithRetries(entry.getValue(), answer);
            }
        }
    }

    /**
     * Sends an update to the specified virtual view with the provided answer and will retry
     * the operation a limited number of times if it fails.
     *
     * @param view   the VirtualView instance to which the update is being sent
     * @param answer the Answer object containing the details of the update to be sent
     */
    private void sendUpdateWithRetries(VirtualView view, Answer answer) {
        GameInstance.sendUpdateWithRetries(view, answer, queueHandler, 0, 3, 2500);
    }
}
