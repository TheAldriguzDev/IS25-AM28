package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.ActionJSON.ActionJSON;
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
    }

    /*
    * Se il tipo di response non è corretto la classe
    * lancia un'eccezione di tipo ClassCastException
    * */

    public EventCard useCard(ActionJSON data) throws ClassCastException {
        //SmugglersResponse smugglersResponse = (SmugglersResponse) response;
        SmugglersJSON smugglersData = (SmugglersJSON) data;
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    if(player.getShip().getFirePower() >= requiredFirepower) {
                        if (smugglersData.getTakeLoot()) {
                            bonusEffect();
                            player.setCursor(player.getCursor() - this.movementSteps);
                        }
                    } else {
                        malusEffect();
                    }
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

                    for(int i = 0; i < smugglersData.getRedToTake(); i++) {
                        for(Storage storage : storages) {
                            if(storage.isSpecialStorage()) {
                                while (storage.getCapacity() > 0) {
                                    storage.storeItem(new Item(ItemColor.RED));
                                }
                            }
                        }
                    }
                    for(int i = 0; i < smugglersData.getYellowToTake(); i++) {
                        for(Storage storage : storages) {
                                while (storage.getCapacity() > 0) {
                                    storage.storeItem(new Item(ItemColor.YELLOW));
                                }
                        }
                    }
                    for(int i = 0; i < smugglersData.getBlueToTake(); i++) {
                        for(Storage storage : storages) {
                            while (storage.getCapacity() > 0) {
                                storage.storeItem(new Item(ItemColor.YELLOW));
                            }
                        }
                    }
                    for(int i = 0; i < smugglersData.getGreenToTake(); i++) {
                        for(Storage storage : storages) {
                            while (storage.getCapacity() > 0) {
                                storage.storeItem(new Item(ItemColor.YELLOW));
                            }
                        }
                    }
                }
        );
    }
    protected void bonusEffect() {}

    protected void malusEffect() {
        //TakeItem
        Optional<Player> playerOptional = getCurrentPlayer();
        playerOptional.ifPresent(
                (Player player) -> {
                    // Rimuove le merci
                    List<Storage> storages = player.getShip().getStorageList();
                    int i = takenItems;
                    for (ItemColor color : ItemColor.values()) {
                        for (Storage storage : storages) {
                            List<Item> storedItems = storage.getStoredItems();
                            for (Item item : storedItems) {
                                if (item.getColor() == color) {
                                    storage.removeItem(item);
                                    i--;
                                    if (i == 0) { return; }
                                }
                            }
                        }
                    }
                    // Rimuove le batterie
                    List<Battery> Batteries = player.getShip().getBatteryList();
                    for (Battery battery : Batteries) {
                        while (battery.getAvailability() > 0) {
                            battery.useBattery(1);
                            i--;
                            if (i == 0) { return; }
                        }
                    }
                }
        );




    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}