package it.polimi.ingsw.is25am28.Model.ResourceBank;

import it.polimi.ingsw.is25am28.Model.ActionJSON.ComponentHelper;
import it.polimi.ingsw.is25am28.Model.Components.Storage;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.util.*;

public class ResourceBank {
    private final Map<ItemColor, Integer> resources;

    /**
     * Create the resource bank and initialize it with the correct amount of resources
     * */
    public ResourceBank(int gameLevel) {
        this.resources = new HashMap<>();

        if (gameLevel == 2) {
            // Add the correct number of resources to the bank
            this.resources.put(ItemColor.RED, 12);
            this.resources.put(ItemColor.YELLOW, 17);
            this.resources.put(ItemColor.GREEN, 13);
            this.resources.put(ItemColor.BLUE, 14);
        } else {
            this.resources.put(ItemColor.RED, Integer.MAX_VALUE);
            this.resources.put(ItemColor.YELLOW, Integer.MAX_VALUE);
            this.resources.put(ItemColor.GREEN, Integer.MAX_VALUE);
            this.resources.put(ItemColor.BLUE, Integer.MAX_VALUE);
        }
    }

//    /**
//     * This method is used to reset the current quantity of the resources when a player reconnects to the game and needs to rebuild the model information
//     * */
//    public void resetResourcesQuantity(int red, int yellow, int green, int blue) {
//        this.resources.put(ItemColor.RED, red);
//        this.resources.put(ItemColor.YELLOW, yellow);
//        this.resources.put(ItemColor.GREEN, green);
//        this.resources.put(ItemColor.BLUE, blue);
//    }

    /**
     * This method is used to reset the current quantity of the resources when a player reconnects to the game and needs to rebuild the model information
     * */
    public void resetResourcesQuantity(Map<ItemColor, Integer> availableResources) {
        this.resources.putAll(availableResources);
    }

    /**
     * Returns the Map that contains the ResourceColor and the amount available
     * */
    public synchronized Map<ItemColor, Integer> getResources() {
        return resources;
    }

    /**
     * Return the amount of availability of the given resource color
     * */
    public synchronized int getResourceAvailabilityFromColor(ItemColor color) {
        return resources.get(color);
    }

    /**
     * Increase the amount of availability for the given resource color
     * */
    public synchronized void addResourceToBank(ItemColor color) {
        resources.put(color, resources.get(color) + 1);
    }

    /**
     * Decrease the amount of availability for the given resource color
     * */
    public synchronized void removeResourceFromBank(ItemColor color) {
        resources.put(color, resources.get(color) - 1);
    }

    /**
     * It checks if:
     * 1. The given component is a Storage
     * 2. If the storage has enough space to store the item
     * 3. If the storage can store the given item (used for RED resources)
     * 4. If the player is not eliminated and the bank has the requested resource --> added to the given storage
     * */
    public synchronized ResourceBank addResourceToPlayerFromBank(Player player, ItemColor color, int i, int j) throws IllegalStateException, NoSuchElementException {
        Storage storage;

        // Check if the given coordinates identify a storage component
        try {
            storage = (Storage) player.getShip().getComponent(i, j);
        } catch (Exception e) {
            throw new IllegalStateException("The given component i: " + i + " j: " + j + " is not a valid Storage component");
        }

        /*
        // Check if the given storage component has enough space
        if (storage.availableSpace() <= 0) {
            throw new IllegalStateException("The given storage component i: " + i + " j: " + j + " has no more space");
        }

        // Check if the given storage component can store the given resource
        if (color.equals(ItemColor.RED) && !storage.isSpecialStorage()) {
            throw new IllegalStateException("The given storage component i: " + i + " j: " + j + " cannot store RED resources");
        }

        // Apply the action if the player is not eliminated and the bank has at least one resource of the required type
        if ( !player.isEliminated() &&
                this.resources.get(color) > 0) {

            storage.storeItem( new Item(color) );
            this.removeResourceFromBank(color);
        }
        */

        // If the given storage component has enough space, then insert the given item
        if (storage.availableSpace() > 0) {
            // Check if the given storage component can store the given resource
            if (color.equals(ItemColor.RED) && !storage.isSpecialStorage()) {
                throw new IllegalStateException("The given storage component cannot store RED resources");
            }

            // Apply the action if the player is not eliminated and the bank has at least one resource of the required type
            if ( !player.isEliminated() && this.resources.get(color) > 0) {
                storage.storeItem( new Item(color) );
                this.removeResourceFromBank(color);
            }
        }

        return this;
    }

    /**
     * It checks if:
     * 1. The given component is a Storage
     * 2. If the player has bot been eliminated and the item is found in the storage --> removed from the storage and added to the bank
     * */
    public synchronized ResourceBank addResourceToBankFromPlayer(Player player, ItemColor color, int i, int j) throws IllegalStateException, NoSuchElementException {
        Storage storage;

        // Check if the given coordinates identify a storage component
        try {
            storage = (Storage) player.getShip().getComponent(i, j);
        } catch (Exception e) {
            throw new IllegalStateException("The given component [i, j] is not a valid Storage component");
        }

        if (!player.isEliminated()) {
            Optional<Item> foundItem = storage.getStoredItems().stream()
                    .filter( item -> item.getColor().equals(color))
                    .findFirst();

            // Remove the resource from the player and add it to the bank
            foundItem.ifPresent( item -> {
                storage.removeItem(item);
                this.addResourceToBank(color);
            });
        }

        return this;
    }
}
