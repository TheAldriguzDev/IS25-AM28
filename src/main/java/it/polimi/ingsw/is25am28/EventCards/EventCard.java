package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;

public abstract class EventCard {
    protected String name;
    protected int cardLevel;
    protected List<Player> players;
    protected Optional<Player> currentPlayer;

    /**
     * General constructor shared between the classes
     * */
    protected EventCard(String name, int cardLevel) {
        this.name = name;
        this.cardLevel = cardLevel;
    }

    /**
     * This method is immediately invoked when the card a new card is extracted.
     * Can be overridden to specify different initialization modes (like reverse player order)
     */
    public void initCardPlayers( List<Player> players ) throws IllegalArgumentException {
        if ( players == null || players.isEmpty() || players.size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            this.players = players;
            currentPlayer = Optional.of(players.getFirst());
        }
    }

    public EventCard(String name, int cardLevel) {
        this.name = name;
        this.cardLevel = cardLevel;
    }

    protected abstract void bonusEffect();

    protected abstract void malusEffect();

    // We will override this method if we need a more specific usage
    protected Player getNextPlayer() {

        if( players == null || players.isEmpty() )
            throw new Error("Players are not set, you must call startUsingCard method before");

        if ( currentPlayer.isPresent() ) {
            currentPlayer = Optional.of(players.get( players.indexOf(currentPlayer.get()) + 1 ));
            return players.get( players.indexOf(currentPlayer.get()) + 1 );
        } else {
            currentPlayer = Optional.of(players.getFirst());
            return players.getFirst();
        }
    }

    protected Optional<Player> getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean hasFinished(){
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

    public abstract EventCard useCard( JSONObject data ) throws IllegalArgumentException;

    /**
     * generateState return a JSONObject that return the current state of the card. It MUST contains all the specific information like:
     * - currentPlayer
     * - cardName
     * - cardData (e.g. planets list with all the related resources)
     * */
    public abstract JSONObject generateState();
}