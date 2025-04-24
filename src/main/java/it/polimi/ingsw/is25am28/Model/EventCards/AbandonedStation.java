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

        // Card information that are needed to play
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

        // If there is a currentPlayer set it in the DTO
        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }

        // if the card is finished and a player has used it, we can update the clients with the changes
        // otherwise send to the players the card information
        if (this.hasFinished()) {
            if (this.hasBeenUsedByPlayer) {
                // Update the board
                //cardState.setBoard(this.getBoard().generateState());

                // Generate the player info that also includes the ship
                //Map<String, PlayerJSON> playerInfo = new HashMap<>();
                //playerInfo.put(this.currentPlayer.get().getNickname(), PlayerJSON.fromPlayer(this.getCurrentPlayer().get(), true));
                //cardState.setPlayersInfo(playerInfo);

                // Info that other players can use to update their version of this player's ship
                cardState.setResourcesToDrop(this.resourceToDropOff);
                cardState.setResourcesToTake(this.resourceToTake);
            }
        } else {
            // If the player can use the card the flag will be set to true, otherwise if it doesn't have the card requirement it
            // will be set to false
            if (this.currentPlayer.isPresent()) {
                cardState.setCardIsUsable(playersThatCanUseTheCard.contains(this.getCurrentPlayer().get().getNickname()));
            }
        }

        return cardState;
    }

    public WidgetTUI generateWidget(CardStateJSON abandonedStationJSON) {
        return null;
    }
}

