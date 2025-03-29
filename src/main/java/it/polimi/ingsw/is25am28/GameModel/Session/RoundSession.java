package it.polimi.ingsw.is25am28.GameModel.Session;

import java.util.List;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import org.json.simple.JSONObject;
import it.polimi.ingsw.is25am28.EventCards.EventCard;

public class RoundSession {
    // CONSTANTS
    static private final int DECK_SIZE = 8;


    //private final HashSet<Player> players;

    private final List<EventCard> deck;

    // indicate the round number and the card to draw from the deck
    private int round = 0;

    public RoundSession( int level, List<EventCard> deck ){
        this.deck = deck;
    }

    /**
     *
     * @return next event card. If the cards ended, null is returned instead
     */
    private EventCard nextRound(){

        round++;

        if( round == DECK_SIZE )
            return null;

        EventCard card = deck.get( round );
        card.initCardPlayers();

        return card;
    }

    private RoundSession useCard( Object response ){
        EventCard card = deck.get( round );

        if( card.hasFinished() ){
            throw new Error("state updated when the card has finished to apply its effects to all players");
        }

        card.useCard( (ActionJSON) response );

        return this;
    }

    private boolean hasCurrentCardFinished(){
        EventCard card = deck.get( round );

        return card.hasFinished();
    }


    /**
     * make the game state to progress,
     * updating the card state with the player response
     * @param response
     * @return
     */
    public Object play( Object response ){
        EventCard nextCard;

        useCard(response);

        if( hasCurrentCardFinished() ){
            nextCard = nextRound();
        }else {
            nextCard = deck.get( round );
        }

        //TODO send game ended
        return nextCard != null ? nextCard.generateState() : null;
    }
}