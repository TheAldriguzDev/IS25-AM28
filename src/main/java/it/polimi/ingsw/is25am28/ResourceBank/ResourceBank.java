package it.polimi.ingsw.is25am28.ResourceBank;

import it.polimi.ingsw.is25am28.Components.Storage;
import it.polimi.ingsw.is25am28.GameModel.GameModel;
import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;
import it.polimi.ingsw.is25am28.Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ResourceBank {
    private final List<Item> resourceList;

    public ResourceBank() {
        this.resourceList = new ArrayList<>();
    }

    public ResourceBank(ArrayList<Item> resourceList) {
        this.resourceList = resourceList;
    }

    /**
     * Returns all the resources list that are contained in the bank
     * */
    public synchronized List<Item> getResources() {
        return this.resourceList;
    }

    /**
     * Returns the resources having a specific color
     * */
    public synchronized List<Item> getResourcesByColor(ItemColor color) {
        return this.resourceList.stream().filter( i -> i.getColor().equals(color) ).toList();
    }

    /**
     * Add a resource to the bank list
     * This method will be used when we need to initialize the resource list or when a player decide to drop a resource from his ship
     * */
    public synchronized ResourceBank addResourceToBank(Item item) {
        this.resourceList.add(item);

        return this;
    }

    /**
     * Delete a resource from the bank list (when a resource is given to a player)
     *
     * This method is used from the addResourceToPlayer(...) method
     * */
    private synchronized ResourceBank deleteResourceFromBank(Item item) {
        this.resourceList.remove(item);

        return this;
    }

    /**
     * Add a resource to a player and deletes it from the bank.
     *
     * It checks if the player is able to store the given resource
     * */
    public synchronized ResourceBank addResourceToPlayerFromBank(Item item, Player player) throws NoSuchElementException {
        if ( this.resourceList.contains(item) && !player.isEliminated() ) {

            List<Storage> storageList;

            // Get the first available storage for the requested resource
            if (item.getColor().equals(ItemColor.RED)) {
                storageList = player.getShip().getStorageList().stream().filter( s -> s.isSpecialStorage() && s.availableSpace() > 0).toList();
            } else {
                storageList = player.getShip().getStorageList().stream().filter( s -> s.availableSpace() > 0).toList();
            }

            if (storageList.isEmpty()) {
                throw new NoSuchElementException("There are no storage available for the requested resource");
            }

            // Store the item in the user component
            storageList.getFirst().storeItem(item);

            // Remove the item from the bank
            this.deleteResourceFromBank(item);
        } else {
            if (player.isEliminated()) {
                throw new NoSuchElementException("The player has been eliminated, it cannot use this method.");
            } else {
                throw new NoSuchElementException("The request item is not contained in the bank resources.");
            }
        }
        return this;
    }

    /**
     * Remove the given resource from the player storage and add it to the bank resource list
     * */
    public synchronized ResourceBank addResourceToBankFromPlayer(Item item, Player player) throws NoSuchElementException {

        // Check if the player has the requested resource --> At least one storage can be used, we don't need to check if the length is > 0
        if ( !player.isEliminated() && player.getShip().getStorageList().stream().flatMap( s -> s.getStoredItems().stream() ).toList().contains(item) ) {

            // Get the first storage that contains the requested item
            Storage storage= player.getShip().getStorageList().stream().filter( s -> s.getStoredItems().contains(item)).toList().getFirst();

            // Remove the resource from the player storage
            storage.removeItem(item);

            // Add the resource to the bank resources list
            this.addResourceToBank(item);
        } else {
            if (player.isEliminated()) {
                throw new NoSuchElementException("The player has been eliminated, it cannot use this method.");
            } else {
                throw new NoSuchElementException("The request item is not contained in the player resources.");
            }
        }
        return this;
    }
}
