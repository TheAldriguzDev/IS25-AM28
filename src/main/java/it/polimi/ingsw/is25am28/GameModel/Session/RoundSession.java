package it.polimi.ingsw.is25am28.GameModel.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.State.FirstRoundState;

public class RoundSession extends Session {
    // CONSTANTS
    static private final int DECK_SIZE = 8;


    //private final HashSet<Player> players;

    private final List<EventCard> deck;

    // indicate the round number and the card to draw from the deck
    private int round = 0;

    private final Board board;

    public RoundSession( Board board, int level, List<EventCard> deck ){
        this.deck = deck;
        this.board = board;
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
     * initialize the rounds and send the first card
     */
    public FirstRoundState init(){
        Map<String,List<Map<String, Object>>> ships = new HashMap<>();

        board
        .getPlayers()
        .forEach( player -> ships.put( 
                                player.getNickname(), 
                                player.getShip().generateState() 
                            ) 
                );
        deck.get(0).initCardPlayers();

        return new FirstRoundState()
            .setBoard(board.generateState())
            .setShips(ships)
            .setCard(deck.get(0).generateState());
    }

    /**
     * make the game state to progress,
     * updating the card state with the player response
     */
    public CardStateJSON playCard( Object response ){
        EventCard nextCard;

        useCard( response );

        if( hasCurrentCardFinished() ){
            nextCard = nextRound();
        }else {
            nextCard = deck.get( round );
        }

        if( nextCard == null ){
            setHasFinished();
            return null;
        }

        return nextCard.generateState();
    }
}