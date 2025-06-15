package it.polimi.ingsw.is25am28.Model.ResourceBank;

import it.polimi.ingsw.is25am28.Model.Components.Storage;
import it.polimi.ingsw.is25am28.Model.Items.Item;
import it.polimi.ingsw.is25am28.Model.Items.ItemColor;
import it.polimi.ingsw.is25am28.Model.Player.Player;

import java.util.*;

public class ResourceBank {
    private final Map<ItemColor, Integer> resources;

    /**
     * Creates and initializes the ResourceBank object with either a finite number of resources for each color based on a specific game level
     * or an unlimited number of resources for all colors.
     *
     * @param gameLevel the game level determining resource limits.
     */
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

    /**
     * Resets the quantities of resources available in the resource bank
     * based on the specified map of resources.
     * Useful to recreate the {@code ClientModel} information after a client reconnection
     *
     * @param availableResources a map where the key is an {@code ItemColor} representing
     *                           the resource color and the value is an {@code Integer}
     *                           representing the available quantity for that color.
     */
    public void resetResourcesQuantity(Map<ItemColor, Integer> availableResources) {
        this.resources.putAll(availableResources);
    }

    /**
     * Retrieves the current mapping of resource colors and their respective quantities
     * available in the resource bank.
     *
     * @return a map where the keys represent {@code ItemColor} instances
     *         (indicating resource colors) and the values represent the corresponding quantities
     *         of each resource.
     */
    public synchronized Map<ItemColor, Integer> getResources() {
        return resources;
    }

    /**
     * Retrieves the quantity of available resources of a specific color from the resource bank.
     *
     * @param color the {@code ItemColor} representing the resource color whose availability
     *              is to be retrieved.
     * @return an {@code int} indicating the quantity of available resources of the specified color.
     */
    public synchronized int getResourceAvailabilityFromColor(ItemColor color) {
        return resources.get(color);
    }

    /**
     * Increments the quantity of the specified resource color in the resource bank by one.
     *
     * @param color the {@code ItemColor} representing the resource color to be added.
     */
    public synchronized void addResourceToBank(ItemColor color) {
        resources.put(color, resources.get(color) + 1);
    }

    /**
     * Decreases the quantity of the specified resource color in the resource bank by one.
     *
     * @param color the {@code ItemColor} representing the resource color to be removed.
     */
    public synchronized void removeResourceFromBank(ItemColor color) {
        resources.put(color, resources.get(color) - 1);
    }

    /**
     * Transfers a resource of a specified color from the resource bank to a player's storage,
     * provided the storage is valid, has enough space, and the player is not eliminated.
     * Ensures that RED resources can only be stored in special storage components.
     * The operation is performed only if the resource bank has at least one resource
     * of the specified color available.
     *
     * @param player the {@code Player} receiving the resource.
     * @param color the {@code ItemColor} of the resource to be transferred.
     * @param i the row index of the target storage component on the player's ship.
     * @param j the column index of the target storage component on the player's ship.
     * @return the {@code ResourceBank} instance after the operation is executed.
     * @throws IllegalStateException if the specified storage component is invalid,
     *                               has no more space, or cannot store RED resources.
     * @throws NoSuchElementException if the resource bank does not have the specified resource.
     */
    public synchronized ResourceBank addResourceToPlayerFromBank(Player player, ItemColor color, int i, int j) throws IllegalStateException, NoSuchElementException {
        Storage storage;

        // Check if the given coordinates identify a storage component
        try {
            storage = (Storage) player.getShip().getComponent(i, j);
        } catch (Exception e) {
            throw new IllegalStateException("The given component i: " + i + " j: " + j + " is not a valid Storage component");
        }

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
     * Transfers a resource of a specified color from a player's storage component to the resource bank.
     * Validates whether the specified storage component exists and has an item of the provided color.
     * The operation is performed only if the player is not eliminated.
     *
     * @param player the {@code Player} whose resource is to be transferred to the bank.
     * @param color the {@code ItemColor} representing the color of the resource to be transferred.
     * @param i the row index of the storage component on the player's ship.
     * @param j the column index of the storage component on the player's ship.
     * @return the {@code ResourceBank} instance after the operation is executed.
     * @throws IllegalStateException if the specified storage component at indices {@code i}, {@code j} is invalid.
     * @throws NoSuchElementException if there is no resource of the specified {@code ItemColor} in the selected storage.
     */
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
