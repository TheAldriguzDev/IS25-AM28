package it.polimi.ingsw.is25am28.EventCards;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;
import it.polimi.ingsw.is25am28.Components.Battery;
import it.polimi.ingsw.is25am28.Components.Component;
import it.polimi.ingsw.is25am28.Components.Storage;
import it.polimi.ingsw.is25am28.Response.SmugglersResponse;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Smugglers extends EventCard {
    private final int requiredFirepower;
    private final int movementSteps;
    private List<Item> givenItems = new ArrayList<>();
    private final int takenItems;

    public Smugglers(String name, int cardLevel, int requiredFirepower, int movementStep, int takenItems) {
        super(name, cardLevel);
        this.requiredFirepower = requiredFirepower;
        this.movementStep = movementStep;
        this.takenItems = takenItems;
    }

    /*
    * Se il tipo di response non è corretto la classe
    * lancia un'eccezione di tipo ClassCastException
    * */

    public EventCard useCard(Object response) throws ClassCastException {
        SmugglersResponse smugglersResponse = (SmugglersResponse) response;
        Player player = getCurrent();
        if(player.getShip().getFirePower() >= requiredFirepower) {
            if (smugglersResponse.getTakeLoot()) {
                bonusEffect();
                player.setCursor(player.getCursor() - this.movementSteps);
            }
        } else {
            malusEffect();
        }
        getNext();
        return this;
    }

    protected void bonusEffect() {
        //GiveItem
        // loadNewItems() dal controller
    }

    protected void malusEffect() {
        //TakeItem
        Player player = getCurrent();
        List<Storage> storages = new ArrayList<>();
        List<Battery> batteries = new ArrayList<>();
        player.getShip().traverse(
                (Component c) -> {
                    if (c.getClass() == Storage.class) {
                        storages.add((Storage) c);
                    }/* else if (c.getClass() == Battery.class) {
                        batteries.add((Battery) c);
                    }*/
                }
        );
        int i = takenItems;
        for (ItemColor color : ItemColor.values()) {
            for (Storage storage : storages) {
                List<Item> storedItems = storage.getStoredItems();
                for (Item item : storedItems) {
                    if (item.getColor() == color) {
                        storage.removeItem(item);
                        i--;
                        if (i == 0) {
                            break;
                        }
                    }
                }
            }
        }
        /*
        for (Battery battery : batteries) {
            // Funzioni di Battery necessarie
        }*/
        player.getShip().setEnergy(player.getShip().getEnergy() - i);
        if (player.getShip().getEnergy() < 0) {
            player.getShip().setEnergy(0);
        }


    }

    @Override
    public JSONObject generateState() {
        return null;
    }
}