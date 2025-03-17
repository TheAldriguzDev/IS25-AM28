package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
import it.polimi.ingsw.is25am28.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Board.Board;
import it.polimi.ingsw.is25am28.ActionJSON.SmugglersJSON;
import it.polimi.ingsw.is25am28.Items.*;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Battery;
import it.polimi.ingsw.is25am28.Components.Storage;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Smugglers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private final int redItems;
    private final int yellowItems;
    private final int blueItems;
    private final int greenItems;
    private final int takenItems;
    private boolean hasBeenDefeated;


    private ArrayList<Item> givenItems = new ArrayList<>();

    public Smugglers(String name, int cardLevel, int requiredFirepower, int movementSteps, int redItems, int yellowItems, int blueItems, int greenItems, int takenItems, Board board) {
        super(name, cardLevel, board);
        this.requiredFirepower = requiredFirepower;
        this.movementSteps = movementSteps;
        this.redItems = redItems;
        this.yellowItems = yellowItems;
        this.blueItems = blueItems;
        this.greenItems = greenItems;
        this.takenItems = takenItems;
        this.hasBeenDefeated = false;
    }

    /*
    * Se il tipo di response non è corretto la classe
    * lancia un'eccezione di tipo ClassCastException
    * */

    public EventCard useCard(ActionJSON data) throws ClassCastException, IllegalArgumentException {
        //SmugglersResponse smugglersResponse = (SmugglersResponse) response;
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

                    if(player.getShip().getFirePower() >= requiredFirepower) {
                        this.hasBeenDefeated = true;
                        if (smugglersData.getTakeLoot()) {
                            bonusEffect();
                            player.setCursor(player.getCursor() - this.movementSteps);
                        }
                    } else {
                        malusEffect();
                    }
                },
                () -> {
                    throw new IllegalArgumentException("There is no player playing in this moment");
                }
        );
        getNextPlayer();
        return this;
    }

    protected void bonusEffect(ActionJSON data) throws ClassCastException {
        SmugglersJSON smugglersData = (SmugglersJSON) data;
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    // Dubbio su come operare la ridistribuzione delle risorse nel caso lo storage della nave sia pieno
                    // Togliere le risorse che si desiderano scaricare
                    // Caricare le scorte possibili scelte
                    List<Storage> storages = player.getShip().getStorageList();

                    // Gestione rimozione riassegnamento da fare

                    // Rimozione di items
                    int itemsToDrop;
                    // Rimozione dei rossi
                    itemsToDrop = smugglersData.getRedToDrop();
                    for(Storage storage : storages) {
                        if (itemsToDrop == 0) { break; }
                        if (storage.isSpecialStorage()) {
                            List<Item> storedItems = storage.getStoredItems();
                            for (Item item : storedItems) {
                                if (item.getColor() == ItemColor.RED) {
                                    storedItems.remove(item);
                                    itemsToDrop--;
                                    if (itemsToDrop == 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // Rimozione dei gialli
                    itemsToDrop = smugglersData.getYellowToDrop();
                    for(Storage storage : storages) {
                        if (itemsToDrop == 0) { break; }
                        List<Item> storedItems = storage.getStoredItems();
                        for(Item item : storedItems) {
                            if (item.getColor() == ItemColor.YELLOW) {
                                storedItems.remove(item);
                                itemsToDrop--;
                                if (itemsToDrop == 0) { break; }
                            }
                        }
                    }
                    // Rimozione dei blu
                    itemsToDrop = smugglersData.getBlueToDrop();
                    for(Storage storage : storages) {
                        if (itemsToDrop == 0) { break; }
                        List<Item> storedItems = storage.getStoredItems();
                        for(Item item : storedItems) {
                            if (item.getColor() == ItemColor.BLUE) {
                                storedItems.remove(item);
                                itemsToDrop--;
                                if (itemsToDrop == 0) { break; }
                            }
                        }
                    }
                    // Rimozione dei verdi
                    itemsToDrop = smugglersData.getGreenToDrop();
                    for(Storage storage : storages) {
                        if (itemsToDrop == 0) { break; }
                        List<Item> storedItems = storage.getStoredItems();
                        for(Item item : storedItems) {
                            if (item.getColor() == ItemColor.GREEN) {
                                storedItems.remove(item);
                                itemsToDrop--;
                                if (itemsToDrop == 0) { break; }
                            }
                        }
                    }


                    // Aggiunta di items
                    int itemsToLoad;
                    // Aggiunta dei rossi
                    itemsToLoad = smugglersData.getRedToLoad();
                    for(Storage storage : storages) {
                        if (itemsToLoad == 0) { break; }
                        if(storage.isSpecialStorage()) {
                            List<Item> storedItems = storage.getStoredItems();
                            while (storage.getCapacity() > 0 && itemsToLoad > 0) {
                                storage.storeItem(new Item(ItemColor.RED));
                                itemsToLoad--;
                            }
                        }
                    }
                    // Aggiunta dei gialli
                    itemsToLoad = smugglersData.getYellowToLoad();
                    for(Storage storage : storages) {
                        if (itemsToLoad == 0) { break; }
                        List<Item> storedItems = storage.getStoredItems();
                        while (storage.getCapacity() > 0 && itemsToLoad > 0) {
                            storage.storeItem(new Item(ItemColor.YELLOW));
                            itemsToLoad--;
                        }
                    }
                    // Aggiunta dei blu
                    itemsToLoad = smugglersData.getBlueToLoad();
                    for(Storage storage : storages) {
                        if (itemsToLoad == 0) { break; }
                        List<Item> storedItems = storage.getStoredItems();
                        while (storage.getCapacity() > 0 && itemsToLoad > 0) {
                            storage.storeItem(new Item(ItemColor.BLUE));
                            itemsToLoad--;
                        }
                    }
                    // Aggiunta dei verdi
                    itemsToLoad = smugglersData.getGreenToLoad();
                    for(Storage storage : storages) {
                        if (itemsToLoad == 0) { break; }
                        List<Item> storedItems = storage.getStoredItems();
                        while (storage.getCapacity() > 0 && itemsToLoad > 0) {
                            storage.storeItem(new Item(ItemColor.GREEN));
                            itemsToLoad--;
                        }
                    }
                }
        );
    }

    @Override
    protected void bonusEffect() {}

    @Override
    protected void malusEffect() {
        //TakeItem
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    // Rimuove le merci
                    List<Storage> storages = player.getShip().getStorageList();
                    int i = 0;
                    for (ItemColor color : ItemColor.values()) {
                        for (Storage storage : storages) {
                            List<Item> storedItems = storage.getStoredItems();
                            for (Item item : storedItems) {
                                if (item.getColor() == color) {
                                    storage.removeItem(item);
                                    i++;
                                    if (i == takenItems) { return; }
                                }
                            }
                        }
                    }
                    // Rimuove le batterie
                    List<Battery> Batteries = player.getShip().getBatteryList();
                    for (Battery battery : Batteries) {
                        while (battery.getAvailability() > 0) {
                            battery.useBattery(1);
                            i++;
                            if (i == takenItems) { return; }
                        }
                    }
                }
        );




    }

    @Override
    public boolean hasFinished() {
        return currentPlayer.map(player -> player.equals(players.getLast())).orElse(false) || this.hasBeenDefeated;
    }

    @Override
    public JSONObject generateState() {
        /*
        CardStateJSON cardState = new CardStateJSON();
        cardState.setCardLevel(getCardLevel());
        cardState.setCardName(getCardName());

        if (this.getCurrentPlayer().isPresent()) {
            cardState.setPlayerNickname(this.getCurrentPlayer().get().getNickname());
        }*/
        @SuppressWarnings("unchecked")
        JSONObject smugglersState = new JSONObject();
        smugglersState.put("cardName", this.name);
        smugglersState.put("cardLevel", cardLevel);

        if(getCurrentPlayer().isPresent()) {
            smugglersState.put("playerNickname", getCurrentPlayer().get().getNickname());
        }



        /*
        smugglersState.put("requiredFirepower", requiredFirepower);
        smugglersState.put("movementSteps", movementSteps);
        smugglersState.put("redItems", redItems);
        smugglersState.put("yellowItems", yellowItems);
        smugglersState.put("blueItems", blueItems);
        smugglersState.put("greenItems", greenItems);
        smugglersState.put("takenItems", takenItems);
        smugglersState.put("hasBeenDefeated", hasBeenDefeated);
        */


        return smugglersState;
    }



}