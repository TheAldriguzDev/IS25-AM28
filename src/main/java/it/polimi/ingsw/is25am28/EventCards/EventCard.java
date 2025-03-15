package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Player.Player;
import java.util.List;

public abstract class EventCard {
    protected String name;
    protected int cardLevel;
    private List<Player> players;
    private int currentPlayer;

    public EventCard(String name, int cardLevel) {
        this.name = name;
        this.cardLevel = cardLevel;
    }

    protected abstract void bonusEffect();

    protected abstract void malusEffect();

    protected Player getNext() {

        if( players == null )
            throw new Error("Players are not settled, you must call startUsingCard method before");

        currentPlayer++;

        return players.get(currentPlayer);
    }

    protected Player getCurrent() {

        if( players == null )
            throw new Error("Players are not settled, you must call startUsingCard method before");

        return players.get(currentPlayer);
    }

    public boolean hasFinished(){
        if( currentPlayer >= players.size() - 1 )
            return false;
        return true;
    }

    /**
     * to call first when the card is extracted.
     * In here will be encapsulated initialization of the card.
     */
    public void startUsingCard( List<Player> players ){
        this.players = players;
        currentPlayer = 0;
    }

    public String getCardName() {
        return name;
    }

    public int getCardLevel() {
        return cardLevel;
    }

    /**
     * the effect applied to each player.
     * return a request to send to the turn player, that
     * contains a list of all effects and changes applied to the turn player.
     * The request must be broadcasted
     */
    public abstract EventCard useCard( Object response );
    public abstract Object generateState();
}