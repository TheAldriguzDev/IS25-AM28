package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.ActionJSON.SmugglersJSON;
import it.polimi.ingsw.is25am28.Items.*;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.ResourceBank.ResourceBank;
import org.json.simple.JSONObject;

import java.util.ArrayList;
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
    }

    /*
     * Se il tipo di response non è corretto la classe
     * lancia un'eccezione di tipo ClassCastException
     * */

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

                    if(player.getShip().getFirePower(smugglersData.getNumberOfDoubleCannonsActivated()) > requiredFirepower) {
                        // // Pirates defeated, even if the player who defeated them does not take the resources, the card won't be used by other players
                        cardUsed();
                        if (smugglersData.getTakeLoot()) {
                            bonusEffect();
                            getBoard().movePlayerBackwards(player, movementSteps);
                            getBoard().validatePlayersPosition();
                            //player.setCursor(player.getCursor() - this.movementSteps);
                        }
                    } else {
                        malusEffect(smugglersData);
                    }
                    if (player.equals(this.players.getLast())) {
                        this.cardUsed(); // Mark the card as used
                        this.getBoard().validatePlayersPosition();
                    } else {
                        this.getNextPlayer();
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
        CardStateJSON smugglersStateJSON;
        if(playerOptional.isPresent()) {
            smugglersStateJSON = new CardStateJSON(
                    playerOptional.get().getNickname(),
                    getCardName(),
                    getCardLevel(),
                    !hasFinished(),
                    this.requiredFirepower,
                    this.movementSteps,
                    this.takenItems,
                    this.redItems,
                    this.yellowItems,
                    this.blueItems,
                    this.greenItems);
        } else {
            throw new IllegalArgumentException("There is no player playing in this moment");
        }
        return smugglersStateJSON;
    }



}

