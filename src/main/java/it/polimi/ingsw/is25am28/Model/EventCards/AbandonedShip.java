package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Cabin;
import it.polimi.ingsw.is25am28.Model.Lifeform.Lifeform;
import it.polimi.ingsw.is25am28.Model.Lifeform.LifeformType;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.Ship.Ship;

import java.util.*;

public class AbandonedShip extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private final int givenCredits;

    private List<ComponentHelper<LifeformType>> lifeformsToBeRemoved;
    private Map <String, List<ComponentHelper<LifeformType>>> removedLifeforms;
    private List<String> eliminatedPlayers;
    private Map<String, Integer> updatedPositions;
    private String prevPlayerNickname;

    private boolean hasBeenUsedByPlayer;

    private List<String> playersThatCanUseTheCard;

    public AbandonedShip(String name, int cardLevel, int requireCrew, int movementStep, int givenCredits, Board board, int uniqueCardId, String path) {
        super(name, cardLevel, board, uniqueCardId, path);
        this.requiredCrew = requireCrew;
        this.movementStep = movementStep;
        this.givenCredits = givenCredits;
        this.lifeformsToBeRemoved = new ArrayList<>();
        this.hasBeenUsedByPlayer = false;
        this.playersThatCanUseTheCard = new ArrayList<>();
        this.removedLifeforms = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
        this.updatedPositions = new HashMap<>();
    }

    /**
     * Override the method to set only the players that can effectively use the card
     * */
    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            this.playersThatCanUseTheCard = this.getBoard().getPlayers().stream()
                    .filter( p -> p.getShip().getAllLifeforms().size() >= this.requiredCrew )
                    .map(Player::getNickname)
                    .toList();

            this.players = new ArrayList<>(this.getBoard().getPlayers());
            currentPlayer = Optional.of(players.getFirst());
        }
        activateCard();
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
        this.prevPlayerNickname = playerNickname;
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

                if (lifeformsToBeRemoved.size() != this.requiredCrew) {

                    throw new IllegalArgumentException("You didn't remove the right amount of crew members, please try again");

                } else {
                    this.removedLifeforms.put(playerNickname, this.lifeformsToBeRemoved);
                    // Apply the bonus effects --> give the credits
                    this.bonusEffect();
                    this.hasBeenUsedByPlayer = true;

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
            this.updatedPositions.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getCursor());
            int tmp = getBoard().getEliminatedPlayers().size();
            this.getBoard().validatePlayersPosition();
            for (int i = 0; i < getBoard().getEliminatedPlayers().size() - tmp; i++) { // TODO: This should add the lapped eliminate players to eliminatedPlayers, further testing is required
                this.eliminatedPlayers.add(this.getBoard().getEliminatedPlayers().get(tmp - i - 1).getNickname());
            }

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
                this.eliminatedPlayers.add(this.getCurrentPlayer().get().getNickname());
                this.getBoard().eliminatePlayer(this.getCurrentPlayer().get());
            }
        }
    }

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON cardState = new CardStateJSON();
        cardState.setUniqueCardId(this.uniqueCardId);

        if (hasBeenActivated()) {
            initStateFlags(cardState);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));
            cardState.setPrevPlayerNickname(this.prevPlayerNickname);

            cardState.setCardIsUsable(playersThatCanUseTheCard.contains(this.getCurrentPlayer().get().getNickname()));
            if (this.hasBeenUsedByPlayer) {
                setUpdatedRemovedLifeformsIfNecessary(cardState, this.removedLifeforms);
                setUpdatedEliminatedPlayersIfNecessary(cardState, this.eliminatedPlayers);
                setUpdatedPositionsIfNecessary(cardState, this.updatedPositions);
            }
        } else {
            // Set the card information that are needed to play the game
            cardState.setCardTypeId(this.cardTypeId);
            cardState.setUniqueCardId(this.uniqueCardId);
            cardState.setCardName(this.getCardName());
            cardState.setImagePath(this.path);
            cardState.setCardLevel(this.cardLevel);
            cardState.setRequiredCrewMembers(this.requiredCrew);
            cardState.setGivenCredits(this.givenCredits);
            cardState.setMovementSteps(this.movementStep);
        }

        cardState.setCardEnded(this.hasFinished());

        return cardState;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardTypeId(this.cardTypeId);
        cardState.setUniqueCardId(this.uniqueCardId);
        cardState.setCardName(this.getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(this.cardLevel);
        cardState.setRequiredCrewMembers(this.requiredCrew);
        cardState.setGivenCredits(this.givenCredits);
        cardState.setMovementSteps(this.movementStep);
        cardState.setImagePath(this.path);

        return cardState;
    }
}
