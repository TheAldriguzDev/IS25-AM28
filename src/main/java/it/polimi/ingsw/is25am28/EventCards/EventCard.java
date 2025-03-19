package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;

public abstract class EventCard {
    protected String name;
    protected int cardLevel;
    protected List<Player> players;
    protected Optional<Player> currentPlayer;
    private Board board;

    /**
     * General constructor shared between the classes
     * */
    protected EventCard(String name, int cardLevel, Board board) {
        this.name = name;
        this.cardLevel = cardLevel;
        this.board = board;
    }

    /**
     * This method is immediately invoked when the card a new card is extracted.
     * Can be overridden to specify different initialization modes (like reverse player order)
     *
     * We do not use the board players list since in some cards the players order could be different
     */
    public void initCardPlayers() throws IllegalArgumentException {
        if ( this.board.getPlayers() == null || this.board.getPlayers().isEmpty() || this.board.getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            this.players = this.board.getPlayers();
            currentPlayer = Optional.of(players.getFirst());
        }
    }

    protected abstract void bonusEffect();

    protected abstract void malusEffect();

    /**
     * Set the currentPlayer to the next player in the game's turn order. If there are no more players left, set the attribute to an empty optional.
     * */
    protected Optional<Player> getNextPlayer() {

        if( players == null || players.isEmpty() ) {
            throw new Error("Players are not set, you must call startUsingCard method before");
        }

        if ( currentPlayer.isPresent() ) {
            if (currentPlayer.get().equals(players.getLast())) {
                return Optional.empty();
            } else {
                currentPlayer = Optional.of(players.get( players.indexOf(currentPlayer.get()) + 1 ));
                return Optional.of(players.get( players.indexOf(currentPlayer.get()) + 1 ));
            }
        } else {
            currentPlayer = Optional.of(players.getFirst());
            return Optional.of(players.getFirst());
        }
    }

    protected Optional<Player> getCurrentPlayer() {
        return currentPlayer;
    }

    protected Board getBoard() {
        return board;
    }

    /**
     * This method will be used in the specific class, but also from outside (game model).
     *
     * It returns true if the current player is the last one of the card players or if there are no active players in the card
     * */
    public boolean hasFinished() {
        return currentPlayer.map(player -> player.equals(players.getLast())).orElse(false);
    }

    public String getCardName() {
        return name;
    }

    public int getCardLevel() {
        return cardLevel;
    }

    /**
     * useCard will be used when a player send some data to the server to complete an action.
     * The method will elaborate the given data and if the actions are valid we return a EventCard that contains the new state that can be return to the client
     * Instead, if the data is not valid we return an exception that will be returned to the client
     *
     * The communication of the new, valid or invalid, state will be sent (broadcast) to all the clients.
     * */

    public abstract EventCard useCard( ActionJSON data ) throws IllegalArgumentException;

    public abstract EventCard useCard(JSONObject data) throws IllegalArgumentException;

    /**
     * generateState return a JSONObject that return the current state of the card. It MUST contains all the specific information like:
     * - currentPlayer
     * - cardName
     * - cardData (e.g. planets list with all the related resources)
     * */
    public abstract CardStateJSON generateState();
}