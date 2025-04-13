package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Ship.Ship;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AbandonedShip extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private final int givenCredits;

    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;

    public AbandonedShip(String name, int cardLevel, int requireCrew, int movementStep, int givenCredits, Board board) {
        super(name, cardLevel, board);
        this.requiredCrew = requireCrew;
        this.movementStep = movementStep;
        this.givenCredits = givenCredits;
        this.lifeformsToBeRemoved = new ArrayList<>();
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
                    .filter( p -> p.getShip().getAllLifeforms().size() >= this.requiredCrew )
                    .toList();

            // if there are no players we do not have to continue, since no one can use the card
            if (this.players.isEmpty()) {
                this.cardUsed();
                this.currentPlayer = Optional.empty();
            } else {
                this.currentPlayer = Optional.of(players.getFirst());
            }
        }
    }

    /**
     * This method suppose that the lifeForm list contains the lifeforms that are present in the player cabin
     * */
    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        // Check if there is a player playing the card
        if (this.currentPlayer.isEmpty()) {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        AbandonedShipJSON abandonedShip;

        try {
            abandonedShip = (AbandonedShipJSON) data;
        } catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid abandonedShip JSON");
        }

        // Retrieve the data from the JSON
        String playerNickname = abandonedShip.getPlayerNickname();
        boolean wantsToVisitTheShip = abandonedShip.getWantToVisitShip();

        // Check if:
        // 1. The player match with the current one
        if ( playerNickname != null &&
                !playerNickname.isEmpty() &&
                playerNickname.equals( this.getCurrentPlayer().get().getNickname()) ) {

            // If the player wants to use the card --> perform the action
            // otherwise get the next player
            if (wantsToVisitTheShip) {
                this.lifeformsToBeRemoved = abandonedShip.getLifeformsToBeRemoved();

                // Check if the given input is valid
                if (lifeformsToBeRemoved.size() != this.requiredCrew) {
                    throw new IllegalArgumentException("The lifeformsToBeRemoved size does not match with the card requirements!");
                } else {

                    // Apply the bonus effects --> give the credits
                    this.bonusEffect();

                    // Apply the malus effects --> move the player and remove the required crew members
                    this.malusEffect();
                }

            } else {
                this.getNextPlayer();
            }

        } else {
            throw new IllegalArgumentException("The given player does not match with the current one!");
        }

        return this;
    }

    // Give the credits to the player that used the card
    @Override
    protected void bonusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            this.cardUsed();
            this.getCurrentPlayer().get().addCredits(this.givenCredits);
        }
    }

    /**
     * Move the player of the set step
     * Remove the crew members from the given cabins
     * */
    @Override
    protected void malusEffect() throws IllegalStateException {
        if (this.getCurrentPlayer().isPresent()) {
            this.cardUsed();

            // Move the player and re-validate the positions
            this.getBoard().movePlayerBackwards(this.getCurrentPlayer().get(), this.movementStep);
            this.getBoard().validatePlayersPosition();

            Ship ship = this.getCurrentPlayer().get().getShip();

            // Remove the crew members from the given cabins
            for (ComponentHelper<LifeformType> lifeform : this.lifeformsToBeRemoved) {

                Cabin tmpCabin;
                try {
                    tmpCabin = (Cabin) ship.getComponent(lifeform.getI(), lifeform.getJ());
                } catch (Exception e) {
                    throw new IllegalStateException("The given component is not a valid cabin");
                }

                lifeform.getItem().ifPresent( l -> {

                    Lifeform tmpLifeFormToBeRemoved = tmpCabin.getInhabitants().stream()
                            .filter( i -> i.getLifeformType().equals(l))
                            .findFirst()
                            .orElseThrow( () -> new NoSuchElementException("The requested lifeform has not been found in the given cabin"));

                    tmpCabin.removeInhabitant(tmpLifeFormToBeRemoved);
                });
            }

            // Check if the player has finished all of its astronauts --> if yes it needs to be eliminated from the game
            if (ship.getCabinList().stream().flatMap(c -> c.getInhabitants().stream()).noneMatch(i -> i.getLifeformType().equals(LifeformType.ASTRONAUT))) {
                this.getBoard().eliminatePlayer(this.getCurrentPlayer().get());
            }
        }
    }

    @Override
    public CardStateJSON generateState() {
        CardStateJSON cardState = new CardStateJSON();

        // Set the card name
        cardState.setCardName(this.getCardName());
        // Set the card level
        cardState.setCardLevel(this.cardLevel);
        // If present set the current player (the one that needs to play the game)
        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        List<Player> playersThatCanUseTheCard = this.getBoard().getPlayers().stream()
                .filter( p -> p.getShip().getAllLifeforms().size() > this.requiredCrew )
                .toList();

        // Set the card isUsable to true when the player has at least the required crew members
        // --> since we filter them in advance should be always set to true
        cardState.setCardIsUsable(playersThatCanUseTheCard.contains(this.getCurrentPlayer().get()));

        // Set the card information that are needed to play
        cardState.setRequiredCrewMembers(this.requiredCrew);
        cardState.setGivenCredits(this.givenCredits);
        cardState.setMovementSteps(this.movementStep);

        return cardState;
    }

    @Override
    public WidgetTUI generateWidget(CardStateJSON cardState) {
        return null;
    }

    @Override
    public WidgetTUI generateWidget() {
        return null;
    }
}