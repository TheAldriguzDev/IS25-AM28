package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Battery;
import it.polimi.ingsw.is25am28.Model.Components.Component;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.Utils.CoordinatePair.CoordinatePair;
import it.polimi.ingsw.is25am28.Utils.Pair.Pair;

import java.util.*;

public class Smugglers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int redItems;
    private final int yellowItems;
    private final int blueItems;
    private final int greenItems;
    private final ResourceBank resourceBank;
    private final int takenItems;
    //private boolean hasBeenDefeated;
    private ArrayList<String> defeatedPlayers;
    //private boolean firstRound;
    private boolean isPlayerDefeated;
    private ArrayList<Player> playersToTakeItemsFrom;
    private Map<String, Integer> updatedPositions;
    private Map<String, List<ComponentHelper<ItemColor>>> droppedResources;
    private Map<String, List<ComponentHelper<ItemColor>>> takenResources;
    private Map<String, List<CoordinatePair>> removedBatteries;
    private List<String> eliminatedPlayers;

    private String prevPlayerNickname;

    public Smugglers(String name, int cardLevel, int movementSteps, int requiredFirepower, int takenItems ,int redItems, int yellowItems,  int greenItems, int blueItems, Board board, ResourceBank resourceBank, int uniqueCardId, String path) {
        super(name, cardLevel, board, uniqueCardId, path);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.redItems = redItems;
        this.yellowItems = yellowItems;
        this.blueItems = blueItems;
        this.greenItems = greenItems;
        this.resourceBank = resourceBank;
        this.takenItems = takenItems;
        //this.hasBeenDefeated = false;
        this.defeatedPlayers = new ArrayList<>();
        //this.firstRound = true;
        this.isPlayerDefeated = false;
        this.playersToTakeItemsFrom = new ArrayList<>();
        this.updatedPositions = new HashMap<>();
        this.droppedResources = new HashMap<>();
        this.takenResources = new HashMap<>();
        this.removedBatteries = new HashMap<>();
        this.eliminatedPlayers = new ArrayList<>();
    }

    /*
     * Se il tipo di response non è corretto la classe
     * lancia un'eccezione di tipo ClassCastException
     * */

//    @Override
//    public void initCardPlayers() throws IllegalArgumentException {
//        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
//            throw new IllegalArgumentException("The player list is null or contains less than two player");
//        } else {
//            if (firstRound) {
//                this.players = new ArrayList<>(this.getBoard().getPlayers());
//            } else {
//                if (!playersToTakeItemsFrom.isEmpty()) {
//                    this.players = new ArrayList<>(this.playersToTakeItemsFrom);
//                }
//            }
//            currentPlayer = Optional.of(players.getFirst());
//        }
//        activateCard();
//    }

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        SmugglersJSON smugglersData;
        try {
            smugglersData = (SmugglersJSON) data;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Card data type in invalid");
        }

        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresentOrElse(
                (Player player) -> {
                    String playerNickname = smugglersData.getPlayerNickname();
                    this.prevPlayerNickname = playerNickname;

                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }

                    if (!this.isPlayerDefeated) {
                        List<Pair<CoordinatePair, CoordinatePair>> activatedDoubleCannons
                                = player.getShip().activateComponents(smugglersData.getDoubleCannonsToActivateCoordinates());

                        // Power consumed by the DoubleCannons
                        if (!activatedDoubleCannons.isEmpty()) {
                            this.removedBatteries.put(
                                    playerNickname,
                                    activatedDoubleCannons.stream()
                                            .map(Pair::getValue)
                                            .toList()
                            );
                        }

                        float playerFirepower = player.getShip().getFirePower(
                                activatedDoubleCannons.stream()
                                        .map(Pair::getKey)
                                        .toList()
                        );

                        if (playerFirepower > requiredFirepower) {
                            cardUsed();
                            if (smugglersData.getTakeLoot()) {
                                bonusEffect(data);
                                getBoard().movePlayerBackwards(player, movementSteps);

                                this.updatedPositions.put(playerNickname, player.getCursor());
                                int tmp = getBoard().getEliminatedPlayers().size();
                                this.getBoard().validatePlayersPosition();

                                for (int i = 0; i < getBoard().getEliminatedPlayers().size() - tmp; i++) { // TODO: This should add the lapped eliminate players to eliminatedPlayers, further testing is required
                                    this.eliminatedPlayers.add(this.getBoard().getEliminatedPlayers().get(tmp - i - 1).getNickname());
                                }
                            }
                        } else if (playerFirepower < requiredFirepower) {
                            this.isPlayerDefeated = true;
                            // playersToTakeItemsFrom.add(player);
                            // malusEffect(smugglersData);
                        }
                    } else {
                        malusEffect(data);
                        this.isPlayerDefeated = false;
                    }

                    // If the player has been defeated the current player
                    // does not change, and the card does not end.
                    if (!isPlayerDefeated) {
                        if (player.equals(players.getLast())) {
                            cardUsed();
                        } else {
                            getNextPlayer();
                        }
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        return this;
    }

    @Override
    protected void bonusEffect(ActionJSON data) throws ClassCastException {
        SmugglersJSON smugglersData = (SmugglersJSON) data;
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
            (Player player) -> {
                List<ComponentHelper<ItemColor>> resourcesToLoad = smugglersData.getItemsToBeTaken();
                List<ComponentHelper<ItemColor>> resourcesToDrop = smugglersData.getItemsToBeRemoved();
                if (!resourcesToDrop.isEmpty()) {
                    this.droppedResources.put(player.getNickname(), resourcesToDrop);
                }
                if (!resourcesToLoad.isEmpty()) {
                    this.takenResources.put(player.getNickname(), resourcesToLoad);
                }
                // Items to drop
                for ( ComponentHelper<ItemColor> resourceDrop : resourcesToDrop) {
                    resourceDrop.getItem().ifPresent( i ->
                            this.resourceBank.addResourceToBankFromPlayer(player, i, resourceDrop.getI(), resourceDrop.getJ()));
                }
                // Items to take
                for ( ComponentHelper<ItemColor> resourceTake : resourcesToLoad) {
                    resourceTake.getItem().ifPresent( i ->
                            this.resourceBank.addResourceToPlayerFromBank(player, i, resourceTake.getI(), resourceTake.getJ()));
                }
            }
        );
    }

    @Override
    protected void malusEffect(ActionJSON data) {
        SmugglersJSON smugglersData = (SmugglersJSON) data;
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
            (Player player) -> {
                // Creates a tmp List of the n=takenItems most valuable item colors in the ship
                List<ItemColor> mostValuableItems = player.getShip().getAllItems().stream()
                        .sorted(Comparator.comparingInt(Item::getValue).reversed())
                        .limit(this.takenItems)
                        .map(Item::getColor)
                        .toList();

                List<ComponentHelper<ItemColor>> resourcesToDrop = smugglersData.getItemsToBeRemoved();

                // Extracts the colors form the resourcesToDrop
                List<ItemColor> colorsToDrop = resourcesToDrop.stream()
                        .map(item -> item.getItem().orElse(null))
                        .toList();

                List<CoordinatePair> stolenBatteries = smugglersData.getBatteriesToBeStolen();
                int batteriesToTake = this.takenItems - resourcesToDrop.size();

                // This covers also the case in which there are not enough resources on board
                if (resourcesToDrop.size() != mostValuableItems.size()) {
                    throw new IllegalArgumentException("The dropped items are not enough!");
                }
                else if (this.countOccurrences(mostValuableItems).equals(colorsToDrop)) {
                    throw new IllegalArgumentException("The dropped items do not correspond to the most valuable items on board!");
                }
                else if (stolenBatteries.size() != batteriesToTake && player.getShip().getAvailableEnergy() != stolenBatteries.size()) {
                    // This exception is triggered only if a wrong number of batteries is sent, the
                    // case in which the player cannot select the required number of batteries is checked
                    throw new IllegalArgumentException("The given up batteries are not enough!");
                } else if (stolenBatteries.size() > batteriesToTake) {
                    throw new IllegalArgumentException("You didn't remove the right amount of batteries, please try again");
                }

                if (!resourcesToDrop.isEmpty()) {
                    this.droppedResources.put(player.getNickname(), resourcesToDrop);
                }

                if (!stolenBatteries.isEmpty()) {
                    this.removedBatteries.put(player.getNickname(), stolenBatteries);
                }

                // Items to drop
                for (ComponentHelper<ItemColor> resourceDrop : resourcesToDrop) {
                    resourceDrop.getItem().ifPresent(i ->
                            this.resourceBank.addResourceToBankFromPlayer(
                                    player,
                                    i,
                                    resourceDrop.getI(),
                                    resourceDrop.getJ()));
                }

                List<CoordinatePair> consumedBatteries = new ArrayList<>();

                if (batteriesToTake > 0) {
                    // Removing 1 unit of charge from each battery selected by the player
                    for (CoordinatePair coords : smugglersData.getBatteriesToBeStolen()) {
                        Component component = player.getShip().getComponent(
                                coords.getI(),
                                coords.getJ()
                        );

                        switch (component) {
                            case Battery battery -> {
                                consumedBatteries.add(coords);
                                batteriesToTake--;
                            }
                            case null, default -> {}
                        }
                    }

                    // Consuming each battery by 1 unit of charge
                    player.getShip().consumeEnergy(consumedBatteries);

                    // Logging the consumed batteries for the current player
                    this.removedBatteries.put(player.getNickname(), consumedBatteries);
                }
            }
        );
    }

    private Map<ItemColor, Integer> countOccurrences(List<ItemColor> colors) {
        Map<ItemColor, Integer> occurrences = new HashMap<>();
        for(ItemColor itemColor : colors) {
            occurrences.put(itemColor, occurrences.getOrDefault(itemColor, 0) + 1);
        }
        return occurrences;
    }

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON smugglersStateJSON = new CardStateJSON();
        smugglersStateJSON.setUniqueCardId(this.uniqueCardId);

        if (hasBeenActivated()) {
            // Initializing the state flags
            initStateFlags(smugglersStateJSON);

            // Setting the playerNickname (if present)
            playerOptional.ifPresent(player -> smugglersStateJSON.setPlayerNickname(player.getNickname()));
            smugglersStateJSON.setPrevPlayerNickname(this.prevPlayerNickname);

            // The clients need to know when to update the right parameters
//            smugglersStateJSON.setFirstRound(this.firstRound);

            // If the first round is finished, send the dynamic info to the players
//            if (!firstRound) {
//                ArrayList<String> defeatedPlayers = new ArrayList<>();
//                for (Player player : playersToTakeItemsFrom) {
//                    defeatedPlayers.add(player.getNickname());
//                }

            smugglersStateJSON.setIsPlayerDefeated(this.isPlayerDefeated);

                // This field is necessary to the clients to know if they need to send additional info
                smugglersStateJSON.setDefeatedPlayers(defeatedPlayers); // TODO: Need more thinking on this

                // Sets the dropped resources (if there are any) // this works both in case of defeat or victory
            setUpdatedDroppedResourcesIfNecessary(smugglersStateJSON, this.droppedResources);

            // Sets the removed batteries (if there are any), due to the smugglers
            setUpdatedRemovedBatteriesIfNecessary(smugglersStateJSON, this.removedBatteries);
            setUpdatedPositionsIfNecessary(smugglersStateJSON, this.updatedPositions);
            setUpdatedTakenResourcesIfNecessary(smugglersStateJSON, this.takenResources);
            setUpdatedEliminatedPlayersIfNecessary(smugglersStateJSON, this.eliminatedPlayers);

        } else {
            // Setting the card's static data
            smugglersStateJSON.setCardTypeId(this.cardTypeId);
            smugglersStateJSON.setCardName(this.getCardName());
            smugglersStateJSON.setImagePath(this.path);
            smugglersStateJSON.setCardLevel(this.getCardLevel());
            smugglersStateJSON.setRequiredFirepower(requiredFirepower);
            smugglersStateJSON.setMovementSteps(movementSteps);
            smugglersStateJSON.setTakenItems(takenItems);
            smugglersStateJSON.setRedItems(redItems);
            smugglersStateJSON.setYellowItems(yellowItems);
            smugglersStateJSON.setBlueItems(blueItems);
            smugglersStateJSON.setGreenItems(greenItems);
        }

        smugglersStateJSON.setCardEnded(this.hasFinished());

        return smugglersStateJSON;
    }

    @Override
    public CardStateJSON generateStaticState() {
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardTypeId(this.cardTypeId);
        cardState.setUniqueCardId(this.uniqueCardId);
        cardState.setCardName(this.getCardName());
        cardState.setImagePath(this.path);
        cardState.setCardLevel(this.getCardLevel());
        cardState.setRequiredFirepower(requiredFirepower);
        cardState.setMovementSteps(movementSteps);
        cardState.setTakenItems(takenItems);
        cardState.setRedItems(redItems);
        cardState.setYellowItems(yellowItems);
        cardState.setBlueItems(blueItems);
        cardState.setGreenItems(greenItems);
        cardState.setImagePath(this.path);

        return cardState;
    }
}
