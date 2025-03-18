package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.AbandonedShipJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Components.Cabin;
import it.polimi.ingsw.is25am28.Lifeform.Lifeform;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Optional;

public class AbandonedShip extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private final int givenCredits;
    private boolean hasBeenUsed;

    public AbandonedShip(String name, int cardLevel, int requireCrew, int movementStep, int givenCredits, Board board) {
        super(name, cardLevel, board);
        this.requiredCrew = requireCrew;
        this.movementStep = movementStep;
        this.givenCredits = givenCredits;
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

            currentPlayer = Optional.of(players.getFirst());

            // if there are no players we do not have to continue, since no one can use the card
            if (this.players.isEmpty()) this.hasBeenUsed = true;
        }
    }

    /**
     * Override needed to end the usage of the card if a previous player already used the card
     * */
    @Override
    public boolean hasFinished() {
        return hasBeenUsed || currentPlayer.map(player -> player.equals(players.getLast())).orElse(false) || (players.isEmpty() && currentPlayer.isEmpty());
    }

    /**
     * This method suppose that the lifeForm list contains the lifeforms that are present in the player cabin
     * */
    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException {
        if (this.getCurrentPlayer().isPresent()) {
            try {
                AbandonedShipJSON abandonedShip = (AbandonedShipJSON) data;

                // Grab the data that we need to compute the action
                String playerNickname = abandonedShip.getPlayerNickname();
                boolean wantsToVisitTheShip = abandonedShip.getVisitShip();
                List<Lifeform> lifeformsToBeRemoved = abandonedShip.getLifeFormToBeRemoved();

                // Check if:
                // 1: The player match X
                // 2: The player wants to visit the ship
                if ( playerNickname != null
                        && !playerNickname.isEmpty()
                        && !playerNickname.equals( this.getCurrentPlayer().get().getNickname()) ) {

                    if (wantsToVisitTheShip) {
                        if (lifeformsToBeRemoved.size() != requiredCrew) {
                            throw new IllegalArgumentException("The lifeformsToBeRemoved size does not match with the card requirements!");
                        } else {
                            this.bonusEffect();

                            // Malus effect
                            // 1. Move the player backwards of the given steps
                            // 2. Remove the lifeform from the players cabin
                            this.getBoard().movePlayerBackwards(this.getCurrentPlayer().get(), this.movementStep);
                            this.getBoard().validatePlayersPosition();

                            for (Lifeform lifeform : lifeformsToBeRemoved) {
                                for (Cabin cabin : this.getCurrentPlayer().get().getShip().getCabinList()) {

                                    // Removes the inhabitant the match the type
                                    boolean removed = cabin.getInhabitants().removeIf(
                                            cabinLifeForm -> lifeform.getLifeformType().equals(cabinLifeForm.getLifeformType())
                                    );

                                    // If the lifeform has been removed we need to proceed to the next one
                                    if (removed) {
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        getNextPlayer();
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

    // Give the credits to the player that used the card
    @Override
    protected void bonusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            this.hasBeenUsed = true;
            this.getCurrentPlayer().get().addCredits(this.givenCredits);
        }
    }

    // Remove the LifeForm from the
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