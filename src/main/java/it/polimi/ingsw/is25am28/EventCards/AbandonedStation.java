package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.AbandonedStationJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class AbandonedStation extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private ArrayList<Item> givenItems;
    private ResourceBank resourceBank;

    private List<ComponentHelper<ItemColor>> resourceToDropOff;
    private List<ComponentHelper<ItemColor>> resourceToTake;


    public AbandonedStation(String name, int cardLevel, int requiredCrew, int movementStep, ArrayList<Item> givenItems, Board board, ResourceBank resourceBank) {
        super(name, cardLevel, board);
        this.requiredCrew = requiredCrew;
        this.movementStep = movementStep;
        this.givenItems = givenItems;
        this.resourceBank = resourceBank;
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

    @Override
    public EventCard useCard(ActionJSON data) throws IllegalArgumentException, IllegalStateException {
        // Check if there is a player playing the card
        if (this.currentPlayer.isEmpty()) {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }

        AbandonedStationJSON abandonedStation;

        try {
            abandonedStation = (AbandonedStationJSON) data;
        } catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid abandonedStation JSON");
        }

        // Retrieve the data from the JSON
        String playerNickname = abandonedStation.getPlayerNickname();
        boolean wantsToVisitTheShip = abandonedStation.getWantToVisitStation();

        // Check if:
        // 1. The player match with the current one
        if ( playerNickname != null &&
                !playerNickname.isEmpty() &&
                playerNickname.equals( this.getCurrentPlayer().get().getNickname()) ) {

            // When a player decide to visit the ship we need to mark the card as used. Then
            // 1. get the list of the resource he wants to drop off --> and drop them
            // 2. get the list of the resources he wants to take
            // 3. move the player of the card required step
            if (wantsToVisitTheShip) {
                // Retrieve the resources needed for the computation
                this.resourceToDropOff = abandonedStation.getItemsToBeRemoved();
                this.resourceToTake = abandonedStation.getItemsToBeTaken();

                // TODO: Try to understand if we need to add some more checks on the resource we need to take / drop

                this.bonusEffect();
                this.malusEffect();
            } else {
                this.getNextPlayer();
            }
        } else {
            throw new IllegalArgumentException("The given player does not match with the current one!");
        }

        return this;
    }

    @Override
    protected void bonusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            this.cardUsed();

            // Add the resources from the player to the bank
            for ( ComponentHelper<ItemColor> resourceDrop : this.resourceToDropOff ) {
                resourceDrop.getItem().ifPresent( i ->
                        this.resourceBank.addResourceToBankFromPlayer(
                                this.getCurrentPlayer().get(),
                                i,
                                resourceDrop.getI(),
                                resourceDrop.getJ()));
            }

            // Add the resources from the bank to the player
            for ( ComponentHelper<ItemColor> resourceTake : this.resourceToTake ) {
                resourceTake.getItem().ifPresent( i ->
                        this.resourceBank.addResourceToPlayerFromBank(
                                this.getCurrentPlayer().get(),
                                i,
                                resourceTake.getI(),
                                resourceTake.getJ()));
            }
        }
    }

    @Override
    protected void malusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            // Move the player of the given steps and re-validate the positions
            this.getBoard().movePlayerBackwards(this.getCurrentPlayer().get(), this.movementStep);
            this.getBoard().validatePlayersPosition();
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
        cardState.setMovementSteps(this.movementStep);

        // Filter the resources to the only available in the bank.
        // The numbers of the resources will be set as the min between the given by the card and the available in the bank
        Map<ItemColor, Integer> givenItemByTypeCount = givenItems.stream()
                .collect(Collectors.groupingBy(
                        Item::getColor,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        givenItemByTypeCount.replaceAll((c, _) -> Math.min(givenItemByTypeCount.get(c), this.resourceBank.getResourceAvailabilityFromColor(c)));

        List<ItemColor> itemList = givenItemByTypeCount.entrySet().stream()
                .flatMap(entry -> Collections.nCopies(entry.getValue(), entry.getKey()).stream())
                .toList();

        cardState.setStationResources(itemList);

        return cardState;
    }
}

