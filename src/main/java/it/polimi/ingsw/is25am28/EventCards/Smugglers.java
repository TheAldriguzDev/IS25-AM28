package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.*;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.Items.*;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
                    if (player.getShip().getAvailableEnergy() >= (takenItems - resourcesToDrop.size())) {
                        player.getShip().consumeEnergy(takenItems - resourcesToDrop.size());
                    } else {
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
        if(playerOptional.isPresent()) {

            smugglersStateJSON.setPlayerNickname(playerOptional.get().getNickname());
            smugglersStateJSON.setCardName(getCardName());
            smugglersStateJSON.setCardLevel(getCardLevel());
            smugglersStateJSON.setCardIsUsable(!hasFinished());
            smugglersStateJSON.setFirstRound(firstRound);

            if(hasFinished()) {
                // Update the board
                smugglersStateJSON.setBoard(this.getBoard().generateState());

                // Generate the player info that also includes the ship
                Map<String, PlayerJSON> playerInfo = new HashMap<>();
                playerInfo.put(playerOptional.get().getNickname(), PlayerJSON.fromPlayer(this.getCurrentPlayer().get(), true));
                smugglersStateJSON.setPlayersInfo(playerInfo);
            } else {
                if (firstRound) {
                    smugglersStateJSON.setRequiredFirepower(requiredFirepower);
                    smugglersStateJSON.setMovementSteps(movementSteps);
                    // TODO : Resorucebank question about number of items (referring to how it's done in abandonedStation)
                    smugglersStateJSON.setTakenItems(takenItems);
                    smugglersStateJSON.setRedItems(redItems);
                    smugglersStateJSON.setYellowItems(yellowItems);
                    smugglersStateJSON.setBlueItems(blueItems);
                    smugglersStateJSON.setGreenItems(greenItems);
                } else {
                    ArrayList<String> defeatedPlayers = new ArrayList<>();
                    for (Player player : playersToTakeItemsFrom) {
                        defeatedPlayers.add(player.getNickname());
                    }
                    smugglersStateJSON.setDefeatedPlayers(defeatedPlayers);
                }
            }
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }
        return smugglersStateJSON;
    }
}

