package it.polimi.ingsw.is25am28.Model.EventCards;

import it.polimi.ingsw.is25am28.Model.ActionJSON.*;
import it.polimi.ingsw.is25am28.Model.Board.Board;
import it.polimi.ingsw.is25am28.Model.Components.Battery;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;
import it.polimi.ingsw.is25am28.Model.ResourceBank.ResourceBank;
import it.polimi.ingsw.is25am28.TUI.WidgetTUI.WidgetTUI;

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
    private boolean hasBeenDefeated;
    private ArrayList<String> defeatedPlayers;
    private boolean firstRound;
    private ArrayList<Player> playersToTakeItemsFrom;
    private Map<String, Integer> updatedPositions;
    private Map<String, List<ComponentHelper<ItemColor>>> droppedResources;
    private Map<String, List<ComponentHelper<ItemColor>>> takenResources;
    //private Map<String, List<ComponentHelper<Battery>>> removedBatteries;
    private Map<String, Integer> removedBatteries;

    public Smugglers(String name, int cardLevel, int movementSteps, int requiredFirepower, int takenItems ,int redItems, int yellowItems,  int greenItems, int blueItems, Board board, ResourceBank resourceBank) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.redItems = redItems;
        this.yellowItems = yellowItems;
        this.blueItems = blueItems;
        this.greenItems = greenItems;
        this.resourceBank = resourceBank;
        this.takenItems = takenItems;
        this.hasBeenDefeated = false;
        this.defeatedPlayers = new ArrayList<>();
        this.firstRound = true;
        this.playersToTakeItemsFrom = new ArrayList<>();
        this.updatedPositions = new HashMap<>();
        this.droppedResources = new HashMap<>();
        this.takenResources = new HashMap<>();
        this.removedBatteries = new HashMap<>();
    }

    /*
     * Se il tipo di response non è corretto la classe
     * lancia un'eccezione di tipo ClassCastException
     * */

    @Override
    public void initCardPlayers() throws IllegalArgumentException {
        if ( this.getBoard().getPlayers() == null || this.getBoard().getPlayers().isEmpty() || this.getBoard().getPlayers().size() < 2 ) {
            throw new IllegalArgumentException("The player list is null or contains less than two player");
        } else {
            if (firstRound) {
                this.players = new ArrayList<>(this.getBoard().getPlayers());
            } else {
                if (!playersToTakeItemsFrom.isEmpty()) {
                    this.players = new ArrayList<>(this.playersToTakeItemsFrom);
                }
            }
            currentPlayer = Optional.of(players.getFirst());
        }
        cardActivated();
    }

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        SmugglersJSON smugglersData;
        try {
            smugglersData = (SmugglersJSON) data;
        } catch (ClassCastException e) {
            throw new ClassCastException("Card data type in invalid");
        }

        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresentOrElse(
                (Player player) -> {

                    String playerNickname = smugglersData.getPlayerNickname();
                    if (playerNickname == null || playerNickname.isEmpty() || !playerNickname.equals(player.getNickname())) {
                        throw new IllegalArgumentException("The given player does not match with the current one");
                    }
                    if (firstRound) {
                        float playerFirepower = player.getShip().getFirePower(smugglersData.getDoubleCannonsToActivateCoordinates());
                        if (playerFirepower > requiredFirepower) {
                            // // Pirates defeated, even if the player who defeated them does not take the resources, the card won't be used by other players
                            hasBeenDefeated = true;
                            //cardUsed();
                            if (smugglersData.getTakeLoot()) {
                                bonusEffect(data);
                                getBoard().movePlayerBackwards(player, movementSteps);
                                this.updatedPositions.put(playerNickname, player.getCursor());
                                getBoard().validatePlayersPosition();
                            }
                        } else if (playerFirepower < requiredFirepower) {
                            playersToTakeItemsFrom.add(player);
                            //malusEffect(smugglersData);
                        }
                    }
                    if (!firstRound) {
                        if (playersToTakeItemsFrom.contains(player)) {
                            malusEffect(smugglersData);
                        }
                    }
                    if (player.equals(players.getLast())) {
                        if (firstRound) {
                            firstRound = false;
                            if (playersToTakeItemsFrom.isEmpty()) {
                                cardUsed();
                            } else {
                                initCardPlayers();
                            }
                        } else {
                            cardUsed();
                        }
                    } else {
                        getNextPlayer();
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        return this;
    }

    protected void bonusEffect(ActionJSON data) throws ClassCastException {
        SmugglersJSON smugglersData = (SmugglersJSON) data;
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    ArrayList<ComponentHelper<ItemColor>> resourcesToLoad = smugglersData.getItemsToBeTaken();
                    ArrayList<ComponentHelper<ItemColor>> resourcesToDrop = smugglersData.getItemsToBeRemoved();
                    this.droppedResources.put(player.getNickname(), resourcesToDrop);
                    this.takenResources.put(player.getNickname(), resourcesToLoad);
                    // Item da lasciare per fare spazio
                    for ( ComponentHelper<ItemColor> resourceDrop : resourcesToDrop) {
                        resourceDrop.getItem().ifPresent( i ->
                                this.resourceBank.addResourceToBankFromPlayer(player, i, resourceDrop.getI(), resourceDrop.getJ()));
                    }
                    // Item da caricare sulla nave
                    for ( ComponentHelper<ItemColor> resourceTake : resourcesToLoad) {
                        resourceTake.getItem().ifPresent( i ->
                                this.resourceBank.addResourceToPlayerFromBank(player, i, resourceTake.getI(), resourceTake.getJ()));
                    }
                }
        );
    }

    @Override
    protected void bonusEffect() {}

    protected void malusEffect(ActionJSON data) throws ClassCastException {
        SmugglersJSON smugglersData = (SmugglersJSON) data;
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {

                    ArrayList<ComponentHelper<ItemColor>> resourcesToDrop = smugglersData.getItemsToBeRemoved();

                    // Item da lasciare
                    for ( ComponentHelper<ItemColor> resourceDrop : resourcesToDrop) {
                        resourceDrop.getItem().ifPresent( i ->
                                this.resourceBank.addResourceToBankFromPlayer(
                                        player,
                                        i,
                                        resourceDrop.getI(),
                                        resourceDrop.getJ()));
                    }

                    // Nel caso gli item da lasciare non siano abbastanza (check lato client), batterie da rimuovere
                    //player.getShip().consumeEnergy(smugglersData.getTakenBatteries());
                    // Le batterie da rimuovere solo nel caso la lista di elementi da rimuovere non isa abbastanza grande, il client farà il controllo di fare la lista di elementi da togliore il piu grande possibile se non è possibile raggiungere una grandezza pari a takenItems
                    // TODO: usare il component helper per le batterie
                    if (player.getShip().getAvailableEnergy() >= (takenItems - resourcesToDrop.size())) {
                        this.removedBatteries.put(player.getNickname(), takenItems - resourcesToDrop.size());
                        player.getShip().consumeEnergy(takenItems - resourcesToDrop.size());
                    } else {
                        this.removedBatteries.put(player.getNickname(), player.getShip().getAvailableEnergy());
                        player.getShip().consumeEnergy(player.getShip().getAvailableEnergy());
                    } // Se viene presa più energia di quanta ne è disponibile semplicemente va a 0

                }
        );
    }

    @Override
    protected void malusEffect() {}

    @Override
    public CardStateJSON generateState() {
        Optional<Player> playerOptional = getCurrentPlayer();
        CardStateJSON smugglersStateJSON = new CardStateJSON();

        if (hasBeenActivated()) {
            smugglersStateJSON.setNeedsBoardUpdate(false);
            smugglersStateJSON.setNeedsShipUpdate(false);
            smugglersStateJSON.setNeedsPlayerUpdate(false);

            playerOptional.ifPresent(player -> smugglersStateJSON.setPlayerNickname(player.getNickname()));

            // The clients need to know when to update the right parameters
            smugglersStateJSON.setFirstRound(this.firstRound);

            // If the first round is finished, send the dynamic info to the players
            if (!firstRound) {
                ArrayList<String> defeatedPlayers = new ArrayList<>();
                for (Player player : playersToTakeItemsFrom) {
                    defeatedPlayers.add(player.getNickname());
                }
                smugglersStateJSON.setDefeatedPlayers(defeatedPlayers);
                // Sets the dropped resources (if there are any) // this works both in case of defeat or victory
                if (!this.droppedResources.isEmpty()) {
                    smugglersStateJSON.setNeedsShipUpdate(true);
                    smugglersStateJSON.setNeedsUpdatedDroppedResources(true);
                    smugglersStateJSON.setDroppedResources(this.droppedResources);
                    this.droppedResources.clear();
                } else {
                    smugglersStateJSON.setNeedsUpdatedDroppedResources(false);
                }
                // Sets the removed batteries (if there are any)
                if (!this.removedBatteries.isEmpty()) {
                    smugglersStateJSON.setNeedsShipUpdate(true);
                    smugglersStateJSON.setNeedsUpdatedBatteries(true);
                    smugglersStateJSON.setRemovedBatteries(this.removedBatteries);
                    this.removedBatteries.clear();
                } else {
                    smugglersStateJSON.setNeedsUpdatedBatteries(false);
                }
            }
            // if the smugglers have been defeated we need to set the rewards (if taken)
            if (this.hasBeenDefeated && !updatedPositions.isEmpty()) {
                smugglersStateJSON.setNeedsBoardUpdate(true);
                smugglersStateJSON.setNeedsUpdatedPositions(true);
                smugglersStateJSON.setUpdatedPositions(updatedPositions);
                this.updatedPositions.clear();
                // Sets the taken resources (if there are any)
                if (!this.takenResources.isEmpty()) {
                    smugglersStateJSON.setNeedsShipUpdate(true);
                    smugglersStateJSON.setNeedsUpdatedTakenResources(true);
                    smugglersStateJSON.setTakenResources(this.takenResources);
                    this.takenResources.clear();
                } else {
                    smugglersStateJSON.setNeedsUpdatedTakenResources(false);
                }
            }
        } else {
            smugglersStateJSON.setId(this.id);
            smugglersStateJSON.setCardName(this.getCardName());
            smugglersStateJSON.setCardLevel(this.getCardLevel());
            smugglersStateJSON.setRequiredFirepower(requiredFirepower);
            smugglersStateJSON.setMovementSteps(movementSteps);
            // TODO : Resourcebank question about number of items (referring to how it's done in abandonedStation)
            smugglersStateJSON.setTakenItems(takenItems);
            smugglersStateJSON.setRedItems(redItems);
            smugglersStateJSON.setYellowItems(yellowItems);
            smugglersStateJSON.setBlueItems(blueItems);
            smugglersStateJSON.setGreenItems(greenItems);
        }
        return smugglersStateJSON;
    }

    public WidgetTUI generateWidget(CardStateJSON smugglersStateJSON) {
        WidgetTUI cardWidget = new WidgetTUI();
        WidgetTUI cardInfoWidget = new WidgetTUI();

        cardWidget.appendString("====" + smugglersStateJSON.getCardName().toUpperCase() + "====");

        if (firstRound) {
            cardInfoWidget.appendString("Level: " + smugglersStateJSON.getCardLevel());
            cardInfoWidget.appendString("Days: " + smugglersStateJSON.getMovementSteps());
            cardInfoWidget.appendString("Require Firepower: " + smugglersStateJSON.getRequiredFirepower());
            cardInfoWidget.appendString("Red items: " + smugglersStateJSON.getRedItems());
            cardInfoWidget.appendString("Yellow items: " + smugglersStateJSON.getYellowItems());
            cardInfoWidget.appendString("Blue items: " + smugglersStateJSON.getBlueItems());
            cardInfoWidget.appendString("Green items: " + smugglersStateJSON.getGreenItems());
            cardInfoWidget.appendString("Taken items: " + smugglersStateJSON.getTakenItems());
        }
        else {
            cardInfoWidget.appendString("Player: " + smugglersStateJSON.getPlayerNickname() + " has to drop " + smugglersStateJSON.getTakenItems() + " items");
        }
        cardInfoWidget.wrapWidgetWithBorder();

        return WidgetTUI.composeTwoWidgetsVertically(cardWidget, cardInfoWidget).centerWidgetScreen().wrapWidgetWithBorder();
    }
}

