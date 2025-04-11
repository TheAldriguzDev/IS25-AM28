package it.polimi.ingsw.is25am28.GameModelv2;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.PlayerJSON;
import it.polimi.ingsw.is25am28.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.PopulateShipDTO;
import it.polimi.ingsw.is25am28.ActionJSON.State.StateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CardRoundState extends State {
    // round indicates the current round number, and it's used to draw a card from the deck
    private int round;
    private List<EventCard> deck;
    private final Board board;

    private boolean isFirstState;

    public CardRoundState(GameModel model) {
        super(model);

        this.round = 0;
        this.deck = this.model.getGameDeck();
        this.board = this.model.getBoard();
        this.isFirstState = true;

        this.deck.getFirst().initCardPlayers();
        System.out.println(this.deck.getFirst().getCardName());
    }

    /**
     * @return the next EventCard that needs to be played. If there are no more card it will return null
     * */
    private EventCard nextRound() {
        round++;

        if (round >= deck.size()) return null;

        EventCard card = deck.get(round);
        card.initCardPlayers();

        return card;
    }

    /**
     * Command executed from a player to play the card
     * */
    public CardRoundDTO playCard(ActionJSON action) {
        // Get the current card
        EventCard card = deck.get(round);

        // If the current card has already finished, skip to the next card
        if (card.hasFinished()) {
            card = this.nextRound();
        } else {
            // Otherwise use the card
            card = card.useCard(action);

            // After the card execution, check if it's finished --> if so we need to get the next card
            if (card.hasFinished()) {
                card = this.nextRound();
            }
        }

        // Build the state to be returned
        CardRoundDTO state = new CardRoundDTO()
                .setRound(this.round);
        state.setStateName(this.toString());

        if (card != null) {
            state.setCardInfo(card.generateState());
        }

        return state;
    }

    @Override
    public void onComplete() {
        // If all the cards has been played, we go to EndGameState
        if (this.round == this.deck.size() - 1) {
            this.model.setCurrentState(new EndGameState(model));
        }
    }

    @Override
    public StateJSON generateState() {
        CardRoundDTO state = new CardRoundDTO();

        // Generate the data for the first state --> we will send more information
        if (this.isFirstState) {
            // Add the board to the state
            state.setBoard(this.board.generateState());

            // Generate the player info that also includes the ship
            Map<String, PlayerJSON> playerInfo = new HashMap<>();
            for (Player p : this.model.getPlayers().values()) {
                playerInfo.put(p.getNickname(), PlayerJSON.fromPlayer(p, true));
            }

            state.setPlayersInfo(playerInfo);
            isFirstState = false;
        }

        state.setRound(this.round);
        state.setCardInfo(this.deck.get(this.round).generateState());
        state.setStateName(this.toString());

        return state;
    }

    public void setFakeDeck(List<EventCard> deck) {
        this.deck = deck;
    }
}
