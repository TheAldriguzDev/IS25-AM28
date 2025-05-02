package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

import javax.smartcardio.Card;
import java.util.*;
import java.util.stream.Collectors;

public class AbandonedStation extends EventCard {
    private final int requiredCrew;
    private final int movementStep;
    private ArrayList<Item> givenItems;
    private ResourceBank resourceBank;

    private List<ComponentHelper<ItemColor>> resourceToDropOff;
    private List<ComponentHelper<ItemColor>> resourceToTake;
    private boolean hasBeenUsedByPlayer;

    private List<String> playersThatCanUseTheCard;

    private Map<String, List<ComponentHelper<ItemColor>>> droppedResources;
    private Map<String, List<ComponentHelper<ItemColor>>> takenResources;
    private Map<String, Integer> updatedPositions;
    private List<String> eliminatedPlayers;

    public AbandonedStation(String name, int cardLevel, int requiredCrew, int movementStep, ArrayList<Item> givenItems, Board board, ResourceBank resourceBank) {
        super(name, cardLevel, board);
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
        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            // Set the players that can use the card
            this.playersThatCanUseTheCard = this.getBoard().getPlayers().stream()
                    .filter( p -> p.getShip().getAllLifeforms().size() >= this.requiredCrew )
                    .map(Player::getNickname)
                    .toList();

            this.players = new ArrayList<>(this.getBoard().getPlayers());
            currentPlayer = Optional.of(players.getFirst());
        }
        cardActivated();
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
                this.hasBeenUsedByPlayer = true;

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

            if (!this.resourceToDropOff.isEmpty()) {
                this.droppedResources.put(this.getCurrentPlayer().get().getNickname(), this.resourceToDropOff);
            }

            // Add the resources from the player to the bank
            for ( ComponentHelper<ItemColor> resourceDrop : this.resourceToDropOff ) {
                resourceDrop.getItem().ifPresent( i ->
                        this.resourceBank.addResourceToBankFromPlayer(
                                this.getCurrentPlayer().get(),
                                i,
                                resourceDrop.getI(),
                                resourceDrop.getJ()));
            }

            if (!this.takenResources.isEmpty()) {
                this.takenResources.put(this.getCurrentPlayer().get().getNickname(), this.resourceToTake);
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
            this.updatedPositions.put(this.getCurrentPlayer().get().getNickname(), this.getCurrentPlayer().get().getCursor());
            int tmp = getBoard().getEliminatedPlayers().size();
            this.getBoard().validatePlayersPosition();
            for (int i = 0; i < getBoard().getEliminatedPlayers().size() - tmp; i++) { // TODO: This should add the lapped eliminate players to eliminatedPlayers, further testing is required
                this.eliminatedPlayers.add(this.getBoard().getEliminatedPlayers().get(tmp - i - 1).getNickname());
            }
        }
    }

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON cardState = new CardStateJSON();

        if (hasBeenActivated()) {
            initStateFlags(cardState);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> cardState.setPlayerNickname(player.getNickname()));

            cardState.setCardIsUsable(playersThatCanUseTheCard.contains(this.getCurrentPlayer().get().getNickname()));
            setUpdatedDroppedResourcesIfNecessary(cardState, droppedResources);
            setUpdatedTakenResourcesIfNecessary(cardState, takenResources);
            setUpdatedPositionsIfNecessary(cardState, updatedPositions);
        } else {
            // Card information that are needed to play
            cardState.setId(this.id);
            cardState.setCardName(this.getCardName());
            cardState.setCardLevel(this.cardLevel);
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
        }
        return cardState;
    }

    public WidgetTUI generateWidget(CardStateJSON abandonedStationJSON) {
        return null;
    }
}

