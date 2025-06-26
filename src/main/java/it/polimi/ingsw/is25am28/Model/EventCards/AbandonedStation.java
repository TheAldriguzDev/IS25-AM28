package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;

import java.util.*;

public class AbandonedStation extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private ArrayList<Item> givenItems;
    private ResourceBank resourceBank;

    private List<ComponentHelper<ItemColor>> resourceToDropOff;
    private List<ComponentHelper<ItemColor>> resourceToTake;

    private List<String> playersThatCanUseTheCard;

    private Map<String, List<ComponentHelper<ItemColor>>> droppedResources;
    private Map<String, List<ComponentHelper<ItemColor>>> takenResources;
    private Map<String, Integer> updatedPositions;
    private List<String> eliminatedPlayers;

    private String prevPlayerNickname;

    // Constructor
    public AbandonedStation(
            String name,
            int cardLevel,
            int requiredCrew,
            int movementStep,
            ArrayList<Item> givenItems,
            Board board,
            ResourceBank resourceBank,
            int uniqueCardId,
            String path
    ) {
        super(name, cardLevel, board, uniqueCardId, path);

        this.requiredCrew = requiredCrew;
        this.movementStep = movementStep;
        this.givenItems = givenItems;
        this.resourceBank = resourceBank;
        this.droppedResources = new HashMap<>();
        this.takenResources = new HashMap<>();
        this.updatedPositions = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
    }

    /**
     * Override the method to set only the players that can effectively use the card
     * */
    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if (this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        }
        else {
            // Set the players that can use the card
            this.playersThatCanUseTheCard = this.getBoard().getPlayers().stream()
                    .filter(p -> p.getShip().getAllLifeforms().size() >= this.requiredCrew)
                    .map(Player::getNickname)
                    .toList();

            this.players = new ArrayList<>(this.getBoard().getPlayers());
            this.currentPlayer = Optional.of(this.players.getFirst());
        }

        activateCard();
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
        }
        catch (Exception e) {
            throw new IllegalArgumentException("The given JSON data is not a valid abandonedStation JSON");
        }

        // Retrieve the data from the JSON
        String playerNickname = abandonedStation.getPlayerNickname();
        this.prevPlayerNickname = playerNickname;
        boolean wantsToVisitTheShip = abandonedStation.getWantToVisitStation();

        // Check if:
        // 1. The player match with the current one
        if (
                (playerNickname != null) &&
                (!playerNickname.isEmpty()) &&
                (playerNickname.equals( this.getCurrentPlayer().get().getNickname()))
        ) {
            // When a player decide to visit the ship we need to mark the card as used. Then
            // 1. get the list of the resource he wants to drop off --> and drop them
            // 2. get the list of the resources he wants to take
            // 3. move the player of the card required step
            if (wantsToVisitTheShip) {
                // Retrieve the resources needed for the computation
                this.resourceToDropOff = abandonedStation.getItemsToBeRemoved();
                this.resourceToTake = abandonedStation.getItemsToBeTaken();

                this.bonusEffect();
                this.malusEffect();
            }
            else {
                this.getNextPlayer();
            }
        }
        else {
            throw new IllegalArgumentException("The given player does not match with the current one!");
        }

        return this;
    }

    @Override
    protected void bonusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            this.cardUsed();

            if (!this.resourceToDropOff.isEmpty()) {
                this.droppedResources.put(this.getCurrentPlayer().get().getNickname(), this.resourceToDropOff);
            }

            // Add the resources from the player to the bank
            for (ComponentHelper<ItemColor> resourceDrop : this.resourceToDropOff) {
                resourceDrop.getItem().ifPresent( i ->
                    this.resourceBank.addResourceToBankFromPlayer(
                        this.getCurrentPlayer().get(),
                        i,
                        resourceDrop.getI(),
                        resourceDrop.getJ()
                    )
                );
            }

            if (!this.resourceToTake.isEmpty()) {
                this.takenResources.put(this.getCurrentPlayer().get().getNickname(), this.resourceToTake);
            }

            // Add the resources from the bank to the player
            for ( ComponentHelper<ItemColor> resourceTake : this.resourceToTake ) {
                resourceTake.getItem().ifPresent( i ->
                    this.resourceBank.addResourceToPlayerFromBank(
                        this.getCurrentPlayer().get(),
                        i,
                        resourceTake.getI(),
                        resourceTake.getJ()
                    )
                );
            }
        }
    }

    @Override
    protected void malusEffect() {
        if (this.getCurrentPlayer().isPresent()) {
            // Move the player of the given steps and re-validate the positions
            this.getBoard().movePlayerBackward(this.getCurrentPlayer().get(), this.movementStep);
            this.updatedPositions.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getCursor());

            // Revalidate the board position and add the lapped players to the eliminated players
            this.eliminatedPlayers.addAll(this.getBoard().validatePlayersPosition());
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
            // The prevPlayer's storages are always updated locally in this card
            cardState.setSkipStoragesUpdate(true);

            cardState.setCardIsUsable(playersThatCanUseTheCard.contains(this.getCurrentPlayer().get().getNickname()));

            // Setting the JSON's fields only if necessary
            setUpdatedDroppedResourcesIfNecessary(cardState, droppedResources);
            setUpdatedTakenResourcesIfNecessary(cardState, takenResources);
            setUpdatedPositionsIfNecessary(cardState, updatedPositions);
        }
        else {
            // Card information that are needed to play
            cardState.setCardTypeId(this.cardTypeId);
            cardState.setCardName(this.getCardName());
            cardState.setImagePath(this.path);
            cardState.setCardLevel(this.cardLevel);
            cardState.setRequiredCrewMembers(this.requiredCrew);
            cardState.setMovementSteps(this.movementStep);
            cardState.setStationResources(new ArrayList<>(this.givenItems.stream().map(Item::getColor).toList()));
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
        cardState.setMovementSteps(this.movementStep);
        cardState.setStationResources(new ArrayList<>(this.givenItems.stream().map(Item::getColor).toList()));
        cardState.setImagePath(this.path);

        return cardState;
    }
}
