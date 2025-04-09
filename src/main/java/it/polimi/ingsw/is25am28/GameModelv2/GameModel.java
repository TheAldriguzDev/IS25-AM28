package it.polimi.ingsw.is25am28.GameModelv2;

import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Board.BoardLevel2;
import it.polimi.ingsw.is25am28.Board.BoardTestFlight;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.GameModel.FileLoader.CardLoader;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Player.PlayerColor;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;

import java.util.*;
import java.util.stream.Collectors;

public class GameModel {
    private int level; // The level range can be 0 - 1 - 2 - 3
    private Board board;
    private final List<EventCard> deck;
    private final ResourceBank resourceBank;
    private int numPlayers;
    private final Map<String, Player> players;
    private State currentState;

    private final Random random = new Random();

    public GameModel() {
        this.deck = new ArrayList<>();
        this.resourceBank = new ResourceBank();
        this.players = new HashMap<>();
        this.numPlayers = 2; // min value
        this.currentState = new CreateGameState(this);
    }

    /**
     * @return the currentState of the game
     * */
    public State getCurrentState() {
        return this.currentState;
    }

    /**
     * Set the currentState of the game, needed to make the state transaction
     * */
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public StateJSON generateState() {
        return this.currentState.generateState();
    }

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
        List<EventCard> tempDeck = CardLoader.get().read(this.board, this.resourceBank, this.level);

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
                    this.deck.add(levelTwoDeck.remove(random.nextInt(levelOneDeck.size())));
                    this.deck.add(levelTwoDeck.remove(random.nextInt(levelOneDeck.size())));
                    this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                }
                break;
            case 3:
                // For the level 3 we have a deck made of 4 sub-decks that contains two lvl three, a lvl two and a lvl one card
                for (int i = 0; i < 4; i++) {
                    this.deck.add(levelThreeDeck.remove(random.nextInt(levelOneDeck.size())));
                    this.deck.add(levelThreeDeck.remove(random.nextInt(levelOneDeck.size())));
                    this.deck.add(levelTwoDeck.remove(random.nextInt(levelOneDeck.size())));
                    this.deck.add(levelOneDeck.remove(random.nextInt(levelOneDeck.size())));
                }
                break;
            default:
                throw new IllegalStateException("The given game level (" + this.level + ") is not valid");
        }
    }

    public void gameConfig(String nickname, PlayerColor playerColor, int level, int numPlayers) {
        // Set the game configuration sent by the leader
        this.currentState.gameConfig(nickname, playerColor, level, numPlayers);

        // Create the board based on the given level
        switch (level) {
            case 0:
                this.board = new BoardTestFlight();
                break;
            case 2:
                this.board = new BoardLevel2();
                break;
            default:
                throw new IllegalStateException("The given game level (" + this.level + ") is not valid");
        }

        // Generate the match deck
        this.generateDeck();

        // TODO: generate the tiles

        // If all the previous operations are validated we can make the state transaction
        this.currentState.onComplete();
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

    List<String> getAvailableColors() {
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
        if (this.players.containsKey(nickName)) {
            throw new IllegalArgumentException("Nickname " + nickName + " is already used");
        }

        Player p = new Player(nickName, playerColor, this.level);
        this.players.put(nickName, p);

        return this.players.size() == this.numPlayers;
    }

}
