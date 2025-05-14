package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.Loader.CardLoader;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
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
     * @return the currentState of the game
     * */
    public State getCurrentState() {
        return this.currentState;
    }

    /**
     * Set the currentState of the game, needed to make the state transition
     * */
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public StateDTO generateState() {
        return this.currentState.generateState();
    }

    /**
     * @return a list of StateDTO where:
     * 1. The first state represent the response to the client disconnection, so it will be a DisconnectedPlayerDTO
     * 2. If there is only one player left, then the game will be in the InsufficientPlayerState, so we add this state
     *     to the response
     * */
    public List<StateDTO> disconnectClient(String nickname) throws IllegalArgumentException{
        Player p = this.players.get(nickname);
        if (p == null) {
            throw new IllegalArgumentException("The given player cannot be disconnected since it doesn't exist");
        }

        // Set the player connection to false
        p.setConnected(false);

        List<StateDTO> states = new ArrayList<>();
        states.add(new DisconnectedPlayerDTO().setNickname(nickname));

        List<Player> connectedPlayers = this.players.values().stream().filter(Player::isConnected).toList();
        if (connectedPlayers.size() == 1) {
            this.setCurrentState(new InsufficientPlayerState(this, this.currentState));
            states.add(this.currentState.generateState());
        }

        return states;
    }

    /**
     * @return the list of disconnected players. It
     * */
    public List<String> getDisconnectedPlayers() {
        return this.players.values()
                .stream()
                .filter(p -> !p.isConnected())
                .map(Player::getNickname).toList();
    }

    /**
     * @return a list of DTO containing useful information about:
     * 1. The response to a client reconnection --> will be used from the reconnected player to resume the game
     *     and from other players to set the player as connected
     * 2. If we had a state transition from InsufficientPlayerState to any other state, the new state will be added to the response
     * */
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

        State prev = this.currentState;
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

//    public List<StateDTO> reconnectClient(String nickname, VirtualView clientView) throws Exception {
//        if (!this.getDisconnectedPlayers().contains(nickname)) {
//            throw new IllegalArgumentException("The given nickname does not exist in the disconnected players");
//        }
//
//        Player p = this.players.get(nickname);
//        if (p == null) {
//            throw new IllegalArgumentException("The given nickname does not exist");
//        }
//        p.setConnected(true);
//        this.playerVirtualViews.put(nickname, clientView);
//
//        List<StateDTO> states = new ArrayList<>();
//
//        // ===== RECONNECT DTO INFO ===== //
//        // Get the current information that the client needs to resume the game
//        ReconnectDTO state = new ReconnectDTO()
//                .setTargetNickname(nickname);
//        state.setBoard(this.board.generateState()); // Board information
//        state.setResourceBank(this.resourceBank.getResources()); // resourceBank information
//        state.setGameLevel(this.level); // Game level
//
//        List<CardStateJSON> cardsInfo = new ArrayList<>();
//        for (EventCard card : this.deck) {
//            cardsInfo.add(card.generateStaticState());
//        }
//
//        state.setCards(cardsInfo);
//
//        // Players information
//        List<PlayerJSON> playerInfo = new ArrayList<>();
//        for (Player player : this.players.values()) {
//            playerInfo.add(PlayerJSON.fromPlayer(player, true));
//        }
//        state.setPlayers(playerInfo);
//        state.setCurrentState(this.currentState.generateState());
//
//        states.add(state);
//
//        // Check if the current state is InsufficientPlayerState
//        State prev = this.currentState;
//        if (this.currentState instanceof InsufficientPlayerState) {
//            this.currentState.onComplete(); // Try to make the state transition --> if two players are
//                                            // connected the game will resume
//        }
//
//        // If we had a state updated, then we need to generate the state to resume the game
//        if (!prev.equals(this.currentState)) {
//            states.add(this.currentState.generateState());
//        }
//
//        return states;
//    }

    /**
     * generateDeck() set the game deck by extracting the correct amount of cards. For each level the deck will be of:
     * Test flight --> The 8 cards that are used for every test flight
     * Level 1 --> 8 level 1 cards
     * Level 2 --> 4 sub-decks of two lvl 2 card and one lvl 1 card
     * Level 3 --> 4 sub-decks of two lvl 3 card, one lvl 2 card and one lvl 1 card
     *
     * The deck is not sorted
     * */
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

        List<EventCard> AllCards = cardLoader.getCards(getBoard(), new ResourceBank(getGameLevel()), getGameLevel());
        List<EventCard> fakeDeck = new ArrayList<>();
//        fakeDeck.add(AllCards.get(8)); // MeteorShower
        fakeDeck.add(AllCards.get(38)); // WarZone
//        fakeDeck.add(AllCards.get(30)); // OpenSpace
//        fakeDeck.add(AllCards.get(24)); // OpenSpace
//        fakeDeck.add(AllCards.get(0)); // AbandonedShip
//        fakeDeck.add(AllCards.get(5)); // AbandonedStation
//        fakeDeck.add(AllCards.get(14)); // Pirates
//        fakeDeck.add(AllCards.get(16)); // VisitPlanets1
//        fakeDeck.add(AllCards.get(31)); // Epidemy
//        fakeDeck.add(AllCards.get(32)); // Smugglers
//        fakeDeck.add(AllCards.get(33)); // Smugglers
//        fakeDeck.add(AllCards.get(34)); // Slavers
//        fakeDeck.add(AllCards.get(36)); // Stardust

        this.deck.clear();
        this.deck.addAll(fakeDeck);
    }

    /**
     * Initializes the game configuration as defined by the leader.
     * Sets the game level, number of players, and creates the leader as the first player.
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

    private void createBoard() {
        switch (this.level) {
            case 0 -> this.board = new BoardTestFlight();
            case 2 -> this.board = new BoardLevel2();
            default -> throw new IllegalStateException("The given game level (" + this.level + ") is not supported");
        };

        this.board.buildBoard();
    }

    /**
     * Add a new player to the game.
     * @return a List of states that represent:
     * 1. The response of the action of the command
     * 2. If all the players have joined it will also include the nextState information
     * */
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

    public ConstructionDeckDTO selectDeselectSubdeck(String player, Integer selectedDeck, Boolean isSelectAction) throws IllegalStateException {
        return this.currentState.selectDeselectSubdeck(player, selectedDeck, isSelectAction);
    }

    /**
     * Execute the command to select a tile
     * @return the ConstructionComponentDTO that represent the selectedTile. The behavior of the communication sendTo / sendToAll
     * is left to the controller
     * */
    public ConstructionComponentDTO selectTile(String player, Integer id) throws IllegalArgumentException {
        return currentState.selectTile(player, id);
    }

    /**
     * Execute the command to deselect a tile
     * @return the ConstructionComponentDTO that represent the selectedTile. The behavior of the communication sendTo / sendToAll
     * is left to the controller
     * */
    public ConstructionComponentDTO deselectTile(String player, Integer id) throws IllegalArgumentException {
        return currentState.deselectTile(player, id);
    }

    /**
     * Execute the command to place a tile
     * @return PlacedComponentDTO that contains the information about the placed component of the player
     * */
    public PlacedComponentDTO placeTile(String player, Integer componentID, Integer i, Integer j, Integer rotation) {
        return currentState.placeTile(player, componentID, i, j, rotation);
    }

    /**
     * Command used when a player finish his ship or the time has ended to send the created ship
     * @return the list of states that are required to update the client:
     * 1. The result of the command executed by the client
     * 2. If all the players has sent the ship it will return the new state. This could be: FixShip if some player has an
     * invalid ship or populateShip if all the players have a valid ship
     * */
    public List<StateDTO> playerEndedSendShip(String player, int reservedTiles) {
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
     * Command executed by the client to fix his ship
     * @return a list of state that are required to update the client:
     * 1. Contains the response of the executed command
     * 2. If all the players have fixed their ship, it contains the PopulateShipState information
     * */
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
     * Command executed by the client to populate his ship
     * @return a list of state that are required to update the client:
     * 1. Contains the response of the executed command
     * 2. If all the players has populated their ship, it contains the CardRoundState information
     * */
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
     * Command executed by the clients to play a card
     * @return a list of StateJSON:
     * 1. Contains the response of the command
     * 2. If all the cards are finished it will also include the nextState or rather EndGameState
     * */
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
    // PACKAGE PRIVATE METHODS --> used by the states
    // ========================================

    // Getters
    int getGameLevel() {
        return this.level;
    }

    List<EventCard> getGameDeck() {
        return this.deck;
    }

    int getNumPlayers() {
        return this.numPlayers;
    }

    Map<String, Player> getPlayers() {
        return this.players;
    }

    /**
     * Set the game level
     * */
    void setGameLevel(int level) throws IllegalArgumentException {
        if (level < 0 || level > 3) {
            throw new IllegalArgumentException("Level must be between 0 and 3");
        }

        this.level = level;
    }

    /**
     * Set the numbers of players that the game will have
     * */
    void setGamePlayersNumber(int numPlayers) throws IllegalArgumentException {
        if (numPlayers < 2 || numPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");
        }

        this.numPlayers = numPlayers;
    }

    public List<String> getAvailableColors() {
        Set<PlayerColor> used = players.values().stream()
                .map(Player::getPlayerColor)
                .collect(Collectors.toSet());

        List<PlayerColor> available = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            available.add(PlayerColor.fromInteger(i));
        }

        available.removeIf(used::contains);

        return available.stream()
                .map(PlayerColor::toString)
                .collect(Collectors.toList());
    }

    /**
     * If the given nickname is available the player will be created and added to the game
     * @return if the game has the required numbers of players in the lobby
    */
    boolean addPlayer(String nickName, PlayerColor playerColor) throws IllegalArgumentException {
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

        return this.players.size() == this.numPlayers;
    }

    void addPlayerToBoard(String player) throws IllegalArgumentException {
        this.board.newPlayer(this.players.get(player));
        this.board.addPlayerToBoard(this.players.get(player));
    }

    Board getBoard() {
        return this.board;
    }

    /**
     * @return a map that associate each player with his VirtualView --> this is needed to update the clients
     * when some events occurred on the server (e.g. onTimerEnd)
     * */
    Map<String, VirtualView> getVirtualViews() {
        return new HashMap<>(this.playerVirtualViews);
    }

    /**
     * Method used by the states to update the clients on certain events
     * */
    void broadCastUpdate(Answer answer) {
        for (Map.Entry<String, VirtualView> entry : this.playerVirtualViews.entrySet()) {
            if (this.players.get(entry.getKey()).isConnected()) {
                try {
                    VirtualView virtualView = entry.getValue();
                    queueHandler.enqueue(() -> {
                        try {
                            virtualView.updateState(answer);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
