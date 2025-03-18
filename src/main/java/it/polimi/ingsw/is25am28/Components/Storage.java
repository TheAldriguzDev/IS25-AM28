package it.polimi.ingsw.is25am28.Components;

import it.polimi.ingsw.is25am28.Items.Item;
import it.polimi.ingsw.is25am28.Items.ItemColor;

import java.util.ArrayList;
import java.util.List;

public final class Storage extends Component {
      final int capacity;
      final boolean isSpecialStorage;
      final List<Item> storedItems;

      public Storage(int row, int col, int[] connectors, int capacity, boolean isSpecialStorage) {
            super(row, col, connectors);
            this.capacity = capacity;
            this.isSpecialStorage = isSpecialStorage;
            storedItems = new ArrayList<>();
      }

      public int getCapacity() {
            return capacity;
      }

      public boolean isSpecialStorage() {
            return isSpecialStorage;
      }

      public List<Item> getStoredItems() {
            return storedItems;
      }

      /**
       * This method needs to handle eventual items drops, since the user can decide to drop some items
       * */
      public void storeItem(Item item) throws IllegalArgumentException{
            if (!isSpecialStorage && item.getColor() == ItemColor.RED) {
                  throw new IllegalArgumentException("You can't store a special item in a normal storage unit!");
            }

            if (this.capacity == storedItems.size()) {
                  throw new IllegalArgumentException("You can't store more than " + this.capacity + " items!");
            }

            // Store the items in order of value
            int idx = 0;
            while (idx < storedItems.size() && item.getValue() < storedItems.get(idx).getValue()) {
                  idx++;
            }

            storedItems.add(idx, item);
      }

      public void removeItem(Item item) {
            storedItems.remove(item);
      }

      public boolean check( Component[] nearest ){
            return false;
      }

      public int availableSpace() {
            return capacity - storedItems.size();
      }
}