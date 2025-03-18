package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Player.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

public class AbandonedStation extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private ArrayList<Item> item;
    private boolean hasBeenUsed;

    public AbandonedStation(String name, int cardLevel, int requiredCrew, int movementStep, ArrayList<Item> item, Board board) {
        super(name, cardLevel, board);
        this.requiredCrew = requiredCrew;
        this.movementStep = movementStep;
        this.item = item;
        this.hasBeenUsed = false;
    }

    /**
     * Override the method to set only the players that can effectively use the card
     * */
    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            this.players = this.getBoard().getPlayers().stream()
                    .filter( p -> p.getShip().getAllLifeforms().size() > this.requiredCrew )
                    .toList();

            // if there are no players we do not have to continue, since no one can use the card
            if (this.players.isEmpty()) {
                this.hasBeenUsed = true;
                this.currentPlayer = Optional.empty();
            } else {
                this.currentPlayer = Optional.of(players.getFirst());
            }
        }
    }

    /**
     * Override needed to end the usage of the card if a previous player already used the card
     * */
    @Override
    public boolean hasFinished() {
        return hasBeenUsed || currentPlayer.map(player -> player.equals(players.getLast())).orElse(false) || (players.isEmpty() && currentPlayer.isEmpty());
    }

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        if (this.getCurrentPlayer().isPresent()) {





        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        return this;
    }

    @Override
    public EventCard useCard(JSONObject data) throws IllegalArgumentException {
        return null;
    }

    @Override
    protected void bonusEffect() {

    }

    @Override
    protected void malusEffect() {

    }

    @Override
    public JSONObject generateState() {
        CardStateJSON cardState = new CardStateJSON();

        cardState.setCardName(this.getCardName());
        cardState.setCardLevel(this.getCardLevel());

        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        return cardState.getData();
    }
}

