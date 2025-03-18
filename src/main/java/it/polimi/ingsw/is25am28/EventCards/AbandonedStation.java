package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.AbandonedStationJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbandonedStation extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private ArrayList<Item> givenItems;
    private ResourceBank resourceBank;
    private boolean hasBeenUsed;

    private List<Item> resourceToDropOff;
    private List<Item> resourceToTake;


    public AbandonedStation(String name, int cardLevel, int requiredCrew, int movementStep, ArrayList<Item> givenItems, Board board, ResourceBank resourceBank) {
        super(name, cardLevel, board);
        this.requiredCrew = requiredCrew;
        this.movementStep = movementStep;
        this.givenItems = givenItems;
        this.resourceBank = resourceBank;
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
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException, IllegalStateException {
        if (this.getCurrentPlayer().isPresent()) {

            try {
                AbandonedStationJSON abandonedStation = (AbandonedStationJSON) data;

                // Check if the player match with the current one to apply the action
                String playerNickname = abandonedStation.getPlayerNickname();
                if ( playerNickname != null
                        && !playerNickname.isEmpty()
                        && !playerNickname.equals( this.getCurrentPlayer().get().getNickname()) ) {

                    // When a player decide to visit the ship we need to mark the card as used. Then
                    // 1. get the list of the resource he wants to drop off --> and drop them
                    // 2. get the list of the resources he wants to take
                    // 3. move the player of the card required step

                    if ( abandonedStation.getVisitStation() ) {

                        // Get the resources that needs to be used in the computation
                        this.resourceToDropOff = abandonedStation.getResourcesToDropOff();
                        this.resourceToTake = abandonedStation.getResourcesToTake();

                        // Check if the resource to take are in a valid number
                        List<Item> availableItems = this.givenItems.stream()
                                .filter(this.resourceBank.getResources()::contains)
                                .toList();

                        if (!availableItems.containsAll(resourceToTake)) {
                            throw new IllegalStateException("The resourceToTake are more than the available ones");
                        }

                        this.bonusEffect();
                        this.malusEffect();
                    } else {
                        // get the next player to continue the game (state transition)
                        this.getNextPlayer();
                    }
                } else {
                    throw new IllegalArgumentException("The given player does not match with the current one");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error while parsing the user requested action: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        return this;
    }

    @Override
    protected void bonusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            this.hasBeenUsed = true;

            // Drop the player resources
            for (Item resource : this.resourceToDropOff) {
                this.resourceBank.addResourceToBankFromPlayer(resource, this.getCurrentPlayer().get());
            }

            // Give to the player the selected resources
            for (Item resource : this.resourceToTake) {
                this.resourceBank.addResourceToBankFromPlayer(resource, this.getCurrentPlayer().get());
            }
        }
    }

    @Override
    protected void malusEffect() {
        // Move the player of the given steps
        this.getBoard().movePlayerBackwards(this.getCurrentPlayer().get(), this.movementStep);
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

