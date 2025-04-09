package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;

public abstract class EventCard {
    protected String name;
    protected int cardLevel;
    protected List<Player> players;
    protected Optional<Player> currentPlayer;
    private Board board;

    private boolean hasBeenUsed;

    /**
     * General constructor shared between the classes
     * */
    protected EventCard(String name, int cardLevel, Board board) {
        this.name = name;
        this.cardLevel = cardLevel;
        this.board = board;
        this.hasBeenUsed = false;
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
            this.players = new ArrayList<>(this.board.getPlayers());
            currentPlayer = Optional.of(players.getFirst());
        }
    }

    protected abstract void bonusEffect();

    protected abstract void malusEffect();

    /**
     * Set the currentPlayer to the next player in the game's turn order. If there are no more players left, set the attribute to an empty optional.
     * */
    protected Optional<Player> getNextPlayer() {
        if (players == null || players.isEmpty()) {
            throw new Error("Players are not set, you must call initCardPlayers method before");
        }

        if (currentPlayer.isPresent()) {
            int currentIndex = players.indexOf(currentPlayer.get());

            if (currentIndex == players.size() - 1) {
                this.cardUsed();
                return Optional.empty();
            }
            else {
                Player nextPlayer = players.get(currentIndex + 1);
                currentPlayer = Optional.of(nextPlayer);

                // If the current player is disconnected, then get the next one in line
                if ( !currentPlayer.get().isConnected()) {
                    currentPlayer = this.getNextPlayer();
                }

                return currentPlayer;
            }
        }
        else {
            currentPlayer = Optional.of(players.getFirst());

            // If the first player is disconnected, then get the next one in line
            if ( !currentPlayer.get().isConnected()) {
                currentPlayer = this.getNextPlayer();
            }

            return currentPlayer;
        }
    }

    protected Optional<Player> getCurrentPlayer() {
        return currentPlayer;
    }

    protected Board getBoard() {
        return board;
    }

    /**
     * Mark the card as used. In this way the game model can understand when to get the next card
     * */
    protected void cardUsed() {
        this.hasBeenUsed = true;
    }

    /**
     * This method will be used in the specific class, but also from outside (game model).
     *
     * It returns true if the current player is the last one of the card players or if there are no active players in the card
     * */
    public boolean hasFinished() {
        return this.hasBeenUsed;
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

    /**
     * generateState return a JSONObject that return the current state of the card. It MUST contain all the specific information like:
     * - currentPlayer
     * - cardName
     * - cardData (e.g. planets list with all the related resources)
     * */
    public abstract CardStateJSON generateState();
}