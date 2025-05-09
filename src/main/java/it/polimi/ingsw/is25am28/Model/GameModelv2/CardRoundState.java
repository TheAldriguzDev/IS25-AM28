package it.polimi.ingsw.is25am28.Model.GameModelv2;

import it.polimi.ingsw.is25am28.FileLoader.CardLoader;
import it.polimi.ingsw.is25am28.Model.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.CardRoundDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.EventCards.Epidemy;
import it.polimi.ingsw.is25am28.Model.EventCards.EventCard;
import it.polimi.ingsw.is25am28.Model.EventCards.OpenSpace;
import it.polimi.ingsw.is25am28.Model.EventCards.Stardust;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;

import java.util.ArrayList;
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
        //List<EventCard> AllCards = CardLoader.get().read(model.getBoard(), new ResourceBank(), model.getGameLevel());
        //List<EventCard> fakeDeck = new ArrayList<>();
//        fakeDeck.add(AllCards.get(0)); // AbandonedShip
//        fakeDeck.add(AllCards.get(5)); // AbandonedStation
        //fakeDeck.add(AllCards.get(8)); // MeteorSHower
//        fakeDeck.add(AllCards.get(14));
//        fakeDeck.add(AllCards.get(16));
//        fakeDeck.add(AllCards.get(24));
//        fakeDeck.add(AllCards.get(31));
//        fakeDeck.add(AllCards.get(32));
//        fakeDeck.add(AllCards.get(34));
//        fakeDeck.add(AllCards.get(36));
//        fakeDeck.add(AllCards.get(38));
//        fakeDeck.add(AllCards.get(30));
        //this.deck = fakeDeck;
        this.board = this.model.getBoard();
        this.isFirstState = true;

        // Initialize the first card players
        this.deck.getFirst().initCardPlayers();
        this.refreshPlayersShip();
    }

    /**
     * @return the next EventCard that needs to be played. If there are no more card it will return null
     * */
    private EventCard nextRound() {
        round++;

        // Refresh the players ship and the board positions
        this.refreshPlayersShip();
        this.model.getBoard().validatePlayersPosition();

        if (round >= deck.size()) return null;

        // If all the players got eliminated except one we set the round equal to the deck size and return null.
        // In this way the game will transit to the EndGameState. This is due to the impossibility of playing the game
        // with just one player.
        // Otherwise, we can init the card players.
        if (this.model.getBoard().getPlayers().size() <= 1) {
            this.round = this.deck.size();
            return null;
        }

        EventCard card = deck.get(round);

        card.initCardPlayers();

        return card;
    }

    /**
     * Command executed from a player to play the card
     * */
    public List<CardRoundDTO> playCard(ActionJSON action) {
        List<CardRoundDTO> result = new ArrayList<>();

        EventCard card = deck.get(round);

        if (card.hasFinished()) {
            // If the card is already finished, we skip to the next one
            card = this.nextRound();
        } else {
            // Execute the action given by the client
            card = card.useCard(action);

            // If the executed action made the card used, then we need to add the state to the response
            if (card.hasFinished()) {
                CardRoundDTO finishedCardState = new CardRoundDTO()
                        .setRound(this.round)
                        .setCardInfo(card.generateState()); // Contains the information about the finished card

                finishedCardState.setStateName(this.toString());
                result.add(finishedCardState);

                // Get the next card
                card = this.nextRound();
            }
        }

        // If present, set the state of the new card
        if (card != null) {
            CardRoundDTO newCardState = new CardRoundDTO()
                    .setRound(this.round)
                    .setCardInfo(card.generateState());

            newCardState.setStateName(this.toString());

            result.add(newCardState);
        }

        // Always set the first cardRoundDTO to false, as it's the response to the command executed by the player.
        // Instead, if present, set the second state as newCard since it's the new card that has just been drawn.
        for (int i = 0; i < result.size(); i++) {
            if (i == 1) {
                result.get(i).setCardNew(true);
                continue;
            }
            result.get(i).setCardNew(false);
        }

        return result;
    }


    @Override
    public void onComplete() {
        // If all the cards has been played, we go to EndGameState
        if (this.round == this.deck.size()) {
            this.model.setCurrentState(new EndGameState(model, null));
        }
    }

    @Override
    public StateDTO generateState() {
        CardRoundDTO state = new CardRoundDTO();

        // Generate the data for the first state --> we will send more information
        if (this.isFirstState) {
            state = this.generateFirstState();
        } else {
            state.setRound(this.round);
            state.setCardInfo(this.deck.get(this.round).generateState());
            state.setStateName(this.toString());
        }

        return state;
    }

    // TODO: Use this method to substitute the deck with
    //       a fake one only during testing
    public void setFakeDeck(List<EventCard> deck) {
        this.deck = deck;
    }

    public CardRoundDTO generateFirstState() {
        CardRoundDTO state = new CardRoundDTO();

        // Add the board to the state
        state.setBoard(this.board.generateState());

        // Generate the player info that also includes the ship
        Map<String, PlayerJSON> playerInfo = new HashMap<>();
        for (Player p : this.model.getPlayers().values()) {
            playerInfo.put(p.getNickname(), PlayerJSON.fromPlayer(p, true));
        }

        state.setPlayersInfo(playerInfo);
        isFirstState = false;

        state.setRound(this.round);
        state.setCardInfo(this.deck.getFirst().generateState());
        state.setStateName(this.toString());

        return state;
    }

    private void refreshPlayersShip() {
        this.model.getPlayers().values().forEach(player -> {
            player.getShip().generateComponentSubLists();
        });
    }
}
